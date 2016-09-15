package org.lmurn.cordova;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class processes received messages on the {@link AndroidMessageService} side.
 * 
 * @author Luka Murn <murn.luka@gmail.com>
 */
public class ServiceIncomingHandler extends Handler {

	private static final String TAG = "AndroidMessageServicePlugin";

	private AndroidMessageService serviceReference;

	/**
	 * Initializes a new instance of {@link ServiceIncomingHandler}.
	 * @param serviceReference Reference to the {@link AndroidMessageService} service.
	 */
	public ServiceIncomingHandler(AndroidMessageService serviceReference) {
		super();
		this.serviceReference = serviceReference;
	}

	@Override
	public void handleMessage(Message message) {
		if (this.serviceReference != null) {
			switch (message.what) {
				case Messages.REGISTER_PLUGIN_CLIENT:
					Log.i(TAG, "(Service) Registering plugin client");
					this.serviceReference.getPluginClients().add(message.replyTo);
					break;
				case Messages.UNREGISTER_PLUGIN_CLIENT:
					Log.i(TAG, "(Service) Unregistering plugin client");
					this.serviceReference.getPluginClients().remove(message.replyTo);
					break;
				case Messages.REGISTER_ANDROID_CLIENT:
					Log.i(TAG, "(Service) Registering Android app client");
					this.serviceReference.getAndroidClients().add(message.replyTo);
					break;
				case Messages.UNREGISTER_ANDROID_CLIENT:
					Log.i(TAG, "(Service) Unregistering Android app client");
					this.serviceReference.getAndroidClients().remove(message.replyTo);
					break;
				case Messages.PLUGIN_MESSAGE:
					Log.i(TAG, "(Service) Message received from plugin, forwarding to Android apps [signal: " + getSignalFromMessage(message) + "]");
					resendMessageToClients(message, this.serviceReference.getAndroidClients());
					break;
				case Messages.ANDROID_APP_MESSAGE:
					Log.i(TAG, "(Service) Message received from Android app, forwarding to plugins [signal: " + getSignalFromMessage(message) + "]");
					resendMessageToClients(message, this.serviceReference.getPluginClients());
					break;
				default:
					super.handleMessage(message);
			}
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

	/**
	 * Resend the provided message to the list of {@link Messenger} clients.
	 * @param message The {@link Message} to be sent to the clients.
	 * @param clients A list of {@Messenger} clients to which the message should be sent.
	 */
	private void resendMessageToClients(Message message, List<Messenger> clients) {
		Iterator<Messenger> iter;
		Messenger messenger;

		iter = clients.iterator();
		while (iter.hasNext()) {
			messenger = iter.next();
			try {
				Message clone = new Message();
				clone.copyFrom(message);
				messenger.send(clone);
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list.
				Log.d(TAG, "(Service) Removing remote client because it's not responding");
				iter.remove();
			}
		}
	}

}
