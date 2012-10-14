package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
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
	public static final int MAX_CONNECTIONS = 50;
	
	// The array of connections
	private LinkedList<Socket> connections;
	private ArrayList<Socket> newConnections;
	
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
		this.start();
	}
	
	@Override
	public void run() {
		System.out.println("ServerProtocol : Starting to run.");
		while(running){
			checkForNewConnections();
			handleClients();
			ServerProtocol.yield();
		}
	}
	
	protected boolean addClient(Socket client){
		if(client == null){
        	System.err.println("ServerProtocol : Client == null");
        	return true;
        } else {
			// Is there still a spot for a connection?
			if(connections.size() < MAX_CONNECTIONS){
				// Add the connection
				System.out.println("ServerProtocol : Adding a client!");
				synchronized(newConnections){
					newConnections.add(client);
					return true;
				}
			}else{
				System.out.println("ServerProtocol : I don't have any space for new clients!");
				// No spots available
				return false;
			}
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
		        	// System.out.println("ServerProtocol " + client.toString() + " : Input = " +input);
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
	
	private void checkForNewConnections(){
		synchronized (newConnections) {
			for(int i = 0; i < newConnections.size() ; i++){
				Socket client = newConnections.remove(i);
				connections.add(client);
				addStreamsAndBuffer(client);
			}
		}
	}
	

	private void addStreamsAndBuffer(Socket client) {
		try{
			System.out.println("ServerProtocol : Adding streams and buffers!");
			
			this.inputStreams.put(client,  new BufferedReader( new InputStreamReader(client.getInputStream() ) ) );
			this.outputStreams.put(client, new ObjectOutputStream(client.getOutputStream()) );
			
			this.inputBuffers.put(client, new StringBuffer());
			System.out.println("ServerProtecol : Done adding streams and buffers");
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
		synchronized(this){
			this.parserCorrectorPool = pool;
			
			this.connections = new LinkedList<Socket>();
			this.newConnections = new ArrayList<Socket>();
			
			this.inputStreams = new HashMap<Socket, BufferedReader>();
			this.outputStreams = new HashMap<Socket, ObjectOutputStream>();
			
			this.inputBuffers = new HashMap<Socket, StringBuffer>();
			
			this.running = true;
		}
	}


}
