package main;

import data.MeasurementReferenceData;
import logic.ParserCorrectorPool;
import server.InputServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MeasurementReferenceData data = MeasurementReferenceData.getMeasurementReferenceData();
		ParserCorrectorPool pool = ParserCorrectorPool.getPool();
		InputServer server = new InputServer();
		server.start();
		try {
			server.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
