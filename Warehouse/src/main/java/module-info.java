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

    // For JAXB and JAX-WS runtime to access generated classes reflectively
    opens com.example.generated to jakarta.xml.bind, jakarta.xml.ws;

    // If your entity classes are here, open that too for reflection
    opens dk.sdu.Warehouse to spring.core, spring.beans, spring.context, spring.data.jpa;

    // Export your packages if other modules need access
    exports dk.sdu.Warehouse;
}
