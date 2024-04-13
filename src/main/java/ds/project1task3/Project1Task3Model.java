/**
 * Author: Tongren Chen
 * Andrew ID: tongrenc
 * Date: 02/08/2024
 *
 * The {@code Project1Task3Model} class provides methods to fetch and parse various pieces of
 * information related to U.S. states from different web sources, including state populations
 * from the Census API, and details like flag URLs, seal URLs, state capitals, and governors
 * from Wikipedia pages.
 *
 * It utilizes the Gson library for parsing JSON responses and Jsoup for parsing HTML content.
 * The class also implements methods to bypass SSL certificate validation for HTTPS connections,
 * which should be used with caution and only in trusted environments.
 */

package ds.project1task3;

// import gson, jsoup, and other necessary classes
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class Project1Task3Model {
    /**
     * Retrieves the population of a specified state using the Census API.
     *
     * @param state The name of the state to query.
     * @return The population of the state as a string, or null if the state is not found.
     * @throws UnsupportedEncodingException If the state name encoding is not supported.
     */
    public String getStatePopulation(String state)
            throws UnsupportedEncodingException {
        // Fetch the population data from the Census API
        String response = "";

        // Construct the API URL
        String url = "https://api.census.gov/data/2020/dec/pl?get=NAME,P1_001N&for=state:*";
        // Fetch the API response
        response = fetch(url);
        Gson gson = new Gson();
        // Parse the JSON response into a list of lists
        List<List<String>> statePopulations =
                gson.fromJson(response, new TypeToken<List<List<String>>>(){}.getType());
        // Iterate through the list to find the matching state and return its population
        for (List<String> statePopulation : statePopulations) {
            if (statePopulation.get(0).equals(state)) {
                return statePopulation.get(1);
            }
        }
        // Return null if the state is not found
        return null;
    }

    /**
     * Fetches the HTML content from a specified Wikipedia URL.
     *
     * @param url The Wikipedia URL to fetch content from.
     * @return The HTML content as a string.
     */
    public String getWikipediaResponse(String url) {
        // Fetch the URL content using TLSv1.3
        return fetch(url, "TLSv1.3");
    }

    /**
     * Extracts the flag URL from the HTML content of a Wikipedia page.
     *
     * @param response The HTML content of a Wikipedia page.
     * @return The URL of the flag image.
     */
    public String getFlagURL(String response) {
        // HTML snippet to locate the flag image URL
        String s = "<div class=\"ib-settlement-cols-cell\"><span class=\"mw-image-border\" typeof=\"mw:File\"><a href=\"/wiki/File:Flag_of_";
        if (!response.contains(s)) {
            // Alternate HTML snippet if the first one is not found
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:Flag_of_";
        }
        // Extract and return the image URL
        return getImageURL(response, s);
    }

    /**
     * Extracts the credit information for the flag image from the Wikimedia Commons page.
     *
     * @param state The name of the state to query.
     * @return The credit information for the flag image.
     */
    public String getFlagCredit(String state) {
        // Check if the state parameter is "Georgia_(U.S._state)" and adjust the name to match the Wikimedia Commons page title
        if (state.equals("Georgia_(U.S._state)")) {
            state = "the_State_of_Georgia";
        }

        // Construct the URL for the Wikimedia Commons page of the state's flag using the state name
        String url = "https://commons.wikimedia.org/wiki/File:Flag_of_" + state + ".svg";

        try {
            // Use Jsoup to fetch and parse the HTML content of the Wikimedia Commons page at the constructed URL
            Document doc = Jsoup.connect(url).get();

            // Use a CSS selector to find the first element that matches the given path in the HTML document,
            // which is expected to contain the credit information for the flag image
            Element credit = doc.select("#mw-imagepage-section-filehistory > table > tbody > tr:nth-child(2) > td:nth-child(5) > a > bdi").first();

            // Check if the credit element is found; if so, return its text content, which should be the credit information
            if (credit != null) {
                return credit.text();
            }
        } catch (IOException e) {
            // Print the stack trace of the exception for debugging purposes
            e.printStackTrace();
        }

        // Return "N.A." if the credit information could not be retrieved (either because the element was not found or due to an exception)
        return "N.A.";
    }


    /**
     * Extracts the seal URL from the HTML content of a Wikipedia page.
     *
     * @param response The HTML content of a Wikipedia page.
     * @return The URL of the seal image.
     */
    public String getSealURL(String response) {
        // Initialize the search pattern for the general format of seal image URLs in the HTML response
        String s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:Seal_of_";

        // Check if the initial pattern is not found in the response and update the pattern to match "State Seal of"
        if (!response.contains(s)) {
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:State_Seal_of_";
        }
        // If the updated pattern is still not found, modify the search pattern to match "State_seal_of"
        if (!response.contains(s)) {
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:State_seal_of_";
        }
        // If the previous pattern is not found, update the search pattern to look for "Great Seal of"
        if (!response.contains(s)) {
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:Great_Seal_of_";
        }

        // If none of the above patterns are found, specifically look for the Arizona state seal image URL
        if (!response.contains(s)) {
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:Arizona_state_seal.svg\"";
        }

        // If the Arizona seal pattern is not found, finally look for the Wyoming state seal image URL
        if (!response.contains(s)) {
            s = "<div class=\"ib-settlement-cols-cell\"><span typeof=\"mw:File\"><a href=\"/wiki/File:Wyoming-StateSeal.svg";
        }

        // Use the final search pattern to extract the seal image URL from the HTML response
        return getImageURL(response, s);
    }


    /**
     * Extracts the credit information for the seal image from the Wikimedia Commons page.
     *
     * @param state The name of the state to query.
     * @return The credit information for the seal image.
     */
    public String getSealCredit(String state) {
        // Check if the state is Georgia, adjust the state name accordingly
        if (state.equals("Georgia_(U.S._state)")) {
            state = "the_State_of_Georgia";
        }
        // Construct the URL for the Wikimedia Commons page of the state seal
        String url = "https://commons.wikimedia.org/wiki/File:Seal_of_" + state + ".svg";
        try {
            // Connect to the URL and retrieve the document using Jsoup
            Document doc = Jsoup.connect(url).get();
            // Select the credit element from the Wikimedia Commons page
            Element credit = doc.select("#mw-imagepage-section-filehistory > table > tbody > tr:nth-child(2) > td:nth-child(5) > a > bdi").first();
            // If credit information is found, return it
            if (credit != null) {
                return credit.text();
            }
        } catch (IOException e) {
            // Print the stack trace if an IOException occurs during the connection
            e.printStackTrace();
        }
        // If credit information is not found or an exception occurs, return "N.A."
        return "N.A.";
    }


    /**
     * Extracts the capital city from the HTML content of a Wikipedia page.
     *
     * @param url The Wikipedia URL to fetch content from.
     * @return The name of the capital city.
     */
    public String getCapital(String url) {
        // Attempt to connect to the provided URL and retrieve the document using Jsoup
        try {
            Document doc = Jsoup.connect(url).get();
            // Select the element representing the capital from the Wikipedia infobox
            Element capital = doc.select("#mw-content-text > div.mw-content-ltr.mw-parser-output > table.infobox.ib-settlement.vcard > tbody > tr:nth-child(12) > td > a").first();
            // If the capital element is found, return its text
            if (capital != null) {
                return capital.text();
            }
        } catch (IOException e) {
            // Print the stack trace if an IOException occurs during the connection
            e.printStackTrace();
        }
        // If the capital element is not found or an exception occurs, return null
        return null;
    }


    /**
     * Extracts the governor from the HTML content of a Wikipedia page.
     *
     * @param url The Wikipedia URL to fetch content from.
     * @return The name of the governor.
     */
    public String getGovernor(String url) {
        // Attempt to connect to the provided URL and retrieve the document using Jsoup
        try {
            Document doc = Jsoup.connect(url).get();
            // Select the element representing the governor from the Wikipedia infobox
            Element governor = doc.select("#mw-content-text > div.mw-content-ltr.mw-parser-output > table.infobox.ib-settlement.vcard > tbody > tr:nth-child(17) > td > span > a:nth-child(1)").first();
            // If the governor element is not found in the specified position, attempt to find it in another position
            if (governor == null) {
                governor = doc.select("#mw-content-text > div.mw-content-ltr.mw-parser-output > table.infobox.ib-settlement.vcard > tbody > tr:nth-child(17) > td > a:nth-child(1)").first();
            }
            // If the governor element is found, return its text
            if (governor != null) {
                return governor.text();
            }
        } catch (IOException e) {
            // Print the stack trace if an IOException occurs during the connection
            e.printStackTrace();
        }
        // If the governor element is not found or an exception occurs, return null
        return null;
    }


    /**
     * Helper method to extract an image URL from HTML content based on a starting snippet.
     *
     * @param response The HTML content to search through.
     * @param s The starting snippet to locate the image URL.
     * @return The extracted image URL.
     */
    private String getImageURL(String response, String s) {
        // Find the start index of the image URL
        int cutLeft = response.indexOf(s) + s.length();
        // The attribute that contains the URL
        s = "src=\"";
        cutLeft = response.indexOf(s, cutLeft) + s.length();
        // Find the end index of the image URL
        int cutRight = response.indexOf("\"", cutLeft);
        // Extract and return the image URL
        return response.substring(cutLeft, cutRight);
    }

    /**
     * Fetches content from a given URL.
     *
     * @param urlString The URL to fetch content from.
     * @return The content as a string.
     */
    private String fetch(String urlString) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read and append each line of the response
            while ((str = in.readLine()) != null) {
                response.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
            // Consider better exception handling or re-throwing
        }
        return response.toString();
    }

    // Method cite from class example https://github.com/CMU-Heinz-95702/Project-1
    private String fetch(String searchURL, String certType) {
        try {
            // Create trust manager, which lets you ignore SSLHandshakeExceptions
            createTrustManager(certType);
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            System.out.println("Shouldn't come here: ");
            ex.printStackTrace();
        }

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(searchURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Something wrong with URL");
            return null;
        }
        return response.toString();
    }

    // Method cite from class example https://github.com/CMU-Heinz-95702/Project-1
    private void createTrustManager(String certType) throws KeyManagementException, NoSuchAlgorithmException{
        /**
         * Annoying SSLHandShakeException. After trying several methods, finally this
         * seemed to work.
         * Taken from: http://www.nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/
         */
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance(certType);
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
