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
	
	@FXML
	private Button setEvents;
	
	@FXML
	private Button boutonAvance;
	
	@FXML
	private Button boutonRecule;
	
	@FXML
	private Button boutonGauche;
	
	@FXML
	private Button boutonDroite;
	
	@FXML
	private Button closePortsButton;
	
	@FXML
	private TextField informationBox;
	
	@FXML
	private Label batteryInfo;
	
	@FXML
	private TextField motorSpeedInput;
	@FXML
	private TextField steeringMotorSpeedInput;
	@FXML
	private TextField motorAccelerationInput;
	@FXML
	private TextField maximumSteeringAngleInput;
	@FXML
	private TextField stopingAccelerationInput;
	
	@FXML
	private ImageView batteryView = new ImageView();
	
	private String buttonPressedStyle = "-fx-background-color:" +
    		"	 	 linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" + 
    		"        linear-gradient(#020b02, #3a3a3a),\n" + 
    		"        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n" + 
    		"        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n" + 
    		"        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%); ";
	
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
		
		HashMap<Button, KeyCode> extButton = new HashMap<>();
		extButton.put(boutonAvance, KeyCode.NUMPAD8);
		extButton.put(boutonRecule, KeyCode.NUMPAD5);
		extButton.put(boutonDroite, KeyCode.NUMPAD6);
		extButton.put(boutonGauche, KeyCode.NUMPAD4);
		
		for(Button mapKey : extButton.keySet()) {
			
			mapKey.addEventHandler(MouseEvent.MOUSE_PRESSED, 
					new EventHandler<MouseEvent>() {
						@Override public void handle(MouseEvent e) {
							mapKey.setStyle(buttonPressedStyle);
					}
				});
			
			mapKey.addEventHandler(MouseEvent.MOUSE_RELEASED, 
				    new EventHandler<MouseEvent>() {
				        @Override public void handle(MouseEvent e) {
				            mapKey.setStyle("");
				        }
				});
			
			mapKey.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if ((event.getCode() == extButton.get(mapKey)) && !started) {
						started = true;
						try {
							MotorControl_v2.MotorStartForward();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			
			mapKey.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == extButton.get(mapKey)) {
						started = false;
						try {
							MotorControl_v2.MotorStop();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});			
		}
		
		showBatteryLevel();
		MotorControl_v2.MotorControllerInit();
	}
	
	@FXML
	private void closeMotorPorts () throws RemoteException{
		MotorControl_v2.ClosePorts();
		informationBox.setText("Motor ports have successfully been closed");
	}
	
	Boolean startedSteering = false;
	Boolean gotHereFirst = true;
	
	@FXML
	private void setAllEvents() throws RemoteException, InterruptedException {
		Scene scene = mainApp.getPrimaryStage().getScene();
		
//		if (!gotHereFirst) MotorControl_v2.ClosePorts();
//		gotHereFirst = false;
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
						MotorControl_v2.TurnLeft();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
//				if (event.getCode() == KeyCode.NUMPAD7) {
//					try {
//						MotorControl_v2.returnToZero();
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				
				if (event.getCode() == KeyCode.NUMPAD6 && !startedSteering) {
					startedSteering = true;
					try {
						MotorControl_v2.TurnRight();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (event.getCode() == KeyCode.NUMPAD5 && !started) {
					started = true;
					try {
						MotorControl_v2.MotorStartForward();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (event.getCode() == KeyCode.NUMPAD8 && !started) {
					started = true;
					try {
						MotorControl_v2.MotorStartBackward();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
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
						MotorControl_v2.MotorStop();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (event.getCode() == KeyCode.NUMPAD5) {
					started = false;
					try {
						MotorControl_v2.MotorStop();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if ((event.getCode() == KeyCode.NUMPAD4 && startedSteering) || (event.getCode() == KeyCode.NUMPAD6 && startedSteering)) {
					startedSteering = false;
					try {
						MotorControl_v2.returnToZero();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
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
