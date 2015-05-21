package com.luksfon.villasalute.campanha.view;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	protected void showMessage(int resourceId){
		Toast toast = Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	protected void showMessage(String message){
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
}
