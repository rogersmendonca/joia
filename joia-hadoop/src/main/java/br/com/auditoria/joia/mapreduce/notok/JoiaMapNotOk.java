package br.com.auditoria.joia.mapreduce.notok;

import static br.com.auditoria.joia.mapreduce.JoiaMain.KEY_JOB_LINE;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import br.com.auditoria.joia.entity.JobLogLine;

/**
 * Map para mapear os registros do log referentes aos abends.
 * <p>
 * key: < Jobname >|< Log Line Number > (exemplo: JOB_ACEROLA|177566).
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
			Text outKey = KEY_JOB_LINE(jobLogLine.getJobName(), key.get());
			Text outValue = new Text(jobLogLine.getCsvLine());
			context.write(outKey, outValue);
		}
	}
}