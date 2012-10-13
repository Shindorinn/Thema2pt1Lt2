package main;

import logic.ParserCorrectorPool;
import data.MeasurementReferenceData;
import server.InputServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputServer server = new InputServer();
		server.start();
	}

}
