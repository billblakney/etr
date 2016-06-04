package etr;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.market.AllQuote;
import com.etrade.etws.market.DetailFlag;
import com.etrade.etws.market.FundamentalQuote;
import com.etrade.etws.market.QuoteData;
import com.etrade.etws.market.QuoteResponse;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.client.MarketClient;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ObtainRequestToken {

	public static void main(String[] args) {
		// Variables
		IOAuthClient client = null;
		ClientRequest request = null;
		Token token = null;
		String oauth_consumer_key = "3e51dedfa098ce133128571699848ee5"; // Your consumer key
		String oauth_consumer_secret = "67b6b71ccb4e46d85b43ed3733033fa8"; // Your consumer secret
		String oauth_request_token = null; // Request token 
		String oauth_request_token_secret = null; // Request token secret
		client = OAuthClientImpl.getInstance(); // Instantiate IOAUthClient
		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment

		request.setConsumerKey(oauth_consumer_key); //Set consumer key
		request.setConsumerSecret(oauth_consumer_secret); // Set consumer secret
		try
		{
			token= client.getRequestToken(request); // Get request-token object
			oauth_request_token  = token.getToken(); // Get token string
			oauth_request_token_secret = token.getSecret(); // Get token secret
			System.out.println("token: " + oauth_request_token);
			System.out.println("token secret: " + oauth_request_token_secret);
			
			request.setToken(oauth_request_token);
			request.setTokenSecret(oauth_request_token_secret);

			String authorizeURL = null;
			authorizeURL = client.getAuthorizeUrl(request); // E*TRADE authorization URL
			URI uri = new java.net.URI(authorizeURL);
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(uri);
		}
		catch (Exception ex)
		{
			System.out.println("Exception on getRequestToken: " + ex);
		}
		
		Scanner scan = new Scanner(System.in);
		String myVerificationCode = scan.next();

		String oauth_access_token = null; // Variable to store access token 
		String oauth_access_token_secret = null; // Variable to store access token secret
		String oauth_verify_code = myVerificationCode; // Should contain the Verification Code received from the authorization step

		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment
		// Prepare request
		request.setConsumerKey(oauth_consumer_key); // Set consumer key
		request.setConsumerSecret(oauth_consumer_secret); // Set consumer secret
		request.setToken(oauth_request_token); // Set request token
		request.setTokenSecret(oauth_request_token_secret); // Set request-token secret
		request.setVerifierCode(oauth_verify_code); // Set verification code

		try
		{
			// Get access token
			token = client.getAccessToken(request); // Get access-token object
			oauth_access_token = token.getToken(); // Access token string
			oauth_access_token_secret = token.getSecret(); // Access token secret
		}
		catch (Exception ex)
		{
			System.out.println("Exception on getAccessToken: " + ex);
			
		}
		
		request = new ClientRequest(); // Instantiate ClientRequest
		// Prepare request
		request.setEnv(Environment.SANDBOX);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		request.setToken(oauth_access_token);
		request.setTokenSecret(oauth_access_token_secret);

		try
		{
		  AccountsClient account_client = new AccountsClient(request);
		  AccountListResponse response = account_client.getAccountList();
		  List<Account> alist = response.getResponse();
		  Iterator<Account> al = alist.iterator();
		  while (al.hasNext()) {
		    Account a = al.next();
		    System.out.println("===================");
		    System.out.println("Account: " + a.getAccountId());
		    System.out.println("===================");
		  }
		}
		catch (Exception e)
		{
		}
//		ClientRequest request = new ClientRequest();
		request.setEnv(Environment.SANDBOX);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		request.setToken(oauth_access_token);
		request.setTokenSecret(oauth_access_token_secret);
		
		ArrayList<String> list = new ArrayList<String>();
		MarketClient marketClient = new MarketClient(request);
		list.add("CSCO");
		list.add("AAPL"); 
		try
		{
			QuoteResponse response = marketClient.getQuote(list, false/*new Boolean(afterHourFlag)*/, DetailFlag.ALL);
//			System.out.println("getQuote response: " + response.toString());
			List<QuoteData> quoteDatas = response.getQuoteData();
			if (quoteDatas == null)
			{
				System.out.println("quoteDatas null");
			}
			for (QuoteData data: quoteDatas)
			{
				AllQuote quoteData = data.getAll();
				System.out.println(quoteData.getSymbolDesc() + ":" + quoteData.getLastTrade());
//				FundamentalQuote quote = data.getFundamental();
//				if (quote == null)
//				{
//					System.out.println("quote null");
//				}
//				else
//				{
//					System.out.println(quote.getCompanyName());
//					System.out.println(quote.getCompanyName() + ":" + quote.getLastTrade());
//				}
			}
		}
		catch (Exception ex)
		{
			System.out.println("Exception on getRequestToken: " + ex);
		}
	}

}
