package com.empsoft.opine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.empsoft.opine.utils.OpineServerUtil;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.internal.AsyncCallback;

public class OpineMainFragment extends Fragment{

	private boolean seenSplash = false;

	public void setSeenSplash(boolean seenSplash) {
		this.seenSplash = seenSplash;
	}
	
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState){

		View view = inflater.inflate(R.layout.fragment_main, container, false);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.com_empsoft_opine_main_login_auth_btn);
		authButton.setFragment(this);
		authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		return view;
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {

			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(final GraphUser user, Response response) {
					if ( user != null ){

						switchToChatArea();
						OpineServerUtil.sendPresenceToServer(getActivity().getApplicationContext(), user.getId(), new AsyncCallback() {
							
							@Override
							public void onSuccess(Object response) {
//								JsonObject responseJSON = (JsonObject) response;
								/*
								 * Fazer coisas interessantes aqui, o usuário está logado e pronto pra usar o app!
								 */
							}
							
							@Override
							public void onFailure(Throwable failure) {
								Log.e(OpineApplication.TAG, "Could not send presence to server.");
								failure.printStackTrace();
							}
							
							@Override
							public void onCancel() {
								Log.e(OpineApplication.TAG, "Endpoint:sendPresenceToServer callback canceled.");
							}
						});
					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {

			switchToLoginArea(false);
			if ( OpineServerUtil.isActiveOnServer() ){
				OpineServerUtil.leaveServer(getActivity().getApplicationContext());
			}
		}
	}

	public void switchToLoginArea(boolean showSplash){

		final LinearLayout chatArea = (LinearLayout) this.getActivity().findViewById(R.id.com_empsoft_opine_main_app);
		final LinearLayout loginArea = (LinearLayout) this.getActivity().findViewById(R.id.com_empsoft_opine_main_login);
		chatArea.setVisibility(View.GONE);
		loginArea.setVisibility(View.VISIBLE);
		
		if ( showSplash && (!seenSplash) ){
        	Intent intent = new Intent(getActivity(), OpineSplashActivity.class);
            startActivity(intent);
            getActivity().finish();
		}
	}

	public void switchToChatArea(){
		final LinearLayout chatArea = (LinearLayout) this.getActivity().findViewById(R.id.com_empsoft_opine_main_app);
		final LinearLayout loginArea = (LinearLayout) this.getActivity().findViewById(R.id.com_empsoft_opine_main_login);
		chatArea.setVisibility(View.VISIBLE);
		loginArea.setVisibility(View.GONE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();

		session = Session.getActiveSession();
		if (session == null || !(session.isOpened() || session.isClosed()) ) {
			OpineServerUtil.getPresenceFromServer(getActivity().getApplicationContext(), new AsyncCallback() {
				
				@Override
				public void onSuccess(Object response) {
					OpineServerUtil.leaveServer(getActivity().getApplicationContext());
				}
				
				@Override
				public void onFailure(Throwable failure) {
					Log.e(OpineApplication.TAG, "Could not get presence from server.");
					failure.printStackTrace();
				}
				
				@Override
				public void onCancel() {
					Log.e(OpineApplication.TAG, "Endpoint:getPresenceFromServer callback canceled.");
				}
			});
			switchToLoginArea(true);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}