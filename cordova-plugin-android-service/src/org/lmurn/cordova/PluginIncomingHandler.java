package org.lmurn.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * This class processes received messages from the {@link AndroidMessageService}
 * on the {@link AndroidMessageServicePlugin} side.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 */
public class PluginIncomingHandler extends Handler {

	private static final String TAG = "AndroidMessageServicePlugin";

	private CallbackContext callbackContext;

	/**
	 * Initializes a new instance of {@link PluginIncomingHandler}.
	 * @param callbackContext The {@link CallbackContext} object which is used to
	 * 						  pass messages back to Cordova JavaScript application.
	 */
	public PluginIncomingHandler(CallbackContext callbackContext) {
		super();
		this.callbackContext = callbackContext;
	}

	@Override
	public void handleMessage(Message message) {
		try {
			JSONObject params = new JSONObject();

			switch (message.what) {
				case Messages.ANDROID_APP_MESSAGE:
					Bundle bundle = message.getData();
					String signal = bundle.getString(Messages.SIGNAL_KEY);
					LOG.i(TAG, "(Plugin) Received message from Android app [signal: " + signal + "]");
					params.put(Messages.SIGNAL_KEY, signal);
					String dataString = bundle.getString(Messages.DATA_KEY);
					JSONObject data = null;
					if (dataString != null) {
						data = (JSONObject)new JSONTokener(dataString).nextValue();
					}
					params.put(Messages.DATA_KEY, data);
					break;
				default:
					super.handleMessage(message);
			}

			if (params.has(Messages.SIGNAL_KEY) && this.callbackContext != null) {
				PluginResult result = new PluginResult(PluginResult.Status.OK, params);
				result.setKeepCallback(true);
				this.callbackContext.sendPluginResult(result);
			}
		} catch (JSONException e) {
			LOG.e(TAG, "(Plugin) Could not handle received message [error: " + e.toString() + "]");
		}
	}
}
