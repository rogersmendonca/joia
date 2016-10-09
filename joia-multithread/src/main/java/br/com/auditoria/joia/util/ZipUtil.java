package br.com.auditoria.joia.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

/**
 * Classe com os metodos utilitarios de descompactacao de arquivos.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class ZipUtil
{
    private static final int BUFFER_SIZE = 8 * 1024;

    public static void unzip(File sourceZipFile, String destFolder)
            throws ZipException
    {
        ZipFile zipFile = new ZipFile(sourceZipFile);
        zipFile.extractAll(destFolder);
    }

    public static void unGzip(File sourceZipFile, String destFolder,
            String destFile) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];

        FileInputStream fileIn = new FileInputStream(sourceZipFile);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
        FileOutputStream fileOutputStream = new FileOutputStream(destFolder
                + "/" + destFile);

        int bytes_read;
        while ((bytes_read = gZIPInputStream.read(buffer)) > 0)
        {
            fileOutputStream.write(buffer, 0, bytes_read);
        }

        gZIPInputStream.close();
        fileOutputStream.close();
    }

    public static void uncompressZ(File sourceZipFile, String destFolder,
            String destFile) throws IOException
    {
        FileInputStream fin = new FileInputStream(sourceZipFile);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream out = new FileOutputStream(destFolder + "/" + destFile);
        ZCompressorInputStream zIn = new ZCompressorInputStream(in);
        final byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = zIn.read(buffer)))
        {
            out.write(buffer, 0, n);
        }
        out.close();
        zIn.close();

    }
}
