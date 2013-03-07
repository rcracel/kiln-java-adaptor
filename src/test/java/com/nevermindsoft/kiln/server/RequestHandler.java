package com.nevermindsoft.kiln.server;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 3:57 PM
 */
public class RequestHandler implements Runnable {

    private List<String> requests;

    private Socket socket;

    public RequestHandler( Socket socket, List<String> requests ) {
        this.socket = socket;
        this.requests = requests;
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
        System.out.println("Handling request....");

        try {
            InputStream input = socket.getInputStream();
            requests.add( readInput( input ) );


            OutputStream output = socket.getOutputStream();
            writeOutput( output );

            input.close();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Request handled....");
    }

    private String readInput( InputStream stream ) throws IOException {
        int prev = 0, next = stream.read();
        Map<String, String> headers = new HashMap<String, String>();
        StringBuilder buffer = new StringBuilder();
        while ( next >= 0 ) {
            while ((next == 10 && prev == 13) || (prev == 10 && next == 13)) {
                next = stream.read();
            }

            if (next == 10 || next == 13) {
                if ( buffer.length() == 0 ) {
                    break;
                } else if ( headers.size() == 0 ) {
                    headers.put( "Method", buffer.toString() );
                } else {
                    String[] tokens = buffer.toString().split("\\s*:\\s*");
                    headers.put( tokens[0], tokens[1] );
                }
                buffer = new StringBuilder();
            } else {
                buffer.append( Character.toChars( next ) );
            }

            prev = next;
            next = stream.read();
        }

        int contentLength = Integer.valueOf( headers.get("Content-Length") );
        StringBuilder body = new StringBuilder();
        while ( contentLength-- >= 0 ) {
            body.append( Character.toChars( stream.read() ) );
        }

        String request = body.toString().trim();

        System.out.println( "Received: " + request );

        return request;
    }

    private void writeOutput( OutputStream stream ) throws IOException {
        stream.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " + Thread.currentThread().getName() + " - " + System.currentTimeMillis() + "").getBytes());
        stream.flush();
    }
}
