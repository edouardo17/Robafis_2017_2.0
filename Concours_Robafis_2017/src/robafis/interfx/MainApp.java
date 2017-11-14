package robafis.interfx;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import robafis.interfx.view.InterfxOverviewController;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Robafis_InterFX");

        initRootLayout();

        showInterfxOverview();
    }

    /**
     * Initialise le RootLayout
     */
    public void initRootLayout() {
        try {
            // Charge le RootLayout depuis le fichier .fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Affiche la scene qui contient le RootLayout
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            	public void handle(WindowEvent we) {
            		System.out.println("Stage is closing");
            		try {
						MotorControl.ClosePorts();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }); 


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initRobot() {
    	
    }

    /**
     * Affiche l'interfx à l'intérieur du RootLayout
     */
    public void showInterfxOverview() {
        try {
            // Charge l'InterfxOverview
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/InterfxOverview.fxml"));
            AnchorPane InterfxOverview = (AnchorPane) loader.load();

            // Affiche le InterfxOverview au milieu du RootLayout
            rootLayout.setCenter(InterfxOverview);

            // Donne au controlleur accès à mainapp
            InterfxOverviewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne la scène principale
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}