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
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.util.SituacaoCampanha;
import com.luksfon.villasalute.campanha.util.TipoEnvio;

public class CampanhaAdapter<E extends Campanha> extends BaseAdapter {

	private ArrayList<E> dataSource;
	private LayoutInflater layoutInflater;
	ViewHolder viewHolder;

	public CampanhaAdapter(Context context, ArrayList<E> dataSource) {
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

		Campanha campanha = dataSource.get(position);

		viewHolder.lblCampanha.setText(campanha.getDescricao());
		
		if (campanha.getMensagem() != null) {
			viewHolder.lblMensagem.setText(campanha.getMensagem());
		} else {
			viewHolder.lblMensagem.setText("Imagem");
		}

		if (SituacaoCampanha.ENVIADO.getValue() == campanha.getSituacao()
				.getIdentificador()) {
			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				viewHolder.imgSituacao
				.setImageResource(R.drawable.ic_action_group_vermelho);
			} else {
				viewHolder.imgSituacao
						.setImageResource(R.drawable.ic_action_person_vermelho);
			}
		} else if (SituacaoCampanha.ENVIANDO.getValue() == campanha
				.getSituacao().getIdentificador()) {
			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				viewHolder.imgSituacao
				.setImageResource(R.drawable.ic_action_group_amarelo);
			} else {
				viewHolder.imgSituacao
						.setImageResource(R.drawable.ic_action_person_amarelo);
			}
		} else if (SituacaoCampanha.NAO_ENVIADO.getValue() == campanha
				.getSituacao().getIdentificador()) {
			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				viewHolder.imgSituacao
				.setImageResource(R.drawable.ic_action_group_verde);
			} else {
				viewHolder.imgSituacao
						.setImageResource(R.drawable.ic_action_person_verde);
			}
		}

		convertView.measure(View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED));

		return convertView;
	}

	class ViewHolder {
		TextView lblCampanha;
		TextView lblMensagem;
		ImageView imgSituacao;
	}
}
