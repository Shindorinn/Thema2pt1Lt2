package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import logic.ParserCorrectorPool;

/**
 * 
 * @author Dustin Meijer
 *
 */

public class ServerProtocol extends Thread{

	// Max amount of connections allowed by a ServerProtocol
	public static final int MAX_CONNECTIONS = 25;
	
	// The array of connections
	private LinkedList<Socket> connections;
	
	// The lists with the connectionStreams
	private HashMap<Socket, BufferedReader> inputStreams;
	private HashMap<Socket, ObjectOutputStream> outputStreams;
	
	// The list with the input buffers
	private HashMap<Socket, StringBuffer> inputBuffers;
	
	// The reference to the ParserCorrectorPool
	private ParserCorrectorPool parserCorrectorPool;
	
	// The fields for the threading
	private boolean running;
	
	private String input;
	
	public ServerProtocol(){
		init(ParserCorrectorPool.getPool());
	}
	
	@Override
	public void run() {
		System.out.println("ServerProtocol : Starting to run.");
		while(running){
			handleClients();
		}
	}
	
	protected boolean addClient(Socket client){
		if(client == null){
        	System.err.println("ServerProtocol : Client == null");
        }
		// Is there still a spot for a connection?
		if(connections.size() < MAX_CONNECTIONS){
			// Add the connection
			System.out.println("ServerProtocol : Adding a client!");
			connections.add(client);
			addStreamsAndBuffer(client);
			return true;
		}else{
			System.out.println("ServerProtocol : I don't have any space for new clients!");
			// No spots available
			return false;
		}
	}

	private void handleClients(){
		for(Socket client : connections){
			try{
				// From Server to Client
		        // ObjectOutputStream out = outputStreams.get(client);
		        // From Client to Server
		        BufferedReader in = inputStreams.get(client);
		       
		        // The variables containing the information received from the Client        
		        StringBuffer fromClient = inputBuffers.get(client);
		        
		        if( ( input = in.readLine() ) != null){
		        	// System.out.println("ServerProtocol : Input = " +input);
		        	// Add the latest data
		        	fromClient.append(input);
		        	// Was the it the end of the file? 
					if(input.equals("</WEATHERDATA>")){
						// Add the complete weatherData to the ParserCorrectorPool
						parserCorrectorPool.addWeatherData( fromClient.toString() );
						// Empty the buffer
						fromClient.delete(0, fromClient.length());
					}
				 
				 input = null;
		        }else{
		        	removeClientConnection(client);
		        }
			}catch(IOException ioex){
				ioex.printStackTrace();
			}
		}

	}
	

	private void addStreamsAndBuffer(Socket client) {
		try{
			System.out.println("ServerProtocol : Adding streams and buffers!");
			
			this.inputStreams.put(client,  new BufferedReader( new InputStreamReader(client.getInputStream() ) ) );
			this.outputStreams.put(client, new ObjectOutputStream(client.getOutputStream()) );
			
			this.inputBuffers.put(client, new StringBuffer());
		}catch (IOException e) {
			System.err.println("ServerProtocol : An error occurred during adding the clientstreams.");
			e.printStackTrace();
		}
		
	}

	
	private void removeClientConnection(Socket client) {
		// TODO Auto-generated method stub
		// If the program manages to call this method, the generator failed.
		throw new UnsupportedOperationException("ServerProtocol : removeClientConnection is not yet implemented.");
	}

	
	private void init(ParserCorrectorPool pool) {
		this.parserCorrectorPool = pool;
		
		this.connections = new LinkedList<Socket>();
		
		this.inputStreams = new HashMap<Socket, BufferedReader>();
		this.outputStreams = new HashMap<Socket, ObjectOutputStream>();
		
		this.inputBuffers = new HashMap<Socket, StringBuffer>();
		
		this.running = true;
		// TODO
		// thread.start();
	}

	public boolean isFull() {
		if(connections.size() == MAX_CONNECTIONS-1){
			return true;
		}else{
			return false;
		}
	}


}
