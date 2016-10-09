package br.com.auditoria.joia.mapreduce.notok;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import br.com.auditoria.joia.entity.JobLogLine;

/**
 * Map para mapear os registros do log referentes aos abends.
 * <p>
 * key: < Date(formato: mmdd) >|< Log Line Number > (exemplo: 1201|177566).
 * </p>
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaMapNotOk extends Mapper<LongWritable, Text, Text, Text>
{
    public JoiaMapNotOk()
    {
    }

    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
    {
        String line = value.toString();
        JobLogLine jobLogLine = new JobLogLine(line);

        if (jobLogLine.isEndedNotOk())
        {
            Text outKey = new Text(String.format("%s|%d", jobLogLine.getDate(),
                    key.get()));

            Text outValue = new Text(jobLogLine.getCsvLine());

            context.write(outKey, outValue);
        }
    }
}