package logic;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import data.DBPortal;
import data.MeasurementReferenceData;
import domain.Measurement;

public class ParserCorrector extends Thread{
	
	// The List with all the Strings that need to be processed
	private ArrayList<String> input;
	
	// The pool where this corrector is a part of
	private ParserCorrectorPool pool;
	
	// The class with the latest reference data
	private MeasurementReferenceData referenceData;
	
	// The JDOM classes for xml parsing
	private Document xmlDoc;
	private SAXBuilder builder;
	
	// The completed measurements
	private ArrayList<Measurement> measurements;
	private DBPortal db;
	
	private boolean running;
	
	public ParserCorrector(ParserCorrectorPool pool){
		
		this.input 			= 	new ArrayList<String>();
		this.pool 			= 	pool;
		this.referenceData 	= 	MeasurementReferenceData.getMeasurementReferenceData();
		
		this.builder 		= 	new SAXBuilder();
		this.measurements 	= 	new ArrayList<Measurement>();
		
		this.running 		= 	true;
		this.db 			= 	DBPortal.getDBPortal();
	}


	@Override
	public void run() {
		System.out.println("ParserCorrector : Starting run cycle");
		while(running){
			if(hasWork()){
				work();
			}else{
				getWork();
			}
			ParserCorrector.yield();
		}
	}

	private void getWork() {
		System.out.println("ParserCorrector : Getting work.");
		String work = pool.checkForAvailableWork(this);
		if(work != null){
			input.add(work);
			System.out.println("ParserCorrectoer : Retrieved work.");
		}
	}


	private boolean hasWork() {
		boolean output = !input.isEmpty();
		System.out.println("ParserCorrector : hasWork = " + output);
		return output;
	}


	private void work() {
		parse();
		correct();
		save();
	}


	private void parse() {
		System.out.println("ParserCorrector : Parsing");
		try {
			xmlDoc = builder.build(new StringReader(input.get(0)) );
		} catch (JDOMException e) {
			System.err.println("ParserCorrector : JDOMException.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("ParserCorrector : IOException.");
			e.printStackTrace();
		}
		Element weatherData = xmlDoc.getRootElement();
		
		//System.out.println(weatherData.getName());
		
		List<Element> measurements = weatherData.getChildren();
		
		
		
		// Start van for-loop
		for(int i = 0; i < measurements.size() ; i++ ) {
		
			List<Element> elements = measurements.get(i).getChildren();
			
			int stn = 0;
			String date = "";
			String time = "";
			float temp = Float.MIN_VALUE;
			float dewp = Float.MIN_VALUE;
			float stp = Float.MIN_VALUE;
			float slp = Float.MIN_VALUE;
			float visib = Float.MIN_VALUE;
			float wdsp = Float.MIN_VALUE;
			float prcp = Float.MIN_VALUE;
			float sndp = Float.MIN_VALUE;
			byte frshtt = Byte.MIN_VALUE;
			float cldc = Float.MIN_VALUE;
			short wnddir = Short.MIN_VALUE;
			
			for(Element e : elements){
				//System.out.println("Name : " + e.getName());
				//System.out.println("Value :" + e.getValue());
				
				if(!e.getValue().equals("")){
					if(e.getName().equals("STN")){
						stn = new Integer(e.getValue()).intValue();
					}
					if(e.getName().equals("DATE")){
						date = e.getValue();
					}
					if(e.getName().equals("TIME")){
						time = e.getValue();
					}
					if(e.getName().equals("TEMP")){
						temp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("DEWP")){
						dewp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("STP")){
						stp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("SLP")){
						slp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("VISIB")){
						visib = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("WDSP")){
						wdsp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("PRCP")){
						prcp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("SNDP")){
						sndp = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("FRSHTT")){
						String tempFrshtt = e.getValue();
						frshtt = Byte.parseByte( tempFrshtt, 2 );
					}
					if(e.getName().equals("CLDC")){
						cldc = new Float(e.getValue()).floatValue();
					}
					if(e.getName().equals("WNDDIR")){
						wnddir = new Short(e.getValue()).shortValue();
					}
				}
			}
			
			this.measurements.add( new Measurement(stn, date, time, temp, dewp, stp, slp, visib, wdsp, prcp, sndp, frshtt, cldc, wnddir));
		}	
	}
	
	/**
	 * Casustekst :
	 * 
	 * Indien één of meer meetwaarden ontbreken, worden ze door het systeem berekend door middel van extrapolatie van de dertig voorafgaande metingen. 
	 * Dit komt ongeveer in 1% van alle gevallen voor.
	 * 
	 * Een meetwaarde voor de temperatuur wordt als irreëel beschouwd indien
	 * ze 20% of meer groter is of kleiner is dan wat men kan verwachten op basis van extrapolatie van de dertig voorafgaande temperatuurmetingen. 
	 * 
	 * In dat geval wordt de geëxtrapoleerde waarde ± 20% voor de temperatuur opgeslagen. 
	 * Voor de andere meetwaarden wordt deze handelswijze niet toegepast.
	 * 
	 */

	private void correct() {
		System.out.println("ParserCorrector : Correcting.");
		
		for(Measurement m: measurements){
			ArrayList<Measurement> referenceMeasurements = this.referenceData.getReferenceMeasurements(m.getStn());
			if(referenceMeasurements != null){
				Measurement extrapolation = this.extrapolate(referenceMeasurements);
				checkMeasurement(m, extrapolation);
			} else {
				System.out.println("Pumping raw data!!");
			}
		}
	}

	private void save() {
		 
		System.out.println("ParserCorrector : Saving.");
		this.referenceData.addMeasurements(measurements);
		this.db.saveMeasurements(measurements);
		this.measurements.clear();
	}

	private Measurement extrapolate(ArrayList<Measurement> referenceMeasurements){
		
		float temp = 0;
		float dewp = 0;
		float stp = 0;
		float slp = 0;
		float visib = 0;
		float wdsp = 0;
		float prcp = 0;
		float sndp = 0;
		float cldc = 0;
		short wnddir = 0;
		
		for(int i = 0; i < referenceMeasurements.size() - 1 ; i++ ){
			Measurement first = referenceMeasurements.get(i);
			Measurement second = referenceMeasurements.get(i + 1);
			
			temp += second.getTemp() - first.getTemp();
			dewp += second.getDewp() - first.getDewp();
			stp += second.getStp() - first.getStp();
			slp += second.getSlp() - first.getSlp();
			visib += second.getVisib() - first.getVisib();
			wdsp += second.getWdsp() - first.getWdsp();
			prcp += second.getPrcp() - first.getPrcp();
			sndp += second.getSndp() - first.getSndp();
			cldc += second.getCldc() - first.getCldc();
			wnddir += second.getWnddir() - first.getWnddir();
		}
		
		int n = referenceMeasurements.size() -1;
		if(n !=0 ){
			temp = referenceMeasurements.get(0).getTemp() + temp/n;
			dewp = referenceMeasurements.get(0).getTemp() + dewp/n;
			stp = referenceMeasurements.get(0).getStp() + stp/n;
			slp = referenceMeasurements.get(0).getSlp() + slp/n;
			visib = referenceMeasurements.get(0).getVisib() + visib/n;
			wdsp = referenceMeasurements.get(0).getWdsp() + wdsp/n;
			prcp = referenceMeasurements.get(0).getPrcp() + prcp/n;
			sndp = referenceMeasurements.get(0).getSndp() + sndp/n;
			cldc = referenceMeasurements.get(0).getCldc() + cldc/n;
			wnddir = (short) (referenceMeasurements.get(0).getWnddir() + wnddir/n);
			
			return new Measurement(-1, null, null, temp, dewp, stp, slp, visib, wdsp, prcp, sndp, (byte) 0, cldc, wnddir);
		} else {
			return referenceMeasurements.get(0);
		}
	}


	private Measurement checkMeasurement(Measurement toCheck, Measurement referenceData){
		
		// Checking if the temperature is present and whether this is plausible, else change it.
		if(toCheck.getTemp() == Float.MIN_VALUE){
			toCheck.setTemp(referenceData.getTemp());
		} else if (toCheck.getTemp() < 0.8f * referenceData.getTemp()){ // Is the value too low?
			toCheck.setTemp(0.8f * referenceData.getTemp()); 
		} else if (toCheck.getTemp() > 1.2f * referenceData.getTemp()){ // Is the value too high?
			toCheck.setTemp(1.2f * referenceData.getTemp());
		}
		
		
		// Checking if the other values are present
		if(toCheck.getDewp() == Float.MIN_VALUE){
			toCheck.setDewp(referenceData.getDewp());
		}
		if(toCheck.getStp() == Float.MIN_VALUE){
			toCheck.setStp(referenceData.getStp());
		}
		if(toCheck.getSlp() == Float.MIN_VALUE){
			toCheck.setSlp(referenceData.getSlp());
		}
		if(toCheck.getVisib() == Float.MIN_VALUE){
			toCheck.setVisib(referenceData.getVisib());
		}
		if(toCheck.getWdsp() == Float.MIN_VALUE){
			toCheck.setWdsp(referenceData.getWdsp());
		}
		if(toCheck.getPrcp() == Float.MIN_VALUE){
			toCheck.setPrcp(referenceData.getPrcp());
		}
		if(toCheck.getSndp() == Float.MIN_VALUE){
			toCheck.setSndp(referenceData.getSndp());
		}
		if(toCheck.getFrshtt() == Byte.MIN_VALUE){
			toCheck.setFrshtt(referenceData.getFrshtt());
		}
		if(toCheck.getCldc() == Float.MIN_VALUE){
			toCheck.setCldc(referenceData.getCldc());
		}
		if(toCheck.getWnddir() == Float.MIN_VALUE){
			toCheck.setWnddir(referenceData.getWnddir());
		}
		
		return toCheck;
	}
}
