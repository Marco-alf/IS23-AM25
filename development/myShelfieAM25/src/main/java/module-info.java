module AM25 {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.logging;

    opens it.polimi.ingsw.view.GUI to javafx.graphics;
    exports it.polimi.ingsw.view.GUI to javafx.graphics;

    opens it.polimi.ingsw.view.GUI.SceneFactories to javafx.fxml;
    exports it.polimi.ingsw.view.GUI.SceneFactories to javafx.fxml;

    exports it.polimi.ingsw.network.client to java.rmi;
    exports it.polimi.ingsw.network.server to java.rmi;

    exports it.polimi.ingsw.model to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.network.messages.serverMessages;
    exports it.polimi.ingsw.network.messages.clientMessages;
    exports it.polimi.ingsw.model.data;
    exports it.polimi.ingsw.view;
}