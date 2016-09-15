package org.lmurn.cordova;

import org.apache.cordova.LOG;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * Service connection class for communication between {@link AndroidMessageServicePlugin} and {@link AndroidMessageService}.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 *
 */
public class PluginServiceConnection implements ServiceConnection {

	private static final String TAG = "AndroidMessageServicePlugin";

	private boolean bound;
	private Messenger incomingMessenger;
	private Messenger service;

	/**
	 * Initializes a new instance of {@link PluginServiceConnection}.
	 * @param incomingMessenger The {@link PluginIncomingHandler} object to handle incoming messages.
	 */
	public PluginServiceConnection(PluginIncomingHandler incomingHandler) {
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
		LOG.i(TAG, "(Plugin) Binding service to plugin service connection");
		bound = activity.bindService(new Intent(activity, AndroidMessageService.class), this, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Unregisters this connection listener from the service.
	 * @param activity The {@link Activity} from which the service connection should be unbound.
	 */
	public void unregister(Activity activity) {
		LOG.i(TAG, "(Plugin) Unbinding service from plugin service connection");
		if (this.service != null) {
			try {
				Message message = Message.obtain(null, Messages.UNREGISTER_PLUGIN_CLIENT);
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
				Message message = Message.obtain(null, Messages.PLUGIN_MESSAGE);
				message.replyTo = this.incomingMessenger;

				Bundle bundle = new Bundle();
				bundle.putString(Messages.SIGNAL_KEY, signal);
				bundle.putString(Messages.DATA_KEY, data.toString());
				message.setData(bundle);

				this.service.send(message);
				LOG.i(TAG, "(Plugin) Sent message to the service [signal: " + signal + "]");
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even do anything with it
				LOG.e(TAG, "(Plugin) Could not send message to the service");
			}
		}
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		LOG.i(TAG, "(Plugin) Service connected to plugin");
		this.service = new Messenger(service);
		try {
			Message message = Message.obtain(null, Messages.REGISTER_PLUGIN_CLIENT);
			message.replyTo = this.incomingMessenger;
			this.service.send(message);
		} catch (RemoteException e) {
			// In this case the service has crashed before we could even do anything with it
			LOG.e(TAG, "(Plugin) Could not register plugin client to the service");
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
		// This is called when the connection with the service has been
		// unexpectedly disconnected - process crashed.
		this.service = null;
	}
}
