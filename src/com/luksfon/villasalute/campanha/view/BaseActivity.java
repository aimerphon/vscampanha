package com.luksfon.villasalute.campanha.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
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

	protected void setGridViewHeightBasedOnChildren(ListView gridView,
			int columns) {
		int totalHeight = getTotalHeight(gridView, columns);
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		gridView.setLayoutParams(params);
	}

	protected int getTotalHeight(ListView gridView, int columns) {
		ListAdapter listAdapter = gridView.getAdapter();
		int totalHeight = 0;
		int rows = gridView.getCount();

		View listItem = listAdapter.getView(0, null, gridView);
		listItem.measure(0, 0);
		totalHeight = listItem.getMeasuredHeight() + 10;
		totalHeight *= rows;

		return totalHeight;
	}
}
