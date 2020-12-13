package client.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import client.controller.StopWatchController;
import client.globalKeyListener.MyGlobalKeyListener;

import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class StopWatchView {

	private JFrame frame;
	private JTextField tfMin;
	private JTextField tfSec;
	public JButton btnStopStartCount;
	private JLabel lblMin;
	private JLabel lblSec;
	private JButton btnResizeFrame;
	private MyGlobalKeyListener gkl;
	private StopWatchView myReference;
	public JButton btnKey;
	JTextArea textArea;
	ArrayList<String> textAreaOutput = new ArrayList<String>();
	private StopWatchController stopWatchController;

	public StopWatchView(StopWatchController stopWatchController) {
		initialize();
		if (stopWatchController == null)
			frame.setVisible(true);
		this.stopWatchController = stopWatchController;
		this.myReference = this;
		
	}
	public void ChangeTextArea(String newLine) {
		System.out.println("add new Line " + newLine);
		textAreaOutput.add(newLine);
		if (textAreaOutput.size() >= 7) {
			textAreaOutput.remove(0);
		}
		String output = "";
		for (int i = textAreaOutput.size() - 1; i >= 0; i--) {
			String text = textAreaOutput.get(i);
			output += text + "\n";

		}
		textArea.setText(output);
	}

	public void SetTime(String min, String sec) {
		lblMin.setText(min);
		lblSec.setText(sec);
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 170, 405);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		lblMin = new JLabel("00");
		lblMin.setHorizontalAlignment(SwingConstants.CENTER);
		lblMin.setBounds(0, 11, 58, 26);
		frame.getContentPane().add(lblMin);

		lblSec = new JLabel("00");
		lblSec.setHorizontalAlignment(SwingConstants.CENTER);
		lblSec.setBounds(57, 17, 46, 14);
		frame.getContentPane().add(lblSec);

		btnResizeFrame = new JButton("^");
		btnResizeFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeFrameSize();
			}
		});
		btnResizeFrame.setBounds(98, 13, 46, 23);
		frame.getContentPane().add(btnResizeFrame);

		tfMin = new JTextField();
		tfMin.setBounds(10, 240, 86, 20);
		frame.getContentPane().add(tfMin);
		tfMin.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Min.");
		lblNewLabel_2.setBounds(12, 213, 46, 14);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Sec.");
		lblNewLabel_3.setBounds(12, 271, 46, 14);
		frame.getContentPane().add(lblNewLabel_3);

		tfSec = new JTextField();
		tfSec.setBounds(10, 296, 86, 20);
		frame.getContentPane().add(tfSec);
		tfSec.setColumns(10);

		JButton btnNewButton_1 = new JButton("Set Time");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopWatchController.SendTime(tfMin.getText(), tfSec.getText());
				new Thread() {
					public void run() {
						btnNewButton_1.setEnabled(false);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						btnNewButton_1.setEnabled(true);

					}
				}.start();
			}
		});
		btnNewButton_1.setBounds(10, 329, 134, 23);
		frame.getContentPane().add(btnNewButton_1);

		btnStopStartCount = new JButton("Start the Count");
		btnStopStartCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopWatchController.StartTimer();
			}
		});
		btnStopStartCount.setBounds(10, 48, 134, 23);
		frame.getContentPane().add(btnStopStartCount);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.scrollRectToVisible(null);
		textArea.setTabSize(7);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(10, 82, 134, 115);
		frame.getContentPane().add(textArea);

		btnKey = new JButton("Key");
		btnKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GlobalScreen.removeNativeKeyListener(gkl);
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException ex) {
					System.err.println("There was a problem remove the native hook.");
					System.err.println(ex.getMessage());
				}
				try {
					GlobalScreen.registerNativeHook();
				} catch (Exception e2) {
					System.err.println("There was a problem registering the native hook.");
					System.err.println(e2.getMessage());
				}
				gkl = new MyGlobalKeyListener(myReference);
				GlobalScreen.addNativeKeyListener(gkl);
			}
		});
		btnKey.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnKey.setBounds(98, 239, 56, 77);
		frame.getContentPane().add(btnKey);
	}

	public void changeFrameSize() {
		System.out.println("Button Text " + btnResizeFrame.getText());
		if (btnResizeFrame.getText().equals("^")) {
			btnResizeFrame.setText("v");
			frame.setPreferredSize(new Dimension(170, 115));
			System.out.println("Change Size ^");
		} else {
			btnResizeFrame.setText("^");
			frame.setPreferredSize(new Dimension(170, 405));
			System.out.println("Change Size v");
		}
		frame.pack();
	}

	public void setVisible(boolean value) {
		frame.setVisible(value);
	}

	public void dispose() {
		frame.dispose();
	}
}
