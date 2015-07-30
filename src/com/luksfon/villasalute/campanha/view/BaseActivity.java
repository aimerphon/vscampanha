package com.luksfon.villasalute.campanha.view;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.luksfon.villasalute.campanha.R;

public abstract class BaseActivity extends Activity {

	protected int layoutResId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(layoutResId);

		inicializarTela();

		carregarTela();
	}

	protected abstract void inicializarTela();

	protected abstract void carregarTela();

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

	protected Bitmap carregarImagem(ProgressBar progressBar,
			ImageView imageView, Uri uri) {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		int newWidth = size.x;
		int newHeight = size.y;
		Bitmap bitmap = null;

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					getContentResolver().openInputStream(uri)));

			bitmap = android.provider.MediaStore.Images.Media.getBitmap(
					getContentResolver(), uri);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

			bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
					true);

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}