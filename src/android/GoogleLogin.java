package com.plugin.GoogleLogin;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.accounts.AccountManager;

import org.json.JSONArray;

import android.util.Log;

import android.content.Intent;

import com.google.android.gms.auth.*;
import com.google.android.gms.common.AccountPicker;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

public class GoogleLogin extends CordovaPlugin {
    
    private static final String TAG = "GoogleLogin";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final int RESULT_OK = -1;

    private CallbackContext savedCallback;
    
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        
        if (action.equals("login")) {
            savedCallback = callbackContext;
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                     false, null, null, null, null);
            cordova.setActivityResultCallback(this);
            cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR);
            
        }
//        else if (action.equals("logout")) {
//            if (mPlusClient.isConnected()) {
//                mPlusClient.clearDefaultAccount();
//                mPlusClient.disconnect();
//            }
//            // mPlusClient.clearDefaultAccount();
//            savedCallback = callbackContext;
//            
//        }
        else {
            PluginResult.Status status = PluginResult.Status.OK;
            String result = "";
            status = PluginResult.Status.INVALID_ACTION;
            callbackContext.sendPluginResult(new PluginResult(status, result));
        }
        return true;
    }

 

    @Override
    public void onActivityResult(int requestCode, int resultCode,
            final Intent data) {
        Log.d(TAG, "result received");
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    Log.d(TAG, accountName);
                    String token;
                    try {
                        token = GoogleAuthUtil.getToken(cordova.getActivity().getApplicationContext(), accountName, "oauth2:https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
                        // PluginResult.Status status = PluginResult.Status.OK;
                        // savedCallback.sendPluginResult(new PluginResult(status, token));
                        
                        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="
                                + token);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        int serverCode = con.getResponseCode();
                        Log.d(TAG, "URL: " + url);
                        Log.d(TAG, "serverCode: " + serverCode);
                        //successful query
                        if (serverCode == 200) {
                            Log.d(TAG, "token valid");
                            PluginResult.Status status = PluginResult.Status.OK;
                            savedCallback.sendPluginResult(new PluginResult(status, token));
                            return;
                        //bad token, invalidate and get a new one
                        } else if (serverCode == 401) {
                            Log.d(TAG, "token invalid");
                            GoogleAuthUtil.invalidateToken(cordova.getActivity().getApplicationContext(), token);
                            run();
                            return;
                        }

                    } catch (GooglePlayServicesAvailabilityException playEx) {
                        PluginResult.Status status = PluginResult.Status.ERROR;
                        savedCallback.sendPluginResult(new PluginResult(status, "failed"));
                    } catch (UserRecoverableAuthException recoverableException) {
                        cordova.getActivity().startActivityForResult(recoverableException.getIntent(), REQUEST_CODE_RESOLVE_ERR);
                        // Use the intent in a custom dialog or just startActivityForResult.
                    } catch (GoogleAuthException authEx) {
                        // This is likely unrecoverable.
                        Log.e(TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);
                        PluginResult.Status status = PluginResult.Status.ERROR;
                        savedCallback.sendPluginResult(new PluginResult(status, "failed"));
                    } catch (IOException ioEx) {
                        Log.i(TAG, "transient error encountered: " + ioEx.getMessage());
                        PluginResult.Status status = PluginResult.Status.ERROR;
                        savedCallback.sendPluginResult(new PluginResult(status, "failed"));
//                        doExponentialBackoff();
                    }
                }
            });
        }
        else {

            PluginResult.Status status = PluginResult.Status.ERROR;
            savedCallback.sendPluginResult(new PluginResult(status, "failed"));
        }
    }


}
