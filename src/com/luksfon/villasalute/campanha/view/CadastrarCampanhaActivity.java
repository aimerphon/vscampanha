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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.controller.ClienteController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.util.TipoEnvio;
import com.luksfon.villasalute.campanha.view.adapter.SelectViewAdapter;

public class CadastrarCampanhaActivity extends BaseActivity {

	private static final int SELECT_PICTURE = 1;
	private static final int PICKFILE_RESULT_CODE = 2;

	private EditText txtDescricao;
	private EditText txtMensagem;
	private ImageView imageView1;
	private ListView grid_clientes;
	private RadioButton rbtTexto;
	private RadioButton rbtImagem;
	private RadioButton rbtManual;
	private RadioButton rbtAutomatico;
	private RadioGroup rdgTipo;
	private TextView lblClientes;
	private TextView lblTipoMensagem;

	private String selectedImagePath;
	private byte[] byteImagem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.cadastrar_campanha;

		super.onCreate(savedInstanceState);
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
				cadastarCampanha();
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void inicializarTela() {
		txtDescricao = (EditText) findViewById(R.id.txtDescricao);
		lblTipoMensagem = (TextView) findViewById(R.id.lblTipoMensagem);
		rdgTipo = (RadioGroup) findViewById(R.id.rdgTipo);
		lblClientes = (TextView) findViewById(R.id.lblClientes);
		grid_clientes = (ListView) findViewById(R.id.grid_clientes);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		txtMensagem = (EditText) findViewById(R.id.txtMensagem);
		rbtImagem = (RadioButton) findViewById(R.id.rbtImagem);
		rbtTexto = (RadioButton) findViewById(R.id.rbtTexto);
		rbtAutomatico = (RadioButton) findViewById(R.id.rbtAutomatico);
		rbtManual = (RadioButton) findViewById(R.id.rbtManual);
	}

	@Override
	protected void carregarTela() {
		txtDescricao.setText(this.getApplicationContext().getString(
				R.string.string_empty));
		txtDescricao.setEnabled(true);
		rbtImagem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtMensagem.setVisibility(View.VISIBLE);
				txtMensagem.setEnabled(true);
				imageView1.setVisibility(View.GONE);
			}
		});

		rbtTexto.setChecked(true);
		rbtTexto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				txtMensagem.setEnabled(false);
				txtMensagem.setVisibility(View.GONE);
				txtMensagem.setText(getApplicationContext().getString(
						R.string.string_empty));
				imageView1.setVisibility(View.VISIBLE);

				if (!isChecked) {
					selecionarImagem();
				}
			}
		});

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

		lblTipoMensagem.setVisibility(View.GONE);
		txtMensagem.setVisibility(View.GONE);
		rdgTipo.setVisibility(View.GONE);

		CarregarGrid();

		rbtManual.setChecked(true);
		rbtManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked) {
					importarArquivo();
				}
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

				CarregarGrid();
			}
		});
	}

	private void CarregarGrid() {
		try {
			ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
					false, getBaseContext());

			ArrayList<Cliente> clientes = clienteController
					.toList(Cliente.class);

			grid_clientes.setAdapter(new SelectViewAdapter<Cliente>(this,
					clientes));
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

	@SuppressWarnings("unchecked")
	public void cadastarCampanha() throws BusinessException,
			IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, NoSuchFieldException {
		if (validarCampos()) {
			Campanha campanha = new Campanha();
			campanha.setDescricao(txtDescricao.getText().toString());

			if (rbtTexto.isChecked()) {
				campanha.setMensagem(txtMensagem.getText().toString());
			}

			if (rbtImagem.isChecked()) {
				campanha.setCaminhoImagem(selectedImagePath);
				campanha.setImagem(byteImagem);
			}

			List<Cliente> clientes = new ArrayList<Cliente>();

			if (rbtAutomatico.isChecked()) {
				campanha.setTipoEnvio(TipoEnvio.AUTOMATICO.getValue());
				SelectViewAdapter<Cliente> selectAdapter = (SelectViewAdapter<Cliente>) grid_clientes
						.getAdapter();
				clientes = (List<Cliente>) selectAdapter.getSelectedItens();
			} else {
				campanha.setTipoEnvio(TipoEnvio.MANUAL.getValue());
			}

			CampanhaController campanhaController = new CampanhaController(
					true, this.getBaseContext());

			campanhaController.insert(campanha, clientes);

			showMessage(R.string.msg_operacao_sucesso);

			super.onBackPressed();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean validarCampos() throws BusinessException {
		SelectViewAdapter<Cliente> selectAdapter = (SelectViewAdapter<Cliente>) grid_clientes
				.getAdapter();

		if (txtDescricao.getText() == null
				|| txtDescricao.getText().toString().trim().length() == 0) {
			txtDescricao.requestFocus();
			throw new BusinessException(this
					.getApplicationContext()
					.getString(R.string.msg_erro_campo_obrigatorio)
					.replace(
							"{1}",
							this.getApplicationContext().getString(
									R.string.string_field_descricao)));
		}

		if (rbtTexto.getVisibility() == View.GONE && rbtTexto.isChecked()
				&& txtMensagem.getText().toString().trim().length() == 0) {
			txtMensagem.requestFocus();
			throw new BusinessException(this
					.getApplicationContext()
					.getString(R.string.msg_erro_campo_obrigatorio)
					.replace(
							"{1}",
							this.getApplicationContext().getString(
									R.string.string_field_mensagem)));
		}

		if (rbtImagem.getVisibility() == View.GONE && rbtImagem.isChecked()
				&& selectedImagePath.trim().length() == 0) {
			throw new BusinessException(this.getApplicationContext().getString(
					R.string.msg_erro_selecione_imagem));
		}

		if (rbtAutomatico.getVisibility() == View.GONE
				&& rbtAutomatico.isChecked()
				&& selectAdapter.getSelectedItens().isEmpty()) {
			throw new BusinessException(this.getApplicationContext().getString(
					R.string.msg_erro_selecione_item));
		}

		return true;
	}

	public void selecionarImagem() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				// Log.d("URI VAL",
				// "selectedImageUri = " + selectedImageUri.toString());
				selectedImagePath = getPath(selectedImageUri);

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				int newWidth = size.x - 50;
				int newHeight = size.y - 50;

				if (selectedImagePath != null) {
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(),
										selectedImageUri);

						selectedImagePath = selectedImageUri.toString();
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
						
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						
						float scaleWidth = ((float) newWidth) / width;
					    float scaleHeight = ((float) newHeight) / height;
						
						Matrix matrix = new Matrix();

					    // Resize the bit map
					    matrix.postScale(scaleWidth, scaleHeight);
					    

//						bitmap = Bitmap.createScaledBitmap(bitmap, width,
//								height, true);
					    
					    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

						imageView1.setImageBitmap(bitmap);

						byteImagem = stream.toByteArray();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(),
										selectedImageUri);
						selectedImagePath = selectedImageUri.toString();

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						
						float scaleWidth = ((float) newWidth) / width;
					    float scaleHeight = ((float) newHeight) / height;
						
						Matrix matrix = new Matrix();

					    // Resize the bit map
					    matrix.postScale(scaleWidth, scaleHeight);
					    

//						bitmap = Bitmap.createScaledBitmap(bitmap, width,
//								height, true);
					    
					    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

						imageView1.setImageBitmap(bitmap);

						byteImagem = stream.toByteArray();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (requestCode == PICKFILE_RESULT_CODE) {
				Uri uri = data.getData();

				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(
							getContentResolver().openInputStream(uri)));

					String line;
					String[] dados;
					Cliente cliente = null;
					ArrayList<Cliente> clientes = new ArrayList<Cliente>();

					while ((line = reader.readLine()) != null) {
						if (!getApplicationContext().getString(
								R.string.string_empty).equals(line)) {
							cliente = new Cliente();
							dados = line.split(getApplicationContext()
									.getString(R.string.string_separator)
									.trim());

							cliente.setNome(dados[0]);
							cliente.setTelefone(dados[1]);
							cliente.setEmail(dados[2]);

							clientes.add(cliente);
						}
					}

					reader.close();

					grid_clientes.setAdapter(new SelectViewAdapter<Cliente>(
							this, clientes));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else {
			return uri.getPath();
		}
	}

	private void importarArquivo() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, PICKFILE_RESULT_CODE);
	}
}