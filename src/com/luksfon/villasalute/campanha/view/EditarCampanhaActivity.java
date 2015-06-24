package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.util.TipoEnvio;
import com.luksfon.villasalute.campanha.view.adapter.SelectViewAdapter;

public class EditarCampanhaActivity extends BaseActivity {

	private Campanha campanha;
	private Button btnSelecionarImagem;
	private EditText txtDescricao;
	private EditText txtMensagem;
	private ListView grid_clientes;
	private ImageView imageView1;
	private TextView lblClientes;
	private TextView lblTipoMensagem;
	private RadioButton rbtTexto;
	private RadioButton rbtImagem;
	private RadioButton rbtManual;
	private RadioButton rbtAutomatico;
	private RadioGroup rdgTipo;
	private boolean voltar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar_campanha);
		voltar = true;
		try {
			inicializarTela();
		} catch (Exception ex) {
			Log.println(0, "EditarCampanhaActivity.onCreate", ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_confirmar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_accept:
			voltar = false;
			editarCampanha();
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onBackPressed() {
		if (!voltar) {
			Intent visuzalizarCampanha = new Intent(
					this.getApplicationContext(),
					VisualizarCampanhaActivity.class);
			visuzalizarCampanha.putExtra(
					VisualizarCampanhaActivity.EXTRA_MESSAGE,
					String.valueOf(campanha.getIdentificador()));
			startActivity(visuzalizarCampanha);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("unchecked")
	protected void editarCampanha() {
		CampanhaController campanhaController = new CampanhaController(true,
				getApplicationContext());
		List<Cliente> clientes = new ArrayList<Cliente>();

		campanha.setDescricao(txtDescricao.getText().toString().trim());

		if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
			SelectViewAdapter<Cliente> selectAdapter = (SelectViewAdapter<Cliente>) grid_clientes
					.getAdapter();
			clientes = (List<Cliente>) selectAdapter.getSelectedItens();
		} else if (campanha.getTipoEnvio() == TipoEnvio.MANUAL.getValue()) {
			if (rbtTexto.isChecked()) {
				campanha.setMensagem(txtMensagem.getText().toString().trim());
			}
		}

		try {
			campanhaController.editarCampanha(campanha, clientes);
			super.showMessage(getApplication().getString(
					R.string.msg_operacao_sucesso));
			super.onBackPressed();
		} catch (BusinessException e) {
			showMessage(e.getMessage());
			super.onBackPressed();
		}
	}

	protected void inicializarTela() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, BusinessException {
		txtDescricao = (EditText) findViewById(R.id.txtDescricao);
		lblTipoMensagem = (TextView) findViewById(R.id.lblTipoMensagem);
		rdgTipo = (RadioGroup) findViewById(R.id.rdgTipo);
		txtDescricao.setEnabled(true);
		lblClientes = (TextView) findViewById(R.id.lblClientes);
		grid_clientes = (ListView) findViewById(R.id.grid_clientes);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		btnSelecionarImagem = (Button) findViewById(R.id.btnImagem);
		btnSelecionarImagem.setVisibility(View.GONE);
		txtMensagem = (EditText) findViewById(R.id.txtMensagem);
		rbtImagem = (RadioButton) findViewById(R.id.rbtImagem);
		rbtImagem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtMensagem.setEnabled(true);
				btnSelecionarImagem.setVisibility(View.GONE);
				btnSelecionarImagem.setEnabled(false);
				imageView1.setVisibility(View.GONE);
			}
		});

		rbtTexto = (RadioButton) findViewById(R.id.rbtTexto);
		rbtTexto.setChecked(true);
		rbtTexto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtMensagem.setEnabled(false);
				txtMensagem.setText(getApplicationContext().getString(
						R.string.string_empty));
			}
		});

		rbtAutomatico = (RadioButton) findViewById(R.id.rbtAutomatico);
		rbtAutomatico.setChecked(true);
		rbtAutomatico.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				grid_clientes.setVisibility(View.GONE);
				lblClientes.setVisibility(View.GONE);
				lblTipoMensagem.setVisibility(View.VISIBLE);
				rdgTipo.setVisibility(View.VISIBLE);
				txtMensagem.setVisibility(View.VISIBLE);
				btnSelecionarImagem.setVisibility(View.GONE);
				imageView1.setVisibility(View.GONE);
				rbtTexto.setChecked(true);
			}
		});

		rbtManual = (RadioButton) findViewById(R.id.rbtManual);
		rbtManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				grid_clientes.setVisibility(View.VISIBLE);
				ViewGroup.LayoutParams layoutParams = grid_clientes
						.getLayoutParams();
				layoutParams.height = getTotalHeight(grid_clientes,
						grid_clientes.getAdapter().getCount());
				grid_clientes.setLayoutParams(layoutParams);
				lblClientes.setVisibility(View.VISIBLE);
				// TODO Ajustar a seleção de imagem
				lblTipoMensagem.setVisibility(View.GONE);
				txtMensagem.setVisibility(View.GONE);
				rdgTipo.setVisibility(View.GONE);
			}
		});

		carregarTela();
	}

	protected void carregarTela() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, BusinessException {
		Intent intent = getIntent();
		String id = intent
				.getStringExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE);

		CampanhaController campanhaController = new CampanhaController(true,
				getBaseContext());

		Campanha campanha = new Campanha();
		campanha.setIdentificador(Integer.parseInt(id));

		campanha = campanhaController.get(campanha);

		txtDescricao.setText(campanha.getDescricao());
//		grid_clientes.setAdapter(new ListViewAdapter<CampanhaCliente>(this,
//				campanha.getClientes()));
//		grid_clientes.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent visuzalizarCliente = new Intent(parent.getContext(),
//						VisualizarClienteActivity.class);
//				visuzalizarCliente.putExtra(
//						ConsultaClientesActivity.EXTRA_MESSAGE,
//						String.valueOf(parent.getItemIdAtPosition(position)));
//				startActivity(visuzalizarCliente);
//			}
//		});
		
		ArrayList<Cliente> clientes = new ClienteController<Cliente>(true, getApplicationContext()).toList(Cliente.class);

		SelectViewAdapter<Cliente> adapter = new SelectViewAdapter<Cliente>(this, clientes);
		
		ArrayList<Cliente> clientesSelecionados = new ArrayList<Cliente>();
		
		for (CampanhaCliente campanhacliente : campanha.getClientes()){
			clientesSelecionados.add(campanhacliente.getCliente());
		}
		
		grid_clientes.setAdapter(adapter);
		this.setGridViewHeightBasedOnChildren(grid_clientes, clientes.size());
		grid_clientes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean checked = ((SelectViewAdapter<?>) parent.getAdapter())
						.selecionarItem(position);
				if (checked) {
					view.setBackgroundColor(Color.rgb(255, 204, 153));
					view.setBackgroundColor(Color.rgb(255, 166, 76));
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		});
		
		adapter.setSelectedItens(clientesSelecionados);

		if (TipoEnvio.AUTOMATICO.getValue() == campanha.getTipoEnvio()) {
			rbtAutomatico.setChecked(true);
			grid_clientes.setVisibility(View.VISIBLE);
			ViewGroup.LayoutParams layoutParams = grid_clientes
					.getLayoutParams();
			layoutParams.height = getTotalHeight(grid_clientes, grid_clientes
					.getAdapter().getCount());
			grid_clientes.setLayoutParams(layoutParams);
			lblClientes.setVisibility(View.VISIBLE);
			// TODO Ajustar a seleção de imagem
			lblTipoMensagem.setVisibility(View.GONE);
			txtMensagem.setVisibility(View.GONE);
			rdgTipo.setVisibility(View.GONE);
		} else {
			grid_clientes.setVisibility(View.GONE);
			lblClientes.setVisibility(View.GONE);
			lblTipoMensagem.setVisibility(View.VISIBLE);
			rdgTipo.setVisibility(View.VISIBLE);
			txtMensagem.setVisibility(View.VISIBLE);
			btnSelecionarImagem.setVisibility(View.GONE);
			imageView1.setVisibility(View.GONE);

			if (campanha.getCaminhoImagem() == null) {
				rbtTexto.setChecked(true);
				txtMensagem.setText(campanha.getMensagem());
			} else {
				rbtImagem.setChecked(true);
				txtMensagem.setVisibility(View.GONE);
			}
		}

		rbtTexto.setEnabled(false);
		rbtImagem.setEnabled(false);
		rbtAutomatico.setEnabled(false);
		rbtManual.setEnabled(false);

		this.campanha = campanha;
	}
}
