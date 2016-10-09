package br.com.auditoria.joia.thread;

import static java.lang.System.out;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import br.com.auditoria.joia.util.JoiaConfig;
import br.com.auditoria.joia.util.JoiaUtil;
import br.com.auditoria.joia.util.ZipUtil;

/**
 * Thread responsavel por descompactar os arquivos de logs dos Jobs.
 * 
 * @author czt7
 * 
 */
public class ThreadUnzipper implements Runnable
{
    private static int I = 0;

    private String threadName;
    private BlockingQueue<File> zipFileQueue;
    private JoiaConfig config;
    private String[] args;

    public ThreadUnzipper(String threadName, BlockingQueue<File> zipFileQueue,
            JoiaConfig config, String[] args)
    {
        this.threadName = threadName;
        this.zipFileQueue = zipFileQueue;
        this.config = config;
        this.args = args;
    }

    public void run()
    {
        File zip = null;
        String sourceLogDir = config.getSourceLogDir();
        try
        {
            while ((zip = zipFileQueue.poll()) != null)
            {
                String zipPathName = zip.getAbsolutePath();
                String unzipFileName = JoiaUtil.getUnzipFileName(zip);
                String unzipPathName = sourceLogDir + "/" + unzipFileName;

                switch (config.getSourceZipType())
                {
                    case Z:
                        out.printf("(%s) %d. %s => %s\n", threadName, ++I,
                                zipPathName, unzipPathName);
                        ZipUtil.uncompressZ(zip, sourceLogDir, unzipFileName);
                        break;
                    case GZIP:
                        out.printf("(%s) %d. %s => %s\n", threadName, ++I,
                                zipPathName, unzipPathName);
                        ZipUtil.unGzip(zip, sourceLogDir,
                                JoiaUtil.getUnzipFileName(zip));
                        break;
                    case ZIP:
                        out.printf("(%s) %d. %s => %s \n", threadName, ++I,
                                zipPathName, sourceLogDir);
                        ZipUtil.unzip(zip, sourceLogDir);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            ThreadJoia.registerException(e, args);
            System.exit(0);
        }
    }
}
