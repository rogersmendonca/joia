package br.com.auditoria.joia.mapreduce.all;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reduce que acumula as reducoes de JobReduceCount e JobReduceNotOk.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaReduceAll extends Reducer<Text, Text, Text, Text>
{
    private Text result;

    public JoiaReduceAll()
    {
        result = new Text();
    }

    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException
    {
        int sum = 0;
        StringBuilder strBuilder = new StringBuilder();
        for (Text value : values)
        {
            if (value.toString().matches("^[0-9]*$"))
            {
                sum += Integer.parseInt(value.toString());
            }
            else
            {
                strBuilder.append(value.toString()).append(" ");
            }
        }

        result.set(strBuilder.length() > 0 ? strBuilder.toString().trim()
                : String.valueOf(sum));
        context.write(key, result);
    }
}