package br.com.auditoria.joia.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filtro utilizado na lista de arquivos dos diretorios configurados no
 * config.properties ("source.zip.dir"; "source.log.dir").
 * <p>
 * Utiliza a expressao regular configurada em "source.zip.filename.filter" ou
 * "source.log.filename.filter".
 * </p>
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaFilenameFilter implements FilenameFilter
{
	private String regex;
	private boolean hasFilter;

	public JoiaFilenameFilter(String regex)
	{
		this.regex = regex;
		this.hasFilter = (regex != null) && (regex.length() > 0);
	}

	public boolean accept(File directory, String fileName)
	{
		return hasFilter ? fileName.matches(regex) : true;
	}
}
