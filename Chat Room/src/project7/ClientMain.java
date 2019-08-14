/* CHAT ROOM ClientMain.java
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

import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;

public class ClientMain extends Observable implements Runnable {
    // global variables
    Socket mySocket;
    Scanner input;
    Scanner send = new Scanner(System.in);
    PrintWriter output;
    GUI clientGUI;

    /**
     * Constructor for client main, which passes in a socket and the gui of the specified client
     * @param mySocket
     * @param gui
     */
    public ClientMain(Socket mySocket, GUI gui) {
        this.mySocket = mySocket;
        clientGUI = gui;
    }

    /**
     * Multithreading poriton that ensures that all clients are running and inputs/outputs are done so
     */
    public void run() {
        try {
            try {
                input = new Scanner(mySocket.getInputStream());
                output = new PrintWriter(mySocket.getOutputStream());
                output.flush();
                verifyDataStream();
            }
            finally {
                mySocket.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * When disconnect button is clicked, user is then disconnected and prompts a alert box stating
     * user has been disconnected
     * @throws IOException
     */
    public void disconnectUser() throws IOException {
        output.println(GUI.username + " has disconnected.");
        for (int i = 0; i < clientGUI.listOnline.getItems().size(); i++) {
            if (GUI.username == clientGUI.listOnline.getItems().get(i)) {
                clientGUI.listOnline.getItems().remove(i);
            }
        }
        output.flush();
        mySocket.close();
        //TODO: Make an alert box that User disconnected!
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disconnect");
        alert.setHeaderText(GUI.username + " has been disconnected!");
        alert.setContentText("Have a great day!");

        alert.showAndWait();
        System.exit(0);
    }

    /**
     * Keeps running method and awaits for a input to occur
     */
    public void verifyDataStream() {
        while(true) {
            awaitInput();
        }
    }

    /**
     * Process the input depending on the attached code and procedes to process the text
     * Can either output to textarea for message or listview for list of users
     */
    public void awaitInput() {
        if (input.hasNext()) {
            String message = input.nextLine();
            // checks for command symbol
            if (message.contains("#?!")) {
                String msg = message.substring(3);
                System.out.println("msg :" + msg );
                msg = msg.replace("{", "");
                msg = msg.replace("}", "");
                String[] userKey = msg.split(", ");

                for (int i = 0; i < userKey.length; i++) {
                    System.out.println(userKey[i]);
                }

                for (int m = 0; m < userKey.length; m++) {
                    String temp = "";
                    for (int j = 0; userKey[m].charAt(j) != '='; j++) {
                        temp += userKey[m].charAt(j);
                    }
                    System.out.println("temp is " + temp);
                    if (!clientGUI.listOnline.getItems().contains(temp)) {
                        clientGUI.listOnline.getItems().add(temp);
                    }
                }
            } else {
                System.out.println("message1 is : " + message);

                char[] array = message.toCharArray();
                int len = 0;
                for (int i = 0; array[i] != ' '; i++) {
                    len++;
                }

                int beginIndex = len + 3;
                String channel = "";
                if (message.contains("Ch.")) {
                    channel = message.substring(beginIndex, beginIndex + 5);
                } else if (message.contains("Broadcast")) {
                    //do nothing
                }

                System.out.println("channel " + channel);

                if (ServerMain.uList.get(GUI.username).contains(channel) || message.contains("Broadcast")) {
                    //if current user can see message specified by channel
                    clientGUI.taConversation.appendText(message + "\n");

                } else {
                    System.out.println(GUI.username + " Cannot see!");
                }

            }
        }
    }


    /**
     * Handles the portion in which a message is either in a channel or in a broadcast
     * @param msg contains the message passed in from gui
     */
    public void sendMessage(String msg) {
        int channelSize = ServerMain.uList.get(GUI.username).size();
        if (channelSize > 1) {
            String ch = ServerMain.uList.get(GUI.username).get(channelSize - 1); //gets the most recent choice
            output.println(GUI.username + " - " + ch + ": " + msg);
        } else {
            output.println(GUI.username + " - Broadcast: " + msg);
        }
        output.flush();
    }
}

