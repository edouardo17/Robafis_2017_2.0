package robafis.interfx;

import java.rmi.RemoteException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Power;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import robafis.interfx.view.InterfxOverviewController;
import sun.tools.jar.Main;

public class MotorControl {
	
	static RMIRegulatedMotor leftMotor = InterfxOverviewController.ev3.createRegulatedMotor("B", 'L');
    static RMIRegulatedMotor rightMotor = InterfxOverviewController.ev3.createRegulatedMotor("C", 'L');
    static RMIRegulatedMotor steeringMotor = InterfxOverviewController.ev3.createRegulatedMotor("D", 'M');
    
    public static int steeringAngle = 0;
	
	public static void MotorControllerInit() throws RemoteException, InterruptedException{
        
//        int accel = 80; //Vitesse du moteur en %
//        rightMotor.setSpeed(accel);
//        leftMotor.setSpeed(accel);

        
//        while(accel<1000) {
//        	
//        	rightMotor.setSpeed(accel);
//        	leftMotor.setSpeed(accel);
//        	accel +=20;
//        }
        
//        rightMotor.backward();
//        Thread.sleep(300);
//        
//        rightMotor.setSpeed(150);
//        
//        Thread.sleep(300);
//        
//        rightMotor.setSpeed(40);
//        Thread.sleep(300);
        
//        Thread.sleep(5000);

//        rightMotor.stop(true);;

	}
	
	public static void TurnLeft() {
		if (steeringAngle >= -40) {
			steeringAngle -= 5;
			try {
				steeringMotor.rotate(-5);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void TurnRight() {
		if (steeringAngle <= 40) {
			steeringAngle += 5;
			try {
				steeringMotor.rotate(5);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void returnToZero() throws RemoteException {
		steeringMotor.rotate(-steeringAngle);
		steeringAngle=0;
	}
	
	public static void MotorStartForward() throws RemoteException {
		rightMotor.setSpeed(InterfxOverviewController.motorSpeed);
		leftMotor.setSpeed(InterfxOverviewController.motorSpeed);
		rightMotor.forward();
		leftMotor.forward();
	}
	
	public static void MotorStartBackward() throws RemoteException {
		rightMotor.setSpeed(InterfxOverviewController.motorSpeed);
		leftMotor.setSpeed(InterfxOverviewController.motorSpeed);
		rightMotor.backward();
		leftMotor.backward();
	}
	
	public static void MotorStop() throws RemoteException{
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
