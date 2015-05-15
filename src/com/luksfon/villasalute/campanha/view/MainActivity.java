package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.view.adapter.ListViewAdapter;

public class MainActivity extends BaseActivity {

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.MainActivity.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			carregarTela();
		} catch (Exception ex) {
			Log.println(0, "MainActivity.onCreate", ex.getMessage());
		}
	}

	protected void carregarTela() throws InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException {
		CampanhaController campanhaController = new CampanhaController(true,
				getBaseContext());

		ArrayList<Campanha> lista1 = campanhaController.toList(Campanha.class);

		GridView gridview = (GridView) findViewById(R.id.grid_campanha);
		gridview.setAdapter(new ListViewAdapter<Campanha>(this, lista1));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent visuzalizarCampanha = new Intent(parent.getContext(),
						VisualizarCampanhaActivity.class);
				visuzalizarCampanha.putExtra(EXTRA_MESSAGE,
						String.valueOf(parent.getItemIdAtPosition(position)));
				startActivity(visuzalizarCampanha);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_novo:
			cadastrarCampanha();
			return true;
		case R.id.action_consulta_clientes:
			consultaCliente();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void cadastrarCampanha() {
		Intent cadastrarCampanha = new Intent(this.getBaseContext(),
				CadastrarCampanhaActivity.class);
		startActivity(cadastrarCampanha);
	}

	private void consultaCliente() {
		Intent consultaClientes = new Intent(this.getBaseContext(),
				ConsultaClientesActivity.class);
		startActivity(consultaClientes);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		try {
			carregarTela();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}