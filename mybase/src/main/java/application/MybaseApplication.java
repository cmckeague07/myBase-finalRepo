package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@SpringBootApplication
public class MybaseApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(MybaseApplication.class, args);
	}

}
