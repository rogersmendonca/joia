package br.com.auditoria.joia.entity;

/**
 * Representa a execucao da interpretacao (inicio e fim) dos logs de um
 * determinado mes. Utilizado na classe <code>ThreadParser</code>.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class MonthExec
{
	private String month;
	private long start;
	private long end;
	private long total;
	private long exec;
	private long notOk;

	public MonthExec(String month, long start)
	{
		reset(month, start);
	}

	public MonthExec(long start)
	{
		this("", start);
	}

	public void reset(String month, long start)
	{
		this.month = month;
		this.start = start;
		this.end = 0;
		this.total = 0;
		this.exec = 0;
		this.notOk = 0;
	}

	public void incTotal()
	{
		total++;
	}

	public void incExec()
	{
		exec++;
	}

	public void incNotOk()
	{
		notOk++;
	}

	public boolean isFirst()
	{
		return this.month.length() == 0;
	}

	public boolean equalsMonth(String month)
	{
		return this.month.equalsIgnoreCase(month);
	}

	public String getMonth()
	{
		return month;
	}

	public void setMonth(String month)
	{
		this.month = month;
	}

	public long getStart()
	{
		return start;
	}

	public void setStart(long start)
	{
		this.start = start;
	}

	public long getEnd()
	{
		return end;
	}

	public void setEnd(long end)
	{
		this.end = end;
	}

	public long getTotal()
	{
		return total;
	}

	public void setTotal(long total)
	{
		this.total = total;
	}

	public long getExec()
	{
		return exec;
	}

	public void setExec(long exec)
	{
		this.exec = exec;
	}

	public long getNotOk()
	{
		return notOk;
	}

	public void setNotOk(long notOk)
	{
		this.notOk = notOk;
	}
}