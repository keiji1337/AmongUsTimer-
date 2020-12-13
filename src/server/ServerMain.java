package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerMain {
	// static ServerSocket variable
	public ServerSocket server;
	// socket server port on which it will listen
	private int port = 9876;
	ArrayList<RoomInstance> rooms = new ArrayList<RoomInstance>();

	public ServerMain() throws IOException {
		server = new ServerSocket(port);
	}

	public void Loop() throws IOException, ClassNotFoundException {
		try {
			while (true) {
				System.out.println("Start loop");
				Socket socket = server.accept();
				System.out.println("server.accept");
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				String message = (String) ois.readObject();

				String[] splitMessage = message.split(":", 2);
				if (splitMessage.length != 2) {
					oos.writeObject("false");
					ois.close();
					oos.close();
					socket.close();
					continue;
				}

				String command = splitMessage[0];
				String info = splitMessage[1];
				switch (command) {
				case "Create":
					System.out.println("Erstelle Raum");
					CreateRoom(info, oos, ois, socket);
					break;
				case "Join":
					JoinRoom(info, oos, ois, socket);
					break;
				default:
					oos.writeObject("false");
					ois.close();
					oos.close();
					socket.close();
					break;
				}

			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
	}

	public void CreateRoom(String roomName, ObjectOutputStream oos, ObjectInputStream ois, Socket socket)
			throws IOException, ClassNotFoundException {
		System.out.println("hier");
		boolean roomAlreadyExisting = false;
		for (RoomInstance room : rooms) {
			if (room.GetRoomCode() == roomName) {
				oos.writeObject("false");
				ois.close();
				oos.close();
				socket.close();
				roomAlreadyExisting = true;
				break;
			}
		}
		if (roomAlreadyExisting)
			return;
		System.out.println("Neue RoomInstance");
		RoomInstance newRoom = new RoomInstance(roomName, socket, this);
		Thread newThread = new Thread(newRoom);
		newThread.start();
		rooms.add(newRoom);
		oos.writeObject("true");
		ois.close();
		oos.close();
		socket.close();
		System.out.println("CreateRoom ende");
	}

	public void JoinRoom(String roomName, ObjectOutputStream oos, ObjectInputStream ois, Socket socket)
			throws IOException, ClassNotFoundException {
		RoomInstance isRoomExisting = null;
		for (RoomInstance room : rooms) {
			if (room.GetRoomCode().toLowerCase().trim().equals(roomName.toLowerCase().trim())) {
				isRoomExisting = room;
				break;
			}
		}

		if (isRoomExisting != null) {
			System.out.println("Raum ist da");
			isRoomExisting.JoinRoom(socket, oos, ois);
		} else {
			oos.writeObject("false");
			oos.close();
			socket.close();
		}

	}

	public void DeleteRoom(RoomInstance room) {
		rooms.remove(room);
	}
}
