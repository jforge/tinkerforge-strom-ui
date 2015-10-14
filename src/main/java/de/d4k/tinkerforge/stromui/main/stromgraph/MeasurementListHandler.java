package de.d4k.tinkerforge.stromui.main.stromgraph;

import java.time.LocalTime;
import java.util.Random;

import org.devoxx4kids.Bricklet;
import org.devoxx4kids.BrickletReader;

import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.IPConnection;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

/**
 * Kümmert sich um das Verarbeiten der Messwerte.
 * 
 * @author Oliver Milke
 *
 */
public class MeasurementListHandler extends Thread {
	final ObservableList<Data<String, Number>> chartData;

	public MeasurementListHandler(final ObservableList<Data<String, Number>> chartData) {
		
		setDaemon(true);
		setName("Measurement Thread");

		this.chartData = chartData;
	}
	
	@Override
	public void run() {
		try {
			// connectBricklet();
			mockValues();
		} catch (Exception e) {
			System.out.println("Fehler beim Lesen des Stroms :(");
			System.out.println(e);
		}
		
	}


	private void mockValues() {

		while (true) {
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			long value = Math.abs(new Random().nextLong() % 100);
			
			System.out.println("value " + value);
			addValue(value);
		}

	}

	private void addValue(Long value) {
		
		String label = getFormattedTimestamp();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chartData.add(new Data<>(label, value));
			}
		});

	}

	@SuppressWarnings("unused")
	private void connectBricklet() throws Exception {

		BrickletReader brickletReader = new BrickletReader();
		Bricklet currentVoltageBricklet = brickletReader.readBricklets()
				.getBrickletByDeviceId(BrickletVoltageCurrent.DEVICE_IDENTIFIER);

		IPConnection ipcon = new IPConnection();
		BrickletVoltageCurrent cv = new BrickletVoltageCurrent(currentVoltageBricklet.getUid(), ipcon);

		ipcon.connect(BrickletReader.HOST, BrickletReader.PORT);

		cv.setCurrentCallbackPeriod(1000l);
		cv.addCurrentListener(current -> addValue((long) current));

	}

	private String getFormattedTimestamp() {

		LocalTime now = LocalTime.now();
		return now.toString();

	}

}