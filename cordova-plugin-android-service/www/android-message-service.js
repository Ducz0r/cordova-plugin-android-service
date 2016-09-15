module.exports = {
  init: function(success, failure) {
    cordova.exec(
      success,
      failure,
      'ANDROID_MESSAGE_SERVICE_PLUGIN',
      'init',
      []
    );
  },
  send: function(signal, data, success, failure) {
      cordova.exec(
      success,
      failure,
      'ANDROID_MESSAGE_SERVICE_PLUGIN',
      'send',
      [signal, data]
    );
  }
};