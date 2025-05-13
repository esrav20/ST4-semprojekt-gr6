package com.example.guidemo_4semester;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.CommonInventory.*"})
public class MySpringBoot {
    // Empty – used for context loading only
}
