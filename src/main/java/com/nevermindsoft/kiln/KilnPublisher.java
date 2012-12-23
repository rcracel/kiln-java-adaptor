package com.nevermindsoft.kiln;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
 * User: rcracel
 * Date: 12/20/12
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class KilnPublisher {

    private String serverUrl;
    private String applicationName;
    private String environmentName;
    private String apiKey;

    public KilnPublisher(String serverUrl, String apiKey, String applicationName, String environmentName) {
        this.serverUrl = serverUrl;
        this.applicationName = applicationName;
        this.environmentName = environmentName;
        this.apiKey = apiKey;
    }

    public void pushItems( List<LoggingEvent> events ) {
        HttpURLConnection connection = null;

        try {
            String parameters = buildAjax( events );
            URL url = new URL( serverUrl );

            //System.out.println("**** Pushing " + parameters);

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty( "Content-Type", "application/json");
            connection.setRequestProperty( "Accept", "application/json");
            connection.setRequestProperty( "Content-Length", String.valueOf( Integer.toString( parameters.getBytes().length ) ) );
            connection.setRequestProperty( "Content-Language", "en-US");

            //connection.setDoInput(true);

            //connection.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream ( connection.getOutputStream() );
            wr.writeBytes( parameters );
            wr.flush ();
            wr.close ();

            if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK ) {
                System.out.println("Response code: " + connection.getResponseCode());

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

                System.out.println( response.toString() );
            }

            //System.out.println("**** Pushing done with code " + connection.getResponseCode() + " - " + connection.getResponseMessage());

        } catch ( MalformedURLException e ) {
            System.out.println("Could not connect to the server: " + e.getMessage());
        } catch ( ProtocolException e ) {
            System.out.println("Could not connect to the server: " + e.getMessage());
        } catch ( IOException e ) {
            System.out.println("Could not connect to the server: " + e.getMessage());
        } catch ( Exception e ) {
            System.out.println("Could not connect to the server: " + e.getMessage());
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
            StringBuilder me = new StringBuilder();

            me.append("{");

            me.append("\"application_name\":\"").append(escapeJSON(applicationName)).append("\",");
            me.append("\"log_level\":\"")       .append(escapeJSON(event.getLevel().toString())).append("\",");
            me.append("\"message\":\"")         .append(escapeJSON(event.getRenderedMessage())).append("\",");
            me.append("\"timestamp\":\"")       .append(escapeJSON(dateFormatter.format(new Date(event.getTimeStamp())))).append("\",");
            me.append("\"thread_name\":\"")     .append(escapeJSON(event.getThreadName())).append("\",");
            me.append("\"stack_trace\":\"")     .append(escapeJSON(StringUtils.join(event.getThrowableStrRep(), "\n"))).append("\",");
            me.append("\"environment_name\":\"").append(escapeJSON(environmentName)).append("\"");

            me.append("}");

            items.add( me.toString() );
        }

        String response = String.format("{ \"api-key\": \"%s\", \"events\": [%s] }", apiKey, StringUtils.join(items.toArray(), ",") );

        //System.out.println( response );

        return response;
    }

    /**
     *
     * @param json
     * @return
     */
    private static String escapeJSON( String json ) {
        return StringEscapeUtils.escapeJavaScript(json);
    }


}
