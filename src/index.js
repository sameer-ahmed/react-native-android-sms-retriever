
import { NativeModules } from 'react-native';

const { RNAndroidSmsRetriever } = NativeModules;

export default {
    startSmsRetriever: (successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetriever(successCallback, failureCallback),
    startSmsRetrieverForOtp: (successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForOtp(successCallback, failureCallback),
    startSmsRetrieverForOtpLength: (otpLength, successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForOtpLength(otpLength, successCallback, failureCallback),
    startSmsRetrieverForRegex: (regex, successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForRegex(regex, successCallback, failureCallback),
};
