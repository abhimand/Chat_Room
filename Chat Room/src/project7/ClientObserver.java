/* CHAT ROOM ClientObserver.java
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends PrintWriter implements Observer {

    /**
     * Observable required method to implement and then used
     *
     */
    ClientObserver(OutputStream out) {
        super(out);
    }
    @Override
    public void update(Observable o, Object arg) {
        this.println(arg); //writer.println(arg);
        this.flush(); //writer.flush();
    }

}
