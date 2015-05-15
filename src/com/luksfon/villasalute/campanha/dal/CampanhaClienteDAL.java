package com.luksfon.villasalute.campanha.dal;

import android.content.ContentValues;
import android.content.Context;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;

public class CampanhaClienteDAL extends BaseDAL<CampanhaCliente> {
	public CampanhaClienteDAL(Context context) {
		super(context, CampanhaCliente.class);
	}

	@Override
    protected ContentValues getParameters(CampanhaCliente entity) {
        ContentValues values = new ContentValues();

        values.put(getContext().getString(R.string.table_column_identificador_campanha), entity.getCampanha().getIdentificador());
        values.put(getContext().getString(R.string.table_column_identificador_cliente), entity.getCliente().getIdentificador());

        return values;
    }

    @Override
    protected String getDefaultOrberBy() {
        return "";
    }
}
