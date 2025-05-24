package com.example.guidemo_4semester;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@Configuration
//@ComponentScan(basePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.CommonInventory.*"})
@SpringBootApplication(scanBasePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.Warehouse"})
public class MySpringBoot {
    public static void main(String[] args) {

        Application.launch(HelloApplication.class, args);

    }
}
