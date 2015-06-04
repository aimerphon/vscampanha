package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.util.SituacaoCampanha;
import com.luksfon.villasalute.campanha.util.TipoEnvio;
import com.luksfon.villasalute.campanha.view.adapter.ListViewAdapter;

public class VisualizarCampanhaActivity extends BaseActivity {

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.VisualizarCampanhaActivity.MESSAGE";
	private static final int SELECT_PICTURE = 1;

	private boolean finalizado;
	private boolean showMessage;
	private Campanha campanha;
	private GridView gridClientes;
	private int indiceUltimoClienteEnviado;
	private TextView lblClientes;
	private TextView txtTipoEnvio;
	private TextView lblTipoMensagem;
	private TextView txtTipoMensagem;
	private TextView lblMensagem;
	private TextView txtMensagem;
	private TextView txtDescricao;
	private TextView txtSituacao;
	private Uri selectedImageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizar_campanha);

		try {
			inicializarTela();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showConfirmation() {
		if (showMessage) {
			showConfirmationMessage(this.getApplicationContext(),
					"Enviar outra messagem",
					"Gostaria de enviar a messagem para outro cliente?",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							showMessage = false;
							enviarCampanha();
							dialog.cancel();
						}
					}, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finalizado = true;
							showMessage(getApplicationContext().getString(
									R.string.msg_operacao_sucesso));
							dialog.cancel();
							onBackPressed();
						}
					});
		}

		showMessage = false;
	}

	protected void inicializarTela() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, BusinessException {
		txtDescricao = (TextView) findViewById(R.id.txtDescricao);
		txtSituacao = (TextView) findViewById(R.id.txtSituacao);
		gridClientes = (GridView) findViewById(R.id.grid_clientes);
		txtTipoEnvio = (TextView) findViewById(R.id.txtTipoEnvio);
		lblTipoMensagem = (TextView) findViewById(R.id.lblTipoMensagem);
		txtTipoMensagem = (TextView) findViewById(R.id.txtTipoMensagem);
		lblMensagem = (TextView) findViewById(R.id.lblMensagem);
		txtMensagem = (TextView) findViewById(R.id.txtMensagem);
		lblClientes = (TextView) findViewById(R.id.lblClientes);

		carregarTela();

		if (this.campanha.getClientes() == null
				|| this.campanha.getClientes().isEmpty()) {
			indiceUltimoClienteEnviado = -1;
		}
	}

	protected void carregarTela() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, BusinessException {
		Intent intent = getIntent();
		String id = intent.getStringExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE);

		CampanhaController campanhaController = new CampanhaController(true,
				getBaseContext());

		Campanha campanha = new Campanha();
		campanha.setIdentificador(Integer.parseInt(id));

		campanha = campanhaController.get(campanha);

		txtDescricao.setText(campanha.getDescricao());
		txtSituacao.setText(campanha.getSituacao().getDescricao());
		gridClientes.setAdapter(new ListViewAdapter<CampanhaCliente>(this,
				campanha.getClientes()));
		gridClientes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent visuzalizarCliente = new Intent(parent.getContext(),
						VisualizarClienteActivity.class);
				visuzalizarCliente.putExtra(
						ConsultaClientesActivity.EXTRA_MESSAGE,
						String.valueOf(parent.getItemIdAtPosition(position)));
				startActivity(visuzalizarCliente);
			}
		});

		if (TipoEnvio.AUTOMATICO.getValue() == campanha.getTipoEnvio()) {
			txtTipoEnvio.setText(this.getApplicationContext().getString(
					R.string.string_label_direto));
			lblTipoMensagem.setVisibility(View.GONE);
			txtTipoMensagem.setVisibility(View.GONE);
			lblMensagem.setVisibility(View.GONE);
			txtMensagem.setVisibility(View.GONE);

			if (SituacaoCampanha.ENVIANDO.getValue() == campanha.getSituacao()
					.getIdentificador()) {
				indiceUltimoClienteEnviado = 0;
				for (CampanhaCliente campanhaCliente : campanha.getClientes()) {
					if (SituacaoCampanha.NAO_ENVIADO.getValue() == campanhaCliente
							.getSituacao().getIdentificador()) {
						break;
					}
					indiceUltimoClienteEnviado++;
				}
			}
		} else {
			txtTipoEnvio.setText(this.getApplicationContext().getString(
					R.string.string_label_manual));

			if (campanha.getCaminhoImagem() == null) {
				txtTipoMensagem.setText(this.getApplicationContext().getString(
						R.string.string_label_texto));
				txtMensagem.setText(campanha.getMensagem());
			} else {
				txtTipoMensagem.setText(this.getApplicationContext().getString(
						R.string.string_label_imagem));
				lblMensagem.setVisibility(View.GONE);
				txtMensagem.setVisibility(View.GONE);
			}

			lblClientes.setVisibility(View.GONE);
			gridClientes.setVisibility(View.GONE);
		}

		this.campanha = campanha;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detalhar_campanha, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_editar:
			editarCampanha();
			return true;
		case R.id.action_excluir:
			excluirCampanha();
			return true;
		case R.id.action_enviar:
			enviarCampanha();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void editarCampanha() {
		Intent editarCampanha = new Intent(this.getApplicationContext(),
				EditarCampanhaActivity.class);
		editarCampanha.putExtra(EXTRA_MESSAGE,
				String.valueOf(campanha.getIdentificador()));
		startActivity(editarCampanha);
	}

	protected void excluirCampanha() {
		CampanhaController campanhaController = new CampanhaController(true,
				getApplicationContext());
		campanhaController.excluirCampanha(campanha);
		super.showMessage(getApplication().getString(
				R.string.msg_operacao_sucesso));
		super.onBackPressed();
	}

	protected void enviarCampanha() {
		try {
			if (Integer.valueOf(
					getApplicationContext()
							.getString(R.string.situacao_enviado)).equals(
					this.campanha.getSituacao().getIdentificador())) {
				finalizado = true;
				throw new BusinessException(getApplicationContext().getString(
						R.string.msg_erro_campanha_ja_enviada));
			}

			String intentAction = Intent.ACTION_SEND;
			Intent i = new Intent(intentAction);
			showMessage = true;

			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				CampanhaCliente campanhaCliente = this.campanha.getClientes()
						.get(indiceUltimoClienteEnviado);
				Uri uri = Uri.parse("smsto:"
						+ campanhaCliente.getCliente().getTelefone());
				intentAction = Intent.ACTION_SENDTO;
				i = new Intent(intentAction, uri);
				i.setPackage("com.whatsapp");
				startActivity(Intent.createChooser(i, ""));
			} else if (campanha.getTipoEnvio() == TipoEnvio.MANUAL.getValue()) {
				if (campanha.getMensagem() != null
						&& campanha.getMensagem().length() > 0) {
					i.setType("text/plain");
					String text = campanha.getMensagem();
					i.putExtra(Intent.EXTRA_TEXT, text);
					i.setPackage("com.whatsapp");
					startActivity(Intent.createChooser(i, ""));
				} else if (campanha.getCaminhoImagem() != null) {
					if (selectedImageUri == null) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								SELECT_PICTURE);
					} else {
						intentAction = Intent.ACTION_SEND;
						i = new Intent(intentAction);
						i.setType("image/*");
						i.putExtra(Intent.EXTRA_STREAM, selectedImageUri);
						i.setPackage("com.whatsapp");
						startActivity(Intent.createChooser(i, ""));
					}
				}
			}
		} catch (BusinessException bex) {
			super.showMessage(bex.getMessage());
		} catch (Exception ex) {
			Log.println(0, "Enviar Campanha", ex.getMessage());
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (!finalizado) {
			CampanhaController campanhaController = new CampanhaController(
					true, getApplicationContext());

			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				indiceUltimoClienteEnviado = campanhaController
						.campanhaEnviadaCliente(campanha,
								indiceUltimoClienteEnviado);

				if (indiceUltimoClienteEnviado < this.campanha.getClientes()
						.size()) {
					enviarCampanha();
				} else {
					finalizado = true;
					super.showMessage(getApplicationContext().getString(
							R.string.msg_operacao_sucesso));
					super.onBackPressed();
				}
			} else if (campanha.getTipoEnvio() == TipoEnvio.MANUAL.getValue()) {
				showConfirmation();
			}

			try {
				carregarTela();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				selectedImageUri = data.getData();
				String intentAction = Intent.ACTION_SEND;
				Intent i = new Intent(intentAction);

				i.setType("image/*");
				i.putExtra(Intent.EXTRA_STREAM, selectedImageUri);
				i.setPackage("com.whatsapp");
				startActivity(Intent.createChooser(i, ""));
			}
		}
	}
}