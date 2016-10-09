package br.com.auditoria.joia.entity;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator usado para ordenar uma colecao de arquivos pelo respectivo nome.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class FileComparator implements Comparator<File>
{
    public int compare(File f1, File f2)
    {
        return ((f1 != null) && (f2 != null)) ? f1.getName().toUpperCase()
                .compareTo(f2.getName().toUpperCase()) : 0;
    }
}
