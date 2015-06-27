package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.dal.ClienteDAL;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;

public class VisualizarClienteActivity extends BaseActivity {

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.VisualizarClienteActivity.MESSAGE";
	public static final int EDITAR_CLIENTE = 2;
	
	private int IdCliente;
	private TextView txtNome;
	private TextView txtTelefone;
	private TextView txtEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.visualizar_cliente;
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void inicializarTela() {
		txtNome = (TextView) findViewById(R.id.txtNome);
		txtTelefone = (TextView) findViewById(R.id.txtTelefone);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
	}

	@Override
	protected void carregarTela() {
		try {
			Intent intent = getIntent();
			String id = intent
					.getStringExtra(ConsultaClientesActivity.EXTRA_MESSAGE);

			ClienteDAL clienteDAL = new ClienteDAL(this.getBaseContext());

			IdCliente = Integer.parseInt(id);
			Cliente cliente = new Cliente();
			cliente.setIdentificador(IdCliente);
			cliente = clienteDAL.get(cliente);

			txtNome.setText(cliente.getNome());
			txtTelefone.setText(cliente.getTelefone());
			txtEmail.setText(cliente.getEmail());	
		} catch (Exception ex) {
			Log.println(0, VisualizarClienteActivity.class.toString(),
					ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_editar_excluir, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.action_editar:
				editarCliente();
				return true;
			case R.id.action_excluir:
				excluirCliente();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (BusinessException ex) {
			super.showMessage(ex.getMessage());
			return super.onOptionsItemSelected(item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent visuzalizarCampanha = getIntent();
		visuzalizarCampanha.putExtra(
				VisualizarCampanhaActivity.EXTRA_MESSAGE_VISUALIZAR,
				String.valueOf(IdCliente));
		setResult(RESULT_OK, visuzalizarCampanha);
		finish();
	}

	private void editarCliente() {
		Intent editarCliente = new Intent(this.getApplicationContext(),
				EditarClienteActivity.class);
		editarCliente.putExtra(EXTRA_MESSAGE,
				String.valueOf(IdCliente));
		startActivityForResult(editarCliente, EDITAR_CLIENTE);
	}

	private void excluirCliente() throws IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException, BusinessException {
		ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
				false, this.getBaseContext());
		Cliente cliente = new Cliente();
		cliente.setIdentificador(IdCliente);

		clienteController.delete(cliente, this.getContentResolver());

		showMessage(R.string.msg_operacao_sucesso);

		super.onBackPressed();
	}
}