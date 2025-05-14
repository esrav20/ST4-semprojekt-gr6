package com.example.guidemo_4semester;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@Configuration
//@ComponentScan(basePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.CommonInventory.*"})
@SpringBootApplication(scanBasePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.Warehouse"})
@EnableJpaRepositories("dk.sdu.Warehouse")
@EntityScan("dk.sdu.Warehouse")
public class MySpringBoot {
    public static void main(String[] args) {

        Application.launch(HelloApplication.class, args);

    }
}
