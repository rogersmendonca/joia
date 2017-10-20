package br.com.auditoria.joia.mapreduce.checksample;

import static br.com.auditoria.joia.mapreduce.JoiaMain.KEY_JOB_LINE;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import br.com.auditoria.joia.entity.JobLogLine;
import br.com.auditoria.joia.mapreduce.JoiaMain;

/**
 * Map para mapear os registros de log referentes a inicializacao e finalizacao
 * de uma amostra de jobs.
 * <p>
 * key: < Jobname >|< Log Line Number > (exemplo: JOB_ACEROLA|177566).
 * </p>
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaMapCheckSample extends Mapper<LongWritable, Text, Text, Text>
{
	private static List<String> jobSampleList = JoiaMain.getJobSampleList();

	public JoiaMapCheckSample()
	{
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException
	{
		String line = value.toString();
		JobLogLine jobLogLine = new JobLogLine(line);

		if (jobSampleList.contains(jobLogLine.getJobName())
				&& jobLogLine.isCheckable())
		{
			Text outKey = KEY_JOB_LINE(jobLogLine.getJobName(), key.get());
			Text outValue = new Text(jobLogLine.getCsvLine());
			context.write(outKey, outValue);
		}
	}
}