package robafis.interfx.view;

import java.io.IOException;
import java.io.PipedWriter;
import java.rmi.RemoteException;
import java.time.Duration;

import javax.naming.spi.InitialContextFactoryBuilder;

import org.reactfx.util.FxTimer;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.FloatExpression;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import robafis.interfx.commMotor;

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
	
	
	public static int motorSpeed;
	public static int steeringMotorSpeed;
	public static int motorAcceleration;
	public static int maximumSteeringAngle;
	public static int stopingAcceleration;
	
	public static FloatProperty batteryLevel = new SimpleFloatProperty(0);
	public static FloatProperty angle = new SimpleFloatProperty(0);
	
	private MainApp mainApp;
	private Gauge angleGauge;
	private Gauge batteryGauge;

	Boolean started = false;
	Boolean startedSteering = false;
	public static Boolean running = true;
	
	
	public InterfxOverviewController() {
	}
	
	// THREAD COMMUNICATION MOTEUR
    public void commMotor_Thread() {
		Runnable task = new Runnable() {
			public void run() {
				try {
					commMotor.connectToEV3();
					commMotor.run();
					} catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
    
    // THREAD COMMANDE MOTEUR
    public void motorControl_Thread() {
    	Runnable task = new Runnable() {
			public void run() {
				//TODO
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
    }
    
    // THREAD NIVEAU DE BATTERY
    public void battery_Thread() {
    	Runnable task = new Runnable() {
			public void run() {
				commMotor.getBatteryLevel();
				FxTimer.runPeriodically(Duration.ofSeconds(30), () -> {
				 commMotor.fifoQueue.add(99);
				});
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
    }
    
    // THREAD PARAMETRES
    public void params_Thread() {
    	Runnable task = new Runnable() {
			public void run() {
				FxTimer.runPeriodically(Duration.ofMillis(200), () -> {
					commMotor.fifoQueue.add(98);
				});
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	private void initialize() throws IOException, InterruptedException {
		
		final ChangeListener batteryLevelListener = new ChangeListener() {
		      @Override
		      public void changed(ObservableValue observableValue, Object oldValue,
		          Object newValue) {batteryGauge.setValue(batteryLevel.floatValue());}
		    };
		    
		    batteryLevel.addListener(batteryLevelListener);
		
		final ChangeListener angleListener = new ChangeListener() {
		      @Override
		      public void changed(ObservableValue observableValue, Object oldValue,
		          Object newValue) {angleGauge.setValue(angle.floatValue());}
		    };
		    
		    angle.addListener(angleListener);
		
		angleGauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .prefSize(1000, 500)
                .title("ANGLE")
                .titleColor(Color.BLACK)
                .maxValue(90)
                .minValue(-90)
                .value(0)
                .animated(true)
                .thresholdVisible(true)
                .threshold(50)
                .thresholdColor(Color.ORANGERED)
                .animationDuration(180)
                .build();
		
		batteryGauge = (GaugeBuilder.create()
                .skinType(SkinType.BATTERY)
                .value(100))
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
		
		commMotor_Thread();
		battery_Thread();
		params_Thread();
		
		motor1Status.setImage(new Image("file:ressources/icons/ok.png"));
		motor2Status.setImage(new Image("file:ressources/icons/ok.png"));
		steeringStatus.setImage(new Image("file:ressources/icons/ok.png"));
	}
	
	@FXML
	private void connectToEV3() throws RemoteException, InterruptedException {
		
		infoBox.setText("Initialising motors");
		setAllEvents();
	}
	
	private void getParameters() {
		
		if(motorSpeedInput.getText().contentEquals("")) {
			motorSpeed = 720;
		} else motorSpeed = Integer.parseInt(motorSpeedInput.getText());
		
		if(steeringMotorSpeedInput.getText().contentEquals("")) {
			steeringMotorSpeed = 20;
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
		
//		motorThread();
//		batteryThread();
//		parametersThread();
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.NUMPAD4 && !startedSteering) {
					commMotor.fifoQueue.add(4);
					startedSteering = true;
					boutonGauche.setStyle(buttonPressedStyle);
				}
				
				if (event.getCode() == KeyCode.NUMPAD6 && !startedSteering) {
					commMotor.fifoQueue.add(6);
					startedSteering = true;
					boutonDroite.setStyle(buttonPressedStyle);
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
				
				if ((event.getCode() == KeyCode.NUMPAD6) && startedSteering) {
					startedSteering = false;
					boutonDroite.setStyle("");
					commMotor.fifoQueue.add(0);
				}
				
				if ((event.getCode() == KeyCode.NUMPAD4) && startedSteering) {
					startedSteering = false;
					boutonGauche.setStyle("");
					commMotor.fifoQueue.add(0);
				}
			}
		});
	}
	
	private void showParameters() throws InterruptedException {
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}
