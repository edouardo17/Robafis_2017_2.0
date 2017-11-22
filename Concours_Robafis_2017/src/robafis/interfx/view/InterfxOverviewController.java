package robafis.interfx.view;

import java.rmi.RemoteException;

import org.reactfx.util.FxTimer;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import lejos.hardware.Battery;
import lejos.hardware.BrickFinder;
import lejos.remote.ev3.RemoteEV3;
import robafis.interfx.MainApp;
import robafis.interfx.MotorControl_v2;

public class InterfxOverviewController {
	
	@FXML private Button connectToEV3;
	@FXML private Button boutonAvance;
	@FXML private Button boutonRecule;
	@FXML private Button boutonGauche;
	@FXML private Button boutonDroite;
	
	@FXML private Label infoBox;
	
	@FXML private TextField motorSpeedInput;
	@FXML private TextField steeringMotorSpeedInput;
	@FXML private TextField motorAccelerationInput;
	@FXML private TextField maximumSteeringAngleInput;
	@FXML private TextField stopingAccelerationInput;
	
	@FXML private ImageView motor1Status;
	@FXML private ImageView motor2Status;
	@FXML private ImageView steeringStatus;
	
	@FXML private GridPane batteryPane;
	@FXML private GridPane anglePane;
	
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
	private Gauge angleGauge;
	private Gauge batteryGauge;

	Boolean started = false;
	Boolean startedSteering = false;
	
	public InterfxOverviewController() {
	}
	
	//Thread de MotorControl_v2
    public void motorThread() {
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
    
    public void batteryThread() {
		Runnable task = new Runnable() {
			public void run() {
				try {
					while(true) {showBatteryLevel(); Thread.sleep(5000);}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
    
    public void parametersThread() {
    	Runnable task = new Runnable() {
			public void run() {
				try {
					showParameters();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
    }
	
	@FXML
	private void initialize() {
		
		angleGauge = GaugeBuilder.create()
                .skinType(SkinType.MODERN)
                .prefSize(500, 250)
                .title("rpm")
                .maxValue(720)
                .foregroundBaseColor(Color.rgb(249, 249, 249))
                .knobColor(Color.BLACK)
                .animated(true)
                .animationDuration(500)
                .build();
		
		batteryGauge = (GaugeBuilder.create()
                .skinType(SkinType.BATTERY)
                .value(70))
				.prefWidth(500)
                .gradientBarEnabled(true) // Gradient filled bar should be visible  
                .gradientBarStops(new Stop(1.0, Color.GREEN), // Gradient for gradient bar  
                                     new Stop(0.75, Color.YELLOWGREEN),  
                                     new Stop(0.5, Color.YELLOW),  
                                     new Stop(0.25, Color.DARKORANGE),  
                                     new Stop(0.0, Color.ORANGERED))
                .build();
		
		batteryPane.setPadding(new Insets(20));
		batteryPane.add(batteryGauge, 0, 0);
		
		anglePane.setPadding(new Insets(20));
		anglePane.add(angleGauge, 0, 0);
		
		infoBox.setAlignment(Pos.CENTER_RIGHT);
		infoBox.setText("Interfx initialization complete");
		
		motor1Status.setImage(new Image("file:ressources/icons/ko.png"));
		motor2Status.setImage(new Image("file:ressources/icons/ko.png"));
		steeringStatus.setImage(new Image("file:ressources/icons/ko.png"));
	}
	
	@FXML
	private void connectToEV3() throws RemoteException, InterruptedException {
		
		ev3 = (RemoteEV3) BrickFinder.getDefault();
		
		infoBox.setText("Initialising motors");
		
		setAllEvents();
		
		motor1Status.setImage(new Image("file:ressources/icons/ok.png"));
		motor2Status.setImage(new Image("file:ressources/icons/ok.png"));
		steeringStatus.setImage(new Image("file:ressources/icons/ok.png"));
	}
	
	private void getParameters() {
		
		if(motorSpeedInput.getText().contentEquals("")) {
			motorSpeed = 720;
		} else motorSpeed = Integer.parseInt(motorSpeedInput.getText());
		
		if(steeringMotorSpeedInput.getText().contentEquals("")) {
			steeringMotorSpeed = 150;
		} else steeringMotorSpeed = Integer.parseInt(steeringMotorSpeedInput.getText());
		
		if(motorAccelerationInput.getText().contentEquals("")) {
			motorAcceleration = 500;
		} else motorAcceleration = Integer.parseInt(motorAccelerationInput.getText());
		
		if(maximumSteeringAngleInput.getText().contentEquals("")) {
			maximumSteeringAngle = 40;
		} else maximumSteeringAngle = Integer.parseInt(maximumSteeringAngleInput.getText());
		
		if(stopingAccelerationInput.getText().contentEquals("")) {
			stopingAcceleration = 500;
		} else stopingAcceleration = Integer.parseInt(stopingAccelerationInput.getText());
	}
	
	private void setAllEvents() throws RemoteException, InterruptedException {
		Scene scene = mainApp.getPrimaryStage().getScene();
		
		getParameters();
		
		motorThread();
		batteryThread();
		parametersThread();
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.NUMPAD4 && !startedSteering) {
					MotorControl_v2.flag4pressed=1;
					startedSteering = true;
					boutonGauche.setStyle(buttonPressedStyle);
//					try {
//						boutonGauche.setStyle(buttonPressedStyle);
//						MotorControl_v2.TurnLeft();
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
				}
				
				if (event.getCode() == KeyCode.NUMPAD6 && !startedSteering) {
					MotorControl_v2.flag6pressed=1;
					startedSteering = true;
					boutonDroite.setStyle(buttonPressedStyle);
//					try {
//						boutonDroite.setStyle(buttonPressedStyle);
//						MotorControl_v2.TurnRight();
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
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
				
				if (event.getCode() == KeyCode.NUMPAD1) {
					try {
						MotorControl_v2.CalibrateLeft();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				if (event.getCode() == KeyCode.NUMPAD3) {
					try {
						MotorControl_v2.CalibrateRight();
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
				
//				if ((event.getCode() == KeyCode.NUMPAD4 && startedSteering) || (event.getCode() == KeyCode.NUMPAD6 && startedSteering)) {
//					startedSteering = false;
//					try {
//						boutonGauche.setStyle("");
//						boutonDroite.setStyle("");
//						MotorControl_v2.returnToZero();
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
//				}
				
				if ((event.getCode() == KeyCode.NUMPAD6) && startedSteering) {
					startedSteering = false;
					boutonDroite.setStyle("");
					MotorControl_v2.flag6pressed=0;
				}
				
				if ((event.getCode() == KeyCode.NUMPAD4) && startedSteering) {
					startedSteering = false;
					boutonGauche.setStyle("");
					MotorControl_v2.flag4pressed=0;
				}
			}
		});
	}
	
	private void showBatteryLevel() throws InterruptedException {
			batteryGauge.setValue(getBatteryLevel());
	}
	
	private void showParameters() throws InterruptedException {
		while(true) {
			angleGauge.setValue(MotorControl_v2.angle);
			Thread.sleep(300);
		}
	}
	
	private float getBatteryLevel() {
		float voltage = Battery.getVoltage();
		float maximumVoltage = (float) 2.75;
		float currentVoltage = (float) (((voltage-6.25)/maximumVoltage)*100);
		
		return currentVoltage;
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}
