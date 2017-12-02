package robafis.interfx;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

import lejos.hardware.Battery;
import lejos.hardware.BrickFinder;
import lejos.remote.ev3.RemoteEV3;
import robafis.interfx.view.InterfxOverviewController;

public class commMotor {
	
	public static RemoteEV3 ev3;
    public static float angle = 0;
    public static float speed = 0;
    static int instr;
    static float maxSteeringAngle = (float) 40.0;
    public static Boolean startedSteering = false;
    static int localAngle;
	
	public static Queue<Integer> fifoQueue = new LinkedList<Integer>();
	
	public static void run() throws IOException, InterruptedException {
		
//		if(InterfxOverviewController.maximumSteeringAngle != 0) {
//			maxSteeringAngle = InterfxOverviewController.maximumSteeringAngle;
//		} else {
//			maxSteeringAngle = 60;
//		}
		
		float ratio = (float) maxSteeringAngle/ (float) 60.0;
		
		while(InterfxOverviewController.running) {
			if (!fifoQueue.isEmpty()) {
				
				instr = fifoQueue.remove();
				if (instr==4) {MotorControl_v2.TurnLeft(); startedSteering=true;}
				if (instr==6) {MotorControl_v2.TurnRight(); startedSteering=true;}
				if (instr==8) {MotorControl_v2.MotorStartForward();}
				if (instr==5) {MotorControl_v2.MotorStartBackward();}
				if (instr==-1) {MotorControl_v2.MotorStop();}
				if (instr==0) {MotorControl_v2.steeringMotor.rotateTo(0, true);}
				if (instr==9) {MotorControl_v2.autoRun();}
				if (instr==98) getParams();
				if (instr==99) getBatteryLevel();
				if (instr >= -160 && instr < -100) {MotorControl_v2.steeringMotor.rotateTo(Math.round(-((instr+100)*ratio)), true); startedSteering=true;}
				if (instr >= 100 && instr <= 160) {MotorControl_v2.steeringMotor.rotateTo(Math.round(-((instr-100)*ratio)), true); startedSteering=true;}
				if (instr >= 200 && instr <=920) {
					MotorControl_v2.leftMotor.setSpeed(instr);
					MotorControl_v2.rightMotor.setSpeed(instr);
					MotorControl_v2.MotorStartForward();
				}
			}
			else {
				if (!startedSteering) {
					try {
						localAngle = MotorControl_v2.steeringMotor.getTachoCount();
						if (localAngle>=4) MotorControl_v2.TurnRight();
						else {if (localAngle<=-4) MotorControl_v2.TurnLeft();
							else MotorControl_v2.steeringStop();
						}
					} catch (IndexOutOfBoundsException e) {InterfxOverviewController.message.set("Index out of bounds\n");}
				}
			}
			Thread.sleep(10);
		}
	}
	
	public static void connectToEV3() {
		ev3 = (RemoteEV3) BrickFinder.getDefault();
	}
	
	public static void getBatteryLevel() {
		float voltage = Battery.getVoltage();
		float maximumVoltage = (float) 2.75;
		float currentVoltage = (float) (((voltage-6.25)/maximumVoltage)*100);
		
		InterfxOverviewController.batteryLevel.set(currentVoltage);
	}
	
	private static void getParams() throws RemoteException {
		angle = MotorControl_v2.steeringMotor.getTachoCount();
		if (MotorControl_v2.leftMotor.isMoving()) {
			speed = -MotorControl_v2.leftMotor.getTachoCount();
		} 
		InterfxOverviewController.speed.set(speed);
		InterfxOverviewController.angle.set(-angle);
		
	}
}
