package com.example.guidemo_4semester;

import com.example.guidemo_4semester.HelloApplication.StageReady;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Component
public class stageListener implements ApplicationListener<StageReady> {
    @Value("classpath:/TabView.fxml")
    private Resource resource;

    private final ApplicationContext applicationContext;

    public stageListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void onApplicationEvent(StageReady event){
        try {

            FXMLLoader fxmlLoader= new FXMLLoader(resource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);

            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            stage.setScene(new Scene(parent, 800, 600));
            stage.setTitle("virk pls");
            stage.show();

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
