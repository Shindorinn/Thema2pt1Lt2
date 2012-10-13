package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.util.HashMap;

import logic.ParserCorrectorPool;

/**
 * 
 * @author Dustin Meijer
 *
 */

public class ServerProtocol implements Runnable{

	// Max amount of connections allowed by a ServerProtocol
	public static final int MAX_CONNECTIONS = 25;
	
	// The array of connections
	private Socket[] connections;
	
	// The lists with the connectionStreams
	private HashMap<Socket, BufferedReader> inputBuffers;
	private HashMap<Socket, ObjectOutputStream> outputStreams;
	
	// The next available index
	private int nextAvailable;
	
	// The reference to the ParserCorrectorPool
	private ParserCorrectorPool parserCorrectorPool;
	
	// The fields for the threading
	private boolean running;
	private Thread thread;
	
	public ServerProtocol(){
		init(ParserCorrectorPool.getPool());
	}
	
	@Override
	public void run() {
		while(running){
			handleClients();
		}
	}
	
	protected boolean addClient(Socket client){
		// Is there still a spot for a connection?
		if(connections.length < MAX_CONNECTIONS){
			// Add the connection
			connections[nextAvailable] = client;
			nextAvailable++;
			addStreamsAndBuffer(client);
			return true;
		}else{
			// No spots available
			return false;
		}
	}

	protected void start(){
		this.thread.start();
	}

	private void handleClients(){
		for(Socket client : connections){
			
			try{
				// From Server to Client
		        ObjectOutputStream out = outputStreams.get(client);
		        // From Client to Server
		        BufferedReader in = inputBuffers.get(client);
		        
		        // The variables containing the information received from the Client        
		        StringBuffer fromClient = new StringBuffer();
		        String input;
		        
		        if( ( input = in.readLine() ) != null){
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
		try {
			
			this.inputBuffers.put(client,  new BufferedReader( new InputStreamReader(client.getInputStream() ) ) );
			this.outputStreams.put(client, new ObjectOutputStream(client.getOutputStream()) );
		} catch (IOException e) {
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
		
		this.connections = new Socket[ServerProtocol.MAX_CONNECTIONS];
		this.nextAvailable = 0;
		
		this.inputBuffers = new HashMap<Socket, BufferedReader>();
		this.outputStreams = new HashMap<Socket, ObjectOutputStream>();
		
		this.running = true;
		this.thread = new Thread(this);
		// TODO
		// thread.start();
	}


}
