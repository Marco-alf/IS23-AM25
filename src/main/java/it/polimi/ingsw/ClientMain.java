package it.polimi.ingsw;

import it.polimi.ingsw.view.TextualUI;

/**
 * ClientMain is the launcher of the Textual User Interface
 */
public class ClientMain {
    /**
     * this main initialize a thread where the textual user interface is running.
     * @param args is ignored
     */
    public static void main(String[] args) {
        TextualUI view = new TextualUI();
        view.start();
    }

}