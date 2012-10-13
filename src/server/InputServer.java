package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class InputServer extends Thread{
	// The thread the server will run on
	private boolean running;
	
	
	// The port the server will run on
	private Integer port;
	// The object representing the socket where the server listens
	private ServerSocket server;
	// The object representing the socket where the client connects with
	private Socket client;
	// The list of connected Clients that are being handled
	private ArrayList<ServerProtocol> clients;
	
	public InputServer(){
		System.out.println("Creating Server");
		init();
		System.out.println("Done creating server");
	}

	@Override
	public void run() {
		System.out.println("Server : Starting run cycle");
		while(running){
			System.out.println("Server : Running!");
			listen();
		}
		System.out.println("Server : Stopped running");
	}
	
	private void listen(){
		try {
			System.out.println("Server : Listening for incoming connections.");
            client = server.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
		
		System.out.println("Client connected to server.");
		
		handleClient(client);
	}
	
	private void handleClient(Socket client){
        ServerProtocol protocol = new ServerProtocol();
        clients.add(protocol);
        protocol.start();
	}
	
	private void init() {
		try {
			// Initialize everything
			port = 7789;
			server = new ServerSocket(port);
			client = null;
			running = true;
			clients = new ArrayList<ServerProtocol>();
			//TODO : Experimental
			this.setDaemon(true);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
		}
	}

	
}
