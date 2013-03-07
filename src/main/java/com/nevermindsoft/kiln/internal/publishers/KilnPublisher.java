package com.nevermindsoft.kiln.internal.publishers;

import com.nevermindsoft.kiln.log4j.RemoteServiceAppender.Config;
import com.nevermindsoft.kiln.utils.JSONArray;
import com.nevermindsoft.kiln.utils.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private Config config;

    /**
     * Constructs a new KilnPublisher that can be used to push event messages to a Kiln server
     *
     * @param config the configuration for this appender
     */
    public KilnPublisher( Config config ) {
        this.config = config;
    }

    /**
     * Pushes a collection of LoggingEvent to the remote Kiln server AJAX
     *
     * @param events a list of LoggingEvent to be pushed to the remote repository
     */
    public void pushItems( List<LoggingEvent> events ) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost( config.getServerUrl() );
        try {
            StringEntity entity = new StringEntity( buildAjax( events ) );

            post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.addHeader(HttpHeaders.ACCEPT, "application/json");
            post.addHeader(HttpHeaders.CONTENT_LANGUAGE, "en-US");
            post.setEntity( entity );

            HttpResponse response = client.execute( post );
            StatusLine status = response.getStatusLine();

            if ( status.getStatusCode() != HttpStatus.SC_OK ) {
                config.getLogger().log(Level.ERROR, String.format("%d %s", status.getStatusCode(), status.getReasonPhrase()));
            }
        } catch ( UnsupportedEncodingException e ) {
            config.getLogger().log(Level.ERROR, "Failed to compose json request: " + e.getMessage());
        } catch ( IOException e ) {
            config.getLogger().log(Level.ERROR, "Could not connect to the server: " + e.getMessage());
        } finally {
            post.releaseConnection();
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
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss ZZZ");

        List<String> items = new ArrayList<String>();

        JSONArray<JSONObject> array = new JSONArray<JSONObject>();

        //- Build the query parameters
        for ( LoggingEvent event : events ) {
            JSONObject object = new JSONObject();

            object.set("module_name",      config.getModuleName());
            object.set("log_level",        event.getLevel().toString());
            object.set("message",          event.getRenderedMessage());
            object.set("timestamp",        dateFormatter.format(new Date(event.getTimeStamp())));
            object.set("thread_name",      event.getThreadName());
            object.set("environment_name", config.getEnvironmentName());
            object.set("platform",         config.getPlatform());

            String stackTrace = StringUtils.join(event.getThrowableStrRep(), "\n");
            if ( StringUtils.isNotBlank( stackTrace ) ) {
                int maxStackTraceSize = config.getMaxStackTraceSize();
                if ( maxStackTraceSize > 0 && maxStackTraceSize < stackTrace.length() ) {
                    stackTrace = stackTrace.substring( 0, maxStackTraceSize );
                }

                    object.set("stack_trace", stackTrace);
            }

            if ( event.locationInformationExists() ) {
                object.set("source", event.getLocationInformation().fullInfo);
            }

            JSONObject metadata = new JSONObject();
            metadata.set("java_version", System.getProperty("java.version"));
            metadata.set("os", System.getProperty("os.name"));
            object.set("metadata", metadata);

            array.add(object);
        }

        JSONObject requestData = new JSONObject();

        requestData.set("api_key", config.getApiKey());
        requestData.set("events", array);

        return requestData.toString();
    }

}
