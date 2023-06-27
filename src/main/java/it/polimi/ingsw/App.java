package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.view.GUI.GraphicalUI;
import it.polimi.ingsw.view.TextualUI;

import java.util.Scanner;

/**
 * App is the main class of the project which purpose is to act as launcher for all the components of the game.
 */
public class App {
    /**
     * main method invoked when the jar is run. Its purpose is to let the user select the game component to run.
     * @param args the expected argument of App is a string that represents the choice of the program to run.
     *             From App is possible to run the Server, the GraphicalUI or the TextualUI.
     */
    public static void main(String[] args) {
        try{
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "1000");
        } catch (Exception e){
            System.out.println("RMI not set");
        }
        String err = "\u001B[1m\u001B[38;5;" + 1 + "m ERROR: ";
        String rst = "\u001B[0m";
        if(args == null || args.length < 1){
            System.out.println(err + " argument is needed. Options are \"Server\", \"Gui\", \"Tui\" and relative abbreviations" + rst);
            System.out.println(" Use \"help\" argument to get more information about the application");

            System.exit(1);
        }
        switch (args[0]) {
            case "Server", "server", "-s", "--server" -> {
                int socketPort = 8088;
                int rmiPort = 1099;
                Scanner s = new Scanner(System.in);
                System.out.println(" M Y S H E L F I E  |  S E R V E R ");
                System.out.print("Use default ports? [Y/n]\n> ");
                String in = s.nextLine();
                if(in.equals("n") || in.equals("N")){
                    System.out.print("Insert port number for RMI connections\n> ");
                    rmiPort = Integer.parseInt(s.nextLine());
                    System.out.print("Insert port number for TCP connections\n> ");
                    socketPort = Integer.parseInt(s.nextLine());
                }
                new Server(socketPort, rmiPort).run();
            }
            case "Tui", "tui", "-t", "--tui" -> {
                TextualUI view = new TextualUI();
                view.start();
            }
            case "Gui", "gui", "-g", "--gui" -> {
                GraphicalUI.main(args);
            }
            case "help", "-h", "--help", "Help" -> {
                System.out.println("Software implementation of the board game \"My Shlefie\"");
                System.out.println("Application takes one argument. Options are \"Server\", \"Gui\", \"Tui\"");
                System.out.println("""
                        Server  ->  The server will start. Once the application has started is possible to
                                    choose whether to use custom or default ports.
                                    default port numbers are 1099 for rmi and 8088 for tcp.
                                    Alternative forms such as "-s", "--server" and "server" are accepted
                        """);
                System.out.println("""
                        Gui     ->  The graphical user interface will start in a new window.
                                    Alternative forms such as "-g", "--gui" and "gui" are accepted

                        """);
                System.out.println("""
                        Tui     ->  The textual user interface will start in this window.
                                    Alternative forms such as "-t", "--tui" and "tui" are accepted
                        """);
            }
            default -> System.out.println(err + "Arguments are not valid. Options are \"Server\", \"Gui\", \"Tui\"" + rst);
        }

    }
}
