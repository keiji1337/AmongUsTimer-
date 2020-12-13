package client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import client.view.StartMenue;
import client.view.StopWatchView;
import main.Main;

public class ClientConroller {
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public Socket socket;
	private StartMenue sm;

	public ClientConroller() {
		sm = new StartMenue(this);
		sm.setVisible(true);
	}

	public void Create(String name, String code, JLabel errorMessage) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			Socket socket = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			
			socket = new Socket(Main.HostIP, Main.Port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			if (!TryToCreateRoom(oos, ois, socket, code)) {
				errorMessage.setText("Error while creating the room, try again");
				ois.close();
				oos.close();
				socket.close();
				return;
			}
			errorMessage.setText("Room was created");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Join(String name, String code, JLabel errorMessage, JFrame frame) {

		try {
			InetAddress host = InetAddress.getLocalHost();

			Socket socket = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			socket = new Socket(Main.HostIP, Main.Port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("Versuche zu joinen: " + code);
			if (!TryToJoin(oos, ois, socket, code)) {
				ois.close();
				oos.close();
				socket.close();
				errorMessage.setText("Login error, try again");
				return;
			}

			System.out.println("Habe mich mit dem Raum verbunden Name: " + name);
			if (!TryToAddName(oos, ois, socket, name)) {
				ois.close();
				oos.close();
				socket.close();
				errorMessage.setText("Login error, try again");
				return;
			}
	
			try {
				StopWatchController swc = new StopWatchController(socket, oos, ois);
				Thread t = new Thread(swc);
				t.start();
				frame.dispose();
				sm.dispose();
			} catch (Exception e) {
				errorMessage.setText("Login error, try again");
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean TryToJoin(ObjectOutputStream oos, ObjectInputStream ois, Socket socket, String code)
			throws IOException, ClassNotFoundException {
		oos.writeObject("Join: " + code);
		String message = (String) ois.readObject();
		System.out.println("TryToJoin message " + message);
		if (message.equals("false")) {
			return false;
		}
		return true;
	}

	private boolean TryToAddName(ObjectOutputStream oos, ObjectInputStream ois, Socket socket, String name)
			throws IOException, ClassNotFoundException {
		String nameCommand = (String) ois.readObject();
		System.out.println("nameCommand: " + nameCommand);
		oos.writeObject("name: " + name);
		String message = (String) ois.readObject();
		if (message.equals("false")) {
			return false;
		}
		return true;
	}

	private boolean TryToCreateRoom(ObjectOutputStream oos, ObjectInputStream ois, Socket socket, String code)
			throws IOException, ClassNotFoundException {
		oos.writeObject("Create: " + code);
		String message = (String) ois.readObject();
		System.out.println(message);
		if (message.equals("false")) {
			return false;
		}
		return true;
	}

	
}
