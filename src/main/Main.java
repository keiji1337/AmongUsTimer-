package main;

import java.io.IOException;
import client.controller.ClientConroller;
import server.ServerMain;

public class Main {

	public static String HostIP = "5.230.70.250";
	public static int Port = 9876;

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		// Client
		try {
			ClientConroller cc = new ClientConroller();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		// Server
		// new ServerMain().Loop();

	}

}
