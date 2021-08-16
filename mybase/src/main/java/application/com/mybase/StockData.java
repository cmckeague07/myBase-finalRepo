package application.com.mybase;

public class StockData {
	
	String instrumentName;
	String companyName;
	String marketName;
	
	public StockData(String instrumentName, String companyName, String marketName) {
		this.companyName = companyName;
		this.instrumentName = instrumentName;
		this.marketName = marketName;
	}
	 
	public String getInstrumentName() {
		return instrumentName;
	}
 
	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}
  
	public String getCompanyName() {
		return companyName;
	}
 
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
}
