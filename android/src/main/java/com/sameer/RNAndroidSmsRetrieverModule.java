
package com.sameer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RNAndroidSmsRetrieverModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;

    private static ReactApplicationContext staticReactContext;

    public enum SMS_RETRIEVE_MODE {FULL, OTP, REGEX}

    public RNAndroidSmsRetrieverModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        staticReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAndroidSmsRetriever";
    }

    @ReactMethod
    public void startSmsRetriever(final Callback successCallback, final Callback failureCallback) {
        start(SMS_RETRIEVE_MODE.FULL, 0, "", successCallback, failureCallback);
    }

    @ReactMethod
    public void startSmsRetrieverForOtp(final Callback successCallback, final Callback failureCallback) {
        start(SMS_RETRIEVE_MODE.OTP, 0, "", successCallback, failureCallback);
    }

    @ReactMethod
    public void startSmsRetrieverForOtpLength(int otpLength, final Callback successCallback, final Callback
        failureCallback) {
        start(SMS_RETRIEVE_MODE.OTP, otpLength, "", successCallback, failureCallback);
    }

    @ReactMethod
    public void startSmsRetrieverForRegex(String regex, final Callback successCallback, final Callback
        failureCallback) {
        start(SMS_RETRIEVE_MODE.REGEX, 0, regex, successCallback, failureCallback);
    }

    private void start(final SMS_RETRIEVE_MODE mode, final int otpLength, final String regex,
        final Callback successCallback, final Callback
        failureCallback) {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this.reactContext);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RNSMSBroadcastReceiver rnsmsBroadcastReceiver =
                    new RNSMSBroadcastReceiver(reactContext, mode, otpLength, regex);
                reactContext.getApplicationContext()
                    .registerReceiver(rnsmsBroadcastReceiver, new IntentFilter(SmsRetriever
                        .SMS_RETRIEVED_ACTION));
                if (successCallback != null) {
                    successCallback.invoke("success");
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (failureCallback != null) {
                    failureCallback.invoke(e.getLocalizedMessage());
                }
            }
        });
    }

    private static class RNSMSBroadcastReceiver extends BroadcastReceiver {

        private final ReactContext innerReactContext;

        private final SMS_RETRIEVE_MODE mode;

        private final int otpLength;

        private final String regex;

        public RNSMSBroadcastReceiver(ReactContext innerReactContext,
            SMS_RETRIEVE_MODE mode, int otpLength, String regex) {
            this.innerReactContext = innerReactContext;
            this.mode = mode;
            this.otpLength = otpLength;
            this.regex = regex;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            context.getApplicationContext().unregisterReceiver(this);
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                WritableMap args = Arguments.createMap();

                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (this.mode != SMS_RETRIEVE_MODE.FULL) {
                            String evaluation = this.evaluate(message);
                            if (this.mode == SMS_RETRIEVE_MODE.OTP) {
                                args.putString("otp", evaluation);
                            } else if (this.mode == SMS_RETRIEVE_MODE.REGEX) {
                                args.putString("regexMatch", evaluation);
                            }
                        }
                        args.putString("message", message);
                        sendEvent("smsRetrievedSuccess", args);
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        args.putString("error", "timeout");
                        sendEvent("smsRetrievedTimeout", args);
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        break;
                    default:
                        args.putString("error", "code: " + status.getStatusCode());
                        sendEvent("smsRetrievedFailure", args);
                        break;
                }
            }
        }

        private String evaluate(@NonNull String message) {

            String patterString = "";

            if (this.mode == SMS_RETRIEVE_MODE.REGEX) {
                patterString = regex;
            } else if (this.mode == SMS_RETRIEVE_MODE.OTP) {
                patterString = "(\\d" + (otpLength <= 0 ? "+" : ("{" + otpLength + "}")) + ")";
            }

            final Pattern pattern = Pattern.compile(patterString);
            final Matcher matcher = pattern.matcher(message);
            String result = null;
            int temp = 0;
            String tempResult = null;
            if (this.mode == SMS_RETRIEVE_MODE.OTP) {
                if (otpLength <= 0) {
                    while (matcher.find() && result == null) {
                        String match = matcher.group(1);
                        int length = match.length();
                        // considering ideal length for otp is max 6 digit
                        if (length == 6) {
                            result = match;
                        } else if (length < 6) {
                            // finding closest length to ideal length
                            int difference = 6 - length;
                            if (temp == 0 || temp > difference) {
                                temp = difference;
                                tempResult = match;
                            }
                        }
                    }
                } else {
                    result = matcher.find() ? matcher.group(1) : null;
                }
            } else if (this.mode == SMS_RETRIEVE_MODE.REGEX) {
                result = matcher.find() ? matcher.group(1) : null;
            }

            if (result == null && tempResult != null) {
                result = tempResult;
            }

            return result;
        }

        private void sendEvent(String eventName, @Nullable WritableMap params) {
            this.innerReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }
}
