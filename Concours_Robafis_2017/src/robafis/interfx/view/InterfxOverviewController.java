package robafis.interfx.view;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;

import org.reactfx.util.FxTimer;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
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
import robafis.interfx.Serveur;
import robafis.interfx.commMotor;
import robafis.interfx.view.Time;

public class InterfxOverviewController {

	@FXML private Button setParameters;
	@FXML private Button boutonAvance;
	@FXML private Button boutonRecule;
	@FXML private Button boutonGauche;
	@FXML private Button boutonDroite;
	@FXML private Button resetParameters;
	@FXML private Button chrono_start;
	@FXML private Button chrono_stop;
	@FXML private Button chrono_reset;
	@FXML private Button chrono_set120;
	@FXML private Button chrono_set360;
	@FXML private Button setRaceParams;
	@FXML private Button setPadParams;
	@FXML private Button obstacleAvoidButton;
	@FXML private Button radarOnOffButton;

	@FXML private Label warningBox;
	@FXML private Label obstacleAvoidLabel;
	@FXML private Label radarOnOffLabel;
	@FXML private Label emerBrakingLabel;

	@FXML private TextArea infoBox;
	@FXML private TextField portInput;
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
	@FXML private ImageView leftRadar;
	@FXML private ImageView centerRadar;
	@FXML private ImageView rightRadar;
	@FXML private ImageView reverseRadar;
	@FXML private ImageView obstacleAvoidButtonImage;
	@FXML private ImageView radarOnOffButtonImage;
	@FXML private ImageView emerBrakingStatus;

	@FXML private GridPane batteryPane;
	@FXML private GridPane anglePane;
	@FXML private GridPane speedPane;
	@FXML private GridPane chronoPane;
	
	private Image l_5 = new Image("file:ressources/radar/l_5.png");
	private Image l_4 = new Image("file:ressources/radar/l_4.png");
	private Image l_3 = new Image("file:ressources/radar/l_3.png");
	private Image l_2 = new Image("file:ressources/radar/l_2.png");
	private Image l_1 = new Image("file:ressources/radar/l_1.png");
	private Image l_0 = new Image("file:ressources/radar/l_0.png");
	
	private Image r_5 = new Image("file:ressources/radar/r_5.png");
	private Image r_4 = new Image("file:ressources/radar/r_4.png");
	private Image r_3 = new Image("file:ressources/radar/r_3.png");
	private Image r_2 = new Image("file:ressources/radar/r_2.png");
	private Image r_1 = new Image("file:ressources/radar/r_1.png");
	private Image r_0 = new Image("file:ressources/radar/r_0.png");
	
	private Image c_5 = new Image("file:ressources/radar/c_5.png");
	private Image c_4 = new Image("file:ressources/radar/c_4.png");
	private Image c_3 = new Image("file:ressources/radar/c_3.png");
	private Image c_2 = new Image("file:ressources/radar/c_2.png");
	private Image c_1 = new Image("file:ressources/radar/c_1.png");
	private Image c_0 = new Image("file:ressources/radar/c_0.png");
	
	private Image on = new Image("file:ressources/icons/on.png");
	private Image off = new Image("file:ressources/icons/off.png");

	private String buttonPressedStyle = "-fx-background-color: \n" + "        #3c7fb1,\n"
			+ "        linear-gradient(#61adc6, #56c4ff),\n"
			+ "        linear-gradient(#6dc9ff 0%, #6caed1 49%, #20acfc 50%, #6eb2d8 100%);\n"
			+ "    -fx-background-insets: 0,1,2;\n" + "    -fx-background-radius: 3,2,1;\n"
			+ "    -fx-font-size: 14px;";
	
	private String chronoButtonStyle = "    -fx-background-color: \n" + 
			"        #000000,\n" + 
			"        linear-gradient(#7ebcea, #2f4b8f),\n" + 
			"        linear-gradient(#426ab7, #263e75),\n" + 
			"        linear-gradient(#395cab, #223768);\n" + 
			"    -fx-background-insets: 0,1,2,3;\n" + 
			"    -fx-background-radius: 3,2,2,2;\n" + 
			"    -fx-padding: 12 30 12 30;\n" + 
			"    -fx-text-fill: white;\n" + 
			"    -fx-font-size: 12px;";

	public static int motorSpeed;
	public static int steeringMotorSpeed;
	public static int motorAcceleration;
	public static int maximumSteeringAngle;
	public static int stopingAcceleration;
	public static int valueChrono = 120;

	public static FloatProperty batteryLevel = new SimpleFloatProperty(0);
	public static FloatProperty angle = new SimpleFloatProperty(0);
	public static FloatProperty speed = new SimpleFloatProperty(0);
	public static DoubleProperty lSensor = new SimpleDoubleProperty(0.0);
	public static DoubleProperty cSensor = new SimpleDoubleProperty(0.0);
	public static DoubleProperty rSensor = new SimpleDoubleProperty(0.0);
	public static DoubleProperty bSensor = new SimpleDoubleProperty(0.0);
	

	private MainApp mainApp;
	private Gauge angleGauge;
	private Gauge batteryGauge;
	private Gauge speedGauge;
	static Gauge chronometer;

	private Time timeRemaining = new Time(new CountdownController());

	public static StringProperty message = new SimpleStringProperty("");

	Boolean started = false;
	Boolean startedSteering = false;
	Boolean startedReverse = false;
	Boolean obstacleAvoidBool = false;
	Boolean radarBool = false;
	public static Boolean startedBrutal = false;
	public static Boolean running = true;
	public static Boolean startedAvoid = false;

	public InterfxOverviewController() {
	}

	// THREAD COMMUNICATION MOTEUR
	public void commMotor_Thread() {
		Runnable task = new Runnable() {
			public void run() {
				try {
					message.set("Connecting to EV3, please wait...\n");
					commMotor.connectToEV3();
					message.set("Connection established!\n");
					message.set("Launching battery thread\n");
					message.set("Launching parameters thread\n");
					message.set("\nReady\n");
					setParameters.setDisable(false);
					motor1Status.setImage(new Image("file:ressources/icons/ok.png"));
					motor2Status.setImage(new Image("file:ressources/icons/ok.png"));
					steeringStatus.setImage(new Image("file:ressources/icons/ok.png"));
					commMotor.run();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
	
	// THREAD RADAR
	public void radar_Thread() {
		Runnable task = new Runnable() {
			public void run() {
				FxTimer.runPeriodically(Duration.ofMillis(300), () -> {
					commMotor.fifoQueue.add(97);
				});
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
	
	// THREAD OBSTACLE AVOIDANCE
	public void obstacleAvoidance_Thread() {
		Runnable task = new Runnable() {
			public void run() {
				FxTimer.runPeriodically(Duration.ofMillis(50), () -> {
					obstacleAvoidance();
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

		warningBox.setText("Parameters have been\nmodified, please reset");
		warningBox.setTextFill(Color.RED);
		setParameters.setDisable(true);
		setPadParams.setDisable(true);
		resetParameters.setDisable(true);
		setRaceParams.setDisable(true);
		warningStatus.setVisible(false);
		warningBox.setVisible(false);

		emerBrakingLabel.setVisible(false);
		emerBrakingStatus.setVisible(false);
		
		chrono_reset.setStyle(chronoButtonStyle);
		chrono_start.setStyle(chronoButtonStyle);
		chrono_stop.setStyle(chronoButtonStyle);
		chrono_set120.setStyle(chronoButtonStyle);
		chrono_set360.setStyle(chronoButtonStyle);
		
		warningStatus.setImage(new Image("file:ressources/icons/warning-icon-hi.png"));
		motor1Status.setImage(new Image("file:ressources/icons/ko.png"));
		motor2Status.setImage(new Image("file:ressources/icons/ko.png"));
		steeringStatus.setImage(new Image("file:ressources/icons/ko.png"));
		
		leftRadar.setImage(l_5);
		centerRadar.setImage(c_5);
		rightRadar.setImage(r_5);
		reverseRadar.setImage(c_5);

		radarOnOffButtonImage.setImage(off);		
		obstacleAvoidButtonImage.setImage(off);
		obstacleAvoidButton.setDisable(true);
		
		obstacleAvoidLabel.setTextFill(Color.LIGHTGRAY);
		radarOnOffLabel.setTextFill(Color.LIGHTGRAY);
		
		final ChangeListener batteryLevelListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				batteryGauge.setValue(batteryLevel.floatValue());
			}
		};

		batteryLevel.addListener(batteryLevelListener);

		final ChangeListener angleListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				angleGauge.setValue(angle.floatValue());
			}
		};

		angle.addListener(angleListener);

		final ChangeListener speedListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				speedGauge.setValue(speed.floatValue());
			}
		};

		speed.addListener(speedListener);

		final ChangeListener infoListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if (infoBox.getText() != null) {
					String oldText = infoBox.getText();
					infoBox.setText((String) oldText + newValue);
				}
			}
		};

		message.addListener(infoListener);
		
		final ChangeListener leftSensorListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if (radarBool) {
					if (((double) newValue) == Double.POSITIVE_INFINITY) {
						radarUpdate("leftRadar", 2.0);
					} else radarUpdate("leftRadar", ((double) newValue));
				} else radarUpdate("Off", 2.0);
			}
		};

		lSensor.addListener(leftSensorListener);
		
		final ChangeListener centerSensorListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if (radarBool) {
					if (((double) newValue) == Double.POSITIVE_INFINITY) {
						radarUpdate("centerRadar", 2.0);
					} else radarUpdate("centerRadar", ((double) newValue));
				} else radarUpdate("Off", 2.0);
			}
		};

		cSensor.addListener(centerSensorListener);
		
		final ChangeListener rightSensorListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if (radarBool) {
					if (((double) newValue) == Double.POSITIVE_INFINITY) {
						radarUpdate("rightRadar", 2.0);
					} else radarUpdate("rightRadar", ((double) newValue));
				} else radarUpdate("Off", 2.0);
			}
		};

		rSensor.addListener(rightSensorListener);
		
		final ChangeListener reverseSensorListener = new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if (radarBool) {
					if (((double) newValue) == Double.POSITIVE_INFINITY) {
					radarUpdate("reverseRadar", 2.0);
					} else radarUpdate("reverseRadar", ((double) newValue));
				} else radarUpdate("Off", 2.0);
			}
		};

		bSensor.addListener(reverseSensorListener);

		angleGauge = GaugeBuilder.create()
				.skinType(SkinType.HORIZONTAL)
				.prefSize(1000, 500).title("ANGLE")
				.titleColor(Color.BLACK).maxValue(85)
				.minValue(-85)
				.value(0).animated(true)
				.sectionsVisible(true)
				.sections(new Section(40, 100, Color.ORANGERED), new Section(-100, -40, Color.ORANGERED))
				.checkSectionsForValue(true)
				.animationDuration(100)
				.build();

		batteryGauge = GaugeBuilder.create()
				.skinType(SkinType.BATTERY)
				.value(100)
				.prefSize(800, 300)
				.gradientBarEnabled(true) // Gradient filled bar should be visible
				.gradientBarStops(new Stop(1.0, Color.GREEN), // Gradient for gradient bar
						new Stop(0.75, Color.YELLOWGREEN), new Stop(0.5, Color.YELLOW),
						new Stop(0.25, Color.DARKORANGE), new Stop(0.0, Color.ORANGERED))
				.build();

		speedGauge = GaugeBuilder.create()
				.skinType(SkinType.HORIZONTAL)
				.prefSize(1000, 500)
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

		chronometer = GaugeBuilder.create()
				.skinType(SkinType.FLAT)
				.title("CHRONO")
				.titleColor(Color.WHITE)
				.valueColor(Color.DEEPSKYBLUE)
				.value(120)
				.minValue(0)
				.maxValue(120)
				.animationDuration(850)
				.animated(true)
				.backgroundPaint(Color.DARKSLATEGREY)
				.sectionsVisible(true)
				.sections(new Section(0, 120, Color.LIMEGREEN))
				.prefSize(1000, 500)
				.build();

		batteryPane.setPadding(new Insets(0));
		batteryPane.add(batteryGauge, 0, 0);

		anglePane.setPadding(new Insets(5));
		anglePane.add(angleGauge, 0, 0);

		speedPane.setPadding(new Insets(5));
		speedPane.add(speedGauge, 0, 0);

		chronoPane.setPadding(new Insets(5));
		chronoPane.add(chronometer, 0, 0);

		commMotor_Thread();
		battery_Thread();
		params_Thread();
		radar_Thread();
		obstacleAvoidance_Thread();
		configureSliders();
	}
	
	private void obstacleAvoidance() {
		if (obstacleAvoidBool && !startedBrutal) {
			
			// STOP IF OBSTACLE DETECTED IN FRONT
			if (!startedReverse && cSensor.doubleValue() < 0.15) {
				// BRUTAL STOP
				startedBrutal = true;
				commMotor.fifoQueue.add(96);
				emerBrakingLabel.setVisible(true);
				emerBrakingStatus.setVisible(true);
			}
			
			// STOP IF OBSTACLE DETECTED BEHIND
			if (!started && bSensor.doubleValue() < 0.15) {
				// BRUTAL STOP
				startedBrutal = true;
				commMotor.fifoQueue.add(96);
				emerBrakingLabel.setVisible(true);
				emerBrakingStatus.setVisible(true);
			}
			
			
			if (!startedAvoid && !startedReverse && lSensor.doubleValue() < 0.3) {
				startedAvoid = true;
				//TURN RIGHT
				commMotor.fifoQueue.add(95);
			}
			
			if (!startedAvoid && !startedReverse && rSensor.doubleValue() < 0.3) {
				startedAvoid = true;
				//TURN LEFT
				commMotor.fifoQueue.add(94);
			}
		}
	}
	
	@FXML
	private void setRadarOnOff() {
		if (radarBool) {
			lSensor.set(2.0);
			rSensor.set(2.0);
			cSensor.set(2.0);
			bSensor.set(2.0);
			radarBool = false;
			radarOnOffButtonImage.setImage(off);
			obstacleAvoidButton.setDisable(true);
			obstacleAvoidLabel.setTextFill(Color.LIGHTGRAY);
			radarOnOffLabel.setTextFill(Color.LIGHTGRAY);
			if (obstacleAvoidBool) setObstacleAvoid();
		} else {
			radarBool = true;
			radarOnOffButtonImage.setImage(on);
			radarOnOffLabel.setTextFill(Color.BLACK);
			obstacleAvoidButton.setDisable(false);
			obstacleAvoidButtonImage.setImage(off);
			commMotor.fifoQueue.add(97);
		}
	}
	
	@FXML
	private void setObstacleAvoid() {
		if (obstacleAvoidBool) {
			obstacleAvoidBool=false;
			obstacleAvoidButtonImage.setImage(off);
			obstacleAvoidLabel.setTextFill(Color.LIGHTGRAY);
		}else {
			obstacleAvoidBool=true;
			obstacleAvoidButtonImage.setImage(on);
			obstacleAvoidLabel.setTextFill(Color.BLACK);
		}
	}

	@FXML
	private void chrono_start() throws InterruptedException {
		Thread.sleep(1000);
		timeRemaining.reset();
		if (valueChrono == 120)
			timeRemaining.addMinutes(2);
		else
			timeRemaining.addMinutes(6);
		Countdown.startCountdown(timeRemaining);
	}

	@FXML
	private void chrono_stop() {
		Countdown.stopCountdown();
	}

	@FXML
	private void chrono_reset() {
		chronometer.setMaxValue(valueChrono);
		chronometer.addSection(new Section(0, valueChrono, Color.LIMEGREEN));
		chronometer.setValue(valueChrono);
	}

	@FXML
	private void chrono_set120() {
		valueChrono = 120;
		chrono_reset();
	}

	@FXML
	private void chrono_set360() {
		valueChrono = 360;
		chrono_reset();
	}

	@FXML
	private void setParameters() throws InterruptedException, IOException {
		message.set("Parameters have been set\n");
		setAllEvents();
		setParameters.setDisable(true);
		resetParameters.setDisable(false);
		setRaceParams.setDisable(false);
		setPadParams.setDisable(false);

		if (portInput.getText().contentEquals("")) {
			Serveur ts = new Serveur(2017);
			portInput.setText("2017");
			ts.open();
		} else {
			Serveur ts = new Serveur(Integer.parseInt(portInput.getText()));
			ts.open();
		}

		portInput.setDisable(true);
		message.set("Connection is open on port " + portInput.getText());
		message.set("\nReady...\n");
	}

	private void configureSliders() {
		stopingAccelerationSlider.setSnapToTicks(true);
		maximumSteeringAngleSlider.setSnapToTicks(true);
		motorAccelerationSlider.setSnapToTicks(true);
		motorSpeedSlider.setSnapToTicks(true);
		steeringMotorSpeedSlider.setSnapToTicks(true);

		stopingAccelerationInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.contentEquals(""))
				stopingAccelerationSlider.setValue(Double.parseDouble(newValue));
		});
		stopingAccelerationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			stopingAccelerationInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
			resetParameters.setDisable(false);
		});

		motorAccelerationInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.contentEquals(""))
				motorAccelerationSlider.setValue(Double.parseDouble(newValue));
		});
		motorAccelerationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			motorAccelerationInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
			resetParameters.setDisable(false);
		});

		maximumSteeringAngleInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.contentEquals(""))
				maximumSteeringAngleSlider.setValue(Double.parseDouble(newValue));
		});
		maximumSteeringAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			maximumSteeringAngleInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
			resetParameters.setDisable(false);
		});

		motorSpeedInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.contentEquals(""))
				motorSpeedSlider.setValue(Double.parseDouble(newValue));
		});
		motorSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			motorSpeedInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
			resetParameters.setDisable(false);
		});

		steeringMotorSpeedInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.contentEquals(""))
				steeringMotorSpeedSlider.setValue(Double.parseDouble(newValue));
		});
		steeringMotorSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			steeringMotorSpeedInput.setText(Integer.toString(newValue.intValue()));
			warningBox.setVisible(true);
			warningStatus.setVisible(true);
			resetParameters.setDisable(false);
		});
	}

	@FXML
	private void setRaceParams() throws RemoteException {
		steeringMotorSpeedInput.setText("300");
		motorAccelerationInput.setText("1400");
		motorSpeedInput.setText("720");
		getParameters();
	}

	@FXML
	private void setPadParams() throws RemoteException {
		steeringMotorSpeedInput.setText("60");
		motorAccelerationInput.setText("300");
		motorSpeedInput.setText("200");
		getParameters();
	}

	@FXML
	private void getParameters() throws RemoteException {

		if (motorSpeedInput.getText().contentEquals("")) {
			motorSpeed = 720;
			motorSpeedInput.setText("720");
		} else
			motorSpeed = Integer.parseInt(motorSpeedInput.getText());

		if (steeringMotorSpeedInput.getText().contentEquals("")) {
			steeringMotorSpeed = 60;
			steeringMotorSpeedInput.setText("60");
		} else
			steeringMotorSpeed = Integer.parseInt(steeringMotorSpeedInput.getText());

		if (motorAccelerationInput.getText().contentEquals("")) {
			motorAcceleration = 600;
			motorAccelerationInput.setText("600");
		} else
			motorAcceleration = Integer.parseInt(motorAccelerationInput.getText());

		if (maximumSteeringAngleInput.getText().contentEquals("")) {
			maximumSteeringAngle = 40;
			maximumSteeringAngleInput.setText("40");
		} else
			maximumSteeringAngle = Integer.parseInt(maximumSteeringAngleInput.getText());

		if (stopingAccelerationInput.getText().contentEquals("")) {
			stopingAcceleration = 200;
			stopingAccelerationInput.setText("200");
		} else
			stopingAcceleration = Integer.parseInt(stopingAccelerationInput.getText());

		MotorControl_v2.resetParameters();

		warningBox.setVisible(false);
		warningStatus.setVisible(false);
		angleGauge.clearSections();
		angleGauge.setSections(new Section(maximumSteeringAngle, 100, Color.ORANGERED),
				new Section(-100, -maximumSteeringAngle, Color.ORANGERED));
		speedGauge.clearSections();
		speedGauge.setSections(new Section(motorSpeed, 800, Color.ORANGERED));
		resetParameters.setDisable(true);
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
					startedBrutal = false;
					emerBrakingLabel.setVisible(false);
					emerBrakingStatus.setVisible(false);
					boutonAvance.setStyle(buttonPressedStyle);
					speed.set(200);
				}

				if (event.getCode() == KeyCode.NUMPAD5 && !started) {
					commMotor.fifoQueue.add(5);
					started = true;
					startedReverse = true;
					startedBrutal = false;
					emerBrakingLabel.setVisible(false);
					emerBrakingStatus.setVisible(false);
					boutonRecule.setStyle(buttonPressedStyle);
					speed.set(200);
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
				if (event.getCode() == KeyCode.NUMPAD8 && started) {
					started = false;
					boutonAvance.setStyle("");
					commMotor.fifoQueue.add(-1);
					speed.set(0);
				}

				if (event.getCode() == KeyCode.NUMPAD5 && started) {
					started = false;
					startedReverse = false;
					boutonRecule.setStyle("");
					commMotor.fifoQueue.add(-1);
					speed.set(0);
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
	
private void radarUpdate(String radar, double dist) {
		
		if(radar.contentEquals("leftRadar")) {
			if(dist>=1.0) leftRadar.setImage(l_0);
			if(dist<0.8 && dist>=0.6) leftRadar.setImage(l_1);
			if(dist<0.6 && dist>=0.4) leftRadar.setImage(l_2);
			if(dist<0.4 && dist>=0.2) leftRadar.setImage(l_3);
			if(dist<0.2 && dist>=0.1) leftRadar.setImage(l_4);
			if(dist<0.1) leftRadar.setImage(l_5);
		}
		
		if(radar.contentEquals("centerRadar")) {
			if(dist>=1.0) centerRadar.setImage(c_0);
			if(dist<0.8 && dist>=0.6) centerRadar.setImage(c_1);
			if(dist<0.6 && dist>=0.4) centerRadar.setImage(c_2);
			if(dist<0.4 && dist>=0.2) centerRadar.setImage(c_3);
			if(dist<0.2 && dist>=0.1) centerRadar.setImage(c_4);
			if(dist<0.1) centerRadar.setImage(c_5);
		}
		
		if(radar.contentEquals("rightRadar")) {
			if(dist>=1.0) rightRadar.setImage(r_0);
			if(dist<0.8 && dist>=0.6) rightRadar.setImage(r_1);
			if(dist<0.6 && dist>=0.4) rightRadar.setImage(r_2);
			if(dist<0.4 && dist>=0.2) rightRadar.setImage(r_3);
			if(dist<0.2 && dist>=0.1) rightRadar.setImage(r_4);
			if(dist<0.1) rightRadar.setImage(r_5);
		}
		
		if(radar.contentEquals("reverseRadar")) {
			if(dist>=1.0) reverseRadar.setImage(c_0);
			if(dist<0.8 && dist>=0.6) reverseRadar.setImage(c_1);
			if(dist<0.6 && dist>=0.4) reverseRadar.setImage(c_2);
			if(dist<0.4 && dist>=0.2) reverseRadar.setImage(c_3);
			if(dist<0.2 && dist>=0.1) reverseRadar.setImage(c_4);
			if(dist<0.1) reverseRadar.setImage(c_5);
		}
		
		if (radar.contentEquals("off")) {
			leftRadar.setImage(l_0);
			centerRadar.setImage(c_0);
			rightRadar.setImage(r_0);
			reverseRadar.setImage(c_0);
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
