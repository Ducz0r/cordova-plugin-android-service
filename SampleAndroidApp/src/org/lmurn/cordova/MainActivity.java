package org.lmurn.cordova;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

/**
 * Sample main activity.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 */
public class MainActivity extends Activity {

	/** Plugin service related vars */
	private AndroidAppIncomingHandler incomingHandler;
	private AndroidAppServiceConnection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		// Initialize communication with service
		incomingHandler = new AndroidAppIncomingHandler() {
			@Override
			protected void onMessageReceived(String signal, JSONObject data) {
				// TODO: Process received message
			}
		};
		connection = new AndroidAppServiceConnection(incomingHandler);
		connection.register(this);
		
		// To send a message, simply call the following line of code
		// connection.sendMessage("<signal>", new JSONObject());
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// Destroy communication with service
		incomingHandler = null;
		connection.unregister(this);

		super.onDestroy();
	}
}
