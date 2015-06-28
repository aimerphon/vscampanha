package com.luksfon.villasalute.campanha.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;
import com.luksfon.villasalute.campanha.util.SituacaoCampanha;

public class ClienteAdapter<E extends CampanhaCliente> extends BaseAdapter {

	private ArrayList<E> dataSource;
	private LayoutInflater layoutInflater;
	ViewHolder viewHolder;

	public ClienteAdapter(Context context, ArrayList<E> dataSource) {
		this.dataSource = dataSource;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public E getItem(int position) {
		return dataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dataSource.get(position).getIdentificador();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			ViewGroup viewGroup = null;
			convertView = layoutInflater.inflate(R.layout.listview_campanha,
					viewGroup);

			viewHolder.lblCampanha = (TextView) convertView
					.findViewById(R.id.lblCampanha);
			viewHolder.lblMensagem = (TextView) convertView
					.findViewById(R.id.lblMensagem);
			viewHolder.imgSituacao = (ImageView) convertView
					.findViewById(R.id.imgSituacao);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		E campanhaCliente = dataSource.get(position);

		viewHolder.lblCampanha.setText(campanhaCliente.getCliente().getNome()
				+ " - " + campanhaCliente.getCliente().getTelefone());

		if (campanhaCliente.getCliente().getEmail() != null) {
			viewHolder.lblMensagem.setText(campanhaCliente.getCliente()
					.getEmail());
		}

		viewHolder.imgSituacao
				.setImageResource(obterImageResId(campanhaCliente));

		convertView.measure(View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED));

		return convertView;
	}

	public static int obterImageResId(CampanhaCliente campanhaCliente) {
		int imageResId = -1;

		if (SituacaoCampanha.ENVIADO.getValue() == campanhaCliente
				.getSituacao().getIdentificador()) {
			imageResId = R.drawable.ic_action_person_vermelho;
		} else if (SituacaoCampanha.NAO_ENVIADO.getValue() == campanhaCliente
				.getSituacao().getIdentificador()) {
			imageResId = R.drawable.ic_action_person_verde;
		}

		return imageResId;
	}

	class ViewHolder {
		TextView lblCampanha;
		TextView lblMensagem;
		ImageView imgSituacao;
	}
}