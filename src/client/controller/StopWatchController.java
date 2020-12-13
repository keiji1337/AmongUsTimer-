package client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.view.StopWatchView;
import model.Timer;

public class StopWatchController implements Runnable {
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public Socket socket;
	private StopWatchView swv;
	private Timer timer;

	public StopWatchController(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		this.ois = ois;
		this.oos = oos;
		this.socket = socket;

		swv = new StopWatchView(this);
		swv.setVisible(true);
		timer = new Timer(swv);
		Thread t = new Thread(timer);
		t.start();
	}

	private boolean tryParseInt(String value) {
		try {
			int number = Integer.parseInt(value);
			if (number < 0 || number > 60)
				return false;
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void SendTime(String min, String sec) {
		if (!tryParseInt(min) || !tryParseInt(sec))
			return;
		try {
			oos.writeObject("Time:" + min + ":" + sec);
		} catch (Exception e) {
			System.out.println("Fehler beim senden der Zeit");
		}
	}

	public void StartTimer() {
		try {
			oos.writeObject("StopStart:StopStart");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				String message = (String) ois.readObject();
				String[] splitMessage = message.split(":");
				if (splitMessage.length == 0)
					continue;
				switch (splitMessage[0]) {
				case "Time": {
					if (splitMessage.length != 5 || !tryParseInt(splitMessage[1]) || !tryParseInt(splitMessage[2]))
						continue;
					String messageToUsers = "";
					messageToUsers = splitMessage[1] + ":" + splitMessage[2];
					timer.setTime(Integer.parseInt(splitMessage[1]), Integer.parseInt(splitMessage[2]));
					swv.ChangeTextArea("SetTime: " + splitMessage[4]);
					break;
				}
				case "Message": {

					break;

				}
				case "StopStart": {
					if (splitMessage.length != 6)
						continue;
					String value = splitMessage[1];
					String min = splitMessage[2];
					String sec = splitMessage[3];
					String username = splitMessage[5];
					String status = "";
					if (value.equals("true")) {
						status = "unpause: ";
						timer.Change(true);
						swv.btnStopStartCount.setText("Stop the Count");
					} else {
						status = "pause: ";
						timer.Change(false);
						swv.btnStopStartCount.setText("Start the Count");
					}

					swv.ChangeTextArea(status + username);
					timer.setTime(Integer.parseInt(min), Integer.parseInt(sec));
					new Thread() {
						public void run() {
							swv.btnStopStartCount.setEnabled(false);
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							swv.btnStopStartCount.setEnabled(true);
						}
					}.start();
					break;
				}
				default:
					break;
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
}
