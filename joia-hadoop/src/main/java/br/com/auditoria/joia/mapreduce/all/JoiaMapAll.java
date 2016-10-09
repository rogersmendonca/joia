package br.com.auditoria.joia.mapreduce.all;

import static br.com.auditoria.joia.mapreduce.count.JoiaMapCount.KEY_JOB_ENDED_NOTOK;
import static br.com.auditoria.joia.mapreduce.count.JoiaMapCount.KEY_JOB_ENDED_OK;
import static br.com.auditoria.joia.mapreduce.count.JoiaMapCount.KEY_PARSED_LOG_LINES;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import br.com.auditoria.joia.entity.JobLogLine;

/**
 * Map que acumula os mapeamentos de JobMapCount e JobMapNotOk.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaMapAll extends Mapper<LongWritable, Text, Text, Text>
{
    public final static Text ZERO_TEXT = new Text("0");
    public final static Text ONE_TEXT = new Text("1");

    public JoiaMapAll()
    {
    }

    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
    {
        String line = value.toString();
        JobLogLine jobLogLine = new JobLogLine(line);

        if (jobLogLine.isEndedOk())
        {
            context.write(KEY_JOB_ENDED_OK(jobLogLine.getJobName()), ONE_TEXT);
        }
        else if (jobLogLine.isEndedNotOk())
        {
            context.write(KEY_JOB_ENDED_NOTOK(jobLogLine.getJobName()),
                    ONE_TEXT);

            Text outKey = new Text(String.format("%s|%d", jobLogLine.getDate(),
                    key.get()));

            Text outValue = new Text(jobLogLine.getCsvLine());

            context.write(outKey, outValue);
        }

        context.write(KEY_PARSED_LOG_LINES, ONE_TEXT);
    }
}