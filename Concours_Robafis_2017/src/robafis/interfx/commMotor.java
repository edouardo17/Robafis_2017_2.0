package robafis.interfx;

import java.io.IOException;
import java.io.PipedReader;
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
    static int instr;
	
	public static Queue<Integer> fifoQueue = new LinkedList<Integer>();
	
	public static void run() throws IOException, InterruptedException {
		
		while(InterfxOverviewController.running) {
			if (!fifoQueue.isEmpty()) {
				
				instr = fifoQueue.remove();
				if (instr==4) MotorControl_v2.TurnLeft(); 
				if (instr==6) MotorControl_v2.TurnRight();
				if (instr==0) MotorControl_v2.returnToZero();
				if (instr==98) getParams();
				if (instr==99) getBatteryLevel();
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
		InterfxOverviewController.angle.set(-angle);
	}
}
