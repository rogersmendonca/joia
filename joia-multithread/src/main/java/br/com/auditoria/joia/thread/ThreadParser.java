package br.com.auditoria.joia.thread;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.auditoria.joia.entity.JobLogLine;
import br.com.auditoria.joia.entity.MonthExec;

/**
 * Thread responsavel por interpretar os arquivos de logs dos Jobs.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class ThreadParser implements Runnable
{
    private static final Pattern PATTERN_JOB_MONTH = Pattern
            .compile(".*_\\d{4}(\\d{2}).*");

    private String threadName;
    private BlockingQueue<File> logFileQueue;
    private String[] args;
    private PrintWriter pwMonthTotal;
    private File fDestExecDir;
    private PrintWriter pwEndedNotOk;
    private boolean registerExec;

    public ThreadParser(String threadName, BlockingQueue<File> logFileQueue,
            String[] args, PrintWriter pwMonthTotal, File fDestExecDir,
            PrintWriter pwEndedNotOk, boolean registerExec)
    {
        this.threadName = threadName;
        this.logFileQueue = logFileQueue;
        this.args = args;
        this.pwMonthTotal = pwMonthTotal;
        this.fDestExecDir = fDestExecDir;
        this.pwEndedNotOk = pwEndedNotOk;
        this.registerExec = registerExec;
    }

    public void run()
    {
        File logFile = null;
        MonthExec monthExec = new MonthExec(System.currentTimeMillis());
        try
        {
            while ((logFile = logFileQueue.poll()) != null)
            {
                registerMonthExec(logFile, monthExec, pwMonthTotal);

                parseLog(logFile, monthExec, fDestExecDir, pwEndedNotOk);
            }

            registerMonthExec(null, monthExec, pwMonthTotal);
        }
        catch (IOException e)
        {
            pwEndedNotOk.close();
            pwMonthTotal.close();
            ThreadJoia.closeMapExecPrintWriters();

            ThreadJoia.registerException(e, args);
            System.exit(0);
        }
    }

    private void parseLog(File logFile, MonthExec monthExec, File fDestDir,
            PrintWriter pwEndedNotOk) throws IOException
    {
        out.println(logFile.getAbsolutePath());
        BufferedReader r = new BufferedReader(new FileReader(logFile));

        String line;
        while ((line = r.readLine()) != null)
        {
            monthExec.incTotal();
            synchronized (ThreadJoia.TOTAL_COUNT)
            {
                out.printf("%d. %s\n", ++ThreadJoia.TOTAL_COUNT, line);
            }
            JobLogLine jobLogLine = new JobLogLine(line);
            if (jobLogLine.startWithDate())
            {

                // Job Ended Not Ok - registra e incrementa contador
                if (jobLogLine.isEndedNotOk())
                {
                    monthExec.incNotOk();
                    synchronized (pwEndedNotOk)
                    {
                        ThreadJoia.NOTOK_COUNT++;
                        pwEndedNotOk.println(jobLogLine.getCsvLine());
                    }
                }

                // Job Execution - registra e incrementa contador
                if (jobLogLine.isEndedAt())
                {
                    monthExec.incExec();
                    ThreadJoia.EXEC_COUNT++;

                    if (registerExec)
                    {
                        synchronized (ThreadJoia.MAP_JOB_EXEC_COUNT)
                        {

                            Long jobExecCount = ThreadJoia.MAP_JOB_EXEC_COUNT
                                    .get(jobLogLine.getJobName());
                            if (jobExecCount == null)
                            {
                                jobExecCount = 0L;
                            }
                            ThreadJoia.MAP_JOB_EXEC_COUNT.put(
                                    jobLogLine.getJobName(), ++jobExecCount);
                        }

                        PrintWriter pwExec = null;
                        synchronized (ThreadJoia.MAP_JOB_EXEC_LOG)
                        {
                            pwExec = ThreadJoia.MAP_JOB_EXEC_LOG.get(jobLogLine
                                    .getJobName());
                            if (pwExec == null)
                            {
                                pwExec = ThreadJoia.createResultFile(fDestDir,
                                        jobLogLine.getJobExecLogFile(),
                                        JobLogLine.CSV_LOG_EXEC_HEADER);
                                ThreadJoia.MAP_JOB_EXEC_LOG.put(
                                        jobLogLine.getJobName(), pwExec);
                            }
                        }
                        synchronized (pwExec)
                        {
                            pwExec.println(jobLogLine.getCsvExec());
                        }
                    }
                }
            }
            else
            {
                continue;
            }
        }

        r.close();
    }

    private void registerMonthExec(File logFile, MonthExec prevMonthExec,
            PrintWriter pwMonthTotal)
    {
        if (logFile != null)
        {
            Matcher jmMatcher = PATTERN_JOB_MONTH.matcher(logFile.getName()
                    .toLowerCase());
            String jobMonth = jmMatcher.matches() ? jmMatcher.group(1) : "";
            if (!prevMonthExec.equalsMonth(jobMonth))
            {
                long month_end = System.currentTimeMillis();
                if (!prevMonthExec.isFirst())
                {
                    prevMonthExec.setEnd(month_end);
                    pwMonthTotal.printf("%s;%s;%d;%d;%d;%d;%d\n", threadName,
                            prevMonthExec.getMonth(), prevMonthExec.getStart(),
                            prevMonthExec.getEnd(), prevMonthExec.getTotal(),
                            prevMonthExec.getExec(), prevMonthExec.getNotOk());
                }
                prevMonthExec.reset(jobMonth, month_end);
            }
        }
        else
        {
            long month_end = System.currentTimeMillis();
            prevMonthExec.setEnd(month_end);
            pwMonthTotal.printf("%s;%s;%d;%d;%d;%d;%d\n", threadName,
                    prevMonthExec.getMonth(), prevMonthExec.getStart(),
                    prevMonthExec.getEnd(), prevMonthExec.getTotal(),
                    prevMonthExec.getExec(), prevMonthExec.getNotOk());
        }
    }
}