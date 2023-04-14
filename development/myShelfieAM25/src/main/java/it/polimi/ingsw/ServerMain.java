package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

public class ServerMain {
    public static void main(String[] args) {
        int socketPort = 8088;
        int rmiPort = 1099;

        System.out.println(" M Y S H E L F I E  |  S E R V E R ");

        new Server(socketPort, rmiPort).run();
    }

}