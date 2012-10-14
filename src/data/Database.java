package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import domain.Measurement;

public class Database {

	private static final String rootFileName 	= "db";
	private static final String slash 			= "/";
	private static final String fileType		= ".db";
		
	protected Database(){
		// This is protected to only allow the DBPortal to create a Database object.
	}
	
	/**
	 * This method will save a single Measurement object based on its stn and its date.
	 * 
	 * @param mes
	 */
	protected void save(Measurement mes){
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
		for(Measurement mes : measurements){
			this.save(mes);
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
