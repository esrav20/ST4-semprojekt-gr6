package com.example.guidemo_4semester;

import com.example.guidemo_4semester.HelloApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@Configuration
//@ComponentScan(basePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.CommonInventory.*"})
@SpringBootApplication(scanBasePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.Warehouse"}, exclude = {DataSourceAutoConfiguration.class})
public class MySpringBoot {
    public static void main(String[] args) {

        Application.launch(HelloApplication.class, args);

    }
}
