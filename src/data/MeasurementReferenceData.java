package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import domain.Measurement;

/**
 * 
 * 
 * 
 * @author Dustin Meijer
 *
 */

public class MeasurementReferenceData {

	private static MeasurementReferenceData instance;
	
	private HashMap<Integer, LinkedList<Measurement>> measurementData;
	public static final int REFERENCE_LIMIT = 30;
	
	private MeasurementReferenceData(){
		this.measurementData = new HashMap<Integer, LinkedList<Measurement>>();
	}
	
	public static MeasurementReferenceData getMeasurementReferenceData(){
		if(MeasurementReferenceData.instance == null){
			MeasurementReferenceData.instance = new MeasurementReferenceData();
			return MeasurementReferenceData.instance;
		}else{
			return MeasurementReferenceData.instance;
		}
	}
	
	public void addMeasurements(ArrayList<Measurement> measurements){
		synchronized(measurements){
			for(Measurement mes : measurements){
				this.addMeasurement(mes);
			}
		}
	}
	
	public void addMeasurement(Measurement mes){
		// Initialize the reference
		LinkedList<Measurement> measurements = null;
		
		// Lock the measurementData resource
		synchronized(measurementData){
			// Get the needed list of measurements
			measurements = measurementData.get(mes.getStn());
		}
		// Lock the list of measurements for work
		synchronized(measurements){
			// Add the new measurement for future reference
			measurements.add(mes);
			// If the measurements exceed the reference limit
			if(measurements.size() > MeasurementReferenceData.REFERENCE_LIMIT){
				// Remove the first added measurement
				measurements.removeFirst();
			}
		}
	}
	
	public ArrayList<Measurement> getReferenceMeasurements(int stn){
		// Initialize the help variable references
		ArrayList<Measurement> measurements = new ArrayList<Measurement>();
		LinkedList<Measurement> referenceMeasurements = null;
		
		// Lock the referenceData
		synchronized(measurementData){
			// Get the needed list of measurements
			referenceMeasurements = measurementData.get(stn);
		}
		// Lock the list of measurements
		synchronized(referenceMeasurements){
			// Retrieve all the measurements
			for(Measurement m : referenceMeasurements){
				// Add them to the ArrayList for returning
				measurements.add(m);
			}
		}
		// Return the newly made ArrayList
		return measurements;
	}
	
}
