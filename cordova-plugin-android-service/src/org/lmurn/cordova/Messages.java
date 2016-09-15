package org.lmurn.cordova;

/**
 * Messages constants class.
 * 
 * @author Luka Murn <murn.luka@gmail.com>
 */
public final class Messages {

	// Messages plugin -> service
	public static final int REGISTER_PLUGIN_CLIENT = 0;
	public static final int UNREGISTER_PLUGIN_CLIENT = 1;

	// Messages plugin -> service -> Android app
	public static final int PLUGIN_MESSAGE = 2;

	// Messages Android app -> service
	public static final int REGISTER_ANDROID_CLIENT = 3;
	public static final int UNREGISTER_ANDROID_CLIENT = 4;

	// Messages Android app -> service -> plugin
	public static final int ANDROID_APP_MESSAGE = 5;

	// JSON keys for values
	public static final String SIGNAL_KEY = "signal";
	public static final String DATA_KEY = "data";
	
	private Messages() {
		// This is only here to prevent this class from being instantialized
	}
}
