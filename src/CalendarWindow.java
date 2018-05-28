import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class CalendarWindow extends JFrame
{
	// Angezeigter Monat
	protected byte month;
	protected short year;

	// Termin-Datenbank
	protected final Appointments appointments;

	// UI-Elemente
	private JButton btnPreviousMonth;
	private JButton btnNextMonth;
	private JLabel lblCurrentMonth;
	private JTable tblAppointments;

	public CalendarWindow(Appointments appointments, byte month, short year)
	{
		// Titel
		super("Java-Kalender");

		// Parameter speichern
		assert(appointments!=null);

		this.appointments = appointments;
		this.month = month;
		this.year = year;

		// Standard-Operation zum Schließen des Fensters
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Kontrollelemente erzeugen und einfügen
		add(createControls(), BorderLayout.NORTH);
		add(createAppointmentsTable(), BorderLayout.CENTER);

		// Kontrollelemente aktualisieren
		updateViews();

		// Position und Größe aller Kontrollelemente neu berechnen
		pack();

		// Das Fenster soll sich in der Mitte des Bildschirms öffnen
		setLocationRelativeTo(null);
	}

	protected void updateViews()
	{
		// Tabelle neu zeichnen lassen
		((AbstractTableModel)tblAppointments.getModel()).fireTableDataChanged();

		// Buttons
		btnPreviousMonth.setEnabled((year>1900) || (month>0));
		btnNextMonth.setEnabled((year<2099) || (month<11));

		// Label-Text
		lblCurrentMonth.setText(DateUtil.MONTHS[month] + " " + year);
	}

	// Kontrollelemente am oberen Fensterrand erzeugen
	private JComponent createControls()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// Button zum vorherigen Monat
		(btnPreviousMonth=new JButton("<")).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (--month < 0)
				{
					month = 11;
					year--;
				}

				updateViews();
			}
		});

		c.gridx = c.gridy = 0;
		panel.add(btnPreviousMonth, c);

		// Label mit dem aktuellen Monat/Jahr
		lblCurrentMonth = new JLabel();

		c.gridx = 1;
		panel.add(lblCurrentMonth, c);

		// Button zum nächsten Monat
		(btnNextMonth=new JButton(">")).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (++month > 11)
				{
					month = 0;
					year++;
				}

				updateViews();
			}
		});

		c.gridx = 2;
		panel.add(btnNextMonth,c);

		// Button für den Auswahldialog
		JButton btnSelectMonth = new JButton("Anderer Monat...");
		btnSelectMonth.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				SelectMonthPanel panel = new SelectMonthPanel(month, year);

				if (JOptionPane.showConfirmDialog(CalendarWindow.this, panel, "Anderer Monat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
				{
					try
					{
						// Prüfen, ob der Benutzer ein gültiges Datum eingegeben hat.
						// Im Fehlerfall löst check() eine IllegalStateExcepion mit Nachricht aus.
						panel.check();

						// Monat und Jahr aus dem Dialogfenster übernehmen
						month = panel.getMonth();
						year =  panel.getYear();

						updateViews();
					}
					catch (IllegalStateException e)
					{
						JOptionPane.showMessageDialog(CalendarWindow.this, e.getMessage(), "Ungültiges Datum", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		c.gridx = c.gridy = 1;
		panel.add(btnSelectMonth, c);

		// Fertiges Panel zurückliefern
		return panel;
	}

	// Appointments-Tabelle erzeugen
	private JComponent createAppointmentsTable()
	{
		(tblAppointments=new JTable(new MonthViewTableModel())).addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				// Doppelklick!
				if (e.getClickCount() == 2)
				{
					final byte day = (byte)tblAppointments.getSelectedRow();
					if (day != -1)
					{
						// DayWindow erzeugen und anzeigen
						new DayWindow(CalendarWindow.this, appointments, day, month, year).setVisible(true);

						// Alles aktualisieren
						updateViews();
					}
				}
			}
		});

		// Relative Spaltenbreiten setzen, damit die erste Spalte schmaler ist
		final TableColumnModel tableColumnModel = tblAppointments.getColumnModel();
		tableColumnModel.getColumn(0).setPreferredWidth(40);
		tableColumnModel.getColumn(1).setPreferredWidth(260);

		// Die erste Spalte soll rechtsbündig formatiert werden.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		tableColumnModel.getColumn(0).setCellRenderer(rightRenderer);

		// Die JTable wird in eine JScrollPane verpackt, damit wir scrollen können.
		JScrollPane scrollPane = new JScrollPane(tblAppointments);
		scrollPane.setPreferredSize(new Dimension(300, 525));

		return scrollPane;
	}

	private class MonthViewTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 2;
		}

		public String getColumnName(int column)
		{
			return (column == 0) ? "Tag" : "Termine";
		}

		public int getRowCount()
		{
			return DateUtil.daysOfMonth(month, year);
		}

		public Object getValueAt(int row, int column)
		{
			assert((column >= 0 )&& (column <= 1));

			if (column == 0)
			{
				return (row+1) + ".";
			}
			else
			{
				final int numberOfAppointments = appointments.countAppointments((byte)row, month, year);

				return (numberOfAppointments == 0) ? "" : String.format((numberOfAppointments == 1) ? "%d Termin" : "%d Termine", numberOfAppointments);
			}
		}
	}
}