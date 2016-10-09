package br.com.auditoria.joia.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

/**
 * Singleton de representacao do config.properties.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaConfig
{
    private static JoiaConfig joiaConfig;

    private boolean sourceUnzip;
    private int sourceUnzipThreads;
    private ZipType sourceZipType;
    private String sourceZipDir;
    private String sourceZipFileNameFilter;
    private boolean sourceParser;
    private boolean sourceRegisterExec;
    private int sourceParserThreads;
    private String sourceLogDir;
    private String sourceLogFileNameFilter;
    private String destDir;

    private Map<String, String> configMap;

    public static JoiaConfig getInstance(String configBaseName, String path)
            throws IOException
    {
        if (joiaConfig == null)
        {
            joiaConfig = new JoiaConfig(configBaseName, path);
        }
        return joiaConfig;
    }

    public static JoiaConfig getInstance(String configBaseName)
            throws IOException
    {
        return getInstance(configBaseName, null);
    }

    private JoiaConfig()
    {
        configMap = new LinkedHashMap<String, String>();
    }

    private JoiaConfig(String configBaseName, String path) throws IOException
    {
        this();
        ResourceBundle config = null;

        if (path != null)
        {
            File file = new File(path);
            URL[] urls = { file.toURI().toURL() };
            ClassLoader loader = new URLClassLoader(urls);
            config = ResourceBundle.getBundle(configBaseName,
                    Locale.getDefault(), loader);
        }
        else
        {
            config = ResourceBundle.getBundle(configBaseName);
        }
        init(config);
    }

    private void init(ResourceBundle config)
    {
        setSourceUnzip(config);
        setSourceUnzipThreads(config);
        setSourceZipType(config);
        setSourceZipDir(config);
        setSourceZipFileNameFilter(config);
        setSourceParser(config);
        setSourceRegisterExec(config);
        setSourceParserThreads(config);
        setSourceLogDir(config);
        setSourceLogFileNameFilter(config);
        setDestDir(config);
    }

    public String toString()
    {
        StringBuilder configStr = new StringBuilder();

        for (Entry<String, String> entry : configMap.entrySet())
        {
            configStr.append(String.format("%s = %s\n", entry.getKey(),
                    entry.getValue()));
        }

        return configStr.toString();
    }

    private String getValue(ResourceBundle config, String key,
            String defaultValue)
    {
        String value = defaultValue;
        try
        {
            value = config.getString(key);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    private int getValue(ResourceBundle config, String key, int defaultValue)
    {
        int valueInt = defaultValue;
        try
        {
            valueInt = Integer.valueOf(getValue(config, key,
                    String.valueOf(defaultValue)));
        }
        catch (Exception e)
        {

        }
        return valueInt;
    }

    public ZipType getSourceZipType()
    {
        return sourceZipType;
    }

    protected void setSourceZipType(ZipType sourceZipType)
    {
        this.sourceZipType = sourceZipType;
    }

    protected void setSourceZipType(ResourceBundle config)
    {
        String key = "source.zip.type";
        String value = getValue(config, key, "Z").toUpperCase();
        configMap.put(key, value);

        setSourceZipType(ZipType.valueOf(value));
    }

    public boolean isSourceUnzip()
    {
        return sourceUnzip;
    }

    protected void setSourceUnzip(boolean sourceUnzip)
    {
        this.sourceUnzip = sourceUnzip;
    }

    protected void setSourceUnzip(ResourceBundle config)
    {
        String key = "source.unzip";
        String value = getValue(config, key, "0");
        configMap.put(key, value);

        setSourceUnzip(JoiaUtil.stringToBoolean(value));
    }

    public String getSourceZipDir()
    {
        return sourceZipDir;
    }

    protected void setSourceZipDir(String sourceZips)
    {
        this.sourceZipDir = JoiaUtil.trim(sourceZips);
    }

    protected void setSourceZipDir(ResourceBundle config)
    {
        String key = "source.zip.dir";
        String value = getValue(config, key, null);
        configMap.put(key, value);

        setSourceZipDir(value);
    }

    public String getSourceLogDir()
    {
        return sourceLogDir;
    }

    protected void setSourceLogDir(String sourceLogDir)
    {
        this.sourceLogDir = JoiaUtil.trim(sourceLogDir);
    }

    protected void setSourceLogDir(ResourceBundle config)
    {
        String key = "source.log.dir";
        String value = getValue(config, key, null);
        configMap.put(key, value);

        setSourceLogDir(value);
    }

    public String getSourceLogFileNameFilter()
    {
        return sourceLogFileNameFilter;
    }

    protected void setSourceLogFileNameFilter(String sourceLogFileNameFilter)
    {
        this.sourceLogFileNameFilter = JoiaUtil.trim(sourceLogFileNameFilter);
    }

    protected void setSourceLogFileNameFilter(ResourceBundle config)
    {
        String key = "source.log.filename.filter";
        String value = getValue(config, key, null);
        configMap.put(key, value);

        setSourceLogFileNameFilter(value);
    }

    public String getDestDir()
    {
        return destDir;
    }

    protected void setDestDir(String destDir)
    {
        this.destDir = JoiaUtil.trim(destDir);
    }

    protected void setDestDir(ResourceBundle config)
    {
        String key = "dest.dir";
        String value = getValue(config, key, null);
        configMap.put(key, value);

        setDestDir(value);
    }

    public String getSourceZipFileNameFilter()
    {
        return sourceZipFileNameFilter;
    }

    protected void setSourceZipFileNameFilter(String sourceZipFileNameFilter)
    {
        this.sourceZipFileNameFilter = JoiaUtil.trim(sourceZipFileNameFilter);
    }

    protected void setSourceZipFileNameFilter(ResourceBundle config)
    {
        String key = "source.zip.filename.filter";
        String value = getValue(config, key, null);
        configMap.put(key, value);

        setSourceZipFileNameFilter(value);
    }

    public boolean isSourceParser()
    {
        return sourceParser;
    }

    protected void setSourceParser(boolean sourceParser)
    {
        this.sourceParser = sourceParser;
    }

    protected void setSourceParser(ResourceBundle config)
    {
        String key = "source.parser";
        String value = getValue(config, key, "0");
        configMap.put(key, value);

        setSourceParser(JoiaUtil.stringToBoolean(value));
    }

    public int getSourceUnzipThreads()
    {
        return sourceUnzipThreads;
    }

    protected void setSourceUnzipThreads(int sourceUnzipThreads)
    {
        this.sourceUnzipThreads = sourceUnzipThreads;
    }

    protected void setSourceUnzipThreads(ResourceBundle config)
    {
        String key = "source.unzip.threads";
        int value = getValue(config, key, 1);
        configMap.put(key, String.valueOf(value));

        setSourceUnzipThreads(value);
    }

    public int getSourceParserThreads()
    {
        return sourceParserThreads;
    }

    protected void setSourceParserThreads(int sourceParserThreads)
    {
        this.sourceParserThreads = sourceParserThreads;
    }

    protected void setSourceParserThreads(ResourceBundle config)
    {
        String key = "source.parser.threads";
        int value = getValue(config, key, 1);
        configMap.put(key, String.valueOf(value));

        setSourceParserThreads(value);
    }

    public boolean isSourceRegisterExec()
    {
        return sourceRegisterExec;
    }

    protected void setSourceRegisterExec(boolean sourceRegisterExec)
    {
        this.sourceRegisterExec = sourceRegisterExec;
    }

    protected void setSourceRegisterExec(ResourceBundle config)
    {
        String key = "source.register.exec";
        String value = getValue(config, key, "0");
        configMap.put(key, value);

        setSourceRegisterExec(JoiaUtil.stringToBoolean(value));
    }
}
