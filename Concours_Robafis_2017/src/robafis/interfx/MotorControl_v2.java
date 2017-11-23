package robafis.interfx;

import java.rmi.RemoteException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Power;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.SampleProvider;
import robafis.interfx.view.InterfxOverviewController;
import sun.tools.jar.Main;

public class MotorControl_v2 {
	
	static RMIRegulatedMotor leftMotor = commMotor.ev3.createRegulatedMotor("B",  'L');
    static RMIRegulatedMotor rightMotor = commMotor.ev3.createRegulatedMotor("C", 'L');
    static RMIRegulatedMotor steeringMotor = commMotor.ev3.createRegulatedMotor("D", 'M');
    public static int flag4pressed = 0;
    public static int flag6pressed = 0;
    
    public static void monitorAngle() {
		Runnable task = new Runnable() {
			public void run() {
				while(true) {
					try {
						commMotor.angle = steeringMotor.getTachoCount();
						Thread.sleep(10);
					} catch (RemoteException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
			}
		};
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
	
	public static void MotorControllerInit() throws RemoteException, InterruptedException{
		
		steeringMotor.resetTachoCount();
        leftMotor.resetTachoCount();
        rightMotor.resetTachoCount();
		
        steeringMotor.setSpeed(InterfxOverviewController.steeringMotorSpeed);
		
	}
	
	public static void TurnLeft() throws RemoteException {
		steeringMotor.setSpeed(60);
		steeringMotor.forward();
//		do {
//			steeringMotor.backward();
//		} while (steeringMotor.getTachoCount() >= -InterfxOverviewController.maximumSteeringAngle);
//		steeringMotor.stop(true);
	}
	
	public static void TurnRight() throws RemoteException {
		steeringMotor.backward();
//		do {
//			steeringMotor.forward();
//		} while (steeringMotor.getTachoCount() <= InterfxOverviewController.maximumSteeringAngle);
//		steeringMotor.stop(true);
	}
	
	public static void CalibrateLeft() throws RemoteException {
		steeringMotor.rotate(-2);
		steeringMotor.resetTachoCount();
	}
	
	public static void CalibrateRight() throws RemoteException {
		steeringMotor.rotate(2);
		steeringMotor.resetTachoCount();
	}
	
	public static void returnToZero() throws RemoteException {
		steeringMotor.stop(true);
//		steeringMotor.rotate(-steeringMotor.getTachoCount());
//		steeringMotor.stop(true);
	}
	
	public static void MotorStartForward() throws RemoteException {
		rightMotor.setAcceleration(500);
    	leftMotor.setAcceleration(500);
		rightMotor.setSpeed(500);
		leftMotor.setSpeed(500);
		rightMotor.backward();
		leftMotor.backward();
	}
	
	public static void MotorStartBackward() throws RemoteException {
		rightMotor.forward();
		leftMotor.forward();
	}
	
	public static void MotorStop() throws RemoteException{
		rightMotor.setAcceleration(InterfxOverviewController.stopingAcceleration);
    	leftMotor.setAcceleration(InterfxOverviewController.stopingAcceleration);
		rightMotor.stop(true);
		leftMotor.stop(true);
	}
	
	public static void ClosePorts() throws RemoteException {
		rightMotor.stop(true);
		leftMotor.stop(true);
		steeringMotor.stop(true);
		leftMotor.close();
		rightMotor.close();
		steeringMotor.close();
	}
}
