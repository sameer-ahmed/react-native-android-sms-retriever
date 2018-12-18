
# react-native-android-sms-retrieve

## Getting started

`$ npm install react-native-android-sms-retrieve --save`

### Mostly automatic installation

`$ react-native link react-native-android-sms-retrieve`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.sameer.RNAndroidSmsRetrieverPackage;` to the imports at the top of the file
  - Add `new RNAndroidSmsRetrieverPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-android-sms-retrieve'
  	project(':react-native-android-sms-retrieve').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-sms-retrieve/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-android-sms-retrieve')
  	```
## Usage
1. This will return whole SMS as string
2. Note: you need to send SMS with application hash to work with android SMS retriver, please check [https://developers.google.com/identity/sms-retriever/verify](https://developers.google.com/identity/sms-retriever/verify)



```javascript
import { DeviceEventEmitter } from 'react-native'
import RNAndroidSmsRetriever from 'react-native-android-sms-retrieve';



componentWillMount() {
	...

	RNAndroidSmsRetriever.startSmsRetriever(successCallback,  failureCallback)

	DeviceEventEmitter.addListener('smsRetrievedSuccess', function (event) { console.log(event) })
    DeviceEventEmitter.addListener('smsRetrievedTimeout', function (event) { console.log(event) })
    DeviceEventEmitter.addListener('smsRetrievedFailure', function (event) { console.log(event) })
	...
}

componentWillUnmount() {
	...
	DeviceEventEmitter.removeListener('smsRetrievedSuccess')
	DeviceEventEmitter.removeListener('smsRetrievedTimeout')
	DeviceEventEmitter.removeListener('smsRetrievedFailure')
	...
}
```

## Other mrthods

1. This will return best possible numeric OTP from, Note: ideal length consider for the otp is 6 and first match will be returned
```javascript
RNAndroidSmsRetriever.startSmsRetrieverForOtp(successCallback,failureCallback)
```

2. This will return first numeric otp matched with given lenght
```javascript
RNAndroidSmsRetriever.startSmsRetrieverForOtpLength(otpLength, successCallback, failureCallback)
```

2. This will return regex match
```javascript
RNAndroidSmsRetriever.startSmsRetrieverForRegex(regex, successCallback, failureCallback)
```

  