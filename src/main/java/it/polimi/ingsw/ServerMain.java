package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

/**
 * ServerMain is a class used to launch the server
 */
public class ServerMain {
    /**
     * this main initialize a Thread where the Server will run.
     * @param args is ignored
     */
    public static void main(String[] args) {
        int socketPort = 8088;
        int rmiPort = 1099;
        try{
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        } catch (Exception e){
            System.out.println("RMI not set");
        }

        System.out.println(" M Y S H E L F I E  |  S E R V E R ");

        new Server(socketPort, rmiPort).run();
    }

}