'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.startSmsRetrieverForRegex = exports.startSmsRetrieverForOtpLength = exports.startSmsRetrieverForOtp = exports.startSmsRetriever = undefined;

var _reactNative = require('react-native');

var RNAndroidSmsRetriever = _reactNative.NativeModules.RNAndroidSmsRetriever;
var startSmsRetriever = exports.startSmsRetriever = function startSmsRetriever(successCallback, failureCallback) {
    return RNAndroidSmsRetriever.startSmsRetriever(successCallback, failureCallback);
};

var startSmsRetrieverForOtp = exports.startSmsRetrieverForOtp = function startSmsRetrieverForOtp(successCallback, failureCallback) {
    return RNAndroidSmsRetriever.startSmsRetrieverForOtp(successCallback, failureCallback);
};

var startSmsRetrieverForOtpLength = exports.startSmsRetrieverForOtpLength = function startSmsRetrieverForOtpLength(otpLength, successCallback, failureCallback) {
    return RNAndroidSmsRetriever.startSmsRetrieverForOtpLength(otpLength, successCallback, failureCallback);
};

var startSmsRetrieverForRegex = exports.startSmsRetrieverForRegex = function startSmsRetrieverForRegex(regex, successCallback, failureCallback) {
    return RNAndroidSmsRetriever.startSmsRetrieverForRegex(regex, successCallback, failureCallback);
};

exports.default = {
    startSmsRetriever: startSmsRetriever,
    startSmsRetrieverForOtp: startSmsRetrieverForOtp,
    startSmsRetrieverForOtpLength: startSmsRetrieverForOtpLength,
    startSmsRetrieverForRegex: startSmsRetrieverForRegex
};