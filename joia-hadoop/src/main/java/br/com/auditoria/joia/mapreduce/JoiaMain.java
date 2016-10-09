package br.com.auditoria.joia.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import br.com.auditoria.joia.mapreduce.all.JoiaMapAll;
import br.com.auditoria.joia.mapreduce.all.JoiaReduceAll;
import br.com.auditoria.joia.mapreduce.count.JoiaMapCount;
import br.com.auditoria.joia.mapreduce.count.JoiaReduceCount;
import br.com.auditoria.joia.mapreduce.notok.JoiaMapNotOk;
import br.com.auditoria.joia.mapreduce.notok.JoiaReduceNotOk;

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
                    .println("Comando: yarn jar joia-hadoop-1.0-job.jar all|count|notok <in> [<in>...] <out>");
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
            job.setCombinerClass(JoiaReduceCount.class);
            job.setReducerClass(JoiaReduceCount.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

        }
        else if (otherArgs[0].equalsIgnoreCase("notok"))
        {
            job.setMapperClass(JoiaMapNotOk.class);
            job.setCombinerClass(JoiaReduceNotOk.class);
            job.setReducerClass(JoiaReduceNotOk.class);
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
}