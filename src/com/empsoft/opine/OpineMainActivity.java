package com.empsoft.opine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.empsoft.opine.utils.OpineServerUtil;
import com.facebook.Session;

public class OpineMainActivity extends FragmentActivity {

	private OpineMainFragment chatFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.fragment_main);
		
	    if (savedInstanceState == null) {
	        chatFragment = new OpineMainFragment();
	    } else {
	        chatFragment = (OpineMainFragment) getSupportFragmentManager()
	            .findFragmentById(R.id.main_frame);
	    }

        getSupportFragmentManager()
        	.beginTransaction()
        	.replace(R.id.main_frame, chatFragment)
        	.commit();
	    
	    Bundle extras = getIntent().getExtras();
	    if ( extras != null ){
	    	chatFragment.setSeenSplash(extras.getBoolean("seenSplash", true));
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if ( OpineServerUtil.isActiveOnServer() ){
			chatFragment.onResume();
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();

		if ( OpineServerUtil.isActiveOnServer() ){
			OpineServerUtil.leaveServer(getApplicationContext());
		}
	}
}