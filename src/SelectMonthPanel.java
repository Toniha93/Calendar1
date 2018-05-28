import java.awt.*;
import javax.swing.*;

public class SelectMonthPanel extends JPanel
{
	private JTextField txtMonth;
	private JTextField txtYear;

	public SelectMonthPanel(byte month, short year)
	{
		// Layout
		super(new GridLayout(2, 2));

		// Kontrollelemente erzeugen und einfügen
		add(new JLabel("Monat:"));
		add(txtMonth=new JTextField(String.valueOf(month + 1)));
		add(new JLabel("Jahr:"));
		add(txtYear=new JTextField(String.valueOf(year)));
	}

	public void check() throws IllegalStateException
	{
		// Monat prüfen
		final byte month = getMonth();
		if ((month < 0) || (month > 11))
			throw new IllegalStateException("Kein gültiger Monat:\n" + txtMonth.getText());

		// Jahr prüfen
		final short year = getYear();
		if (year == -1)
			throw new IllegalStateException("Kein gültiges Jahr:\n" + txtYear.getText());

		if (year < 1900)
			throw new IllegalStateException("Der Kalender kann nur Jahre ab 1900 speichern.");

		if (year > 2099)
			throw new IllegalStateException("Der Kalender kann nur Jahre bis 2099 speichern.");
	}

	public byte getMonth()
	{
		try
		{
			return (byte)(Byte.parseByte(txtMonth.getText()) - 1);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}

	public short getYear()
	{
		try
		{
			return Short.parseShort(txtYear.getText());
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}
}