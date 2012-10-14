package logic;

import java.util.ArrayList;

public class ParserCorrectorPool extends Thread{

	// The reference to itself to enable Singleton
	private static ParserCorrectorPool instance;
	// The max amount of ParserCorrector threads the pool can have
	public static final int MAX_PARSERCORRECTORS = 25;
	
	private ArrayList<ParserCorrector> correctors;
	
	private ArrayList<String> availableWork;
	
	private ParserCorrectorPool(){
		init();
	}
	

	public void addWeatherData(String weatherData) {
		// Lock the list
		synchronized (availableWork) {
			// Add the new data
			availableWork.add(weatherData);
		}
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
		synchronized(corrector){
			while(availableWork.isEmpty()){
				try {
					ParserCorrector.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			synchronized(availableWork){
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
