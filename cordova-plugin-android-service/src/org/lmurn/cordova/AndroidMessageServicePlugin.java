package org.lmurn.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cordova plugin that allows Cordova application to communicate
 * with a native Android app via messaging service.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 *
 */
public class AndroidMessageServicePlugin extends CordovaPlugin {

	private static final String TAG = "AndroidMessageServicePlugin";

	// Actions
	private static final String ACTION_INIT = "init";
	private static final String ACTION_SEND = "send";

	// Saved Cordova callback context for repeated messages
	// back to Cordova (JS) - in init action
	private CallbackContext listenerCallbackContext = null;

	// Handles incoming messages (Android background service -> this plugin)
	private PluginIncomingHandler incomingHandler;
	
	// Handles connection to service (This plugin <-> Android background service)
	private PluginServiceConnection connection;

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		LOG.setLogLevel(LOG.INFO);
		LOG.i(TAG, "Execute action = " + action);

		if (ACTION_INIT.equals(action)) {
			// Remember a callback context
			listenerCallbackContext = callbackContext;
			LOG.d(TAG, callbackContext != null ? "Callback context not null" : "Callback context is null");

			// Init the incoming handler from the service
			incomingHandler = new PluginIncomingHandler(listenerCallbackContext);
			LOG.d(TAG, incomingHandler != null ? "Incoming handler initialized" : "Incoming handler is null");

			// Initialize connection
			connection = new PluginServiceConnection(incomingHandler);
			LOG.d(TAG, connection != null ? "Connection initialized" : "Connection is null");

			// Initialize the service
			connection.register(cordova.getActivity());
			
			if (this.incomingHandler != null && this.connection != null) {
				// Respond with an "initialized ok" callback & setup
				// continuous callbacks
				JSONObject params = new JSONObject();
				params.put("signal", "INIT_OK");
				params.put("data", new JSONObject());
				PluginResult result = new PluginResult(PluginResult.Status.OK, params);
				result.setKeepCallback(true);
				callbackContext.sendPluginResult(result);
				return true;
			} else {
				callbackContext.error("Could not initialize service");
				return false;
			}
		} else if (ACTION_SEND.equals(action)) {
			if (this.connection != null) {
				String signal = args.getString(0);
				JSONObject data = args.getJSONObject(1);
				this.connection.sendMessage(signal, data);

				callbackContext.success("Successfully sent command");
				return true;
			} else {
				callbackContext.error("Connection must be previously initialized");
				return false;
			}
		} else {
			callbackContext.error("Invalid action");
			return false;
		}
	}

	@Override
	public void onDestroy() {
		if (this.connection != null) {
			connection.unregister(cordova.getActivity());
		}
		listenerCallbackContext = null;
		incomingHandler = null;
	}
}
