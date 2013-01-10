package com.nevermindsoft.kiln.internal.publishers;

import com.nevermindsoft.kiln.internal.log.KilnInternalLogger;
import com.sun.net.ssl.internal.ssl.Provider;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Security;
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

        try {
            StringEntity entity = new StringEntity( buildAjax( events ) );

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost( serverUrl );

            post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.addHeader(HttpHeaders.ACCEPT, "application/json");
            post.addHeader(HttpHeaders.CONTENT_LANGUAGE, "en-US");
            post.setEntity( entity );

            HttpResponse response = client.execute( post );
            StatusLine status = response.getStatusLine();

            if ( status.getStatusCode() != HttpStatus.SC_OK ) {
                logger.log( Level.ERROR, String.format("%d %s", status.getStatusCode(), status.getReasonPhrase()) );
            }
        } catch ( UnsupportedEncodingException e ) {
            logger.log( Level.ERROR, "Failed to compose json request: " + e.getMessage());
        } catch ( IOException e ) {
            logger.log( Level.ERROR, "Could not connect to the server: " + e.getMessage());
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
