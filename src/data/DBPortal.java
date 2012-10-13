package data;

public class DBPortal {

	private static DBPortal instance;
	private Database db;
	
	private DBPortal(){
		
	}
	
	public static DBPortal getDBPortal(){
		if(DBPortal.instance == null){
			DBPortal.instance = new DBPortal();
			return DBPortal.instance;
		}else{
			return DBPortal.instance;
		}
	}

}
