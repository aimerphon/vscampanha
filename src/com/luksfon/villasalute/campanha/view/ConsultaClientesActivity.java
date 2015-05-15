package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.view.adapter.ListViewAdapter;

public class ConsultaClientesActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.ConsultaClientesActivity.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consulta_clientes);

		try {
			CarregarGrid();
		} catch (Exception ex) {
			Log.println(0, "ConsultaClientesActivity.onCreate", ex.getMessage());
		}
	}

	private void CarregarGrid() throws InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException {
		ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
				false, getBaseContext());

		ArrayList<Cliente> lista1 = clienteController.toList(Cliente.class);

		GridView gridview = (GridView) findViewById(R.id.grid_cliente);
		gridview.setAdapter(new ListViewAdapter<Cliente>(this, lista1));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent visuzalizarCliente = new Intent(parent.getContext(),
						VisualizarClienteActivity.class);
				visuzalizarCliente.putExtra(EXTRA_MESSAGE,
						String.valueOf(parent.getItemIdAtPosition(position)));
				startActivity(visuzalizarCliente);
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		
		try {
			CarregarGrid();
		} catch (Exception ex) {
			Log.println(0, "ConsultaClientesActivity.onCreate", ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_novo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_novo:
			cadastarCliente();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void cadastarCliente() {
		Intent consultaClientes = new Intent(this.getBaseContext(),
				CadastrarClienteActivity.class);
		startActivity(consultaClientes);
	}
}
