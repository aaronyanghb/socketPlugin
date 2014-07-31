var socketClient = {
    connect: function(addressList, successCallback,errorCallback) {
        cordova.exec(
        	successCallback, // success callback function
        	errorCallback, // error callback function
            'SocketClient', // mapped to our native Java class called "CalendarPlugin"
            'connect', // with this action name
            addressList
        ); 
     },
     send:function(message,successCallback,errorCallback) {
         cordova.exec(
        	successCallback, // success callback function
        	errorCallback, // error callback function
            'SocketClient', // mapped to our native Java class called "CalendarPlugin"
            'send', // with this action name
            [message]
        );     
     }
}

module.exports = socketClient;


