package com.example.guidemo_4semester;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example",
        "dk.sdu",
        "dk.sdu.AGV",
        "dk.sdu.AssemblyStation",
        "dk.sdu.CommonInventory"
})
@EnableJpaRepositories(basePackages = "dk.sdu.CommonInventory")
@EntityScan(basePackages = "dk.sdu.CommonInventory")
public class MySpringBoot {
    // Empty: Only used for bootstrapping Spring context
}
