module CommonWarehouse {
    requires spring.data.jpa;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires jakarta.persistence;
    requires spring.boot;
    requires spring.context;
    requires spring.beans;
    requires spring.data.commons;
    requires javax.jws;
    requires java.xml.ws;
    requires java.xml.bind;
    requires org.junit.jupiter.api;
    requires spring.boot.test;
    //requires org.junit.platform.commons;

    opens dk.sdu.CommonInventory to spring.core, spring.beans, spring.data.jpa, javafx.fxml;
    opens dk.sdu.CommonInventory.Service to spring.core, spring.beans, spring.data.jpa, javafx.fxml;

    exports dk.sdu.CommonInventory;
    exports dk.sdu.CommonInventory.Service;
}