package com.empsoft.opine.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.empsoft.opine.OpineApplication;
import com.empsoft.opine.utils.json.JsonObject;
import com.parse.internal.AsyncCallback;


public class OpineServerUtil {

	private static boolean activeOnServer = false;

	public static synchronized boolean isActiveOnServer(){
		return activeOnServer;
	}
	
	public static synchronized void setActiveOnServer(boolean active){
		OpineServerUtil.activeOnServer = active;
	}

	public static void getPresenceFromServer(final Context context, final AsyncCallback callback){

		OpineHttpUtil.doGETHttpRequest("/presence", new AsyncCallback() {
			
			@Override
			public void onSuccess(Object response) {
				JsonObject responseJSON = (JsonObject) response;
				setActiveOnServer(true);
				if ( OpineApplication.DEBUG_MODE ){
					String username = responseJSON.get("username").asString();
					CharSequence toastText = "You are logged as: " + username;
					Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
					toast.show();
				}
				callback.onSuccess(response);
			}
			
			@Override
			public void onFailure(Throwable failure) {
				setActiveOnServer(false);
				if ( OpineApplication.DEBUG_MODE ){
					CharSequence toastText = "You are not logged.";
					Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
					toast.show();
				}
				callback.onFailure(failure);
			}
			
			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	public static void sendPresenceToServer(final Context context, final String username, final AsyncCallback callback){

		OpineHttpUtil.doPOSTHttpRequest("/presence/"+username, new AsyncCallback(){

			@Override
			public void onSuccess(Object response) {
				setActiveOnServer(true);
				if ( OpineApplication.DEBUG_MODE ){
					CharSequence toastText = "You have just logged as: " + username;
					Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
					toast.show();
				}
				callback.onSuccess(response);
			}

			@Override
			public void onFailure(Throwable failure) {
				if ( OpineApplication.DEBUG_MODE ){
					CharSequence toastText = "You could not log in.";
					Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
					toast.show();
				}
				callback.onFailure(failure);
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	public static void leaveServer(final Context context){

		OpineHttpUtil.doPOSTHttpRequest("/leave", new AsyncCallback() {
			
			@Override
			public void onSuccess(Object response) {
				JsonObject responseJSON = (JsonObject) response;
				String responseCode = responseJSON.get("code").asString();
				if ( responseCode.equals("ok") ){
					setActiveOnServer(false);
					if ( OpineApplication.DEBUG_MODE ){
						CharSequence toastText = "You have just logged out.";
						Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
						toast.show();
					}
				}				
			}
			
			@Override
			public void onFailure(Throwable failure) {
				if ( OpineApplication.DEBUG_MODE ){
					CharSequence toastText = "You could not log out.";
					Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
					toast.show();
				}
				Log.e(OpineApplication.TAG, "Could not leave server.");
				failure.printStackTrace();
			}
			
			@Override
			public void onCancel() {
				Log.e(OpineApplication.TAG, "Endpoint:leaveServer callback canceled.");
			}
		});
	}
}