package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JTable mainTable = new JTable();

	private final JTextField searchField = new JTextField(40);

	private final JButton bookingButton = new JButton("Book");

	private final GuiController guiController;

	public MainWindow() {
		super("Hotels");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		guiController = new GuiController(
				"C:\\java\\workspace\\URLyBird\\db-1x3.db");

		mainTable.getSelectionModel().addListSelectionListener(
				new SelectionListener());

		refreshTable();

		this.add(new HotelRoomScreen());
		pack();

		setVisible(true);
	}

	private void refreshTable() {
		String searchString = searchField.getText();

		HotelRoomTableModel hotelRoomTableModel;
		try {
			hotelRoomTableModel = guiController.find(searchString);
			mainTable.setModel(hotelRoomTableModel);
		} catch (GuiControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class HotelRoomScreen extends JPanel {
		private static final long serialVersionUID = 1L;

		public HotelRoomScreen() {
			setLayout(new BorderLayout());

			JButton searchButton = new JButton("Search");
			searchButton.setMnemonic(KeyEvent.VK_S);
			searchButton.addActionListener(new SearchHotel());
			JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			searchPanel.add(searchField);
			searchPanel.add(searchButton);

			JScrollPane tableScroll = new JScrollPane(mainTable);

			bookingButton.setMnemonic(KeyEvent.VK_B);
			bookingButton.addActionListener(new BookHotel());
			// Initially disabled. Selecting some row will enable this button.
			bookingButton.setEnabled(false);
			JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			bookingPanel.add(bookingButton);

			this.add(searchPanel, BorderLayout.NORTH);
			this.add(tableScroll, BorderLayout.CENTER);
			this.add(bookingPanel, BorderLayout.SOUTH);

			mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			mainTable.setToolTipText("Select hotel room for booking");

		}
	}

	public class SelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			int rowIndex = mainTable.getSelectedRow();
			if (rowIndex >= 0) {
				HotelRoomTableModel hotelRoomTableModel = (HotelRoomTableModel) mainTable
						.getModel();
				boolean bookingButtonEnabled = hotelRoomTableModel
						.isAvailable(rowIndex);

				bookingButton.setEnabled(bookingButtonEnabled);
			}
		}
	}

	private class SearchHotel implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			refreshTable();
		}
	}

	private class BookHotel implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			HotelRoomTableModel tableModel = (HotelRoomTableModel) mainTable
					.getModel();
			final int selectedRow = mainTable.getSelectedRow();
			final int recordNumber = tableModel.getRecordNumber(selectedRow);

			BookingConfirmationWindow
					.showWindow(new BookingConfirmationCallback() {

						@Override
						public void book(String ownerId) {

							try {
								guiController.book(recordNumber, ownerId);
								refreshTable();
							} catch (GuiControllerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		}
	}

}
