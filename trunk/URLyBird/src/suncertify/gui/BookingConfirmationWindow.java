package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

public class BookingConfirmationWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private static BookingConfirmationWindow singletonConfirmationWindow = null;

	private JTextField ownerField;

	private BookingConfirmationCallback callbackObject;

	public static void showWindow(BookingConfirmationCallback callbackObject) {
		if (singletonConfirmationWindow == null) {
			singletonConfirmationWindow = new BookingConfirmationWindow();
		}

		// TODO: Use setter instead
		singletonConfirmationWindow.callbackObject = callbackObject;
		singletonConfirmationWindow.resetForm(true);
	}

	private BookingConfirmationWindow() {
		super("Booking confirmation");
		this.add(new BookingConfirmationPanel());
		pack();
	}

	private void resetForm(boolean visible) {
		if (ownerField != null) {
			ownerField.setText(null);
			ownerField.requestFocusInWindow();
		}
		singletonConfirmationWindow.setVisible(visible);
	}

	private class BookingConfirmationPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public BookingConfirmationPanel() {
			setLayout(new BorderLayout());
			JPanel formPanel = new JPanel(new GridLayout(1, 2));

			JLabel ownerFieldLabel = new JLabel("Owner ID:");
			formPanel.add(ownerFieldLabel);
			try {
				// TODO: At the moment spaces between numbers seem to be valid.
				// They shouldn't.
				ownerField = new JFormattedTextField(new MaskFormatter(
						"########"));
				formPanel.add(ownerField);
			} catch (ParseException e) {
				// TODO Log this error
				e.printStackTrace();
			}

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton confirmButton = new JButton("Confirm booking");
			confirmButton.addActionListener(new ConfirmBooking());
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CancelBooking());
			buttonPanel.add(confirmButton);
			buttonPanel.add(cancelButton);

			this.add(formPanel, BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);

		}

		private class ConfirmBooking implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				callbackObject.book(ownerField.getText());
				singletonConfirmationWindow.resetForm(false);
			}
		}

		private class CancelBooking implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				singletonConfirmationWindow.resetForm(false);
			}
		}
	}

}
