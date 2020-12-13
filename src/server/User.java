package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class User implements Runnable {
	public String userName = "";
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public Socket socket;
	private RoomInstance room;

	public User(ObjectInputStream ois, ObjectOutputStream oos, Socket socket, RoomInstance room) {

		System.out.println("Bin User: " + userName + " " + "und befinde mich in " + room.GetRoomCode());
		this.userName = userName.toLowerCase().trim();
		this.ois = ois;
		this.oos = oos;
		this.socket = socket;
		this.room = room;
	}

	public void CloseUserConnection() {
		try {
			ois.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		try {
			oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void AreYouAlive() {
		try {
			oos.writeObject("Alive?");

		} catch (Exception e) {
			CloseUserConnection();
			System.out.println(e.toString());
		}
	}

	public boolean IsConnected() {
		return socket.isConnected();
	}

	public boolean isClosed() {
		return socket.isClosed();
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

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			oos.writeObject("name");
			System.out.println("warte auf den Username");
			String message = (String) ois.readObject();
			if (userName.length() > 15) {
				String result = message.substring(0, 15);
				userName = result;
			} else {
				userName = message;

			}
			System.out.println("Habe Username bekommen " + userName);
			oos.writeObject("true");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
//			System.out.println(userName);
			try {
				if (!IsConnected()) {
					CloseUserConnection();
					break;
				}
				String message = (String) ois.readObject();
				System.out.println("User: " + message);
				String[] splitMessage = message.split(":");
				if (splitMessage.length == 0)
					continue;
				switch (splitMessage[0]) {
				case "Time": {
					if (splitMessage.length != 3 || !tryParseInt(splitMessage[1]) || !tryParseInt(splitMessage[2]))
						continue;
					String messageToUsers = "";
					messageToUsers = splitMessage[1] + ":" + splitMessage[2];
					room.SendTime(messageToUsers, Integer.parseInt(splitMessage[1]), Integer.parseInt(splitMessage[2]),userName);
					break;
				}
				case "Message": {

					break;
				}
				case "StopStart": {
					System.out.println("start time wird gestartet");
					this.room.StopOrStartTimer(userName);
					break;
				}
				default:
					System.out.println("nachricht nicht bekannt: " + message);
					break;
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				CloseUserConnection();
				System.out.println(e.toString());
				break;
			}
		}
	}
}
