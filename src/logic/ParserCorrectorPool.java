package logic;

import java.util.ArrayList;

public class ParserCorrectorPool extends Thread{

	// The reference to itself to enable Singleton
	private static ParserCorrectorPool instance;
	// The max amount of ParserCorrector threads the pool can have
	public static final int MAX_PARSERCORRECTORS = 25;
	
	private ArrayList<ParserCorrector> correctors;
	
	private ArrayList<String> availableWork;
	
	private boolean running;
	
	private ParserCorrectorPool(){
		init();
	}
	
	@Override
	public void run(){
		System.out.println("ParserCorrectorPool : Starting run cycle");
		while(running);
	}
	
	public void addWeatherData(String weatherData) {
		System.out.println("ParserCorrectorPool : Adding work!");
		// Lock the list
		synchronized (availableWork) {
			System.out.println("ParserCorrectorPool : Locked the availableWork!");
			// Add the new data
			availableWork.add(weatherData);
			System.out.println("ParserCorrectorPool : Added work! " + weatherData);
		}
		ParserCorrectorPool.yield();
	}

	public static ParserCorrectorPool getPool() {
		if(ParserCorrectorPool.instance == null){
			ParserCorrectorPool.instance = new ParserCorrectorPool();
			ParserCorrectorPool.instance.start();
			return ParserCorrectorPool.instance;
		}else{
			return ParserCorrectorPool.instance;
		}
	}
	
	protected String checkForAvailableWork(ParserCorrector corrector){
		System.out.println("ParserCorrectorPool : checkForAvailableWork");
		synchronized(corrector){
			System.out.println("ParserCorrectorPool : Synced on Corrector.");
			while(availableWork.isEmpty());
			synchronized(availableWork){
				System.out.println("ParserCorrectorPool : Locked the AvailableWork.");
				return availableWork.remove(0);
			}
		}
	}

	private void init() {
		// TODO
		availableWork = new ArrayList<String>();
		
		correctors = new ArrayList<ParserCorrector>();
		
		for(int i = 0; i < ParserCorrectorPool.MAX_PARSERCORRECTORS; i++){
			ParserCorrector corrector = new ParserCorrector(this);
			correctors.add(corrector);
			corrector.start();
		}
	}

}
