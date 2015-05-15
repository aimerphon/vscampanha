package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;

public class CadastrarClienteActivity extends BaseActivity {

	private EditText txtNome;
	private EditText txtTelefone;
	private EditText txtEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastrar_cliente);

		txtNome = (EditText) findViewById(R.id.txtNome);
		txtTelefone = (EditText) findViewById(R.id.lblTelefone1);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_confirmar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.action_accept:
				cadastarCliente();
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	private void cadastarCliente() throws BusinessException,
			IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException {
		if (validarCampos()) {
			Cliente cliente = new Cliente();
			cliente.setNome(txtNome.getText().toString());
			cliente.setTelefone(txtTelefone.getText().toString());

			if (txtEmail.getText() != null
					&& !txtEmail.getText().toString().isEmpty()) {
				cliente.setEmail(txtEmail.getText().toString());
			}

			ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
					false, this.getBaseContext());

			clienteController.insert(cliente, this.getContentResolver());

			showMessage(R.string.msg_operacao_sucesso);

			super.onBackPressed();
		}
	}

	private boolean validarCampos() throws BusinessException {

		if (txtNome.getText() == null || txtNome.getText().length() == 0) {
			txtNome.requestFocus();
			throw new BusinessException(this.getApplicationContext()
					.getString(R.string.msg_erro_campo_obrigatorio)
					.replace("{1}", "Nome"));
		}

		if (txtTelefone.getText() == null
				|| txtTelefone.getText().length() == 0) {
			txtTelefone.requestFocus();
			throw new BusinessException(this.getApplicationContext()
					.getString(R.string.msg_erro_campo_obrigatorio)
					.replace("{1}", "Telefone"));
		}

		return true;
	}
}
