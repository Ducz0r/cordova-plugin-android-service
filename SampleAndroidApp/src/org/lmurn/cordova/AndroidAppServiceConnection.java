package org.lmurn.cordova;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Service connection class for communication between Android application
 * and {@link AndroidMessageService}.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 *
 */
public class AndroidAppServiceConnection implements ServiceConnection {

	private static final String TAG = "AndroidMessageServicePlugin";

	private static final String BIND_INTENT = "org.lmurn.cordova.BindAndroidApp";
	
	private boolean bound;
	private Messenger incomingMessenger;
	private Messenger service;

	/**
	 * Initializes a new instance of {@link AndroidAppServiceConnection}.
	 * @param incomingMessenger The {@link AndroidAppIncomingHandler} object to handle incoming messages.
	 */
	public AndroidAppServiceConnection(AndroidAppIncomingHandler incomingHandler) {
		super();
		this.bound = false;
		this.incomingMessenger = new Messenger(incomingHandler);
		this.service = null;
	}

	/**
	 * Get the reference to the service messenger (for sending messages to the service).
	 * @return The service reference.
	 */
	public Messenger getService() {
		return this.service;
	}

	/**
	 * Register this connection listener to the service.
	 * @param activity The {@link Activity} to which the service connection should be bound.
	 */
	public void register(Activity activity) {
		Log.i(TAG, "(Android App) Binding service to Android app service connection");
		Intent bindIntent = new Intent(BIND_INTENT);
		bound = activity.bindService(bindIntent, this, 0);
	}

	/**
	 * Unregisters this connection listener from the service.
	 * @param activity The {@link Activity} from which the service connection should be unbound.
	 */
	public void unregister(Activity activity) {
		Log.i(TAG, "(Android App) Unbinding service from Android app service connection");
		if (this.service != null) {
			try {
				Message message = Message.obtain(null, Messages.UNREGISTER_ANDROID_CLIENT);
				message.replyTo = this.incomingMessenger;
				this.service.send(message);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even do anything with it
			}
		}
		if (activity != null && bound) {
			activity.unbindService(this);
			bound = false;
		}
	}

	/**
	 * Send the message to the service.
	 */
	public void sendMessage(String signal, JSONObject data) {
		if (this.service != null && this.incomingMessenger != null) {
			try {
				Message message = Message.obtain(null, Messages.ANDROID_APP_MESSAGE);
				message.replyTo = this.incomingMessenger;

				Bundle bundle = new Bundle();
				bundle.putString(Messages.SIGNAL_KEY, signal);
				if (data != null) {
					bundle.putString(Messages.DATA_KEY, data.toString());
				}
				message.setData(bundle);

				this.service.send(message);
				Log.i(TAG, "(Android App) Sent message to the service [signal: " + signal + "]");
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even do anything with it
				Log.e(TAG, "(Android App) Could not send message to the service");
			}
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.i(TAG, "(Android App) Service connected to plugin");
		this.service = new Messenger(service);
		try {
			Message message = Message.obtain(null, Messages.REGISTER_ANDROID_CLIENT);
			message.replyTo = this.incomingMessenger;
			this.service.send(message);
		} catch (RemoteException e) {
			// In this case the service has crashed before we could even do anything with it
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
		// This is called when the connection with the service has been
		// unexpectedly disconnected - process crashed.
		this.service = null;
	}
}
