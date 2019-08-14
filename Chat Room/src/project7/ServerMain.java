/* CHAT ROOM ServerMain.java
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

// starts server sits and listens, ready to take connections
public class ServerMain extends Observable {
    /** Global Variables */
    // holds all connections (any message sent by one client must be echoed to all others)
    public static ArrayList<Socket> connectionList = new ArrayList<>();
    public static ArrayList<String> userList = new ArrayList<>();   // holds current users
    public static HashMap<String, ArrayList<String>> uList = new HashMap<>();
    public static Observable obs = new Observable();

    /**
     * sets up server networking by invoking setUpNetworking()
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            new ServerMain().setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  instantiates new server socket to myPort to sit and listen.
     *  The method constantly accepts new client sockets and creates a
     *  chat thread for each client and adds client to connectionList (socket)
     *
     * @throws IOException
     */
    public void setUpNetworking() throws IOException {
        try {
            final int myPort = 8098;
            ServerSocket myServer = new ServerSocket(myPort);   // instantiate new server socket on myPort to sit & listen
            System.out.println("Waiting for clients...");

            while (true) {
                Socket mySocket = myServer.accept();

                //TODO: implemented writer
                ClientObserver writer = new ClientObserver(mySocket.getOutputStream());
                this.addObserver(writer);

                connectionList.add(mySocket);
                // display client's IP address & host name
                System.out.println("Client connected from: " + mySocket.getLocalAddress().getHostName());

                // add user to list of users online
                addUser(mySocket);

                // pass in every user that connects that server socket accepts, make sure that user's message is echoed to everyone else
                ServerReturn chat = new ServerReturn(mySocket);
                Thread chatThread = new Thread(chat);
                chatThread.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * notifies the server that a user has joined, adds user and channel (default Broadcast)
     * to hash map (key: username, value: channel). The method then tells all existing users
     * in connectionList that user has joined and sends out command to add user.
     *
     * @param mySocket
     * @throws IOException
     */
    public static void addUser(Socket mySocket) throws IOException {
        // let server know user joined
        Scanner input = new Scanner(mySocket.getInputStream());
        String username = input.nextLine();
//        userList.add(username);


        uList.put(username, new ArrayList<>());
        uList.get(username).add("Broadcast"); //default broadcast

        // tell all other existing users in connectionList that user joined
        for (int i = 1; i <= connectionList.size(); i++) {
            Socket userSocket = connectionList.get(i - 1);  // ea existing users socket
            PrintWriter output = new PrintWriter(userSocket.getOutputStream());     // output data
            //changed from userList
            output.println("#?!" + uList);   // '#?!' represents command to add users to jlist object in client
            output.flush();     // flush out data stream
        }
    }

}
