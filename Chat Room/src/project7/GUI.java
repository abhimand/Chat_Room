/* CHAT ROOM GUI.java
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

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class GUI extends Application {
    private static ClientMain chatClient;
    public static String username = "Anonymous";
    private static String host = null;
    private static String password = "password";

    @FXML private Button bAbout;
    @FXML private Button bPassEnter;
    @FXML private Button bInput;
    @FXML private Button bDisconnect;
    @FXML private Button bHelp;
    @FXML private Button bSend;
    @FXML private PasswordField passField;
    @FXML private TextField tfInput;
    @FXML public TextField tfMessage;
    @FXML public TextArea taConversation;
    @FXML public ListView<String> listOnline;
    @FXML private Label labelLoggedInBox;
    @FXML private TextField tfUsername;
    @FXML private Button bEnter;
    @FXML private ComboBox<String> listChannelsBox = new ComboBox<>();

    public static void main(String args[]) {
        launch(args);
    }


    /**
     * Creates and starts the GUI for the client. Server is running in the background
     * @param primaryStage for our GUI
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        Scene scene = new Scene(root, 550, 400);
        primaryStage.setTitle("Chat Box");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

    }

    /**
     * This will set up our user to connect to the server on a specified port and host
     * Starts thread
     */
    public void Connect() {
        try {
            final int port = 8098;
            Socket mySocket = new Socket(host, port);
            System.out.println("You connected to: " + host);

            chatClient = new ClientMain(mySocket, this);

            // send name to add to online lst
            PrintWriter output = new PrintWriter(mySocket.getOutputStream());
            output.println(username);
            output.flush();

            Thread chatThread = new Thread(chatClient);
            chatThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Passes in input for the IP into the global string Host, will allow cross connection
     * for multiple computers
     */
    @FXML private void handleIP() {
        if (!tfInput.getText().equals("")) {
            host = tfInput.getText();
            bInput.setDisable(true);
            tfInput.setDisable(true);
            bPassEnter.setDisable(false);
            passField.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Invalid IP!");
        }
    }

    /**
     * Verify if the password is matched and allow continue access, otherwise bars users
     */
    @FXML private void handlePassword() {
        if(passField.getText().equals(password)) {
            bPassEnter.setDisable(true);
            passField.setDisable(true);
            bEnter.setDisable(false);
            tfUsername.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Password!");
        }

    }

    /**
     * Once a user inputs a name, begins the program offically. Fills out channel box,
     * enables buttons, connects to server
     */
    @FXML private void handleEnter() {
        if (!tfUsername.getText().equals("")) {
            username = tfUsername.getText().trim();
            System.out.println(username);
            labelLoggedInBox.setText(username);

            bSend.setDisable(false);
            bDisconnect.setDisable(false);
            bEnter.setDisable(true);
            tfUsername.clear();
            tfUsername.setDisable(true);
            listChannelsBox.setDisable(false);
            listChannelsBox.getItems().addAll(
                    "Broadcast",
                    "Ch. 1",
                    "Ch. 2",
                    "Ch. 3",
                    "Ch. 4",
                    "Ch. 5",
                    "Ch. 6",
                    "Ch. 7",
                    "Ch. 8",
                    "Ch. 9",
                    "CH. 10"
            );
            ServerMain.uList.put(username, new ArrayList<>());
            ServerMain.uList.get(username).add("Broadcast"); //default broadcast
            Connect();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Invalid name!");
        }
    }

    /**
     * Takes an message and will send it to the rest of the broadcast or channel
     */
    @FXML private void handleSend() {
        if (!tfMessage.getText().equals("")) {
            chatClient.sendMessage(tfMessage.getText());
            tfMessage.setText("");
            tfMessage.requestFocus();
        }
    }

    /**
     * Disconnect the user form the server and closes the program
     */
    @FXML private void handleDisconnect() {
        try {
            chatClient.disconnectUser();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Manages the combolist of a specified user and ensures he/she is on the correct one.
     */
    @FXML private void handleCombo() {
        String choice = listChannelsBox.getValue();
        if (!ServerMain.uList.get(username).contains(choice)) {
            ServerMain.uList.get(username).add(choice); //adds to end to make it most recent choice
            //usersChannelBox.getItems().add(choice);
        } else {
            ServerMain.uList.get(username).remove(choice);
            ServerMain.uList.get(username).add(choice); //adds to end to make it most recent choice
        }
    }

    /**
     * Provides a dialog box that informs how to use the program
     * @param actionEvent
     */
    @FXML private void handleHelp(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Enter a username, then send a message in the broadcast! " +
                "This is defaulted. If you would like to chat with someone, you and that person should " +
                "select the same channel, and chat away!");
        alert.showAndWait();
    }

    /**
     * Provides a dialog box on what the program is about
     * @param actionEvent
     */
    @FXML private void handleAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("This is a chat client that supports unicast, multicast, and broadcast.");
        alert.showAndWait();
    }


}
