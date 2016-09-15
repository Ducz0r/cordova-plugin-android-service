package org.lmurn.cordova;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Background service used for communication between Cordova plugin and Android app.
 *
 * @author Luka Murn <murn.luka@gmail.com>
 *
 */
public class AndroidMessageService extends Service {

	private static final String TAG = "AndroidMessageServicePlugin";

	// Arrays of clients that are connected & bound to this service
	private List<Messenger> pluginClients;
	private List<Messenger> androidClients;

	// Messenger for handling received messages
	private Messenger incomingMessenger;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "(Service) Service created");
		this.pluginClients = new ArrayList<Messenger>();
		this.androidClients = new ArrayList<Messenger>();
		this.incomingMessenger = new Messenger(new ServiceIncomingHandler(this));
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "(Service) Binding to remote client");
		if (this.incomingMessenger != null) {
			return this.incomingMessenger.getBinder();
		} else {
			return null;
		}
	}

	public List<Messenger> getPluginClients() {
		return this.pluginClients;
	}

	public List<Messenger> getAndroidClients() {
		return this.androidClients;
	}
}
