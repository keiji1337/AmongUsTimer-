package client.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import client.controller.ClientConroller;
import client.model.ClientModel;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class StartMenue {

	private JFrame frame;
	private JTextField name;
	private JTextField code;
	private ClientConroller cc;

	public StartMenue(ClientConroller cc) {
		this.cc = cc;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 420, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		name = new JTextField();
		name.setDocument(new FixedSizeDocument(9, false));
		name.setBounds(155, 36, 168, 20);
		frame.getContentPane().add(name);
		name.setColumns(10);

		code = new JTextField();
		code.setDocument(new FixedSizeDocument(9, true));
		code.setBounds(155, 67, 168, 20);
		frame.getContentPane().add(code);
		code.setColumns(10);

		JLabel lblNewLabel = new JLabel("Name:");
		lblNewLabel.setBounds(74, 39, 46, 14);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Code:");
		lblNewLabel_1.setBounds(74, 70, 46, 14);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel errorMessage = new JLabel("Message");
		errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
		errorMessage.setBounds(74, 230, 249, 20);
		frame.getContentPane().add(errorMessage);

		JButton btnStart = new JButton("CreateRoom");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					cc.Create(name.getText(), code.getText(), errorMessage);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnStart.setBounds(74, 125, 249, 23);
		frame.getContentPane().add(btnStart);
		JButton btnNewButton = new JButton("JoinRoom");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					cc.Join(name.getText(), code.getText(), errorMessage, frame);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(74, 183, 249, 23);
		frame.getContentPane().add(btnNewButton);
	}

	public void setVisible(boolean value) {
		frame.setVisible(value);
	}

	public void dispose() {
		frame.dispose();
	}

	public class FixedSizeDocument extends PlainDocument {
		private int max = 10;
		private boolean toUpper = false;

		public FixedSizeDocument(int max, boolean toUpper) {
			this.max = max;
			this.toUpper = toUpper;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if ((getLength() + str.length() > max)) {
				str = str.substring(0, max - getLength());
			} else {
				if (!str.matches("[a-zA-Z]")) {
					str = "";
				}
			}

			if (toUpper)
				str = str.toUpperCase();
			super.insertString(offs, str, a);
		}
	}
}
