package com.luksfon.villasalute.campanha.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luksfon.villasalute.campanha.R;

public class CampanhaFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.campanha,
				container, false);
		return view;
	}
}
