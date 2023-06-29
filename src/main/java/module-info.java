module AM25_myShelfie {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.logging;

    exports it.polimi.ingsw.model;
    exports it.polimi.ingsw.controller;
    exports it.polimi.ingsw.network.client;
    exports it.polimi.ingsw.network.server;
    exports it.polimi.ingsw.network.messages;
    exports it.polimi.ingsw.view;
    exports it.polimi.ingsw.exception;

    opens it.polimi.ingsw.view.GUI to javafx.graphics;
    exports it.polimi.ingsw.view.GUI to javafx.graphics;

    opens it.polimi.ingsw.view.GUI.SceneFactories to javafx.fxml;
    exports it.polimi.ingsw.view.GUI.SceneFactories to javafx.fxml;

}