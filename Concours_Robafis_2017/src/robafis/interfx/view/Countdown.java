package robafis.interfx.view;

import robafis.interfx.view.Time;
import robafis.interfx.view.CountdownController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Florian Bauer on 17.01.15.
 */
public class Countdown extends Application {

    private static Thread countdownThread;
    private Stage primaryStage;
    private CountdownController countdownController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        countdownController.stop();
    }

    public static void startCountdown(final Time timeRemaining) {
        countdownThread = new Thread(() -> {
            while (timeRemaining.hasTimeLeft() && !Thread.interrupted()) {
                Platform.runLater(() -> timeRemaining.subSeconds(1));
                InterfxOverviewController.chronometer.setValue(InterfxOverviewController.chronometer.getValue()-1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        countdownThread.start();
    }

    public static void stopCountdown() {
        if (countdownThread != null) {
            countdownThread.interrupt();
            countdownThread = null;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Countdown");

        initLayout();
    }

    private void initLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Countdown.class.getResource("/view/countdown.fxml"));
        Pane layout;
        try {
            layout = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        countdownController = loader.getController();
        countdownController.setCountdownApp(this);
    }
}
