package com.luksfon.villasalute.campanha.dal;

import android.content.ContentValues;
import android.content.Context;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.entity.Cliente;

public class ClienteDAL extends BaseDAL<Cliente> {

	public ClienteDAL(Context context) {
		super(context, Cliente.class);
	}

	@Override
    protected ContentValues getParameters(Cliente entity) {
        ContentValues values = new ContentValues();

        values.put(getContext().getString(R.string.table_column_nome), entity.getNome());
        values.put(getContext().getString(R.string.table_column_telefone), entity.getTelefone());
        values.put(getContext().getString(R.string.table_column_email), entity.getEmail());

        return values;
    }

    @Override
    protected String getDefaultOrberBy() {
        return " " + getContext().getString(R.string.order_by_nome);
    }
}
