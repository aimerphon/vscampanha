package com.luksfon.villasalute.campanha.view;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
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

	private Button btnSelecionarImagem;
	private EditText txtDescricao;
	private EditText txtMensagem;
	private GridView grid_clientes;
	private ImageView imageView1;
	private TextView lblClientes;
	private RadioButton rbtTexto;
	private RadioButton rbtImagem;
	private RadioButton rbtManual;
	private RadioButton rbtAutomatico;

	private String selectedImagePath;
	private byte[] byteImagem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastrar_campanha);

		try {
			inicializarTela();
		} catch (Exception ex) {
			Log.println(0, "ConsultaClientesActivity.onCreate", ex.getMessage());
		}
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
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return super.onOptionsItemSelected(item);
		}
	}

	private void inicializarTela() throws InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException {
		txtDescricao = (EditText) findViewById(R.id.txtDescricao);
		txtDescricao.setText(this.getApplicationContext().getString(
				R.string.string_empty));
		txtDescricao.setEnabled(true);
		lblClientes = (TextView) findViewById(R.id.lblClientes);
		grid_clientes = (GridView) findViewById(R.id.grid_clientes);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		btnSelecionarImagem = (Button) findViewById(R.id.btnImagem);
		btnSelecionarImagem.setVisibility(View.GONE);
		btnSelecionarImagem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selecionarImagem();
			}
		});
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
				btnSelecionarImagem.setVisibility(View.VISIBLE);
				btnSelecionarImagem.setEnabled(true);
				imageView1.setVisibility(View.VISIBLE);
				imageView1.setImageDrawable(null);
				selectedImagePath = getApplicationContext().getString(
						R.string.string_empty);
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
			}
		});

		CarregarGrid();

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

				try {
					CarregarGrid();
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
		});
	}

	public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
		int totalHeight = getTotalHeight(gridView, columns);
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		gridView.setLayoutParams(params);
	}

	private int getTotalHeight(GridView gridView, int columns) {
		ListAdapter listAdapter = gridView.getAdapter();
		int totalHeight = 0;
		int rows = gridView.getCount();

		View listItem = listAdapter.getView(0, null, gridView);
		listItem.measure(0, 0);
		totalHeight = listItem.getMeasuredHeight();

		totalHeight *= rows;

		return totalHeight;
	}

	private void CarregarGrid() throws InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException {
		ClienteController<Cliente> clienteController = new ClienteController<Cliente>(
				false, getBaseContext());

		ArrayList<Cliente> lista1 = clienteController.toList(Cliente.class);

		grid_clientes.setAdapter(new SelectViewAdapter<Cliente>(this, lista1));
		this.setGridViewHeightBasedOnChildren(grid_clientes, lista1.size());
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

		if (rbtTexto.isChecked()
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

		if (rbtImagem.isChecked() && selectedImagePath.trim().length() == 0) {
			btnSelecionarImagem.requestFocus();
			throw new BusinessException(this.getApplicationContext().getString(
					R.string.msg_erro_selecione_imagem));
		}

		if (rbtAutomatico.isChecked()
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
				Log.d("URI VAL",
						"selectedImageUri = " + selectedImageUri.toString());
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {
					// IF LOCAL IMAGE, NO MATTER IF ITS DIRECTLY FROM GALLERY
					// (EXCEPT PICASSA ALBUM),
					// OR OI/ASTRO FILE MANAGER. EVEN DROPBOX IS SUPPORTED BY
					// THIS BECAUSE DROPBOX DOWNLOAD THE IMAGE
					// IN THIS FORM -
					// file:///storage/emulated/0/Android/data/com.dropbox.android/...
					System.out.println("local image");
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(),
										selectedImageUri);
						imageView1.setImageBitmap(bitmap);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("picasa image!");
					// loadPicasaImageFromGallery(selectedImageUri);
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(),
										selectedImageUri);
						selectedImagePath = selectedImageUri.toString();
						imageView1.setImageBitmap(bitmap);
						
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						
						byteImagem = stream.toByteArray();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else
			return uri.getPath(); // FOR OI/ASTRO/Dropbox etc
	}
}