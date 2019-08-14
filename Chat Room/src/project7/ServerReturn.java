/* CHAT ROOM ServerReturn.java
 * EE422C Project 7 submission by
 * Abhishek Mandal
 * aem3898
 * 16195
 * James Lin
 * jl62356
 * 16195
 * Slip days used: 1
 * Spring 2019
 */

package project7;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerReturn extends Observable implements Runnable  {

    Socket SOCK;
    private Scanner INPUT;
    private PrintWriter OUT;
    String MESSAGE = "";

    /**
     * Constructor
     * @param X is Socket
     */
    public ServerReturn(Socket X){
        this.SOCK = X;
    }


    /**
     * Checks the server connection with the client and to handle client disconnection.
     * When a client disconnects CheckConnection removes the clients connection from the server.
     * @throws IOException
     */
    public void CheckConnection() throws IOException {
        if (!SOCK.isConnected()) {
            for (int i = 1; i <= ServerMain.connectionList.size(); i++) {
                if (ServerMain.connectionList.get(i) == SOCK) {
                    ServerMain.connectionList.remove(i);
                }
            }

            for (int i = 1; i <= ServerMain.connectionList.size(); i++) {
                Socket TEMP_SOCK = (Socket) ServerMain.connectionList.get(i - 1); //typecast may not be neccessary
                PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
                TEMP_OUT.println(TEMP_SOCK.getLocalAddress().getHostName() + "disconnected!");
                TEMP_OUT.flush();
                // Show disconnection at server
                System.out.println(TEMP_SOCK.getLocalAddress().getHostName() + "disconnected!");
            }
        }
    }

    /**
     * Multithreading portion that ensures all messages are processed and outputted to all users, checks connections
     */
    public void run() {
        try {
            try {
                INPUT = new Scanner(SOCK.getInputStream());
                OUT = new PrintWriter(SOCK.getOutputStream());

                while (true) {
                    CheckConnection();

                    if(!INPUT.hasNext()) {
                        return;
                    }

                    MESSAGE = INPUT.nextLine();
                    System.out.println("Client said: " + MESSAGE);

                    for (int i = 1; i <= ServerMain.connectionList.size(); i++) {
                        //Note: if necessary take CAST below out. I added it to make it compile
                        Socket TEMP_SOCK = (Socket) ServerMain.connectionList.get(i - 1); //typecast may not be neccessary
                        PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
                        TEMP_OUT.println(MESSAGE);
                        TEMP_OUT.flush();
                        System.out.println("Sent to: " + TEMP_SOCK.getLocalAddress().getHostName());
                    }
                    //TODO: extended and notify
                    notifyObservers(MESSAGE);
                }
            }
            finally {
                SOCK.close();
            }
        }
        catch(Exception X) {
            System.out.println(X);
        }
    }

}
