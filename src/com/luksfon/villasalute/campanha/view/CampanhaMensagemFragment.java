package com.luksfon.villasalute.campanha.view;

import com.luksfon.villasalute.campanha.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CampanhaMensagemFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.campanha_mensagem, container,
				false);

		return view;
	}
}
