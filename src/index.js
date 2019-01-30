
import { NativeModules } from 'react-native';

const { RNAndroidSmsRetriever } = NativeModules;

export const startSmsRetriever = (successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetriever(successCallback, failureCallback)

export const startSmsRetrieverForOtp = (successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForOtp(successCallback, failureCallback)

export const startSmsRetrieverForOtpLength = (otpLength, successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForOtpLength(otpLength, successCallback, failureCallback)

export const startSmsRetrieverForRegex = (regex, successCallback, failureCallback) => RNAndroidSmsRetriever.startSmsRetrieverForRegex(regex, successCallback, failureCallback)

export default {
    startSmsRetriever,
    startSmsRetrieverForOtp,
    startSmsRetrieverForOtpLength,
    startSmsRetrieverForRegex
}
