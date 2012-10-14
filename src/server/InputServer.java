package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class InputServer extends Thread{
	// The thread the server will run on
	private boolean running;
	
	
	// The port the server will run on
	private Integer port;
	// The object representing the socket where the server listens
	private ServerSocket server;
	// The list of connected Clients that are being handled
	private LinkedList<ServerProtocol> clients;
	
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
			handleClient(server.accept() );
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
		
		System.out.println("Client connected to server.");
		
		
	}
	
	private void handleClient(Socket client){
		
		if(clients.isEmpty()){
			System.out.println("InputServer : No protocols available, adding new one.");
			ServerProtocol protocol = new ServerProtocol();
			protocol.start();
			clients.add(protocol);
	        while(!protocol.addClient(client));
	        
	        System.out.println("InputServer : Adding client succeeded!");
		}else if(clients.getLast().isFull()){
			System.out.println("InputServer : All protocols are full, adding new one.");
			ServerProtocol protocol = new ServerProtocol();
			protocol.start();
			clients.add(protocol);
			 while(!protocol.addClient(client));
	       
	        System.out.println("InputServer : Adding client succeeded!");
		}else{
			while(!clients.getLast().addClient(client) );
	        System.out.println("InputServer : Adding client succeeded!");
		}
		
	}
	
	private void init() {
		try {
			// Initialize everything
			port = 7789;
			server = new ServerSocket(port);
			running = true;
			clients = new LinkedList<ServerProtocol>();
			//TODO : Experimental
			this.setDaemon(true);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
		}
	}

	
}
