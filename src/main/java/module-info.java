module com.example.chatdesktop {

    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires java.prefs;
    requires java.sql;

    opens com.example.chatdesktop.model to com.fasterxml.jackson.databind;

    exports com.example.chatdesktop;
}