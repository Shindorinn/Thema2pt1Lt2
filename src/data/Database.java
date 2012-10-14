package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import domain.Measurement;

public class Database extends Thread{

	private static final String rootFileName 	= "db";
	private static final String slash 			= "/";
	private static final String fileType		= ".db";
	
	private LinkedList<Measurement> measurementsBuffer;
	private LinkedList<Object> queries;
	
	private boolean running;
	
	protected Database(){
		// This is protected to only allow the DBPortal to create a Database object.
		this.measurementsBuffer = new LinkedList<Measurement>();
		this.queries = new LinkedList<Object>();
		
		this.running = true;
		this.start();
	}
	
	public void run(){
		while(running){
			checkForMeasurements();
			// checkForQueries();
			Database.yield();
		}
	}
	
	private void checkForMeasurements() {
		// System.out.println("Database : Checking for Measurements.");
		synchronized(measurementsBuffer){
			while(!measurementsBuffer.isEmpty()){
				save(measurementsBuffer.removeFirst());
			}
		}
	}

	/**
	 * This method will save a single Measurement object based on its stn and its date.
	 * 
	 * @param mes
	 */
	private void save(Measurement mes){
		try {
			ObjectOutputStream out = new ObjectOutputStream(
										new FileOutputStream(rootFileName + slash + mes.getStn() + slash + mes.getDate() + fileType)
										);
			out.writeObject(mes);
			out.close();
		} catch (FileNotFoundException ex){
			//create file.
			File file = new File(rootFileName + slash + mes.getStn() + slash);
			file.mkdirs();
			save(mes);
		} catch (IOException e) {
			System.err.println("Database : An error occured during saving.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will save an entire ArrayList<Measurement> according to
	 * their stn and date.
	 * 
	 * @param measurements
	 */
	protected void saveMeasurements(ArrayList<Measurement> measurements){
		//System.out.println("Database : Saving measurements!");
		synchronized(this.measurementsBuffer){
			for(Measurement mes : measurements){
				this.measurementsBuffer.add(mes);
			}
		}
	}
	
	/**
	 * This method will attempt to load an ArrayList<Measurement> given the 
	 * stn and date. If the file is not found, an empty ArrayList is returned.
	 * 
	 * @param stn
	 * @param date
	 * @return ArrayList<Measurement>
	 */
	
	protected ArrayList<Measurement> load(int stn, String date){
		ArrayList<Measurement> measurements = new ArrayList<Measurement>();
				
		try {
			// Construct the InputStream to read objects
			ObjectInputStream in = new ObjectInputStream(
									new FileInputStream(rootFileName + slash + stn + slash + date + fileType));
			// Declare the help variable
			Measurement mes = null;
			
			// As long as there are measurements to read
			while( (mes = (Measurement) in.readObject() )!= null){
				measurements.add(mes);
			}
			// Close the stream
			in.close();
					
		} catch (FileNotFoundException e) {
			System.err.println("Database : File does not exist.");
		} catch (IOException e) {
			System.err.println("Database : An error occured during loading.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Database : Measurement definition does not match.");
			e.printStackTrace();
		} 
		// Return what was found.
		return measurements;
		
	}
}
