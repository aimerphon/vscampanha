package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.view.adapter.ListViewAdapter;

public class ConsultaClientesActivity extends BaseActivity {
	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.ConsultaClientesActivity.MESSAGE";

	private ListView gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.consulta_clientes;
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void inicializarTela() {
		gridview = (ListView) findViewById(R.id.grid_cliente);
	}

	@Override
	protected void carregarTela() {
		try {
			ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
					false, getBaseContext());

			ArrayList<Cliente> clientes = clienteController
					.toList(Cliente.class);

			gridview.setAdapter(new ListViewAdapter<Cliente>(this, clientes));

			gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent visuzalizarCliente = new Intent(parent.getContext(),
							VisualizarClienteActivity.class);
					visuzalizarCliente.putExtra(EXTRA_MESSAGE, String
							.valueOf(parent.getItemIdAtPosition(position)));
					startActivity(visuzalizarCliente);
				}
			});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		carregarTela();
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
