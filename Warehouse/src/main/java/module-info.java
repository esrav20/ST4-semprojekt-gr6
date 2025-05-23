module Warehouse {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.jpa;
    requires spring.web;
    requires spring.beans;
    requires jakarta.persistence;
    requires jakarta.xml.bind;
    requires jakarta.xml.ws;
    requires com.fasterxml.jackson.databind;
    requires CommonWarehouse;
    requires rt;

    opens com.example.generated to jakarta.xml.bind, jakarta.xml.ws;
    opens dk.sdu.Warehouse to spring.core, spring.beans, spring.context, spring.data.jpa;

    exports dk.sdu.Warehouse;
}