package br.com.auditoria.joia.mapreduce.count;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reduce para sumarizar:<br/>
 * (1) Quantidade de linhas do log interpretadas (key: #PARSED_LOG_LINES);<br>
 * (2) Quantidade de Jobs executados com sucesso (key: < Jobname > - ENDED OK);<br>
 * (3) Quantidade de Abends (key: < Jobname > - ENDED NOTOK).
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaReduceCount extends
        Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable result;

    public JoiaReduceCount()
    {
        result = new IntWritable();
    }

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException
    {
        int sum = 0;
        for (IntWritable val : values)
        {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}