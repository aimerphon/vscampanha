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

	private int IdCliente;
	private TextView txtNome;
	private TextView txtTelefone;
	private TextView txtEmail;
	private TextView lblNome;
	private TextView lblTelefone;
	private TextView lblEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizar_cliente);

		try {
			Intent intent = getIntent();
			String id = intent
					.getStringExtra(ConsultaClientesActivity.EXTRA_MESSAGE);

			ClienteDAL clienteDAL = new ClienteDAL(this.getBaseContext());

			IdCliente = Integer.parseInt(id);
			Cliente cliente = new Cliente();
			cliente.setIdentificador(IdCliente);
			cliente = clienteDAL.get(cliente);

			txtNome = (TextView) findViewById(R.id.txtNome);
			txtTelefone = (TextView) findViewById(R.id.txtTelefone);
			txtEmail = (TextView) findViewById(R.id.txtEmail);
			lblNome = (TextView) findViewById(R.id.lblNome);
			lblTelefone = (TextView) findViewById(R.id.lblTelefone);
			lblEmail = (TextView) findViewById(R.id.lblEmail);

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
		// Inflate the menu; this adds items to the action bar if it is present.
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	private void editarCliente() {
		// TODO Implementar o editar cliente
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
