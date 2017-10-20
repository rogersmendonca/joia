package br.com.auditoria.joia.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representa uma linha do log de execucao dos Jobs.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class JobLogLine
{
	public static final String DELIMITER = "\\|";
	 
	public static final String SUBMITTED_TO = "SUBMITTED TO";
	public static final String STARTED_AT = "STARTED AT";
	public static final String EXECUTING = "JOB STATE CHANGED TO EXECUTING";
	public static final String ENDED = "ENDED";
	public static final String ENDED_AT = "ENDED AT";
	public static final String ENDED_OK = "ENDED OK";
	public static final String ENDED_NOTOK = "ENDED NOTOK";

	public static final String CSV_LOG_LINE_HEADER = String.format(
			"%s;%s;%s;%s;%s;%s;%s", "Date", "Time", "Jobname", "Order", "SS",
			"MID", "Message");
	public static final String CSV_LOG_EXEC_HEADER = String.format(
			"%s;%s;%s;%s", "Jobname", "ENDED AT", "OSCOMPSTAT", "RUNCNT");

	public static final Pattern PATTERN_ENDED_AT = Pattern
			.compile("ENDED AT\\p{Space}(\\d+)\\p{Punct}\\p{Space}OSCOMPSTAT\\p{Space}(\\d+)\\p{Punct}\\p{Space}RUNCNT\\p{Space}(\\d+).*");
	public static final Pattern PATTERN_DATE_TIME = Pattern.compile("\\d{8}");

	private String originalLogLine;
	private String date;
	private String time;
	private String jobName;
	private String order;
	private String ss;
	private String mid;
	private String message;

	public JobLogLine()
	{
		this(null);
	}

	public JobLogLine(String logLine)
	{
		setOriginalLogLine(logLine);
	}

	private String trim(String str)
	{
		return (str != null) ? str.trim() : "";
	}

	public boolean isSubmittedTo()
	{
		return getMessage().toUpperCase().contains(SUBMITTED_TO);
	}

	public boolean isStartedAt()
	{
		return getMessage().toUpperCase().contains(STARTED_AT);
	}

	public boolean isExecuting()
	{
		return getMessage().toUpperCase().contains(EXECUTING);
	}

	public boolean isEndedAt()
	{
		return getMessage().toUpperCase().contains(ENDED_AT);
	}

	public boolean isEndedNotOk()
	{
		return getMessage().toUpperCase().contains(ENDED_NOTOK);
	}

	public boolean isEndedOk()
	{
		return getMessage().toUpperCase().contains(ENDED_OK);
	}

	public boolean isEnded()
	{
		return getMessage().toUpperCase().contains(ENDED);
	}

	public boolean isCheckable()
	{
		return isSubmittedTo() || isStartedAt() || isExecuting() || isEnded();
	}

	public String getCsvLine()
	{
		return String.format("%s;%s;%s;%s;%s;%s;%s", getDate(), getTime(),
				getJobName(), getOrder(), getSs(), getMid(), getMessage());
	}

	public String getCsvExec()
	{
		Matcher msgMatcher = PATTERN_ENDED_AT.matcher(getMessage());
		String endedAt;
		String oscompstat;
		String runcnt;
		if (msgMatcher.matches())
		{
			endedAt = msgMatcher.group(1);
			oscompstat = msgMatcher.group(2);
			runcnt = msgMatcher.group(3);
		}
		else
		{
			jobName = "";
			endedAt = "";
			oscompstat = "";
			runcnt = "";
		}

		return String.format("%s;%s;%s;%s", getJobName(), endedAt, oscompstat,
				runcnt);
	}

	public String getJobExecLogFile()
	{
		return getJobName() + ".csv";
	}

	public boolean startWithDate()
	{
		return PATTERN_DATE_TIME.matcher(getDateTime()).matches();
	}

	public String getOriginalLogLine()
	{
		return originalLogLine;
	}

	protected void setOriginalLogLine(String logLine)
	{
		this.originalLogLine = trim(logLine);

		String[] tokens = this.originalLogLine.split(DELIMITER);
		if (tokens.length >= 8)
		{
			setDate(tokens[1]);
			setTime(tokens[2]);
			setJobName(tokens[3]);
			setOrder(tokens[4]);
			setSs(tokens[5]);
			setMid(tokens[6]);
			setMessage(tokens[7]);
		}
		else
		{
			setDate(null);
			setTime(null);
			setJobName(null);
			setOrder(null);
			setSs(null);
			setMid(null);
			setMessage(null);
		}
	}

	public String getDate()
	{
		return date;
	}

	protected void setDate(String date)
	{
		this.date = trim(date);
	}

	public String getTime()
	{
		return time;
	}

	protected void setTime(String time)
	{
		this.time = trim(time);
	}

	public String getDateTime()
	{
		return date + time;
	}

	public String getJobName()
	{
		return jobName;
	}

	protected void setJobName(String jobName)
	{
		this.jobName = trim(jobName);
	}

	public String getOrder()
	{
		return order;
	}

	protected void setOrder(String order)
	{
		this.order = trim(order);
	}

	public String getSs()
	{
		return ss;
	}

	protected void setSs(String ss)
	{
		this.ss = trim(ss);
	}

	public String getMid()
	{
		return mid;
	}

	protected void setMid(String mid)
	{
		this.mid = trim(mid);
	}

	public String getMessage()
	{
		return message;
	}

	protected void setMessage(String message)
	{
		this.message = trim(message);
	}
}