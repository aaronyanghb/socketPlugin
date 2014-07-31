var socket = {
    connect: function(ip, successCallback,errorCallback) {
        cordova.exec(
        	successCallback, // success callback function
        	errorCallback, // error callback function
            'Socket', // mapped to our native Java class called "CalendarPlugin"
            'connect', // with this action name
            [ip]
        ); 
     },
     send:function(ip,message,successCallback,errorCallback) {
         cordova.exec(
        	successCallback, // success callback function
        	errorCallback, // error callback function
            'Socket', // mapped to our native Java class called "CalendarPlugin"
            'send', // with this action name
            [ip,message]
        );     
     }
}

module.exports = socket;


