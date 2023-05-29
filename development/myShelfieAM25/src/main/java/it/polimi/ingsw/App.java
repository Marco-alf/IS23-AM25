package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.GUI.GraphicalUI;
import it.polimi.ingsw.view.TextualUI;

public class App {
    public static void main(String[] args) {
        switch (args[0]) {
            case "Server" -> {
                int socketPort = 8088;
                int rmiPort = 1099;
                System.out.println(" M Y S H E L F I E  |  S E R V E R ");
                new Server(socketPort, rmiPort).run();
            }
            case "Tui" -> {
                TextualUI view = new TextualUI();
                view.start();
            }
            case "Gui" -> {
                GraphicalUI.main(args);
            }
            default -> System.out.println("arg not valid");
        }

    }
}
