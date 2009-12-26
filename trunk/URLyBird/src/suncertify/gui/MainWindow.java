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

public class MainWindow extends JFrame {

	private final JTable mainTable = new JTable();

	private final JTextField searchField = new JTextField(40);

	private final GuiController guiController;

	public MainWindow() {
		super("Hotels");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		guiController = new GuiController(
				"C:\\java\\workspace\\URLyBird\\db-1x3.db");

		HotelRoomTableModel hotelRoomTableModel;
		try {
			hotelRoomTableModel = guiController.find(null);
			refreshTable(hotelRoomTableModel);
		} catch (GuiControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.add(new HotelRoomScreen());
		pack();

		setVisible(true);
	}

	private void refreshTable(HotelRoomTableModel hotelRoomTableModel) {
		mainTable.setModel(hotelRoomTableModel);
	}

	private class HotelRoomScreen extends JPanel {
		public HotelRoomScreen() {
			setLayout(new BorderLayout());

			JButton searchButton = new JButton("Search");
			searchButton.setMnemonic(KeyEvent.VK_S);
			searchButton.addActionListener(new SearchHotel());
			JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			searchPanel.add(searchField);
			searchPanel.add(searchButton);

			JScrollPane tableScroll = new JScrollPane(mainTable);
			this.add(searchPanel, BorderLayout.NORTH);
			this.add(tableScroll, BorderLayout.CENTER);

			mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			mainTable.setToolTipText("Select hotel room for booking");

		}
	}

	private class SearchHotel implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String searchString = searchField.getText();
			try {
				HotelRoomTableModel hotelRoomTableModel = guiController
						.find(searchString);
				refreshTable(hotelRoomTableModel);

			} catch (GuiControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
