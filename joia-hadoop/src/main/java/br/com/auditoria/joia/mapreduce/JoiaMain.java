package br.com.auditoria.joia.mapreduce;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.GenericOptionsParser;

import br.com.auditoria.joia.entity.JobLogLine;
import br.com.auditoria.joia.mapreduce.all.JoiaMapAll;
import br.com.auditoria.joia.mapreduce.all.JoiaReduceAll;
import br.com.auditoria.joia.mapreduce.checksample.JoiaMapCheckSample;
import br.com.auditoria.joia.mapreduce.count.JoiaMapCount;
import br.com.auditoria.joia.mapreduce.notok.JoiaMapNotOk;

/**
 * Classe principal do job Hadoop (main).
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaMain
{
	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if ((otherArgs.length < 3)
				|| ((!otherArgs[0].equalsIgnoreCase("all"))
						&& (!otherArgs[0].equalsIgnoreCase("count")) && (!otherArgs[0]
							.equalsIgnoreCase("notok"))))
		{
			System.err
					.println("Comando: yarn jar joia-hadoop-1.0-job.jar all|count|notok|ended <in> [<in>...] <out>");
			System.exit(2);
		}

		Job job = Job.getInstance(conf, "joia");
		job.setJarByClass(JoiaMain.class);
		if (otherArgs[0].equalsIgnoreCase("all"))
		{
			job.setMapperClass(JoiaMapAll.class);
			job.setCombinerClass(JoiaReduceAll.class);
			job.setReducerClass(JoiaReduceAll.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
		}
		else if (otherArgs[0].equalsIgnoreCase("count"))
		{
			job.setMapperClass(JoiaMapCount.class);
			job.setCombinerClass(IntSumReducer.class);
			job.setReducerClass(IntSumReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

		}
		else if (otherArgs[0].equalsIgnoreCase("notok"))
		{
			job.setMapperClass(JoiaMapNotOk.class);
			job.setCombinerClass(Reducer.class);
			job.setReducerClass(Reducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
		}
		else if (otherArgs[0].equalsIgnoreCase("ended"))
		{
			job.setMapperClass(JoiaMapCheckSample.class);
			job.setCombinerClass(Reducer.class);
			job.setReducerClass(Reducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
		}

		for (int i = 1; i < otherArgs.length - 1; ++i)
		{
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		FileOutputFormat.setOutputPath(job, new Path(
				otherArgs[otherArgs.length - 1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public final static Text KEY_PARSED_LOG_LINES = new Text(
			"#PARSED_LOG_LINES");

	public static Text KEY_JOB_ENDED_OK(String jobName)
	{
		return new Text(jobName + ";" + JobLogLine.ENDED_OK);
	}

	public static Text KEY_JOB_ENDED_NOTOK(String jobName)
	{
		return new Text(jobName + ";" + JobLogLine.ENDED_NOTOK);
	}

	public static Text KEY_JOB_LINE(String jobName, long lineNumber)
	{
		return new Text(String.format("%s;%d", jobName, lineNumber));
	}

	public static List<String> getJobSampleList()
	{
		List<String> jobList = new ArrayList<String>();
		Scanner reader = null;
		File fIn = new File(new File("."), "jobSample.txt");
		try
		{
			reader = new Scanner(fIn);
			while (reader.hasNext())
			{
				String str = reader.nextLine();
				if (str != null)
				{
					jobList.add(str.trim());
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.printf("Arquivo %s nao encontrado\n",
					fIn.getAbsolutePath());
			System.exit(3);
		}

		return jobList;
	}
}