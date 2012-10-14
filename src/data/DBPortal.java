package data;

import java.util.ArrayList;

import domain.Measurement;

public class DBPortal {

	private static DBPortal instance;
	private Database db;
	
	private DBPortal(){
		this.db = new Database();
	}
	
	public static DBPortal getDBPortal(){
		if(DBPortal.instance == null){
			DBPortal.instance = new DBPortal();
			return DBPortal.instance;
		}else{
			return DBPortal.instance;
		}
	}

	public void saveMeasurements(ArrayList<Measurement> measurements){
		db.saveMeasurements(measurements);
	}
}
