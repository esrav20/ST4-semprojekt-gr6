// File: AppConfig.java
package com.example.guidemo_4semester;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.example", "dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu"})
public class AppConfig {
}
