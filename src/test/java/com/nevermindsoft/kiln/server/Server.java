package com.nevermindsoft.kiln.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nevermindsoft.kiln.internal.json.Event;
import com.nevermindsoft.kiln.internal.json.Request;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 2:35 PM
 */
public class Server implements Runnable {

    private Thread SERVER_THREAD;

    private int port;

    private boolean shouldStop = false;

    private List<String> requests = Collections.synchronizedList( new ArrayList<String>() );

    public void start() {
        SERVER_THREAD.start();
    }

    public void stop() {
        shouldStop = true;

        int count = 0;
        while ( SERVER_THREAD.isAlive() && count < 5 ) {
            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }

        if ( SERVER_THREAD.isAlive() ) {
            SERVER_THREAD.interrupt();
        }
    }

    public void clear() {
        requests.clear();
    }

    public Server( int port ) {
        this.port = port;
        SERVER_THREAD = new Thread( this );
    }

    public List<Event> getEvents() {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers( Modifier.STATIC ).create();

        List<Event> events = new ArrayList<Event>();

        for ( String json : requests ) {
            Request request = gson.fromJson( json, Request.class );
            events.addAll( request.getEvents() );
        }

        return events;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            ServerSocket ssock = new ServerSocket( port );

            while ( !shouldStop ) {
                Socket sock = ssock.accept();
                new Thread( new RequestHandler( sock, requests ) ).start();
            }

            ssock.close();

            System.out.println("Server stopped");

        } catch ( IOException e ) {
            e.printStackTrace();
        }


    }
}
