
// Methoden aus UEB02

public final class DateUtil
{
	public static final String[] MONTHS = new String[] { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli",  "August", "September", "Oktober", "November", "Dezember" };
	public static final String[] DAY_SHORTHANDS = new String[] { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };

	public static boolean isLeapYear(short year)
	{
		return ((year & 3)==0) && ((year % 100)!=0) || ((year % 400)==0);
	}

	public static byte daysOfMonth(byte month, short year)
	{
		/*
		 * Wir verwenden ein switch-Statement und lassen Werte durchfallen.
		 *
		 * Durchfallen bedeutet, dass man leere case-Statements (oder zumindest case-Statements ohne
		 * break-Anweisung) verwendet. Dies resultiert darin, dass der Programmfluss "durch" das
		 * Statement durchfällt und in den nächsten Fall fließt.
		 */
		switch (month)
		{
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			return 31;

		case 1:
			return isLeapYear(year) ? (byte)29 : (byte)28;

		case 3:
		case 5:
		case 8:
		case 10:
			return 30;
		}

		throw new IllegalArgumentException("Illegal month: " + month);
	}

	public static byte firstDayOfYear(short year)
	{
		year--;

		return (byte)((5*(year & 3) + 4*(year % 100) + 6*(year % 400)) % 7);
	}

	public static byte firstDayOfMonth(byte month, short year)
	{
		// Wir starten mit dem ersten Tag des Jahres.
		short firstDay = firstDayOfYear(year);

		// Für jeden bereits vergangenen Monat addieren wir dessen Tage
		for (byte currentMonth = 0; currentMonth < month; currentMonth++)
			firstDay += daysOfMonth(currentMonth, year);

		// Durch das Modulo begrenzen wir das Ergebnis auf die Tage einer Woche
		return (byte)(firstDay % 7);
	}

	public static boolean checkDate(byte day, byte month, short year)
	{
		// Das Jahr muss zwischen 1900 und 2099 liegen
		if ((year < 1900) || (year > 2099))
			return false;

		// Der Monat muss zwischen 0 und 11 liegen
		if ((month < 0) || (month > 11))
			return false;

		// Der Tag muss zwischen 0 und daysOfMonth(...)-1 liegen
		if (day < 0 || day >= daysOfMonth(month, year))
			return false;

		// Alles ok!
		return true;
	}
}