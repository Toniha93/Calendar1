import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.time.*;
import javax.swing.*;


public class Calendar
{
	public static void main(String[] args) throws FileNotFoundException
	{
		// Aktueller Monat
		final LocalDate now = LocalDate.now();
		final byte month = (byte)(now.getMonthValue()-1);
		final short year = (short)now.getYear();

		// Termin-Datenbank erzeugen
		final Appointments appointments = new Appointments();

		// Beispiel-Termin eintragen
		Appointment app1 = new Appointment(null, null);
		app1.setDate((byte)20, (byte)11, (short)2017);
		app1.setSubject("Abgabetermin Bonusaufgabe");
		app1.setStart((short)1200);
		app1.setLength((short)1);
		
			
		//Deserialisierung
		File sfile2 = new File("termin.ser");
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		Appointment app4 = null;
		
		try {
			fis = new FileInputStream(sfile2);
			ois = new ObjectInputStream(fis);
			app4 = (Appointment)ois.readObject();
		}catch(IOException e) {
			System.out.println("Fehler bei der Deserialisierung");
		}catch(ClassNotFoundException e) {
			//TODO
		}finally {
			try {
				if(ois!=null)ois.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		appointments.add(app1);
		appointments.add(app4);

		// Weitere Beispiel-Termine eintragen
		for (int a=0; a<5; a++)
		{
			Appointment app2 = new Appointment(null, null);
			app2.setDate((byte)19, (byte)11, (short)2017);
			app2.setSubject("PK1-Praktikum");
			app2.setStart((short)(510+a*105));
			app2.setLength((short)95);

			appointments.add(app2);
		}

		// "Nimbus"-Skin laden, damit wir keinen Augenkrebs bekommen...
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		// Hauptfenster CalendarWindow erzeugen und anzeigen
		new CalendarWindow(appointments, month, year).setVisible(true);
	}
}