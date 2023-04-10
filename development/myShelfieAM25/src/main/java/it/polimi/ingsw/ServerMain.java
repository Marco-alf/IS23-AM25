package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

public class ServerMain {
    public static void main(String[] args) {
        int socketPort = 2802;
        int rmiPort = 3100;

        System.out.println(" M Y S H E L F I E  |  S E R V E R ");

        new Server(socketPort, rmiPort).run();
    }

}