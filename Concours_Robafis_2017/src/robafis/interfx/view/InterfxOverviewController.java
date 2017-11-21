package robafis.interfx.view;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lejos.hardware.Battery;
import lejos.hardware.BrickFinder;
import lejos.remote.ev3.RemoteEV3;
import robafis.interfx.MainApp;
import robafis.interfx.MotorControl_v2;

public class InterfxOverviewController {
	
	@FXML private Button setEvents;
	@FXML private Button boutonAvance;
	@FXML private Button boutonRecule;
	@FXML private Button boutonGauche;
	@FXML private Button boutonDroite;
	
	@FXML private Label batteryInfo;
	
	@FXML private TextField motorSpeedInput;
	@FXML private TextField steeringMotorSpeedInput;
	@FXML private TextField motorAccelerationInput;
	@FXML private TextField maximumSteeringAngleInput;
	@FXML private TextField stopingAccelerationInput;
	
	@FXML private ImageView batteryView = new ImageView();
	
	private String buttonPressedStyle = "-fx-background-color: \n" + 
			"        #3c7fb1,\n" + 
			"        linear-gradient(#61adc6, #56c4ff),\n" + 
			"        linear-gradient(#6dc9ff 0%, #6caed1 49%, #20acfc 50%, #6eb2d8 100%);\n" + 
			"    -fx-background-insets: 0,1,2;\n" + 
			"    -fx-background-radius: 3,2,1;\n" + 
			"    -fx-font-size: 14px;";
	
	public static RemoteEV3 ev3;
	public static int motorSpeed;
	public static int steeringMotorSpeed;
	public static int motorAcceleration;
	public static int maximumSteeringAngle;
	public static int stopingAcceleration;
	
	private MainApp mainApp;

	Boolean started = false;
	
	public InterfxOverviewController() {
	}
	
	@FXML
	private void initialize() throws IOException, InterruptedException {
		
		Gauge gauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .prefSize(500, 250)
                .foregroundBaseColor(Color.rgb(249, 249, 249))
                .knobColor(Color.BLACK)
                .build();
		
		ev3 = (RemoteEV3) BrickFinder.getDefault();
		showBatteryLevel();
		MotorControl_v2.MotorControllerInit();
	}
	
	Boolean startedSteering = false;
	
	@FXML
	private void setAllEvents() throws RemoteException, InterruptedException {
		Scene scene = mainApp.getPrimaryStage().getScene();
		
		if(motorSpeedInput.getText().contentEquals("")) {
			motorSpeed = 720;
		} else motorSpeed = Integer.parseInt(motorSpeedInput.getText());
		
		if(steeringMotorSpeedInput.getText().contentEquals("")) {
			steeringMotorSpeed = 150;
		} else steeringMotorSpeed = Integer.parseInt(steeringMotorSpeedInput.getText());
		
		if(motorAccelerationInput.getText().contentEquals("")) {
			motorAcceleration = 0;
		} else motorAcceleration = Integer.parseInt(motorAccelerationInput.getText());
		
		if(maximumSteeringAngleInput.getText().contentEquals("")) {
			maximumSteeringAngle = 40;
		} else maximumSteeringAngle = Integer.parseInt(maximumSteeringAngleInput.getText());
		
		if(stopingAccelerationInput.getText().contentEquals("")) {
			stopingAcceleration = 0;
		} else stopingAcceleration = Integer.parseInt(stopingAccelerationInput.getText());
		
		MotorControl_v2.MotorControllerInit();
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.NUMPAD4 && !startedSteering) {
					startedSteering = true;
					try {
						boutonGauche.setStyle(buttonPressedStyle);
						MotorControl_v2.TurnLeft();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				if (event.getCode() == KeyCode.NUMPAD6 && !startedSteering) {
					startedSteering = true;
					try {
						boutonDroite.setStyle(buttonPressedStyle);
						MotorControl_v2.TurnRight();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				if (event.getCode() == KeyCode.NUMPAD5 && !started) {
					started = true;
					try {
						boutonRecule.setStyle(buttonPressedStyle);
						MotorControl_v2.MotorStartForward();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				if (event.getCode() == KeyCode.NUMPAD8 && !started) {
					started = true;
					try {
						boutonAvance.setStyle(buttonPressedStyle);
						MotorControl_v2.MotorStartBackward();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.NUMPAD8) {
					started = false;
					try {
						boutonAvance.setStyle("");
						MotorControl_v2.MotorStop();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				if (event.getCode() == KeyCode.NUMPAD5) {
					started = false;
					try {
						boutonRecule.setStyle("");
						MotorControl_v2.MotorStop();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				if ((event.getCode() == KeyCode.NUMPAD4 && startedSteering) || (event.getCode() == KeyCode.NUMPAD6 && startedSteering)) {
					startedSteering = false;
					try {
						boutonGauche.setStyle("");
						boutonDroite.setStyle("");
						MotorControl_v2.returnToZero();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
		public void startMotorControllerTask() {
		Runnable task = new Runnable() {
			public void run() {
				try {
					MotorControl_v2.MotorControllerInit();
				} catch (RemoteException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
	
	private int getBatteryLevel() {
		float voltage = Battery.getVoltage();
		float maximumVoltage = 9;
		float currentVoltage = (voltage/maximumVoltage)*100;
		
		batteryInfo.setText(String.valueOf(Math.round(currentVoltage))+"%");
		
		if (currentVoltage>80) return 100;
		if (currentVoltage<=80 && currentVoltage>60) return 80;
		if (currentVoltage<=60 && currentVoltage>40) return 60;
		if (currentVoltage<=40 && currentVoltage>20) return 40;
		if (currentVoltage<=20 && currentVoltage>0) return 20;
		else return 0;
	}
	
	private void showBatteryLevel() {

		int level = getBatteryLevel();
		if (level==100) {batteryView.setImage(new Image("file:ressources/icons/battery_100.png"));}
		if (level==80) {batteryView.setImage(new Image("file:ressources/icons/battery_80.png"));}
		if (level==60) {batteryView.setImage(new Image("file:ressources/icons/battery_60.png"));}
		if (level==40) {batteryView.setImage(new Image("file:ressources/icons/battery_40.png"));}
		if (level==20) {batteryView.setImage(new Image("file:ressources/icons/battery_20.png"));}
		if (level==0) {batteryView.setImage(new Image("file:ressources/icons/battery_0.png"));}
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}
