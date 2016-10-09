package br.com.auditoria.joia.mapreduce.notok;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reduce para registar o mapeamento resultante do Map na saida.
 * <p>
 * Exemplo de registro < key , value > no mapeamento de saida: <br />
 * < 1201|177566 , 1201;1648;JOB_ACEROLA;313nu;TR;5134;ENDED NOTOK. NUMBER OF
 * FAILURES SET TO num >
 * </p>
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JoiaReduceNotOk extends Reducer<Text, Text, Text, Text>
{
    public JoiaReduceNotOk()
    {
    }

    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException
    {
        for (Text value : values)
        {
            context.write(key, value);
        }
    }
}