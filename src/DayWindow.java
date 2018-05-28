import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;



@SuppressWarnings("serial")
public class DayWindow extends JDialog
{
	// Angezeigter Tag
	private final byte day;
	private final byte month;
	private final short year;

	// Termin-Datenbank	und Liste aller Termine an diesem Tag
	private final Appointments appointments;
	private java.util.List<Appointment> appointmentsList;

	// UI-Elemente
	private JLabel lblDate;
	private JTable tblAppointments;
	private JButton btnAdd;
	private JButton btnRemove;

	public DayWindow(JFrame owner, Appointments appointments, byte day, byte month, short year)
	{
		super(owner, "Tagesansicht", true);

		// Parameter speichern
		assert(appointments!=null);

		this.appointments = appointments;
		this.day = day;
		this.month = month;
		this.year = year;

		// Kontrollelemente erzeugen und einfügen
		add(createDatePanel(), BorderLayout.NORTH);
		add(createAppointmentsTable(), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);

		// Kontrollelemente aktualisieren
		updateViews();

		// Position und Größe aller Kontrollelemente neu berechnen
		pack();

		// Das Fenster soll sich in der Mitte des Elternfensters öffnen
		setLocationRelativeTo(owner);
	}

	protected void updateViews()
	{
		// Termine für den aktuellen Tag aufbereiten
		(appointmentsList=appointments.getAppointments(day, month, year)).sort(new Comparator<Appointment>()
		{
			public int compare(Appointment o1, Appointment o2)
			{
				// Durch das Speichern der Startzeit als Anzahl der Minuten nach Mitternacht ist es
				// besonders einfach zwei Startzeiten für das Sortieren der Termine zu vergleichen:
				return o1.getStart() - o2.getStart();
			}
		});

		// Tabelle neu zeichnen lassen
		((AbstractTableModel)tblAppointments.getModel()).fireTableDataChanged();
	}

	// Datumsanzeige erzeugen
	private JComponent createDatePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		lblDate = new JLabel();
		lblDate.setFont(lblDate.getFont().deriveFont(24.0f));
		lblDate.setText(String.format("%d. %s %d", day + 1, DateUtil.MONTHS[month], year));

		// Das GridBagLayout bietet uns die Möglichkeit, das Label mit größerem Rand (8px) zu platzieren.
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 8, 8, 8);

		panel.add(lblDate, c);

		return panel;
	}

	private JComponent createButtonsPanel()
	{
		JPanel panel  = new JPanel();

		// Button zum Hinzufügen eines neuen Termins
		(btnAdd=new JButton("Hinzufügen...")).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					editAppointment(null);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		panel.add(btnAdd);

		// Button zum Entfernen eines neuen Termins
		(btnRemove=new JButton("Löschen")).addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Durch diese Schleife werden ggf. mehrere ausgewählte Termine entfernt.
				for (int index : tblAppointments.getSelectedRows())
					appointments.remove(appointmentsList.get(index));

				updateViews();
			}
		});

		// Der Button ist zunächst deaktiviert. Er wird aktiv, sobald mindestens ein Termin
		// in der Liste ausgewählt wird (siehe ListSelectionListener).
		btnRemove.setEnabled(false);

		panel.add(btnRemove);

		return panel;
	}

	// Appointment editieren
	protected void editAppointment(Appointment appointment) throws FileNotFoundException
	{
		Appointment appointmentCopy = new Appointment(appointment,  new RandomAccessFile(
				new File("termin.ser"), "rw"));

		// Beim Hinzufügen eines Appointments setzen wir das Datum ein
		if (appointment == null)
			appointmentCopy.setDate(day, month, year);

		EditAppointmentPanel panel = new EditAppointmentPanel(appointmentCopy);
		if (JOptionPane.showConfirmDialog(DayWindow.this, panel, "Termin bearbeiten", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			try
			{
				// Aktualisiert das Appointment-Objekt mit den Eingaben des Benutzers.
				// Im Fehlerfall löst check() eine IllegalArgumentExcepion oder eine IllegalStateExcepion mit Nachricht aus.
				panel.updateAppointment();

				// Das ursprünglich Appointment löschen
				if (appointment != null)
					appointments.remove(appointment);
				
				//Serialsierung
				File sfile = new File("termin.ser");
				ObjectOutputStream oos = null;
				FileOutputStream fos = null;
				
				try {
					fos = new FileOutputStream(sfile);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(appointmentCopy);
				}catch(IOException e) {
					System.out.println("Fehler bei der Serialisierung");
				}finally {
					try {if(oos!=null)oos.close();
					}catch(IOException e) {e.printStackTrace();}
				}
				
				// Das veränderte Appointment hinzufügen
				appointments.add(appointmentCopy);
				updateViews();
			}
			catch (IllegalArgumentException | IllegalStateException e)
			{
				JOptionPane.showMessageDialog(DayWindow.this, e.getMessage(), "Ungültiger Termin", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Appointments-Tabelle erzeugen
	private JComponent createAppointmentsTable()
	{
		(tblAppointments=new JTable(new DayViewTableModel())).addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				// Doppelkltk!
				if (event.getClickCount() == 2)
				{
					final int index = tblAppointments.getSelectedRow();
					if (index != -1)
						try {
							editAppointment(appointmentsList.get(index));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});

		// Der "Löschen"-Button soll nur aktiv sein, wenn auch wirklich ein Termin ausgewählt ist.
		tblAppointments.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				btnRemove.setEnabled(tblAppointments.getSelectedRow() != -1);
			}
		});

		// Relative Spaltenbreiten setzen, damit die ersten Spalten schmaler sind
		final TableColumnModel tableColumnModel = tblAppointments.getColumnModel();
		tableColumnModel.getColumn(0).setPreferredWidth(40);
		tableColumnModel.getColumn(1).setPreferredWidth(50);
		tableColumnModel.getColumn(2).setPreferredWidth(360);

		// Die erste und zweite Spalte sollen rechtsbündig formatiert werden.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		tableColumnModel.getColumn(0).setCellRenderer(rightRenderer);
		tableColumnModel.getColumn(1).setCellRenderer(rightRenderer);

		// Die JTable wird in eine JScrollPane verpackt, damit wir scrollen können.
		JScrollPane scrollPane = new JScrollPane(tblAppointments);
		scrollPane.setPreferredSize(new Dimension(450, 220));

		return scrollPane;
	}

	private class DayViewTableModel extends AbstractTableModel
	{
		public int getColumnCount()
		{
			return 3;
		}

		public String getColumnName(int column)
		{
			switch (column)
			{
			case 0:
				return "#";

			case 1:
				return "Beginn";

			case 2:
				return "Betreff";
			}

			return "?";
		}

		public int getRowCount()
		{
			return appointmentsList.size();
		}

		public Object getValueAt(int row, int column)
		{
			assert((column >= 0 )&& (column <= 2));
			assert(row < appointmentsList.size());

			if (column == 0)
				return row + 1;

			Appointment app = appointmentsList.get(row);
			System.out.println("App:"+app);

			return (column == 1 ) ? String.format("%2d:%02d", app.getStart() / 60, app.getStart() % 60) : app.getSubject();
		}
	}
}