package com.luksfon.villasalute.campanha.view.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.ListViewEntityBase;

@SuppressLint("UseSparseArrays")
public class SelectViewAdapter<E extends ListViewEntityBase> extends
		BaseAdapter {

	private ArrayList<E> dataSource;
	private LayoutInflater layoutInflater;
	private HashMap<Integer, E> selectedItems;
	private boolean[] checkeds;
	ViewHolder viewHolder;

	public SelectViewAdapter(Context context, ArrayList<E> dataSource) {
		this.dataSource = dataSource;
		this.layoutInflater = LayoutInflater.from(context);
		this.checkeds = new boolean[dataSource.size()];
		this.selectedItems = new HashMap<Integer, E>();

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
		return dataSource.get(position).getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			viewHolder = new ViewHolder();
			ViewGroup viewGroup = null;
			convertView = layoutInflater
					.inflate(R.layout.selectview, viewGroup);

			viewHolder.textviewTitle = (TextView) convertView
					.findViewById(R.id.txtTitle);
			viewHolder.textviewSubTitle = (TextView) convertView
					.findViewById(R.id.txtSubTitle);
			viewHolder.labelviewTitle = (TextView) convertView
					.findViewById(R.id.lblTitle);
			viewHolder.labelviewSubTitle = (TextView) convertView
					.findViewById(R.id.lblSubTitle);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		E item = dataSource.get(position);
		viewHolder.textviewTitle.setText(item.getTitle());
		viewHolder.textviewSubTitle.setText(item.getSubTitle());
		viewHolder.labelviewTitle.setText(item.getLabelTitle());
		viewHolder.labelviewSubTitle.setText(item.getLabelSubTitle());

		convertView.measure(View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED));

		return convertView;
	}

	public boolean selecionarItem(int position) {
		boolean checked = !checkeds[position];
		checkeds[position] = checked;
		E item = dataSource.get(position);
		if (checked) {
			selectedItems.put(item.getId(), item);
		} else {
			selectedItems.remove(item.getId());
		}

		return checked;
	}

	public ArrayList<E> getSelectedItens() {
		ArrayList<E> lista = new ArrayList<E>();

		for (E entity : selectedItems.values()) {
			lista.add(entity);
		}

		return lista;
	}

	class ViewHolder {
		TextView textviewTitle;
		TextView textviewSubTitle;
		TextView labelviewTitle;
		TextView labelviewSubTitle;
	}
}
