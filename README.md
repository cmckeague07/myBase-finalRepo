# MyBase
MyBase is a Stock portfolio application that allows users to manage their stock portfolios. The app offers real-time stock price updates via the Yahoo Finance API, and provides real-time profit and loss updates. Technologies used include Java SpringBoot, HTML, CSS and Javascript FrontEnd, MySQL Database backend and Yahoo Finance API. The YahooFinance API was used as it is a free api that provides realtime access to stock data quotes. I developed this app mostly for for fun and to build my Java knowledge working with SpringBoot & APIs. 

------------------------------------------------------------------------------
Features
------------------------------------------------------------------------------
Portfolio Management: 

- Add stocks to your portfolio with details like ticker, quantity, price, and purchase date.

- View current portfolio performance, including profit/loss and total market value.

- Automatically update stock data based on changes in the market, and provide realtime calculations based on YahooFinance API data. 

Data Cleaning:

- Automatically remove discontinued or invalid stocks from the user's portfolio, with user notification.

- Maintain a separate database for discontinued stocks to prevent future additions.

Reporting:

- View detailed profit and loss calculations for each stock.

- Generate portfolio performance charts based on historical market value data.

Security:

- User authentication and session management via Spring Security.

Tech Stack

- Backend: Java (Spring Boot)

- Frontend: HTML, CSS, Thymeleaf

- Database: MySQL

- APIs: Yahoo Finance API (now deprecated)
------------------------------------------------------------------------------
Database Schema
------------------------------------------------------------------------------
Tables

stockdata:

- Stores all available stock details from supported markets.

usertransactions:

- Tracks user transactions, including stock purchases and sales.

userstockdata:

- Maintains aggregated portfolio details for each user.

discontinuedstocks:

- Tracks invalid or discontinued stocks to prevent re-addition.

usertotals:

- Tracks total investment, market value, and profit/loss for each user.

portfoliovalue:

- Stores historical market value data for portfolio performance tracking.

  
------------------------------------------------------------------------------
⚠️ Yahoo Finance API Notice
Deprecation of Yahoo Finance API

This application was originally designed to utilize the unofficial Yahoo Finance API to fetch stock data. However, the functionality of the Yahoo Finance API has either been deprecated or restricted, and the implementation of the YahooFinance library used in this project is no longer functional for live data retrieval.

Unfortunately the application can no longer fetch or display live stock data.
Historical features that rely on external stock data will not work as intended.

------------------------------------------------------------------------------
Application Screenshots
------------------------------------------------------------------------------
![StockPortfolioApp2](https://user-images.githubusercontent.com/65572743/129645453-ec514dcf-eff5-4f95-a3bd-05fe1c71b280.jpg)
![image](https://github.com/user-attachments/assets/354b9923-92ba-45c4-83a7-30a1872999ed)
![StockPortfolioApp2](https://user-images.githubusercontent.com/65572743/129645594-c18bc1af-440a-48d1-811e-1e90f5710d69.jpg)
![StockPortfolioApp3](https://user-images.githubusercontent.com/65572743/129645467-534903f3-f1a4-4cf7-97f0-277d68a13c86.JPG)



