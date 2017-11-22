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
	
	static RMIRegulatedMotor leftMotor = InterfxOverviewController.ev3.createRegulatedMotor("B",  'L');
    static RMIRegulatedMotor rightMotor = InterfxOverviewController.ev3.createRegulatedMotor("C", 'L');
    static RMIRegulatedMotor steeringMotor = InterfxOverviewController.ev3.createRegulatedMotor("D", 'M');
    public static int flag4pressed = 0;
    public static int flag6pressed = 0;
    public static volatile float angle = 0;
    
    public static void monitorAngle() {
		Runnable task = new Runnable() {
			public void run() {
				while(true) {
					try {
						angle = steeringMotor.getTachoCount();
						Thread.sleep(300);
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
        
        monitorAngle();
		
        steeringMotor.setSpeed(InterfxOverviewController.steeringMotorSpeed);
		
		while(true) {
			
			if (flag4pressed==1) {
				steeringMotor.forward();
				flag4pressed = 2;
			}
			if (flag4pressed==0) {
				steeringMotor.stop(true);
				flag4pressed = 2;
			}
			if (flag6pressed==1) {
				steeringMotor.backward();
				flag6pressed = 2;
			}
			if (flag6pressed==0) {
				steeringMotor.stop(true);
				flag6pressed = 2;
			}
			
//			if (flag4pressed==2) {
//				returnToZero();
//			}
//			
//			if (flag6pressed==2) {
//				returnToZero();
//			}
			
			Thread.sleep(1);
		}
	}
	
	public static void TurnLeft() throws RemoteException {
		do {
			steeringMotor.backward();
		} while (steeringMotor.getTachoCount() >= -InterfxOverviewController.maximumSteeringAngle);
		steeringMotor.stop(true);
	}
	
	public static void TurnRight() throws RemoteException {
		do {
			steeringMotor.forward();
		} while (steeringMotor.getTachoCount() <= InterfxOverviewController.maximumSteeringAngle);
		steeringMotor.stop(true);
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
//		steeringMotor.stop(true);
		steeringMotor.rotate(-steeringMotor.getTachoCount());
//		steeringMotor.stop(true);
	}
	
	public static void MotorStartForward() throws RemoteException {
		rightMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
    	leftMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		rightMotor.setSpeed(InterfxOverviewController.motorSpeed);
		leftMotor.setSpeed(InterfxOverviewController.motorSpeed);
		rightMotor.forward();
		leftMotor.forward();
	}
	
	public static void MotorStartBackward() throws RemoteException {
		rightMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
    	leftMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		rightMotor.setSpeed(InterfxOverviewController.motorSpeed);
		leftMotor.setSpeed(InterfxOverviewController.motorSpeed);
		rightMotor.backward();
		leftMotor.backward();
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
