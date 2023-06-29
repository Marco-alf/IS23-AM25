package it.polimi.ingsw;

import it.polimi.ingsw.view.TUI.TextualUI;

/**
 * ClientMain is the launcher of the Textual User Interface
 */
public class ClientMain {
    /**
     * this main initialize a thread where the textual user interface is running.
     * @param args is ignored
     */
    public static void main(String[] args) {
        try{
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        } catch (Exception e){
            System.out.println("RMI not set");
        }
        TextualUI view = new TextualUI();
        view.start();
    }

}