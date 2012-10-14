package main;

import server.InputServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputServer server = new InputServer();
		server.start();
		try {
			server.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
