package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.rmi.RemoteException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

import suncertify.remote.RmiURLyBirdDBRegistry;

public class ServerWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField portField;
	private JTextField selectedFileField;
	private JButton browseButton;

	public ServerWindow() {
		super("Server settings");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try {
			portField = new JFormattedTextField(new MaskFormatter("#####"));
			portField.setColumns(5);
			//Default port
			portField.setText("11111");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		selectedFileField = new JTextField(40);
		selectedFileField.setEnabled(false);
		
		//Default location
		selectedFileField.setText("C:\\Users\\Raido\\java\\workspace\\URLyBird\\db-1x3.db");
		
		browseButton = new JButton("Browse");
		browseButton.addActionListener(new BrowseDBListener());
		
		this.add(new ServerSettingsPanel(), BorderLayout.CENTER);
		this.add(new StatusPanel(), BorderLayout.SOUTH);
		
		pack();
		
		setVisible(true);
	}
	
	private class ServerSettingsPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public ServerSettingsPanel() {
			GridBagLayout gridBagLayout = new GridBagLayout();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			setLayout(gridBagLayout);

			gridBagConstraints.insets = new Insets(1, 1, 1, 1);

			JLabel portLabel = new JLabel("Port:");
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagLayout.setConstraints(portLabel, gridBagConstraints);
			this.add(portLabel);
			
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagLayout.setConstraints(portField, gridBagConstraints);
			this.add(portField);
			
			JLabel databaseLocationLabel = new JLabel("Database location:");
			gridBagConstraints.gridwidth = 1;//GridBagConstraints.RELATIVE;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagLayout.setConstraints(databaseLocationLabel, gridBagConstraints);
			this.add(databaseLocationLabel);

			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagLayout.setConstraints(selectedFileField, gridBagConstraints);
			this.add(selectedFileField);
			
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagLayout.setConstraints(browseButton, gridBagConstraints);
			this.add(browseButton);
			
			
			JButton startServerButton = new JButton("Start server");
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagLayout.setConstraints(startServerButton, gridBagConstraints);
			startServerButton.addActionListener(new StartServerActionListener());
			this.add(startServerButton);

			JButton quitServerButton = new JButton("Quit server");
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagLayout.setConstraints(quitServerButton, gridBagConstraints);
			this.add(quitServerButton);
		}
	}
	
	private class StatusPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel statusLabel = new JLabel();
		public StatusPanel() {
			super(new BorderLayout());
			statusLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			this.add(statusLabel, BorderLayout.CENTER);
			
			setStatusText("Configure settings");
		}
		
		public void setStatusText(String text) {
			statusLabel.setText(text);
		}
	}

	private class BrowseDBListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(System
					.getProperty("user.dir"));

			fileChooser.addChoosableFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "UrlyBird database files (";
				}

				@Override
				public boolean accept(File file) {
					return true;
				}
			});
			
			if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
				String fileLocation = fileChooser.getSelectedFile().toString();
				selectedFileField.setText(fileLocation);
			}
		}
	}
	
	private class StartServerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String fileLocation = selectedFileField.getText();
			int port = new Integer(portField.getText());
			try {
				RmiURLyBirdDBRegistry.register(fileLocation, port);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JButton invokingButton = (JButton) event.getSource();
			invokingButton.setEnabled(false);
		}
	}
}
