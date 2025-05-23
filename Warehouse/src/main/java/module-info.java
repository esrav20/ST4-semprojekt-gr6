module Warehouse {

    requires jakarta.persistence;
    requires spring.context;
    //requires spring.data.jpa;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.beans;
    requires java.xml;
    requires CommonWarehouse;
    requires jakarta.xml.ws;
    requires jakarta.xml.bind;
    requires jakarta.annotation;


    opens dk.sdu.Warehouse.Config to spring.core, spring.beans;

    opens warehouseclient to jakarta.xml.bind, jakarta.xml.ws;
    opens dk.sdu.Warehouse.Service to spring.core, spring.beans, spring.context;

    exports dk.sdu.Warehouse.Service;

}