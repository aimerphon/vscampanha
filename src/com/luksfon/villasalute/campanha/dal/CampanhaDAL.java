/**
 * 
 */
package com.luksfon.villasalute.campanha.dal;

import android.content.ContentValues;
import android.content.Context;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.Campanha;


public class CampanhaDAL extends BaseDAL<Campanha> {

	public CampanhaDAL(Context context) {
		super(context, Campanha.class);
	}

	@Override
	protected ContentValues getParameters(Campanha entity) {
		ContentValues values = new ContentValues();

        values.put(getContext().getString(R.string.table_column_descricao_campanha), entity.getDescricao());
        values.put(getContext().getString(R.string.table_column_identificador_situacao), entity.getSituacao().getIdentificador());

        return values;
	}

	@Override
	protected String getDefaultOrberBy() {
		return getContext().getString(R.string.order_by_descricao_campanha);
	}

}
