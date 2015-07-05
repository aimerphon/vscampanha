package com.luksfon.villasalute.campanha.view;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.EditarCampanhaActivity.MESSAGE";

	private Campanha campanha;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.editar_campanha;

		super.onCreate(savedInstanceState);
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
			// voltar = false;
			editarCampanha();
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onBackPressed() {
		Intent visuzalizarCampanha = getIntent();
		visuzalizarCampanha.putExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE,
				String.valueOf(campanha.getIdentificador()));
		visuzalizarCampanha.putExtra(
				VisualizarCampanhaActivity.EXTRA_MESSAGE_EDITAR,
				String.valueOf(campanha.getIdentificador()));
		setResult(RESULT_OK, visuzalizarCampanha);
		finish();
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

			Intent visuzalizarCampanha = getIntent();
			visuzalizarCampanha.putExtra(
					VisualizarCampanhaActivity.EXTRA_MESSAGE,
					String.valueOf(campanha.getIdentificador()));
			visuzalizarCampanha.putExtra(
					VisualizarCampanhaActivity.EXTRA_MESSAGE_EDITAR,
					String.valueOf(campanha.getIdentificador()));

			onBackPressed();
		} catch (BusinessException e) {
			showMessage(e.getMessage());
			super.onBackPressed();
		}
	}

	@Override
	protected void inicializarTela() {
		txtDescricao = (EditText) findViewById(R.id.txtDescricao);
		lblTipoMensagem = (TextView) findViewById(R.id.lblTipoMensagem);
		rdgTipo = (RadioGroup) findViewById(R.id.rdgTipo);
		txtDescricao.setEnabled(true);
		lblClientes = (TextView) findViewById(R.id.lblClientes);
		grid_clientes = (ListView) findViewById(R.id.grid_clientes);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		txtMensagem = (EditText) findViewById(R.id.txtMensagem);
		rbtImagem = (RadioButton) findViewById(R.id.rbtImagem);
		rbtImagem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtMensagem.setEnabled(true);
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
				lblTipoMensagem.setVisibility(View.GONE);
				txtMensagem.setVisibility(View.GONE);
				rdgTipo.setVisibility(View.GONE);
			}
		});
	}

	protected void carregarTela() {
		try {
			Intent intent = getIntent();
			String id = intent
					.getStringExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE);

			CampanhaController campanhaController = new CampanhaController(
					true, getBaseContext());

			Campanha campanha = new Campanha();
			campanha.setIdentificador(Integer.parseInt(id));

			campanha = campanhaController.get(campanha);

			txtDescricao.setText(campanha.getDescricao());

			if (TipoEnvio.AUTOMATICO.getValue() == campanha.getTipoEnvio()) {

				ArrayList<Cliente> clientes = new ClienteController<Cliente>(
						true, getApplicationContext()).toList(Cliente.class);

				SelectViewAdapter<Cliente> adapter = new SelectViewAdapter<Cliente>(
						this, clientes);

				ArrayList<Cliente> clientesSelecionados = new ArrayList<Cliente>();

				for (CampanhaCliente campanhacliente : campanha.getClientes()) {
					clientesSelecionados.add(campanhacliente.getCliente());
				}

				grid_clientes.setAdapter(adapter);
				this.setGridViewHeightBasedOnChildren(grid_clientes,
						clientes.size());
				grid_clientes.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						boolean checked = ((SelectViewAdapter<?>) parent
								.getAdapter()).selecionarItem(position);
						if (checked) {
							view.setBackgroundColor(Color.rgb(255, 204, 153));
							view.setBackgroundColor(Color.rgb(255, 166, 76));
						} else {
							view.setBackgroundColor(Color.TRANSPARENT);
						}
					}
				});

				adapter.setSelectedItens(clientesSelecionados);

				rbtAutomatico.setChecked(true);
				grid_clientes.setVisibility(View.VISIBLE);
				ViewGroup.LayoutParams layoutParams = grid_clientes
						.getLayoutParams();
				layoutParams.height = getTotalHeight(grid_clientes,
						grid_clientes.getAdapter().getCount());
				grid_clientes.setLayoutParams(layoutParams);
				lblClientes.setVisibility(View.VISIBLE);
				lblTipoMensagem.setVisibility(View.GONE);
				txtMensagem.setVisibility(View.GONE);
				rdgTipo.setVisibility(View.GONE);
			} else {
				grid_clientes.setVisibility(View.GONE);
				lblClientes.setVisibility(View.GONE);
				lblTipoMensagem.setVisibility(View.VISIBLE);
				rdgTipo.setVisibility(View.VISIBLE);

				if (campanha.getCaminhoImagem() == null) {
					rbtTexto.setChecked(true);
					txtMensagem.setText(campanha.getMensagem());
					txtMensagem.setVisibility(View.VISIBLE);
					imageView1.setVisibility(View.GONE);
				} else {
					rbtImagem.setChecked(true);
					txtMensagem.setVisibility(View.GONE);
					imageView1.setVisibility(View.VISIBLE);

					Uri uri = Uri.parse(campanha.getCaminhoImagem());

					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);

					int newWidth = size.x;
					int newHeight = size.y;

					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new InputStreamReader(
								getContentResolver().openInputStream(uri)));

						Bitmap bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(), uri);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

						// int width = bitmap.getWidth();
						// int height = bitmap.getHeight();
						//
						// float scaleWidth = ((float) newWidth) / width;
						// float scaleHeight = ((float) newHeight) / height;
						//
						// Matrix matrix = new Matrix();
						//
						// // Resize the bit map
						// matrix.postScale(scaleWidth, scaleHeight);
						//
						// bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
						// height, matrix, false);
						
						bitmap = Bitmap.createScaledBitmap(bitmap,
								newWidth, newHeight, true);

						imageView1.setImageBitmap(bitmap);
						imageView1.setAdjustViewBounds(true);
						imageView1.setScaleType(ScaleType.CENTER_CROP);

						reader.close();

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			rbtTexto.setEnabled(false);
			rbtImagem.setEnabled(false);
			rbtAutomatico.setEnabled(false);
			rbtManual.setEnabled(false);

			this.campanha = campanha;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
}