package robafis.interfx.view;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import javafx.animation.Timeline;

public class Chronometer {

	public static Queue<Integer> fifoChrono = new LinkedList<Integer>();
	static int instrChrono;
	static double valueChrono;

	// timeline.setCycleCount(Animation.INDEFINITE);
	// timeline.play();

	public static Timer timer = FxTimer.createPeriodic(Duration.ofSeconds(1), () -> {
		double value = InterfxOverviewController.chronometer.getValue();
		InterfxOverviewController.chronometer.setValue(value - 1);
	});

	public static void run() throws InterruptedException {

		while (InterfxOverviewController.running) {
			if (!fifoChrono.isEmpty()) {
				instrChrono = fifoChrono.remove();
				System.out.println(instrChrono);
				if (instrChrono == 1)
					timer.restart();
				if (instrChrono == 0)
					timer.stop();
				if (instrChrono == -1) {
					timer.stop();
					InterfxOverviewController.chronometer.setValue(120);
				}
			} else
				timer.stop();
			// valueChrono = InterfxOverviewController.chronometer.getValue();
			Thread.sleep(500);
		}
	}
}
