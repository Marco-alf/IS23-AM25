module AM25 {
    requires javafx.graphics;
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.logging;

    opens it.polimi.ingsw.view.GUI to javafx.graphics;
    exports it.polimi.ingsw.view.GUI to javafx.graphics;

}