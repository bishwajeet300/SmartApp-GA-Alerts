package com.til.smartapp.galert.google.analytics;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.RealtimeData;
import com.google.api.services.analytics.model.Webproperties;
import com.til.smartapp.galert.constants.SmartAppConstants;

@Repository
public class SmartAppAnalytics {

	private static final Logger logger = Logger.getLogger(SmartAppAnalytics.class);

	/**
	 * Be sure to specify the name of your application. If the application name
	 * is {@code null} or blank, the application will log a warning. Suggested
	 * format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "SmartApp";
	
	private static String PROFILE_ID;
	
	/** Directory to store user credentials. */
	  private static final java.io.File DATA_STORE_DIR =
	      new java.io.File(System.getProperty("user.home"), ".store/analytics_samples");
	  
	  /**
	   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	   * globally shared instance across your application.
	   */
	  private static FileDataStoreFactory dataStoreFactory;

	  /** Global instance of the HTTP transport. */
	  private static HttpTransport httpTransport;

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	static Analytics analytics;

	public SmartAppAnalytics() {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			analytics = initializeAnalytics();
			PROFILE_ID = getFirstProfileId(analytics);
		    if (PROFILE_ID == null) {
		      System.err.println("No profiles found.");
		    }
		} catch (GoogleJsonResponseException e) {
		      System.err.println("There was a com.til.smartapp.galert.service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (Throwable t) {
		      t.printStackTrace();
		}
	}

	/**
	 * Performs all necessary setup steps for running requests against the API.
	 * 
	 * @return An initialized Analytics com.til.smartapp.galert.service object.
	 * 
	 * @throws Exception
	 *             if an issue occurs with OAuth2Native authorize.
	 */
	private static Analytics initializeAnalytics() throws Exception {

		logger.debug("initializeAnalytics()");
		logger.debug("initializeAnalytics : Call this.authorize() which returns Credential instance");
		Credential credential = authorize(); // Authorization.
		logger.debug("initializeAnalytics : Return Value : new Analytics instance");
		return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
		        APPLICATION_NAME).build();
	}

	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {

		logger.debug("authorize()");
		// load client secrets
		logger.debug(
				"authorize : Call GoogleClientSecrets.load(JSON_FACTORY, SmartAppAnalytics.class.getResourceAsStream(//client_secrets.json)");

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(SmartAppAnalytics.class.getResourceAsStream("/client_secret.json")));
		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			logger.debug("Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
					+ "into analytics-cmdline-RegexMatches/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		logger.debug("authorize : Create new FileCredentialStore");
		
		// set up authorization code flow
		logger.debug("authorize : Create new GoogleAuthorizationCodeFlow");
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		        httpTransport, JSON_FACTORY, clientSecrets,
		        Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setDataStoreFactory(
		        dataStoreFactory).build();
		// authorize
		logger.debug("authorize : Return Value : new Credential instance");
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * Returns the first profile id by traversing the Google Analytics
	 * Management API. This makes 3 queries, first to the accounts collection,
	 * then to the web properties collection, and finally to the profiles
	 * collection. In each request the first ID of the first entity is retrieved
	 * and used in the query for the next collection in the hierarchy.
	 * 
	 * @param analytics
	 *            the analytics com.til.smartapp.galert.service object used to access the API.
	 * @return the profile ID of the user's first account, web property, and
	 *         profile.
	 * @throws IOException
	 *             if the API encounters an error.
	 */
	private static String getFirstProfileId(Analytics analytics) throws IOException {

		logger.debug("getFirstProfileId : Input Value(Analytics analytics) : " + analytics);
		String profileId = null;

		logger.debug("getFirstProfileId : Query accounts collection using input object 'analytics'");
		// Query accounts collection.
		Accounts accounts = analytics.management().accounts().list().execute();

		if (accounts.getItems().isEmpty()) {
			logger.error("getFirstProfileId : No accounts found");
			System.err.println("No accounts found");
		} else {
			logger.debug("getFirstProfileId : If accounts are found then, query webproperties collection using input object 'analytics'");
			// Query webproperties collection.
			Webproperties webproperties = analytics.management().webproperties()
					.list(SmartAppConstants.SMARTAPP_GA_ACCOUNT_ID).execute();
			if (webproperties.getItems().isEmpty()) {
				logger.error("getFirstProfileId : No Webproperties found");
				System.err.println("No Webproperties found");
			} else {
				String firstWebpropertyId = webproperties.getItems().get(0).getId();
				logger.debug(
						"getFirstProfileId : If Webproperties are found then, query profiles collection using input object 'analytics'");
				// Query profiles collection.
				Profiles profiles = analytics.management().profiles()
						.list(SmartAppConstants.SMARTAPP_GA_ACCOUNT_ID, firstWebpropertyId).execute();
				if (profiles.getItems().isEmpty()) {
					logger.error("getFirstProfileId : No profiles found");
					System.err.println("No profiles found");
				} else {
					profileId = profiles.getItems().get(3).getId();
				}
			}
		}

		logger.debug("getFirstProfileId : Return Value : " + profileId);
		return profileId;
	}

	public RealtimeData getRealTimeUserCount() {
		RealtimeData realtimeData = null;
		try {
			logger.debug("getRealTimeUserCount : Input Value : Analytics : " + analytics + ", profileId : " + PROFILE_ID);
			logger.debug("getRealTimeUserCount : Return Value : Integer");
			if (PROFILE_ID == null) {
				logger.debug("getRealTimeUserCount : No profiles found.  ");
				System.err.println("No profiles found.");
				logger.debug("getRealTimeUserCount : Return Values : " + realtimeData);
				return realtimeData;
			} else {
				realtimeData = analytics.data().realtime().get("ga:" + PROFILE_ID, // Table Id. ga: + profile id.
								"rt:activeUsers")	// Metrics.
						.setMetrics("rt:activeUsers").execute();
			
				logger.debug("Total number of active users : "	+ realtimeData.getTotalsForAllResults().get("rt:activeUsers"));
			}
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return realtimeData;
	}
}
