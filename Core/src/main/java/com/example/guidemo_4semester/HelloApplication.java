package com.example.guidemo_4semester;


import javafx.application.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;


//@SpringBootApplication

public class HelloApplication extends Application {


    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(MySpringBoot.class).run();
    }

    @Override
    public void start(Stage stage) {
        springContext.publishEvent(new StageReady(stage));
    }
    @Override
    public void stop(){
        springContext.close();
        Platform.exit();
    }

    static class StageReady extends ApplicationEvent{
        public StageReady(Stage stage){
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
