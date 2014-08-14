package com.empsoft.opine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.empsoft.opine.utils.OpineServerUtil;
import com.facebook.Session;

public class MainActivity extends FragmentActivity {

	private MainFragment mainFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
	        mainFragment = new MainFragment();
	    } else {
	        mainFragment = (MainFragment) getSupportFragmentManager()
	            .findFragmentById(android.R.id.content);
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
			mainFragment.onResume();
		}
	}
	
}