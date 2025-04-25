package com.example.guidemo_4semester;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import dk.sdu.AGVConnection.AGVConnection;
public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Label agvParameterLabel;

    private Timeline updateTimer;

    @FXML
    public void initialize() {
        startAGVUpdates();
    }

    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> updateAGVDisplay())
        );
        updateTimer.setCycleCount(Animation.INDEFINITE);
        updateTimer.play();
    }

    private void updateAGVDisplay() {
        AGVConnection.updateStatus();
        String statusText;
        String circleColor;

        switch(AGVConnection.currentState) {
            case 1:
                statusText = "Idle";
                circleColor = "DODGERBLUE";
                break;
            case 2:
                statusText = "Working";
                circleColor = "#1fff25";
                break;
            case 3:
                statusText = "Charging";
                circleColor = "ORANGE";
                break;
            default:
                statusText = "Error";
                circleColor = "RED";
        }

        // Update connection circle
        String connectionStatus = AGVConnection.isConnected ? "#1fff25" : "RED";

        // Apply updates on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(javafx.scene.paint.Color.valueOf(circleColor));
            agvConnectionCircle.setFill(javafx.scene.paint.Color.valueOf(connectionStatus));
            agvParameterLabel.setText("Battery: " + AGVConnection.battery + "%");
        });

    }
}