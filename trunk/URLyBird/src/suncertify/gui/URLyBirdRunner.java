package suncertify.gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class URLyBirdRunner {

	public URLyBirdRunner() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new MainWindow();
	}

	public static void main(String[] args) {
		new URLyBirdRunner();
	}
}
