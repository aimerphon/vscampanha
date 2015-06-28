package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.view.adapter.CampanhaAdapter;

public class MainActivity extends BaseActivity {

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.MainActivity.MESSAGE";
	
	private ListView gridview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.activity_main;
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void inicializarTela() {
		gridview = (ListView) findViewById(R.id.grid_campanha);		
	}

	protected void carregarTela() {
		try {
			CampanhaController campanhaController = new CampanhaController(
					true, getBaseContext());

			// campanhaController.executeSQL("alter table tCampanha add DsMensagem TEXT");
			// campanhaController.executeSQL("alter table tCampanha add DsPathImagem TEXT");
			// campanhaController.executeSQL("alter table tCampanha add StManual INTEGER");
			// campanhaController.executeSQL("alter table tCampanha add BlImagem BLOB");

			ArrayList<Campanha> campanhas = campanhaController.toList(Campanha.class);

			gridview.setAdapter(new CampanhaAdapter<Campanha>(this, campanhas));

			gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent visuzalizarCampanha = new Intent(
							parent.getContext(),
							VisualizarCampanhaActivity.class);
					visuzalizarCampanha.putExtra(
							VisualizarCampanhaActivity.EXTRA_MESSAGE, String
									.valueOf(parent
											.getItemIdAtPosition(position)));
					startActivity(visuzalizarCampanha);
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
	public boolean onCreateOptionsMenu(Menu menu) {
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
		super.onRestart();

		carregarTela();
	}

	@Override
	public void onBackPressed() {
		showConfirmationMessage(
				getApplicationContext(),
				getApplicationContext().getString(
						R.string.title_sair_aplicativo),
				getApplicationContext().getString(R.string.msg_sair_aplicativo),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						sair();
					}
				}, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
	}

	private void sair() {
		super.finish();
		super.onDestroy();
	}
}