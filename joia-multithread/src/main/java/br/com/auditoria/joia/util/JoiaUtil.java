package br.com.auditoria.joia.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe com metodos utilitarios.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaUtil
{
	public static boolean stringToBoolean(String str)
	{
		return ((str != null) && (str.equalsIgnoreCase("1")
				|| str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true") || str
					.equalsIgnoreCase("sim")));
	}

	public static String trim(String str)
	{
		return str != null ? str.trim() : "";
	}

	public static String getUnzipFileName(File fZip)
	{
		String unzipFileName = fZip.getName();
		int pos = unzipFileName.lastIndexOf(".");
		unzipFileName = (pos > 0) ? unzipFileName.substring(0, pos)
				: unzipFileName + ".uncompress";
		return unzipFileName;
	}

	public static String[] csvToArray(String dirs)
	{
		return dirs != null ? dirs.split(";") : new String[0];
	}

	public static PrintWriter createPrintWriter(File fOutput)
			throws IOException
	{
		return createPrintWriter(fOutput, null);
	}

	public static PrintWriter createPrintWriter(File fOutput, String header)
			throws IOException
	{
		boolean newFile = !fOutput.exists();

		if (newFile)
		{
			fOutput.delete();
			fOutput.createNewFile();
		}

		PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(
				fOutput, true)), true);
		if (newFile && (header != null))
		{
			w.println(header);
		}
		return w;
	}
}
