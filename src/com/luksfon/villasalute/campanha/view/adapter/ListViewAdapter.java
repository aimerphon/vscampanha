package com.luksfon.villasalute.campanha.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.ListViewEntityBase;

public class ListViewAdapter<E extends ListViewEntityBase> extends BaseAdapter {

	private ArrayList<E> dataSource;
	private LayoutInflater layoutInflater;
	ViewHolder viewHolder;

	public ListViewAdapter(Context context, ArrayList<E> dataSource) {
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
		return dataSource.get(position).getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			viewHolder = new ViewHolder();
			ViewGroup viewGroup = null;
			convertView = layoutInflater.inflate(R.layout.listview, viewGroup);

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

		viewHolder.textviewTitle.setText(((ListViewEntityBase) dataSource
				.get(position)).getTitle());
		viewHolder.textviewSubTitle.setText(((ListViewEntityBase) dataSource
				.get(position)).getSubTitle());
		viewHolder.labelviewTitle.setText(((ListViewEntityBase) dataSource
				.get(position)).getLabelTitle());
		viewHolder.labelviewSubTitle.setText(((ListViewEntityBase) dataSource
				.get(position)).getLabelSubTitle());

		convertView.measure(View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED));

		return convertView;
	}

	class ViewHolder {
		TextView textviewTitle;
		TextView textviewSubTitle;
		TextView labelviewTitle;
		TextView labelviewSubTitle;
	}
}