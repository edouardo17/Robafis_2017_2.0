package robafis.interfx;

import java.rmi.RemoteException;

import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.utility.Delay;
import robafis.interfx.view.InterfxOverviewController;

public class MotorControl_v2 {

	public static float maxSteeringAngle;
	public static float ratio;
	static RMIRegulatedMotor leftMotor = commMotor.ev3.createRegulatedMotor("B", 'L');
	static RMIRegulatedMotor rightMotor = commMotor.ev3.createRegulatedMotor("C", 'L');
	static RMIRegulatedMotor steeringMotor = commMotor.ev3.createRegulatedMotor("D", 'M');

	public static void resetParameters() throws RemoteException {
		steeringMotor.setSpeed(InterfxOverviewController.steeringMotorSpeed);
		
		rightMotor.setSpeed(InterfxOverviewController.motorSpeed);
		leftMotor.setSpeed(InterfxOverviewController.motorSpeed);
		maxSteeringAngle = InterfxOverviewController.maximumSteeringAngle;
		
		ratio = (float) maxSteeringAngle / (float) 60.0;
	}

	public static void autoRun() throws RemoteException {
		leftMotor.setSpeed(500);
		rightMotor.setSpeed(500);
		leftMotor.rotate(-1930, true);
		rightMotor.rotate(-1930, false);

		leftMotor.rotate(-1200, true);
		rightMotor.rotate(-1200, true);
		steeringMotor.rotate(-17, true);
		Delay.msDelay(3000);
		steeringMotor.rotate(20);
	}

	public static void TurnLeft() throws RemoteException {
		steeringMotor.forward();
	}

	public static void TurnRight() throws RemoteException {
		steeringMotor.backward();
	}

	public static void steeringStop() throws RemoteException {
		steeringMotor.stop(true);
	}

	public static void MotorStartForward() throws RemoteException {
		rightMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		leftMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		rightMotor.backward();
		leftMotor.backward();
	}

	public static void MotorStartBackward() throws RemoteException {
		rightMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		leftMotor.setAcceleration(InterfxOverviewController.motorAcceleration);
		rightMotor.forward();
		leftMotor.forward();
	}

	public static void MotorStop() throws RemoteException {
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

	public static void CalibrateLeft() throws RemoteException {
		steeringMotor.rotate(3);
		steeringMotor.resetTachoCount();
	}

	public static void CalibrateRight() throws RemoteException {
		steeringMotor.rotate(-3);
		steeringMotor.resetTachoCount();
	}
}
