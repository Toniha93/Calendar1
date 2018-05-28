import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditAppointmentPanel extends JPanel
{
	// Appointment
	private Appointment appointment;

	// UI-Elemente
	private JTextField txtSubject;
	private JTextField txtDay;
	private JTextField txtMonth;
	private JTextField txtYear;
	private JTextField txtStart;
	private JTextField txtEnd;
	private JCheckBox cbAllDay;

	public EditAppointmentPanel(Appointment appointment)
	{
		// Parameter speichern
		assert(appointment != null);
		this.appointment = appointment;

		// Kontrollelemente erzeugen und einfügen
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);

		add(new JLabel("Betreff:"), c);

		c.gridx = 1;
		add(txtSubject=new JTextField(15), c);
		txtSubject.setText(appointment.getSubject());

		c.gridx = 0;
		c.gridy = 1;
		add(new JLabel("Tag:"), c);

		c.gridx = 1;
		add(txtDay=new JTextField(), c);
		txtDay.setText(String.valueOf(appointment.getDay() + 1));

		c.gridx = 0;
		c.gridy = 2;
		add(new JLabel("Monat:"), c);

		c.gridx = 1;
		add(txtMonth=new JTextField(), c);
		txtMonth.setText(String.valueOf(appointment.getMonth() + 1));

		c.gridx = 0;
		c.gridy = 3;
		add(new JLabel("Jahr:"), c);

		c.gridx = 1;
		add(txtYear=new JTextField(), c);
		txtYear.setText(String.valueOf(appointment.getYear()));

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;

		add(cbAllDay=new JCheckBox("Ganztägig"), c);
		cbAllDay.setSelected(appointment.isAllDay());

		cbAllDay.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				updateViews();
			}
		});

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		add(new JLabel("Beginn:"), c);

		c.gridx = 1;
		add(txtStart=new JTextField(), c);
		txtStart.setText(timeToString(appointment.getStart()));

		c.gridx = 0;
		c.gridy = 6;
		add(new JLabel("Ende:"), c);

		c.gridx = 1;
		add(txtEnd=new JTextField(), c);
		txtEnd.setText(timeToString(appointment.getStart() + appointment.getLength()));

		// Kontrollelemente aktualisieren
		updateViews();
	}

	private void updateViews()
	{
		final boolean selected = cbAllDay.isSelected();
		txtStart.setEnabled(!selected);
		txtEnd.setEnabled(!selected);
	}

	public Appointment updateAppointment()
	{
		appointment.setSubject(txtSubject.getText());
		appointment.setDate(getDay(), getMonth(), getYear());

		if (cbAllDay.isSelected())
		{
			appointment.setAllDay(true);
		}
		else
		{
			appointment.setAllDay(false);
			appointment.setStart((short)stringToTime(txtStart.getText()));
			appointment.setLength((short)(stringToTime(txtEnd.getText()) - appointment.getStart()));
		}

		return appointment;
	}

	private byte getDay()
	{
		try
		{
			return (byte)(Byte.parseByte(txtDay.getText()) - 1);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}

	private byte getMonth()
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

	private short getYear()
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

	private static int stringToTime(String time)
	{
		final String[] token = time.split(":");

		// Haben wir zu wenige oder zu viele Token?
		if ((token.length < 2) || (token.length > 3))
			return 0;

		final int hours =  Integer.parseInt(token[0]);
		final int minutes = Integer.parseInt(token[1]);

		if ((hours < 0) || (hours > 23) || (minutes < 0) || (minutes > 59))
			throw new IllegalArgumentException("Eine eingegebene Zeit ist ungültig!");

		return hours * 60 + minutes;
	}

	private static String timeToString(int minutes)
	{
		return String.format("%02d:%02d", minutes / 60, minutes % 60);
	}
}