// Benötigte Collections importieren
import java.util.ArrayList;
import java.util.List;

public class Appointments
{
	private Link<Appointment>[][] buckets;

	/*
	 * Diese Annotation einfach ignorieren - sie entfernt eine Compilerwarnung, die durch den
	 * Typecast des Arrays entsteht. Leider ist dieser notwendig (siehe Aufgabenblatt). Java...
	 */
	@SuppressWarnings("unchecked")
	public Appointments()
	{
		buckets = (Link<Appointment>[][])new Link[200][366];
	}

	public boolean add(Appointment ap)
	{
		// Die Termin-Referenz app darf nicht null sein!
		// Dies kann im normalen Betrieb auch niemals vorkommen, daher wird mit assert() geprüft.
		assert(ap!=null);

		/*
		 * Wir implementieren den Test auf ein korrektes Datum in der Appointment-Klasse. Diese hat
		 * internen Zugriff auf alle Attribute und die Korrektheit des Datums ist ein Attribut
		 * dessen. Daher ist diese Klasse ein guter Ort für diesen Test.
		 *
		 * Durch die Auslagerung in eine eigene Methode können wir den Code zudem einfach
		 * wiederverwenden und reduzieren die Chance auf Fehler.
		 */
		if (!ap.isValid())
			return false;

		final int yearBucket = calculateYearBucket(ap);
		final int dayBucket = calculateDayBucket(ap);

		buckets[yearBucket][dayBucket] = new Link<Appointment>(ap, buckets[yearBucket][dayBucket]);

		return true;
	}

	public boolean remove(Appointment victim)
	{
		// Die Termin-Referent app darf nicht null sein!
		// Dies kann im normalen Betrieb auch niemals vorkommen, daher wird mit assert() geprüft.
		assert(victim != null);

		if (!victim.isValid())
			return false;

		final int yearBucket = calculateYearBucket(victim);
		final int dayBucket = calculateDayBucket(victim);

		Link<Appointment> zeiger = buckets[yearBucket][dayBucket];

		/*
		 * Diese Variable dient dazu, uns das Vorgängerelement zu merken. Wir werden es später
		 * brauchen, wenn wir ein Element aus der Mitte der Liste entfernen. Zudem können wir es
		 * nutzen, um zu erkennen, ob das zu entfernende Element am Anfang der Liste steht - dann
		 * ist diese Variable noch mit dem Wert null belegt. Dies ist für die Sonderbehandlung des
		 * ersten Element notwendig, da es dann keinen Vorgänger gibt.
		 */
		Link<Appointment> vorgaenger = null;

		while (zeiger != null)
		{
			/*
			 * Prüfe, ob list.appointment unser gesuchter Termin ist. Um den Code zum Vergleichen
			 * wiederverwenden zu können, haben wir ihn in eine eigene Methode ausgelagert.
			 *
			 * Normalerweise würde man diesen Test in der equals-Methode von Appointment
			 * implementieren. Wir haben hier darauf verzichtet, weil equals einige weitere
			 * Bedingungen stellt, deren Umsetzung für diese Musterlösung zu komplex werden würde.
			 */
			if (zeiger.appointment == victim)
			{
				// Sonderbehandlung für das erste Listenelement.
				if (vorgaenger == null)
				{
					// Das erste Element wird aus der Liste entfernt. Das geht besonders einfach.
					buckets[yearBucket][dayBucket] = zeiger.next;
				}
				else
				{
					// Wir entfernen nun das Element aus der Liste durch Umbiegen des Vorgänger-Zeigers.
					vorgaenger.next = zeiger.next;
				}

				// Fertig!
				return true;
			}

			// Wir merken uns das aktuelle Element nun als vorheriges Element.
			vorgaenger = zeiger;

			// Setze zeiger auf das nächste Element (ggf. auch null).
			zeiger = zeiger.next;
		}

		return false;
	}

	public void removeAll(byte day, byte month, short year)
	{
		// Eingabewerte prüfen!
		if (!DateUtil.checkDate(day, month, year))
			throw new IllegalArgumentException("Ungültiges Datum!");

		final int yearBucket = calculateYearBucket(year);
		final int dayBucket = calculateDayBucket(day, month, year);

		// Der Bucket wird vollständig gelöscht, alle Elemente sind sofort entfernt.
		buckets[yearBucket][dayBucket] = null;
	}

	public int countAppointments(byte day, byte month, short year)
	{
		// Eingabewerte prüfen!
		if (!DateUtil.checkDate(day, month, year))
			throw new IllegalArgumentException("Ungültiges Datum!");

		final int yearBucket = calculateYearBucket(year);
		final int dayBucket = calculateDayBucket(day, month, year);

		int count = 0;

		// Der Bucket wird vollständig iteriert, um die Elemente zu zählen
		Link<Appointment> zeiger = buckets[yearBucket][dayBucket];

		while (zeiger != null)
		{
			count++;
			zeiger = zeiger.next;
		}

		return count;
	}

	public List<Appointment> getAppointments(byte day, byte month, short year)
	{
		// Wir erstellen unsere ArrayList direkt mit der passenden Größe.
		// Die Eingabewerte werden bereits in countAppointsments() geprüft, eine Exception
		// würde gegebenenfalls an unseren eigenen Aufrufer durchgereicht - wie praktisch!
		List<Appointment> list = new ArrayList<>(countAppointments(day, month, year));

		final int yearBucket = calculateYearBucket(year);
		final int dayBucket = calculateDayBucket(day, month, year);

		Link<Appointment> zeiger = buckets[yearBucket][dayBucket];
		while (zeiger != null)
		{
			list.add(zeiger.appointment);
			zeiger = zeiger.next;
		}

		return list;
	}

	public void print(byte day, byte month, short year)
	{
		// Eingabewerte prüfen!
		if (!DateUtil.checkDate(day, month, year))
			throw new IllegalArgumentException("Ungültiges Datum!");

		final int yearBucket = calculateYearBucket(year);
		final int dayBucket = calculateDayBucket(day, month, year);

		int count = 0;

		// Der Bucket wird vollständig iteriert, um die Elemente auszugeben
		Link<Appointment> zeiger = buckets[yearBucket][dayBucket];

		while (zeiger != null)
		{
			// Termin ausgeben
			System.out.println("Termin " + count + ": " + zeiger.appointment);

			count++;
			zeiger = zeiger.next;
		}
	}

	public void printAll()
	{
		// Alle Buckets iterieren
		for (short year = 0; year < buckets.length; year++)
			for (int day = 0; day < buckets[year].length; day++)
			{
				// Der Bucket wird vollständig iteriert, um die Elemente auszugeben
				Link<Appointment> zeiger = buckets[year][day];

				while (zeiger != null)
				{
					// Termin ausgeben
					System.out.println(zeiger.appointment);

					zeiger = zeiger.next;
				}
			}
	}

	public boolean check()
	{
		// Alle Buckets iterieren
		for (int year = 0; year < buckets.length; year++)
			for (int day = 0; day < buckets[year].length; day++)
			{
				// Der Bucket wird vollständig iteriert, um die Elemente auszugeben
				Link<Appointment> zeiger = buckets[year][day];

				while (zeiger != null)
				{
					/*
					 * Zunächst einmal prüfen wir, ob eine etwaige Änderung das Datum komplett
					 * ungültig gemacht hat.
					 */
					if (!zeiger.appointment.isValid())
						return false;

					/*
					 * Anschließend berechnen wir die korrekte Position des Termins neu.
					 */
					final int yearBucket = calculateYearBucket(zeiger.appointment);
					final int dayBucket = calculateDayBucket(zeiger.appointment);

					// Wenn der eigentliche Bucket sich vom aktuellen Bucket unterscheidet,
					// ist etwas nicht in Ordnung.
					if ((yearBucket != year) || (dayBucket != day))
						return false;

					// Weiter
					zeiger = zeiger.next;
				}
			}

		// Es wurden keine Probleme gefunden, also kann true zurückgegeben werden
		return true;
	}


	/*
	 * Die folgenden Methoden sind als static deklariert.
	 * Wir greifen in diesen Methoden auf keine Attribute aus Appointments zu. Deshalb
	 * können wir uns das PUSHen des this-Zeigers auf den Stack sparen, so dass die
	 * Methoden etwas effizienter aufgerufen werden können.
	 */

	private static int calculateYearBucket(int year)
	{
		// Subtraktion von 1900, um auf Index zwischen 0 und 199 zu kommen.
		return year - 1900;
	}

	private static int calculateYearBucket(Appointment ap)
	{
		assert(ap != null);

		// Wrapper-Methode zum einfacheren Aufruf.
		return calculateYearBucket(ap.getYear());
	}

	private static int calculateDayBucket(byte day, byte month, short year)
	{
		// Eingabewerte prüfen!
		if (!DateUtil.checkDate(day, month, year))
			throw new IllegalArgumentException("Ungültiges Datum!");

		/*
		 * Um den Tag im Jahr zu erhalten, addieren wir auf dayBucket die Tage aller bereits
		 * vergangenen Monate des Jahres. Anschließend müssen wir nur nochday aufaddieren und
		 * erhalten den korrekten Tag.
		 *
		 * Wichtig: day und month MÜSSEN dafür 0-basiert sein, sprich der erste Tag des
		 * Jahres, der 1.1., wäre korrekterweise der 0. Tag des 0. Monats! Sie wurden in der
		 * Vorlesung deutlich darauf hingewiesen.
		 */
		int dayBucket = 0;

		for (byte a = 0; a < month; a++)
			dayBucket += DateUtil.daysOfMonth(a, year);

		return dayBucket + day;
	}

	private static int calculateDayBucket(Appointment ap)
	{
		assert(ap != null);

		// Wrapper-Methode zum einfacheren Aufruf.
		return calculateDayBucket(ap.getDay(), ap.getMonth(), ap.getYear());
	}
}