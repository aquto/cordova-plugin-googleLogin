//
//  GoogleLogin.js
//


var exec = require('cordova/exec');

module.exports = {
    login: function(success, error, options) {
        cordova.exec(success, error, "GoogleLogin", "login", [options]);
    },
    logout: function(success, error) {
        cordova.exec(success, function(err) { error('GoogleLogin not available.'); }, "GoogleLogin", "logout", []);
    }

};
