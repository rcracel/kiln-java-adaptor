package com.nevermindsoft.kiln.internal.publishers;

import com.nevermindsoft.kiln.internal.log.KilnInternalLogger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The KilnPublisher is responsible for pushing collections of LoggingEvent to a preconfigured Kiln server. This class is
 * thread safe once constructed and stores no state other than the configuration specified during construction. Configuration
 * properties specified during construction cannot be modified.
 *
 * User: Roger Cracel
 * Date: 12/20/12
 * Time: 3:56 PM
 */
public class KilnPublisher {

    private KilnInternalLogger logger;

    private String serverUrl;
    private String moduleName;
    private String environmentName;
    private String apiKey;

    /**
     * Constructs a new KilnPublisher that can be used to push event messages to a Kiln server
     *
     * @param serverUrl URL of the remote Kiln server
     * @param apiKey The API key acquired from the Kiln server
     * @param moduleName The name of the module to report to Kiln
     * @param environmentName The name of the environment to report to Kiln
     */
    public KilnPublisher(String serverUrl, String apiKey, String moduleName, String environmentName, KilnInternalLogger logger) {
        this.serverUrl = serverUrl;
        this.moduleName = moduleName;
        this.environmentName = environmentName;
        this.apiKey = apiKey;
        this.logger = logger;
    }

    /**
     * Pushes a collection of LoggingEvent to the remote Kiln server AJAX
     *
     * @param events a list of LoggingEvent to be pushed to the remote repository
     */
    public void pushItems( List<LoggingEvent> events ) {
        HttpURLConnection connection = null;

        try {
            String ajaxRequest = buildAjax( events );
            URL url = new URL( serverUrl );

            // KilnInternalLogger.log( Level.INFO, "Pushing " + parameters);

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty( "Content-Type", "application/json");
            connection.setRequestProperty( "Accept", "application/json");
            connection.setRequestProperty( "Content-Length", String.valueOf( Integer.toString( ajaxRequest.getBytes().length ) ) );
            connection.setRequestProperty( "Content-Language", "en-US");

            //Send request
            DataOutputStream wr = new DataOutputStream ( connection.getOutputStream() );
            wr.writeBytes( ajaxRequest );
            wr.flush ();
            wr.close ();

            //- If the response was anything other than OK, print the response information
            if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK ) {
                logger.log(Level.INFO, "For Request  : " + ajaxRequest);
                logger.log( Level.INFO, "Response code: " + connection.getResponseCode());

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();

                logger.log( Level.INFO, response.toString() );
            }

        } catch ( MalformedURLException e ) {
            logger.log( Level.ERROR, "Could not connect to the server: " + e.getMessage());
        } catch ( ProtocolException e ) {
            logger.log( Level.ERROR, "Could not connect to the server: " + e.getMessage());
        } catch ( IOException e ) {
            logger.log( Level.ERROR, "Could not connect to the server: " + e.getMessage());
        } catch ( Exception e ) {
            logger.log( Level.ERROR, "Could not connect to the server: " + e.getMessage());
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
    }

    /**
     * Convert the collection of LoggingEvent into ajax to be submitted by the post request
     *
     * @param events the collection of LoggingEvent events to use when creating the ajax array
     * @return an ajax string representing the collection of logs
     *
     * @throws java.io.UnsupportedEncodingException
     */
    private String buildAjax( List<LoggingEvent> events ) throws UnsupportedEncodingException {
        List<String> items = new ArrayList<String>();

        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss ZZZ");

        //- Build the query parameters
        for ( LoggingEvent event : events ) {
            List<String> keyValueList = new ArrayList<String>();

            keyValueList.add(String.format("\"module_name\":\"%s\"", escapeJSON(moduleName)));
            keyValueList.add(String.format("\"log_level\":\"%s\"", escapeJSON(event.getLevel().toString())));
            keyValueList.add(String.format("\"message\":\"%s\"", escapeJSON(event.getRenderedMessage())));
            keyValueList.add(String.format("\"timestamp\":\"%s\"", escapeJSON(dateFormatter.format(new Date(event.getTimeStamp())))));
            keyValueList.add(String.format("\"thread_name\":\"%s\"", escapeJSON(event.getThreadName())));
            keyValueList.add(String.format("\"environment_name\":\"%s\"", escapeJSON(environmentName)));

            String stackTrace = StringUtils.join(event.getThrowableStrRep(), "\n");
            if ( StringUtils.isNotBlank( stackTrace ) ) {
                keyValueList.add(String.format("\"stack_trace\":\"%s\"", escapeJSON(stackTrace)));
            }

            if ( event.locationInformationExists() ) {
                keyValueList.add(String.format("\"source\":\"%s\"", event.getLocationInformation().fullInfo));
            }

            items.add( String.format( "{%s}", StringUtils.join( keyValueList, "," ) ) );
        }

        String result = String.format("{ \"api_key\": \"%s\", \"events\": [%s] }", apiKey, StringUtils.join(items.toArray(), ",") );

        return result;
    }

    /**
     * Escapes a JSON string
     *
     * @param json the input string
     * @return the escaped json string
     */
    private static String escapeJSON( String json ) {
        return json == null ? null : StringEscapeUtils.escapeJavaScript(json);
    }


}
