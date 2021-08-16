package application.com.mybase;

public class userStockData {

	
	String instrumentName;
	String tickerSymbol;
	String quantity;
	String price;
	String avgPrice;
	String result;
	 
	public userStockData(String instrumentName, String tickerSymbol, String quantity, String price, String avgPrice, String result) {
		this.tickerSymbol = tickerSymbol;
		this.instrumentName = instrumentName;
		this.quantity = quantity;
		this.price = price;
		this.avgPrice = avgPrice;
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(String avgPrice) {
		this.avgPrice = avgPrice;
	}
	
	

}
