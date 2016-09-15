package org.lmurn.cordova;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class processes received messages from the {@link AndroidMessageService} on the
 * Android application side.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 */
public abstract class AndroidAppIncomingHandler extends Handler {

	private static final String TAG = "AndroidMessageServicePlugin"; 
	
	/**
	 * Initializes a new instance of {@link AndroidAppIncomingHandler}.
	 */
	public AndroidAppIncomingHandler() {
		super();
	}

	@Override
	public void handleMessage(Message message) {
		try {
			JSONObject params = new JSONObject();

			switch (message.what) {
				case Messages.ANDROID_APP_MESSAGE:
					String signal = getSignalFromMessage(message);
					Log.i(TAG, "(Android App) Received message from Cordova plugin [signal: " + signal + "]");
					params.put(Messages.SIGNAL_KEY, signal);
					Bundle bundle = message.getData();
					String dataString = bundle.getString(Messages.DATA_KEY);
					JSONObject data = null;
					if (dataString != null) {
						data = (JSONObject)new JSONTokener(dataString).nextValue();
					}
					params.put(Messages.DATA_KEY, data);

					// Call handler
					onMessageReceived(signal, data);
					break;
				default:
					super.handleMessage(message);
			}
		} catch (JSONException e) {
			Log.e(TAG, "(Android app) Could not handle received message [error: " + e.toString() + "]");
		}
	}

	/**
	 * Retrieve the signal String from the given message.
	 * @param message The message.
	 * @return The signal.
	 */
	private String getSignalFromMessage(Message message) {
		String signal = null;
		Bundle bundle = message.getData();
		signal = bundle != null ? bundle.getString(Messages.SIGNAL_KEY) : null;
		if (signal == null) {
			signal = "";
		}
		return signal;
	}
	
	protected abstract void onMessageReceived(String signal, JSONObject data);
}
