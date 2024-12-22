package application.com.mybase;


import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

@Controller
public class MyBaseController {
	String con = "jdbc:mysql://localhost:3306/demo?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT";
	String user = "root";
	String pass = "###";
	
	//String con = "jdbc:mysql://ba4eede6275c40:4479a7b3@eu-cdbr-west-03.cleardb.net/heroku_a752318e732856e?reconnect=true";
	//String user = "ba4eede6275c40";
	//String pass = "4479a7b3"; 
	
	@GetMapping("/")
	public String root(Model model, RedirectAttributes redirectAttrs) throws SQLException, IOException {
		ArrayList<StockData>  stockData = new ArrayList<>();
		ArrayList<StockData>  unsupportedStocks = new ArrayList<>();
		Map<String, String> totalAmountInvested = new HashMap<String, String>();
		Map<String, String> totalProfit = new HashMap<String, String>();
		Map<String, String> totalMarketValue = new HashMap<String, String>();
		Map<String, String> chartData = new LinkedHashMap<String, String>();
		   
		double total = 0;
		double profitLoss = 0;
		double mv = 0;
		
		//Remove null stocks from our stockdata table
		Connection connBadStocks = DriverManager.getConnection(con,user, pass);
		String queryBadStocks = "DELETE FROM `demo`.`stockdata` " + 
								"USING `demo`.`stockdata` INNER JOIN `demo`.`discontinuedstocks` " + 
								"ON stockdata.INSTRUMENT = discontinuedstocks.INSTRUMENT " + 
								"WHERE stockdata.INSTRUMENT=discontinuedstocks.INSTRUMENT;";
		Statement stmtBadStocks = connBadStocks.createStatement();
		stmtBadStocks.execute(queryBadStocks);
		
		Connection connBadStocksUserTransactions = DriverManager.getConnection(con,user, pass);
		String queryBadStocksUserTransactions = "DELETE FROM `demo`.`usertransactions`\r\n" + 
								"USING `demo`.`usertransactions` INNER JOIN `demo`.`discontinuedstocks`\r\n" + 
								"ON usertransactions.TICKER = discontinuedstocks.INSTRUMENT \r\n" + 
								"WHERE usertransactions.TICKER= discontinuedstocks.INSTRUMENT";
		Statement stmtBadStocksUserTransactions = connBadStocksUserTransactions.createStatement();
		stmtBadStocksUserTransactions.execute(queryBadStocksUserTransactions );
		// Get Stock Data.
		try {
		//	Connection conn = DriverManager.getConnection(con,"ba4eede6275c40", "4479a7b3");
			Connection conn56 = DriverManager.getConnection(con,user, pass);
			String query56 = "Select * FROM stockdata where (`MARKET NAME` LIKE 'NASDAQ' \r\n" + 
					"OR `MARKET NAME` LIKE 'LSE' OR `MARKET NAME` LIKE 'NYSE' OR `MARKET NAME` LIKE 'NON-ISA NYSE' ) \r\n" + 
					"AND  (LENGTH(Company) <= 60) \r\n" + 
					"AND (`COMPANY` NOT LIKE '%ETF%' AND `COMPANY` NOT LIKE '%ETP%' AND `COMPANY` NOT LIKE '%1x %' AND `COMPANY` NOT LIKE '%2x %' AND `COMPANY` NOT LIKE '%3x %');\r\n" + 
					"";
			Statement stmt56 = conn56.createStatement();
			ResultSet rs56 = stmt56.executeQuery(query56);
			
			while(rs56.next()) {
			StockData stock = new StockData(rs56.getString(1),rs56.getString(2),rs56.getString(6) );
			stockData.add(stock);
			}
			
			
			//Check for invalid stock Tickers to remove them from the add screen	
		/*	Connection conn = DriverManager.getConnection(con, user, pass);
			String query = "Select * FROM stockdata where (`MARKET NAME` LIKE 'NASDAQ' \r\n" + 
					"OR `MARKET NAME` LIKE 'LSE' OR `MARKET NAME` LIKE 'NYSE' OR `MARKET NAME` LIKE 'NON-ISA NYSE' ) \r\n" + 
					"AND  (LENGTH(Company) <= 60) \r\n" + 
					"AND (`COMPANY` NOT LIKE '%ETF%' AND `COMPANY` NOT LIKE '%ETP%' AND `COMPANY` NOT LIKE '%1x %' AND `COMPANY` NOT LIKE '%2x %' AND `COMPANY` NOT LIKE '%3x %');\r\n" + 
					"";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				try {
					Stock stockRequest = null;
					stockRequest = YahooFinance.get(rs.getString(1));
					System.out.println("This value is not null: " + rs.getString(1) + " " + stockRequest.getQuote());
					
				} catch (Exception e) {
					StockData badStock = new StockData(rs.getString(1), rs.getString(2), rs.getString(6));
					unsupportedStocks.add(badStock);
					
					// if stock doesnt already exist in this table go ahead and insert it
					Connection connNull1 = DriverManager.getConnection(con, user, pass);
					String queryNull1 = "SELECT * FROM demo.discontinuedstocks WHERE INSTRUMENT = '" + rs.getString(1) + "'";
					Statement stmtNull1 = connNull1.createStatement();
					ResultSet rsDis = stmtNull1.executeQuery(queryNull1);
					
					if (rsDis.next()) {
						System.out.println("This value is null: " + rs.getString(1));
						System.out.println("This stock is a part of this table, so it wont be reinserted.");
					} else {
						System.out.println("This stock isnt a part of this table, so inserting it now. " + rs.getString(1));
						Connection connNull = DriverManager.getConnection(con, user, pass);
						String queryNull = "INSERT INTO demo.discontinuedstocks (INSTRUMENT, MARKETNAME, COMPANY) VALUES ('"
								+ rs.getString(1) + "','" + rs.getString(6) + "','" + rs.getString(2) + "')";
						Statement stmtNull = connNull.createStatement();
						stmtNull.execute(queryNull);
					}
					System.out.println(e.getStackTrace());
					System.out.println("Exception has been caught - there is a null value in your stock list" + e.getCause() + " ");
				}
			}
			*/
			
		    } catch (SQLException e) {
		    	System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
				System.out.println(e.getLocalizedMessage());
				System.out.println(e.getStackTrace());
				System.out.println(e.getCause());
				System.out.println(e.getClass());
			}
	
		
		model.addAttribute("stockData", stockData);
		
	 //--------------------------------------------------------------------------------------------------//
			
			
		ArrayList<userStockData>  userStockData = new ArrayList<>();
		String currentPrice = "";
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Connection conn = DriverManager.getConnection(con, user, pass);
			String query = "SELECT * FROM userstockdata WHERE email LIKE '"+ auth.getName() + "'";
			model.addAttribute("username", auth.getName());
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				Stock stockRequest = null;
				stockRequest = YahooFinance.get(rs.getString(4));
				
				if(stockRequest == null) {
					System.out.println("This value is null in the userstockdata table: " + rs.getString(4));
					//unsupportedStocks.add(rs.getString(4));
					userStockData stock = new userStockData(rs.getString(3),rs.getString(4),rs.getString(5), "Unsupported", rs.getString(6), rs.getString(7));
					userStockData.add(stock);
				}else {
					/*  This this is the version you need to do in the instance that the stock market quotes are not available:
					 *  - StockQuote price = stockRequest.getQuote(false);
					 *  //BigDecimal d = st.getQuote().getPrice();
					 */
					try {
						BigDecimal price = stockRequest.getQuote().getPrice();
						currentPrice = price+"";
						userStockData stock = new userStockData(rs.getString(3),rs.getString(4),rs.getString(5), currentPrice, rs.getString(6), rs.getString(7));
						userStockData.add(stock);
					}catch (Exception e) { 
						//throw new NullPointerException();
						System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
					}
				}
				
			}
			
			
			//--------------------------------------------------------------------------------------------------//
			
			//Update Total Profit and Loss
			ArrayList<String> stockTickers = new ArrayList<String>();
			double quantity = 0;
			double stockprice =0 ;
			Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();
			Connection conn1 = DriverManager.getConnection(con, user, pass);
			String query1 = "SELECT * FROM usertransactions WHERE email LIKE '"+ auth1.getName() + "'";
			Statement stmt1 = conn1.createStatement();
			ResultSet rs1 = stmt1.executeQuery(query1);
			
			//create list of tickers user holds
			while (rs1.next()) {
				stockTickers.add(rs1.getString(3));
			}
		
			Set<String> set = new HashSet<String>();
				for(String t: stockTickers) {
		            set.add(t);
		     }	
			double avegPrice = 0;
			
			for(String n : set) {
				double t = 0;
				//Filter out specific stocks and calculate an average
				stockprice = 0;
				quantity = 0;
				Connection conn2 = DriverManager.getConnection(con, user, pass);
				String query2 = "SELECT * FROM usertransactions WHERE ticker LIKE '"+ n + "' AND email LIKE '"+ auth1.getName() + "'";
				Statement stmt2 = conn2.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while(rs2.next()) {
					quantity = Double.parseDouble(rs2.getString(4));
					String p = rs2.getString(5);
					stockprice = stockprice + Double.parseDouble(p) * quantity;
					t = t+Double.parseDouble(rs2.getString(4));
				}
				DecimalFormat df = new DecimalFormat("#.##");
				avegPrice = stockprice/t;
				//Update profit & loss result
				Stock stockRequestResult = null;
				stockRequestResult = YahooFinance.get(n);
				try {
					BigDecimal cp = stockRequestResult.getQuote().getPrice();
					System.out.println("Details printed on table   -    Name of Stock: " + n + ", Current Price: " + cp + " Avg price: " + avegPrice);
					double result =  cp.doubleValue()- avegPrice;
					double marketvalue = cp.doubleValue() * t;
					result= result * t;
					Connection conn4 = DriverManager.getConnection(con, user, pass);
					String query4 = "UPDATE userstockdata SET result = '" + df.format(result) + "' WHERE email LIKE '"+ auth1.getName() + "' AND `userstockdata`.`Ticker Symbol` LIKE '" + n + "'";
					Statement stmt4 = conn4.createStatement();
					stmt4.execute(query4);
					
					totalProfit.put(n, String.valueOf(result));
					totalAmountInvested.put(n, String.valueOf(stockprice));
					totalMarketValue.put(n, String.valueOf(marketvalue));
				}catch(NullPointerException e) {
					System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
					System.out.println("There is a bad stock in the UserTransactionTable causing a Nullpointer Exception you need to get rid of it" );
					System.out.println(e.getStackTrace());
				}
				
			}
			
			total =0;
			for(String value: totalAmountInvested.values()) {
				total = total + Double.valueOf(value);
			}
			System.out.println("<---------------------------------------------------------------------------->");
			System.out.println("Total Value Invested: " + total);
			
			profitLoss = 0;
			for(String value: totalProfit.values()) {
				profitLoss = profitLoss + Double.valueOf(value);
			}
			System.out.println("Total Profit Loss: " + profitLoss);
			
			mv = 0;
			for(String value: totalMarketValue.values()) {
				mv = mv + Double.valueOf(value);
			}
			System.out.println("Total MarketValue: " + mv);
			
			//update usertotals table
			DecimalFormat df = new DecimalFormat("#.##");
			Connection conn4 = DriverManager.getConnection(con, user, pass);
			String query4 = "UPDATE usertotals SET totalInvested = '" + df.format(total) + "', marketValue = '" + df.format(mv) + "', profitnloss = '" + df.format(profitLoss) + "' WHERE email LIKE '"+ auth1.getName() + "'";
			Statement stmt4 = conn4.createStatement();
			stmt4.execute(query4);
			
			//update portfolio value table
			LocalDate localDate = LocalDate.now();
			Connection conn5 = DriverManager.getConnection(con, user, pass);
			String query5 = "SELECT * FROM portfoliovalue WHERE email LIKE '" + auth.getName() + "' AND date LIKE '" + localDate + "'";
			Statement stmt5 = conn5.createStatement();
			ResultSet rscheck = stmt5.executeQuery(query5);
			if(!rscheck.next()) {
				Connection conn6 = DriverManager.getConnection(con, user, pass);
				String query6 = "INSERT INTO portfoliovalue (marketvalue, email, date) VALUES ('" + mv + "','" + auth.getName() + "','" + localDate + "') ";
				Statement stmt6 = conn6.createStatement();
				stmt6.execute(query6);
				
				Connection conn7 = DriverManager.getConnection(con, user, pass);
			    String query7 = "SELECT * FROM portfoliovalue WHERE email LIKE '" + auth.getName() + "' ORDER BY `date`";
				Statement stmt7 = conn7.createStatement();
				ResultSet rs2 = stmt7.executeQuery(query7);
				while(rs2.next()) {
				  chartData.put(rs2.getString(4), rs2.getString(2));
				  //print out dates in chart
				  //System.out.println(rs2.getString(4));
				}
			    System.out.println("Chart data has been updated for first time today.");
				
			}else {
				Connection conn6 = DriverManager.getConnection(con, user, pass);
				String query6 = "UPDATE portfoliovalue SET marketvalue = '" + mv + "',  email = '" + auth.getName() + "',  date = '" + localDate + "' WHERE email LIKE '" + auth.getName() + "' AND date LIKE '" + localDate + "'";
				Statement stmt6 = conn6.createStatement();
				stmt6.execute(query6);
				
				Connection conn7 = DriverManager.getConnection(con, user, pass);
			    String query7 = "SELECT * FROM portfoliovalue WHERE email LIKE '" + auth.getName() + "' ORDER BY `date`";
				Statement stmt7 = conn7.createStatement();
				ResultSet rs2 = stmt7.executeQuery(query7);
				while(rs2.next()) {
				  chartData.put(rs2.getString(4), rs2.getString(2));
				  //print out details in chart
				  //System.out.println(rs2.getString(4));
				}
			    System.out.println("Chart data has been updated.");
			}
			
			
		} catch (SQLException | IOException e) {
			System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
			System.out.println("Exception info: " + e.getLocalizedMessage() + " " + e.getStackTrace());
		}
		DecimalFormat format = new DecimalFormat("####0.00");
		//Add data to model to display on screen
		model.addAttribute("userStockData", userStockData);
		model.addAttribute("stockPrice", currentPrice);
		model.addAttribute("totalInvested", format.format(total));
		model.addAttribute("profitLoss", format.format(profitLoss));
		model.addAttribute("marketValue", format.format(mv));
		model.addAttribute("data", chartData);
		 
		//Alert the user if they are holding any discontinued stocks
		boolean isHoldingDiscontinued = false;
		try {
			Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();
			Connection conn = DriverManager.getConnection(con, user, pass);
			String query = "Select * FROM discontinuedstocks INNER JOIN userstockdata ON discontinuedstocks.INSTRUMENT = userstockdata.`Ticker Symbol`"
					+ " WHERE email LIKE '" + auth1.getName() + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			ArrayList<StockData> discontinuedStocks = new ArrayList<StockData>();
			if(rs.isBeforeFirst()) {
			while(rs.next()) {
				StockData discontinuedStock = new StockData(rs.getString(2), rs.getString(4), rs.getString(3));
				discontinuedStocks.add(discontinuedStock);
				System.out.println(discontinuedStock.companyName + " - Adding discontinued stock for logging purposes.");
				isHoldingDiscontinued = true;
			}}
			
			//ADD SELECT STATEMENT TO POINT TO CHECK IF THE USER CURRENTLY HOLDS ANY DISCONTINUED STOCKS
			if (!discontinuedStocks.isEmpty()) {
				model.addAttribute("discontinuedStocks", discontinuedStocks);
				model.addAttribute("isHoldingDiscontinued", "" + isHoldingDiscontinued);
				return "index";
			} else {
				return "index";
			}
		} catch (Exception e) {
			System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
			System.out.println("Exception info: " + e.getLocalizedMessage() + "/n " + e.getStackTrace());
			return "redirect:/";
		}
	
		
	}
	
	@PostMapping("/")
	public String addStock(@RequestParam("stockInfo") String results, RedirectAttributes redirectAttrs, Model model) throws SQLException, IOException {
		String[] data = results.split(",");
		String name = data[0];
		String noShares = data[1];
		String price = data[2];
		String date = data[3];
		
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Connection conn = DriverManager.getConnection(con, user, pass);
			Connection conn2 = DriverManager.getConnection(con, user, pass);
			Connection conn3 = DriverManager.getConnection(con, user, pass);
			String query = "INSERT INTO usertransactions (`email`,`ticker`,`quantity`, `price`,`date`) VALUES("
					+ "'"+ auth.getName() + "',"+ "'"+ name + "',"+ "'"+ noShares + "',"+ "'"+ price + "',"+ "'"+ date + "')";
			Statement stmt = conn.createStatement();
			stmt.execute(query);
			
			//update userstockdata table with additional positions
			int count = 0;
			String query2 = "SELECT * FROM userstockdata WHERE email LIKE '"+ auth.getName() + "' AND `userstockdata`.`Ticker Symbol` LIKE '" + name + "'";
			Statement stmttwo = conn2.createStatement();
			ResultSet rs = stmttwo.executeQuery(query2);
			while (rs.next()) {
				String quantity = rs.getString(5);
				int q = Integer.parseInt(quantity);
				int n = Integer.parseInt(noShares);
				int quant = q+n;
				String updatequery = "UPDATE userstockdata SET quantity = '" + quant + "' WHERE email LIKE '"+ auth.getName() + "' AND `userstockdata`.`Ticker Symbol` LIKE '" + name + "'";
				Statement stmtthree = conn3.createStatement();
				stmtthree.execute(updatequery);
				count++;
			}
			if(count<1) {
				//result set is empty, meaning this is the first transaction we made with this stock, then insert our new position
				Stock stockRequest = YahooFinance.get(name);
				String instr = stockRequest.getName();
				String insertquery = "INSERT INTO userstockdata (`email`,`instrumentName`,`Ticker Symbol`, `quantity`,`avgPrice`) VALUES(" + 
						"'"+ auth.getName() + "',"+ "'"+ instr + "',"+ "'"+ name + "',"+ "'"+ noShares + "',"+ "'"+ 0 + "')";
				stmt.execute(insertquery);    }
			}catch(Exception e) {
				System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
				System.out.println("Something went wrong: " + e.getLocalizedMessage());
			}
		
		//Calculate average stock price 
		try {
			ArrayList<String> stockTickers = new ArrayList<String>();
			Map<String, String> totalAmountInvested = new HashMap<String, String>();
			Map<String, String> totalProfit = new HashMap<String, String>();
			Map<String, String> totalMarketValue = new HashMap<String, String>();
			double quantity = 0;
			double stockprice =0 ;
			Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();
			Connection conn1 = DriverManager.getConnection(con, user, pass);
			String query1 = "SELECT * FROM usertransactions WHERE email LIKE '"+ auth1.getName() + "'";
			Statement stmt1 = conn1.createStatement();
			ResultSet rs1 = stmt1.executeQuery(query1);
			
			//create list of tickers user holds
			while (rs1.next()) {
				stockTickers.add(rs1.getString(3));
			}
		
			Set<String> set = new HashSet<String>();
				for(String t: stockTickers) {
		            set.add(t);
		     }	
			double avgPrice = 0;
			
			for(String n : set) {
				double t = 0;
				//Filter out specific stocks and calculate an average
				stockprice = 0;
				quantity = 0;
				Connection conn2 = DriverManager.getConnection(con, user, pass);
				String query2 = "SELECT * FROM usertransactions WHERE ticker LIKE '"+ n + "' AND email LIKE '"+ auth1.getName() + "'";
				Statement stmt2 = conn2.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while(rs2.next()) {
					quantity = Double.parseDouble(rs2.getString(4));
					String p = rs2.getString(5);
					stockprice = stockprice + Double.parseDouble(p) * quantity;
					t = t+Double.parseDouble(rs2.getString(4));
				}
				//Update average price for this position
				avgPrice = stockprice/t;
				DecimalFormat df = new DecimalFormat("#.##");
				Connection conn3 = DriverManager.getConnection(con, user, pass);
				String query3 = "UPDATE userstockdata SET avgPrice = '" + df.format(avgPrice) + "' WHERE email LIKE '"+ auth1.getName() + "' AND `userstockdata`.`Ticker Symbol` LIKE '" + n + "'";
				Statement stmt3 = conn3.createStatement();
				stmt3.execute(query3);
				
				//Update profit & loss result
				Stock stockRequestResult = null;
				stockRequestResult = YahooFinance.get(n);
				BigDecimal cp = stockRequestResult.getQuote().getPrice();
				System.out.println("Name of Stock: " + n + ", Current Price: " + cp);
				double result =  cp.doubleValue()- avgPrice;
				result= result * t;
				double marketvalue = cp.doubleValue() * t;
				Connection conn4 = DriverManager.getConnection(con, user, pass);
				String query4 = "UPDATE userstockdata SET result = '" + df.format(result) + "', amountInvested = '" + stockprice + "' WHERE email LIKE '"+ auth1.getName() + "' AND `userstockdata`.`Ticker Symbol` LIKE '" + n + "'";
				Statement stmt4 = conn4.createStatement();
				stmt4.execute(query4);
				
				totalProfit.put(n, String.valueOf(result));
				totalAmountInvested.put(n, String.valueOf(stockprice));
				totalMarketValue.put(n, String.valueOf(marketvalue));
				
			}
			System.out.println("<---------------------------------------------------------------------------->");
			System.out.println("Combined Amount invested per stock: ");
			//Calculate Portfolio total value and total Profit/Loss
			totalAmountInvested.forEach((k,v) -> {
			    System.out.println(k + "[" + v + "]");
			});
			
			System.out.println("<---------------------------------------------------------------------------->");
			System.out.println("Total Profit per stock: ");
			totalProfit.forEach((k,v) -> {
			    System.out.println(k + "[" + v + "]");
			});
			
			System.out.println("<---------------------------------------------------------------------------->");
			System.out.println("Total Market Value per stock: ");
			totalMarketValue.forEach((k,v) -> {
			    System.out.println(k + "[" + v + "]");
			});
			double total =0;
			for(String value: totalAmountInvested.values()) {
				total = total + Double.valueOf(value);
			}
			System.out.println("<---------------------------------------------------------------------------->");
			System.out.println("Total Value Invested: " + total);
			
			double profitloss = 0;
			for(String value: totalProfit.values()) {
				profitloss = profitloss + Double.valueOf(value);
			}
			System.out.println("Total Profit Loss: " + profitloss);
			
			double mv = 0;
			for(String value: totalMarketValue.values()) {
				mv = mv + Double.valueOf(value);
			}
			System.out.println("Total MarketValue: " + mv);
			
			Connection conn4 = DriverManager.getConnection(con, user, pass);
			String query4 = "UPDATE usertotals SET totalInvested = '" + total + "', marketValue= '" + mv +  "', profitnloss= '" + profitloss + "' WHERE email LIKE '"+ auth1.getName() + "'";
			Statement stmt4 = conn4.createStatement();
			stmt4.execute(query4);
			
			
		    
			}catch(Exception e) {
				System.out.println("Something bad happened around line " + e.getStackTrace()[0].getLineNumber());
				System.out.println("Something was caught: " + e.getLocalizedMessage());
			}
		
		
		return "redirect:/";
	}
	
	@PostMapping("/reports")
	public String removeStock(@RequestParam("removeStockInfo") String results, RedirectAttributes redirectAttrs, Model model) {
		System.out.println("Remove stock results details: " + results);
		return "redirect:/";
	}
	
	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    
    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
    
  
    
	/*
	 * @GetMapping("/reports") public String showDashboard(Model model) {
	 * Map<String, String> data = new LinkedHashMap<String, String>();
	 * 
	 * try { Authentication auth =
	 * SecurityContextHolder.getContext().getAuthentication(); Connection conn5 =
	 * DriverManager.getConnection(con, user, pass); String query5 =
	 * "SELECT * FROM portfoliovalue WHERE email LIKE '" + auth.getName() +
	 * "' ORDER BY `date`"; Statement stmt5 = conn5.createStatement(); ResultSet rs
	 * = stmt5.executeQuery(query5); while(rs.next()) { data.put(rs.getString(4),
	 * rs.getString(2)); System.out.println("STUFF WAS EXECUTED"); }
	 * 
	 * }catch(Exception e) { System.out.println("Exception details: " +
	 * e.getLocalizedMessage()); System.out.println("More Info: " + e.getMessage());
	 * }
	 * 
	 * model.addAttribute("data", data); return "reports"; }
	 * 
	 */
    
    
   	
}
