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
}