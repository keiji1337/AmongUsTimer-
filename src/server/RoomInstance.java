package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;

import model.StopWatch;
import model.Timer;

public class RoomInstance implements Runnable {
	private String roomCode;
	ArrayList<User> users = new ArrayList<User>();
	private ServerMain serverMain;
	private StopWatch sw = new StopWatch();
	private boolean StopWatchStatus = true; // true Zeit läuft | false Zeit bleibt stehen
	private Timer timer = new Timer(null);

	public RoomInstance(String roomCode, Socket socket, ServerMain serverMain) {
		if (roomCode.length() > 10) {
			String result = roomCode.substring(0, 10);
			roomCode = result;
		}

		this.roomCode = roomCode.toLowerCase().trim();
		this.serverMain = serverMain;
		Thread t = new Thread(timer);
		t.start();

	}

	public void JoinRoom(Socket socket, ObjectOutputStream oos, ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		User newUser = new User(ois, oos, socket, this);
		users.add(newUser);
		Thread newThread = new Thread(newUser);
		newThread.start();
	}

	public String GetRoomCode() {
		return roomCode;
	}

	public void DeleteUser(User user) {
		users.remove(user);
	}

	public void SendTime(String time, int min, int sec, String username) {
		for (User user : users) {
			try {
				user.oos.writeObject("Time:" + time + ":" + username);
			} catch (Exception e) {
				System.out.println("Konnte Nachricht nicht sehen.\n " + e.toString());
			}
		}
		timer.setTime(min, sec);
		sw.start();
	}

	public void StopOrStartTimer(String username) {
		try {
			sw.stop();
			Duration duration = sw.getElapsedTime();
			if (duration == null)
				return;
			int time = (int) (duration.toMillis() / 1000);
			System.out.println("the time: " + time);
			if (time <= 3) {
				sw.start();
				return;
			}
			sw.reset();
			sw.start();
			timer.Change(StopWatchStatus);
			System.out.println("Timer send to Users: " + timer.GetMin() + ":" + timer.GetSec());
			for (User user : users) {
				try {
					user.oos.writeObject("StopStart:" + StopWatchStatus + ":" + timer.GetMin() + ":" + timer.GetSec()
							+ ":" + username);
				} catch (IOException e) {
					System.out.println("StartStop fail.\n " + e.toString());
				}
			}
			StopWatchStatus = !StopWatchStatus;
		} catch (Exception e) {
			System.out.println(e.toString());
			// System.exit(0);
		}

	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(10000);
				System.out.println("Verbunden sind " + users.size());
				for (User user : users) {
					user.AreYouAlive();
					System.out.println(
							"Username im Raum: " + user.userName + " - " + user.IsConnected() + user.isClosed());
				}
				if (users.size() != 0) {
					for (int i = users.size() - 1; i >= 0; i--) {
						User user = users.get(i);
						if (user.isClosed()) {
							user.CloseUserConnection();
							users.remove(user);
							System.out.println("Anzahl der verbunden User " + users.size());
						}
					}
				} else {
					serverMain.DeleteRoom(this);
					break;
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				// TODO: handle exception
			}
		}

	}
}
