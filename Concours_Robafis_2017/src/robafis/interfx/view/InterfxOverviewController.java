package robafis.interfx.view;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;

import org.reactfx.util.FxTimer;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import robafis.interfx.MainApp;
import robafis.interfx.MotorControl_v2;
import robafis.interfx.commMotor;

public class InterfxOverviewController {
	
	@FXML private Button setParameters;
	@FXML private Button boutonAvance;
	@FXML private Button boutonRecule;
	@FXML private Button boutonGauche;
	@FXML private Button boutonDroite;
	@FXML private Button resetParameters;
	
	@FXML private Label infoBox;
	@FXML private Label warningBox;
	
	@FXML private TextField motorSpeedInput;
	@FXML private TextField steeringMotorSpeedInput;
	@FXML private TextField motorAccelerationInput;
	@FXML private TextField maximumSteeringAngleInput;
	@FXML private TextField stopingAccelerationInput;
	@FXML private Slider motorSpeedSlider;
	@FXML private Slider steeringMotorSpeedSlider;
	@FXML private Slider motorAccelerationSlider;
	@FXML private Slider stopingAccelerationSlider;
	@FXML private Slider maximumSteeringAngleSlider;
	
	@FXML private ImageView motor1Status;
	@FXML private ImageView motor2Status;
	@FXML private ImageView steeringStatus;
	@FXML private ImageView warningStatus;
	
	@FXML private GridPane batteryPane;
	@FXML private GridPane anglePane;
	@FXML private GridPane speedPane;
	
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
	public static FloatProperty speed = new SimpleFloatProperty(0);
	
	private MainApp mainApp;
	private Gauge angleGauge;
	private Gauge batteryGauge;
	private Gauge speedGauge;

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
					setParameters.setDisable(false);
					motor1Status.setImage(new Image("file:ressources/icons/ok.png"));
					motor2Status.setImage(new Image("file:ressources/icons/ok.png"));
					steeringStatus.setImage(new Image("file:ressources/icons/ok.png"));
					commMotor.run();
					} catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
    
    // THREAD NIVEAU DE BATTERIE
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
    
    //THREAD CHRONOMETER
    public void chronometer() {
    	Runnable task = new Runnable() {
    		public void run() {
    			FxTimer.runPeriodically(Duration.ofSeconds(1), () -> {
    				
    			});
    		}
    	};
    	Thread chronometerThread = new Thread(task);
    	chronometerThread.setDaemon(true);
    	chronometerThread.start();
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	private void initialize() throws IOException, InterruptedException {
		
		infoBox.setAlignment(Pos.CENTER_RIGHT);
		infoBox.setText("Ready to rock!");
		
		setParameters.setDisable(true);
		resetParameters.setDisable(true);
		warningStatus.setVisible(false);
		warningBox.setVisible(false);
		warningStatus.setImage(new Image("file:ressources/icons/warning-icon-hi.png"));
		motor1Status.setImage(new Image("file:ressources/icons/ko.png"));
		motor2Status.setImage(new Image("file:ressources/icons/ko.png"));
		steeringStatus.setImage(new Image("file:ressources/icons/ko.png"));
		
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
		
	    final ChangeListener speedListener = new ChangeListener() {
		      @Override
		      public void changed(ObservableValue observableValue, Object oldValue,
		          Object newValue) {speedGauge.setValue(speed.floatValue());}
		    };
		    
		    speed.addListener(speedListener);
		
		angleGauge = GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .prefSize(1000, 500)
                .title("ANGLE")
                .titleColor(Color.BLACK)
                .maxValue(85)
                .minValue(-85)
                .value(0)
                .animated(true)
                .sectionsVisible(true)
                .sections(new Section(40, 100, Color.ORANGERED), new Section(-100,-40, Color.ORANGERED))
                .checkSectionsForValue(true)
                .animationDuration(100)
                .build();
		
		batteryGauge = GaugeBuilder.create()
                .skinType(SkinType.BATTERY)
                .value(100)
				.prefWidth(500)
                .gradientBarEnabled(true) // Gradient filled bar should be visible  
                .gradientBarStops(new Stop(1.0, Color.GREEN), // Gradient for gradient bar  
                                     new Stop(0.75, Color.YELLOWGREEN),  
                                     new Stop(0.5, Color.YELLOW),  
                                     new Stop(0.25, Color.DARKORANGE),  
                                     new Stop(0.0, Color.ORANGERED))
                .build();
		
		speedGauge = GaugeBuilder.create()
				.skinType(SkinType.HORIZONTAL)
				.prefSize(1000,500)
				.title("SPEED")
				.titleColor(Color.BLACK)
				.minValue(0)
				.maxValue(800)
				.value(0)
				.animated(true)
				.animationDuration(100)
				.sectionsVisible(true)
				.sections(new Section(720, 800, Color.ORANGERED))
				.build();
		
		batteryPane.setPadding(new Insets(20));
		batteryPane.add(batteryGauge, 0, 0);
		
		anglePane.setPadding(new Insets(20));
		anglePane.add(angleGauge, 0, 0);
		
		speedPane.setPadding(new Insets(20));
		speedPane.add(speedGauge, 0, 0);
		
		commMotor_Thread();
		battery_Thread();
		params_Thread();
		configureSliders();
	}
	
	@FXML
	private void setParameters() throws RemoteException, InterruptedException {
		setAllEvents();
		setParameters.setDisable(true);
		resetParameters.setDisable(false);
	}
	
	private void configureSliders() {
		stopingAccelerationSlider.setSnapToTicks(true);
		maximumSteeringAngleSlider.setSnapToTicks(true);
		motorAccelerationSlider.setSnapToTicks(true);
		motorSpeedSlider.setSnapToTicks(true);
		steeringMotorSpeedSlider.setSnapToTicks(true);
		
		stopingAccelerationInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.contentEquals("")) stopingAccelerationSlider.setValue(Double.parseDouble(newValue));
		});
		stopingAccelerationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			stopingAccelerationInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
		});
		
		motorAccelerationInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.contentEquals("")) motorAccelerationSlider.setValue(Double.parseDouble(newValue));
		});
		motorAccelerationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			motorAccelerationInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
		});
		
		maximumSteeringAngleInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.contentEquals("")) maximumSteeringAngleSlider.setValue(Double.parseDouble(newValue));
		});
		maximumSteeringAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			maximumSteeringAngleInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
		});
		
		motorSpeedInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.contentEquals("")) motorSpeedSlider.setValue(Double.parseDouble(newValue));
		});
		motorSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			motorSpeedInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
		});
		
		steeringMotorSpeedInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.contentEquals("")) steeringMotorSpeedSlider.setValue(Double.parseDouble(newValue));
		});
		steeringMotorSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			steeringMotorSpeedInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
		});
	}
	
	@FXML
	private void getParameters() throws RemoteException {
		
		if(motorSpeedInput.getText().contentEquals("")) {
			motorSpeed = 720;
			motorSpeedInput.setText("720");
		} else motorSpeed = Integer.parseInt(motorSpeedInput.getText());
		
		if(steeringMotorSpeedInput.getText().contentEquals("")) {
			steeringMotorSpeed = 60;
			steeringMotorSpeedInput.setText("60");
		} else steeringMotorSpeed = Integer.parseInt(steeringMotorSpeedInput.getText());
		
		if(motorAccelerationInput.getText().contentEquals("")) {
			motorAcceleration = 600;
			motorAccelerationInput.setText("600");
		} else motorAcceleration = Integer.parseInt(motorAccelerationInput.getText());
		
		if(maximumSteeringAngleInput.getText().contentEquals("")) {
			maximumSteeringAngle = 40;
			maximumSteeringAngleInput.setText("40");
		} else maximumSteeringAngle = Integer.parseInt(maximumSteeringAngleInput.getText());
		
		if(stopingAccelerationInput.getText().contentEquals("")) {
			stopingAcceleration = 200;
			stopingAccelerationInput.setText("200");
		} else stopingAcceleration = Integer.parseInt(stopingAccelerationInput.getText());
		
		MotorControl_v2.resetParameters();
		
		warningBox.setVisible(false);
		warningStatus.setVisible(false);
		angleGauge.clearSections();
		angleGauge.setSections(new Section(maximumSteeringAngle, 100, Color.ORANGERED), new Section(-100,-maximumSteeringAngle, Color.ORANGERED));
	}
	
	private void setAllEvents() throws RemoteException, InterruptedException {
		Scene scene = mainApp.getPrimaryStage().getScene();
		
		getParameters();
		
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
				
				if (event.getCode() == KeyCode.NUMPAD8 && !started) {
					commMotor.fifoQueue.add(8);
					started = true;
					boutonAvance.setStyle(buttonPressedStyle);
				}
				
				if (event.getCode() == KeyCode.NUMPAD5 && !started) {
					commMotor.fifoQueue.add(5);
					started = true;
					boutonRecule.setStyle(buttonPressedStyle);
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
				
				if (event.getCode() == KeyCode.NUMPAD9) {
					commMotor.fifoQueue.add(9);
				}
			}
		});
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.NUMPAD8 && started) {
					started = false;
					boutonAvance.setStyle("");
					commMotor.fifoQueue.add(-1);
				}
				
				if (event.getCode() == KeyCode.NUMPAD5 && started) {
					started = false;
					boutonRecule.setStyle("");
					commMotor.fifoQueue.add(-1);
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
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}
