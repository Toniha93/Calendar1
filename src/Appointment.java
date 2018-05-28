import java.io.RandomAccessFile;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Appointment implements Serializable
{
	private String subject;
	private byte day;
	private byte month;
	private short year;
	private boolean allDay;
	private short start;
	private short length;
	@SuppressWarnings("unused")
	private transient RandomAccessFile raf;

	public Appointment(Appointment ap, RandomAccessFile raf)
	{
		if (ap!=null)
		{
			// Einen Klon eines anderen Appointment-Objekts erstellen
			this.subject = ap.subject;
			this.day = ap.day;
			this.month = ap.month;
			this.year = ap.year;
			this.allDay = ap.allDay;
			this.start = ap.start;
			this.length = ap.length;
			this.raf = raf;

		}
		else
		{
			// 01.01.1900
			this.year = 1900;
			this.raf = raf;
		}
	}

	public String toString()
	{
		return "Appointment [subject=" + subject + ", day=" + day + ", month=" + month + ", year=" + year + ", allDay="
				+ allDay + ", start=" + start + ", length=" + length + "]";
	}

	public boolean isValid()
	{
		return DateUtil.checkDate(day, month, year);
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public void setDate(byte day, byte month, short year)
	{
		if (!DateUtil.checkDate(day, month, year))
			throw new IllegalArgumentException("Ungültiges Datum!");

		this.day = day;
		this.month = month;
		this.year = year;
	}

	public byte getDay()
	{
		return day;
	}

	public byte getMonth()
	{
		return month;
	}

	public short getYear()
	{
		return year;
	}

	public boolean isAllDay()
	{
		return allDay;
	}

	public void setAllDay(boolean allDay)
	{
		this.allDay = allDay;

		// Konsistenz herstellen
		if (allDay)
			this.start = this.length = 0;
	}

	public short getStart()
	{
		return start;
	}

	public void setStart(short start)
	{
		if (allDay)
			throw new IllegalStateException("Startzeit ist bei ganztägigen Terminen nicht setzbar!");

		this.start = start;
	}

	public short getLength()
	{
		return length;
	}

	public void setLength(short length)
	{
		if (allDay)
			throw new IllegalStateException("Länge ist bei ganztägigen Terminen nicht setzbar!");

		this.length = length;
	}
}
