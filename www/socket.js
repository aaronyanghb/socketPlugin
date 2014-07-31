var socket = {
    connect: function(ip, successCallback,errorCallback) {
        cordova.exec(
        	successCallback, // success callback function
        	errorCallback, // error callback function
            'Socket', // mapped to our native Java class called "CalendarPlugin"
            'connect', // with this action name
            [ip]
        ); 
     }
}

module.exports = socket;


