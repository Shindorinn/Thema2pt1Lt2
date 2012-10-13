package domain;

/**
 * 
 * @author Arjan Strijland
 *
 */
public class Measurement {
	
	private int stn;
	private String date;
	private String time;
	private float temp;
	private float dewp;
	private float stp;
	private float slp;
	private float visib;
	private float wdsp;
	private float prcp;
	private float sndp;
	private byte frshtt;
	private float cldc;
	private short wnddir;
    
    public Measurement(int stn, String date, String time, float temp, float dewp, 
			float stp, float slp, float visib, float wdsp, float prcp, 
			float sndp, byte frshtt, float cldc, short wnddir){
    	
    	this.setStn(stn);
		this.setDate(date);
		this.setTime(time);
		this.setTemp(temp);
		this.setDewp(dewp);
		this.setStp(stp);
		this.setSlp(slp);
		this.setVisib(visib);
		this.setWdsp(wdsp);
		this.setPrcp(prcp);
		this.setSndp(sndp);
		this.setFrshtt(frshtt);
		this.setCldc(cldc);
		this.setWnddir(wnddir);
    	
    }

	public int getStn() {
		return stn;
	}

	public void setStn(int stn) {
		this.stn = stn;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public float getDewp() {
		return dewp;
	}

	public void setDewp(float dewp) {
		this.dewp = dewp;
	}

	public float getStp() {
		return stp;
	}

	public void setStp(float stp) {
		this.stp = stp;
	}

	public float getSlp() {
		return slp;
	}

	public void setSlp(float slp) {
		this.slp = slp;
	}

	public float getVisib() {
		return visib;
	}

	public void setVisib(float visib) {
		this.visib = visib;
	}

	public float getWdsp() {
		return wdsp;
	}

	public void setWdsp(float wdsp) {
		this.wdsp = wdsp;
	}

	public float getPrcp() {
		return prcp;
	}

	public void setPrcp(float prcp) {
		this.prcp = prcp;
	}

	public float getSndp() {
		return sndp;
	}

	public void setSndp(float sndp) {
		this.sndp = sndp;
	}

	public byte getFrshtt() {
		return frshtt;
	}

	public void setFrshtt(byte frshtt) {
		this.frshtt = frshtt;
	}

	public float getCldc() {
		return cldc;
	}

	public void setCldc(float cldc) {
		this.cldc = cldc;
	}

	public short getWnddir() {
		return wnddir;
	}

	public void setWnddir(short wnddir) {
		this.wnddir = wnddir;
	}
}
    	
    
