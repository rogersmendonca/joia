package br.com.auditoria.joia.thread;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.lingala.zip4j.exception.ZipException;
import br.com.auditoria.joia.entity.FileComparator;
import br.com.auditoria.joia.entity.JobLogLine;
import br.com.auditoria.joia.util.JoiaConfig;
import br.com.auditoria.joia.util.JoiaFilenameFilter;
import br.com.auditoria.joia.util.JoiaUtil;

/**
 * Thread principal de execucao do programa (main).
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class ThreadJoia implements Runnable
{
	private static String FILE_JOB_ENDED_NOTOK = "JOB_ENDED_NOTOK.csv";
	private static String FILE_JOB_COUNT = "JOB_COUNT.csv";
	private static String FILE_JOB_MONTH_TOTAL = "JOB_MONTH_TOTAL.csv";

	public static Long START;
	public static Long END;
	public static Long TOTAL_COUNT;
	public static Long EXEC_COUNT;
	public static Long NOTOK_COUNT;

	public static Map<String, Long> MAP_JOB_EXEC_COUNT;
	public static Map<String, PrintWriter> MAP_JOB_EXEC_LOG;

	private JoiaConfig config;
	private String[] args;

	public static void main(String[] args)
	{
		try
		{
			// Le config.properties e cria objeto config
			String configPath = getConfigPath(args);
			JoiaConfig config = JoiaConfig.getInstance("config", configPath);

			// Executa thread principal
			new Thread(new ThreadJoia(config, args)).start();
		}
		catch (Exception e)
		{
			registerException(e, args);
		}
	}

	private static String getConfigPath(String[] args)
	{
		return ((args != null) && (args.length > 0) && (args[0] != null) && (args[0]
				.trim().length() > 0)) ? args[0] : null;
	}

	public ThreadJoia(JoiaConfig config, String[] args)
	{
		this.config = config;
		this.args = args;
		initMapExec();
	}

	public void run()
	{
		try
		{
			// Display inicial
			initDisplay();

			// Unzip
			if (config.isSourceUnzip() && (config.getSourceUnzipThreads() > 0))
			{
				unzipLogFiles();
			}

			// Interpreta logs
			if (config.isSourceParser()
					&& (config.getSourceParserThreads() > 0))
			{
				parseLogFiles();
			}

			// Display final
			finalDisplay();
		}
		catch (Exception e)
		{
			registerException(e, args);
		}
	}

	private void initDisplay()
	{
		out.println("");
		out.println("==================================================");
		out.println(" JOIA: Job Operation - Interpretador Avancado");
		out.println("==================================================");
		out.println("Configuracao (config.properties):");
		out.println(config);
		out.print("Confirma (S/N)? ");
		Scanner in = new Scanner(System.in);
		String strIn = in.next();
		in.close();
		if ((strIn == null) || !strIn.trim().equalsIgnoreCase("S"))
		{
			out.println("");
			out.println("Execucao cancelada. Por favor, ajuste a configuracao em \"config.properties\".");
			System.exit(0);
		}

		// Inicio da execucao
		START = System.currentTimeMillis();
	}

	private void unzipLogFiles() throws IOException, ZipException,
			InterruptedException
	{
		out.println("Unzipping logs ...");

		File fSourceZipDir = new File(config.getSourceZipDir());
		File[] zips = fSourceZipDir.listFiles(new JoiaFilenameFilter(config
				.getSourceZipFileNameFilter()));

		if ((zips != null) && (zips.length > 0))
		{
			// Cria zips blocking queue
			Arrays.sort(zips, new FileComparator());
			BlockingQueue<File> zipFileQueue = new ArrayBlockingQueue<File>(
					zips.length, true);
			for (int i = 0; i < zips.length; i++)
			{
				zipFileQueue.offer(zips[i]);
			}

			// Executa unzipper threads
			int unzipperCount = config.getSourceUnzipThreads();
			Thread[] threads = new Thread[unzipperCount];
			for (int i = 0; i < unzipperCount; i++)
			{
				String threadName = String.format("ThreadUnzipper-%03d", i + 1);
				Thread t = new Thread(new ThreadUnzipper(threadName,
						zipFileQueue, config, args));
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
				threads[i] = t;
			}

			for (int i = 0; i < unzipperCount; i++)
			{
				threads[i].join();
			}
		}
	}

	private void parseLogFiles() throws IOException, InterruptedException
	{
		File fLogDir = new File(config.getSourceLogDir());
		File[] logs = fLogDir.listFiles(new JoiaFilenameFilter(config
				.getSourceLogFileNameFilter()));

		if ((logs != null) && (logs.length > 0))
		{
			// Inicializacoes
			String destDir = config.getDestDir();
			File fDestDir = getDirectory(destDir);
			File fDestExecDir = getDirectory(destDir + "/exec");
			PrintWriter pwEndedNotOk = createResultFile(fDestDir,
					FILE_JOB_ENDED_NOTOK, JobLogLine.CSV_LOG_LINE_HEADER);
			PrintWriter pwMonthTotal = createResultFile(fDestDir,
					FILE_JOB_MONTH_TOTAL,
					"THREAD;MONTH;START;END;TOTAL;EXEC;NOTOK");
			initMapExec();
			initMapExecPrintWriters();

			// Cria logs blocking queue
			Arrays.sort(logs, new FileComparator());
			BlockingQueue<File> logFileQueue = new ArrayBlockingQueue<File>(
					logs.length, true);
			for (int i = 0; i < logs.length; i++)
			{
				logFileQueue.offer(logs[i]);
			}

			// Executa parser threads
			int parserCount = config.getSourceParserThreads();
			Thread[] threads = new Thread[parserCount];
			long startParser = System.currentTimeMillis();
			for (int i = 0; i < parserCount; i++)
			{
				String threadName = String.format("ThreadParser-%03d", i + 1);
				Thread t = new Thread(new ThreadParser(threadName,
						logFileQueue, args, pwMonthTotal, fDestExecDir,
						pwEndedNotOk, config.isSourceRegisterExec()));
				t.start();
				t.setPriority(Thread.MAX_PRIORITY);
				threads[i] = t;
			}

			for (int i = 0; i < parserCount; i++)
			{
				threads[i].join();
			}

			// Registra total de interpretacoes
			long endParser = System.currentTimeMillis();
			pwMonthTotal.printf("%s;%s;%d;%d;%d;%d;%d\n", "TOTAL", "",
					startParser, endParser, TOTAL_COUNT, EXEC_COUNT,
					NOTOK_COUNT);

            // Registra total de execucoes de Jobs
			PrintWriter pwJobCount = createResultFile(fDestDir, FILE_JOB_COUNT,
					"Jobname;Exec Count");
			for (Map.Entry<String, Long> entry : MAP_JOB_EXEC_COUNT.entrySet())
			{
				pwJobCount.printf("%s;%d\n", entry.getKey(), entry.getValue());
			}
			pwJobCount.close();

			pwEndedNotOk.close();
			pwMonthTotal.close();
			closeMapExecPrintWriters();
		}
	}

	private void finalDisplay() throws IOException
	{
		// Final da execucao
		END = System.currentTimeMillis();

		File fDir = new File(config.getDestDir());
		PrintWriter pwfExecFinalized = createResultFile(fDir,
				"EXEC_FINALIZED.TXT", null);
		pwfExecFinalized.printf(
				"Joia executed (start: %d; end: %d; duration: %d)\n", START,
				END, END - START);
		pwfExecFinalized.printf("%d LOG LINES PARSED.\n", TOTAL_COUNT);
		pwfExecFinalized.printf("%d JOB EXECUTIONS.\n", EXEC_COUNT);
		pwfExecFinalized.printf("%d JOB ENDED NOT OK.\n", NOTOK_COUNT);
		pwfExecFinalized.close();

		out.println("EXECUCAO FINALIZADA COM SUCESSO\n");
		out.printf("INICIO: %d ms\n", START);
		out.printf("FIM: %d ms\n", END);
		out.printf("DURACAO (FIM - INICIO): %d ms\n", END - START);
		out.printf("LINHAS INTERPRETADAS: %d\n", TOTAL_COUNT);
		out.printf("JOB EXECUTIONS: %d\n", EXEC_COUNT);
		out.printf("JOB ENDED NOT OK: %d\n", NOTOK_COUNT);
	}

	public static void registerException(Exception e, String[] args)
	{
		try
		{
			String configPath = getConfigPath(args);
			String destDir = JoiaConfig.getInstance("config", configPath)
					.getDestDir();
			PrintWriter pwfExecFinalized = createResultFile(new File(destDir),
					"EXEC_FINALIZED.TXT", null);
			pwfExecFinalized.println(e.toString());
			pwfExecFinalized.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public static PrintWriter createResultFile(File fDir, String name,
			String header) throws IOException
	{
		File file = new File(fDir, name);
		return JoiaUtil.createPrintWriter(file, header);
	}

	private static File getDirectory(String dir)
	{
		File fDir = new File(dir);
		if (!fDir.exists())
		{
			fDir.mkdirs();
		}

		return fDir;
	}

	private void initMapExec()
	{
		TOTAL_COUNT = 0L;
		EXEC_COUNT = 0L;
		NOTOK_COUNT = 0L;

		MAP_JOB_EXEC_COUNT = new HashMap<String, Long>();
		MAP_JOB_EXEC_LOG = new HashMap<String, PrintWriter>();
	}

	private void initMapExecPrintWriters()
	{
		closeMapExecPrintWriters();

		MAP_JOB_EXEC_LOG = new HashMap<String, PrintWriter>();
	}

	public static void closeMapExecPrintWriters()
	{
		for (PrintWriter pw : MAP_JOB_EXEC_LOG.values())
		{
			pw.close();
		}
	}
}