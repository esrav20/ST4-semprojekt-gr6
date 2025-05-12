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
    exports dk.sdu.CommonInventory;
}