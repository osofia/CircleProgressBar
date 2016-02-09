package com.example.circleprogressbar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
	TextView tv;
	ProgressBar pBar;
	int pStatus = 0;
	private Handler handler = new Handler();
	float initialBalance = 25570f;
	float transactionAmount = 5000f;

	private LocationManager lm;

	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		pBar = (ProgressBar) findViewById(R.id.progressBar1);

		Button decreaseButton = (Button) findViewById(R.id.button1);

		decreaseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				increaseAmount(initialBalance, transactionAmount);
				pStatus = 0;
				initialBalance += transactionAmount;
			}

		});
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		altitude = location.getAltitude();
		accuracy = location.getAccuracy();

		String msg = String.format("new location", latitude, longitude, altitude, accuracy);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		String msg = "provider_disabled" + provider;
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		String msg = "provider_enabled" + provider;
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		String newStatus = "";
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			newStatus = "OUT_OF_SERVICE";
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			newStatus = "TEMPORARILY_UNAVAILABLE";
			break;
		case LocationProvider.AVAILABLE:
			newStatus = "AVAILABLE";
			break;
		}
		String msg = String.format("provider disabled", provider, newStatus);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(this);
	}

	private void setText(final TextView text, final String value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				text.setText(value);
			}
		});
	}

	public void decreaseAmount(final float initialBalance, final float transactionAmount) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				setText(tv, String.valueOf(initialBalance));
				// TODO Auto-generated method stub
				while (pStatus <= 100) {
					handler.post(new Runnable() {
						float currentValue = initialBalance - ((transactionAmount * pStatus) / 100);

						@Override
						public void run() {
							// TODO Auto-generated method stub
							pBar.setProgress(pStatus);
							pBar.setSecondaryProgress(pStatus + 5);
							setText(tv, "$ " + String.valueOf(currentValue));
						}
					});
					try {
						// Sleep for 200 milliseconds.
						// Just to display the progress slowly
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pStatus++;
				}
			}
		}).start();

	}

	public void increaseAmount(final float initialBalance, final float transactionAmount) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				setText(tv, String.valueOf(initialBalance));
				// TODO Auto-generated method stub
				while (pStatus <= 100) {
					handler.post(new Runnable() {
						float currentValue = initialBalance + ((transactionAmount * pStatus) / 100);

						@Override
						public void run() {
							// TODO Auto-generated method stub
							pBar.setProgress(pStatus);
							pBar.setSecondaryProgress(pStatus + 5);
							setText(tv, "$ " + String.valueOf(currentValue) + "\n" + latitude
									+"-"+longitude);
						}
					});
					try {
						// Sleep for 200 milliseconds.
						// Just to display the progress slowly
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pStatus++;
				}
			}
		}).start();

	}

	/** Play the sound using android.media.MediaPlayer */
	public static void playSound(Context context, int soundID) {
		MediaPlayer mp = MediaPlayer.create(context, soundID);
		mp.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}