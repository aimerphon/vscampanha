package com.luksfon.villasalute.campanha.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.Toast;

import com.luksfon.villasalute.campanha.R;

public class BaseActivity extends Activity {

	protected void showMessage(int resourceId) {
		Toast toast = Toast.makeText(getApplicationContext(), resourceId,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
				0, 0);
		toast.show();
	}

	protected void showMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
				0, 0);
		toast.show();
	}

	protected void showConfirmationMessage(Context context, String title,
			String message, OnClickListener positiveListener,
			OnClickListener negativeListiner) {
		AlertDialog.Builder builderAlert = new AlertDialog.Builder(this);
		builderAlert.setTitle(title);
		builderAlert.setMessage(message);
		builderAlert.setIcon(R.drawable.ic_launcher);
		builderAlert.setCancelable(true);
		builderAlert.setPositiveButton("Sim", positiveListener);
		builderAlert.setNegativeButton("Não", negativeListiner);
		AlertDialog alertDialog = builderAlert.create();
		alertDialog.show();
	}
}
