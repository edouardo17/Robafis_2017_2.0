/*
 * Copyright 2015 Florian Bauer, florian.bauer@posteo.de
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package robafis.interfx.view;

import robafis.interfx.view.CountdownController;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by Florian Bauer on 17.01.15.
 */
public class Time {

	private boolean hasTimeLeft;
	private SimpleIntegerProperty seconds;

	public Time(CountdownController countdownController) {
		seconds = new SimpleIntegerProperty(0);
		seconds.addListener(countdownController);
		hasTimeLeft = true;
	}

	public void addMinutes(int i) {
		seconds.set(seconds.get() + (i * 60));
		if (seconds.get() > 0) {
			hasTimeLeft = true;
		}
	}

	public void subMinutes(int i) {
		seconds.set(seconds.get() - (i * 60));
		if (seconds.get() <= 0) {
			hasTimeLeft = false;
		}
	}

	public boolean hasTimeLeft() {
		return hasTimeLeft;
	}

	@Override
	public String toString() {
		// hh:MM:ss
		String seconds = String.valueOf(this.seconds.get() % 60);
		String minutes = String.valueOf(this.seconds.get() / 60 % 60);
		String hours = String.valueOf(this.seconds.get() / 60 / 60);
		seconds = seconds.length() == 1 ? "0" + seconds : seconds;
		minutes = minutes.length() == 1 ? "0" + minutes : minutes;
		hours = hours.length() == 1 ? "0" + hours : hours;
		return hours + ":" + minutes + ":" + seconds;
	}

	public void reset() {
		seconds.set(0);
		hasTimeLeft = false;
	}

	public void subSeconds(int i) {
		seconds.set(seconds.get() - i);
		if (seconds.get() <= 0) {
			hasTimeLeft = false;
		}
	}
}
