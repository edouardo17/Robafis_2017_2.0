package robafis.interfx;

import java.rmi.RemoteException;

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
	
	public static void MotorController() throws RemoteException, InterruptedException{
		
		RMIRegulatedMotor leftMotor = InterfxOverviewController.ev3.createRegulatedMotor("B", 'L');
        RMIRegulatedMotor rightMotor = InterfxOverviewController.ev3.createRegulatedMotor("C", 'L');
        
        int accel = 70; //Vitesse du moteur en %
        rightMotor.setSpeed(accel);
        leftMotor.setSpeed(accel);
        rightMotor.backward();
        leftMotor.backward();
        
        while(accel!=1000) {
        	
        	rightMotor.setSpeed(accel);
        	leftMotor.setSpeed(accel);
//        	Thread.sleep(10);
        	accel += 10;
        	
        }
        
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

        rightMotor.stop(true);;
        leftMotor.close();
        rightMotor.close();
	}
}
