package br.com.auditoria.joia.mapreduce.count;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import br.com.auditoria.joia.entity.JobLogLine;

/**
 * Map para mapear:<br/>
 * (1) Linhas do log interpretadas (key: #PARSED_LOG_LINES);<br>
 * (2) Jobs executados com sucesso (key: < Jobname > - ENDED OK);<br>
 * (3) Abends (key: < Jobname > - ENDED NOTOK).
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaMapCount extends Mapper<LongWritable, Text, Text, IntWritable>
{
    public final static IntWritable ZERO = new IntWritable(0);
    public final static IntWritable ONE = new IntWritable(1);

    public final static Text KEY_PARSED_LOG_LINES = new Text(
            "#PARSED_LOG_LINES");

    public static Text KEY_JOB_ENDED_OK(String jobName)
    {
        return new Text(jobName + " - " + JobLogLine.ENDED_OK);
    }

    public static Text KEY_JOB_ENDED_NOTOK(String jobName)
    {
        return new Text(jobName + " - " + JobLogLine.ENDED_NOTOK);
    }

    public JoiaMapCount()
    {
    }

    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
    {
        String line = value.toString();
        JobLogLine jobLogLine = new JobLogLine(line);

        if (jobLogLine.isEndedOk())
        {
            context.write(KEY_JOB_ENDED_OK(jobLogLine.getJobName()), ONE);
        }
        else if (jobLogLine.isEndedNotOk())
        {
            context.write(KEY_JOB_ENDED_NOTOK(jobLogLine.getJobName()), ONE);
        }

        context.write(KEY_PARSED_LOG_LINES, ONE);
    }
}