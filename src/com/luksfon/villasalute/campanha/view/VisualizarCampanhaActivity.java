package com.luksfon.villasalute.campanha.view;

import java.lang.reflect.InvocationTargetException;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.controller.CampanhaController;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.util.SituacaoCampanha;
import com.luksfon.villasalute.campanha.util.TipoEnvio;
import com.luksfon.villasalute.campanha.view.adapter.CampanhaAdapter;
import com.luksfon.villasalute.campanha.view.adapter.ClienteAdapter;

public class VisualizarCampanhaActivity extends BaseActivity {

	public final static String EXTRA_MESSAGE = "com.luksfon.villasalute.campanha.view.VisualizarCampanhaActivity.MESSAGE";
	public final static String EXTRA_MESSAGE_EDITAR = "com.luksfon.villasalute.campanha.view.VisualizarCampanhaActivity.EDITAR";
	public final static String EXTRA_MESSAGE_VISUALIZAR = "com.luksfon.villasalute.campanha.view.VisualizarCampanhaActivity.VISUALIZARCLIENTE";
	public final static String EXTRA_MESSAGE_WHATSAPP = "com.luksfon.villasalute.campanha.view.VisualizarCampanhaActivity.WHATSAPP";
	private static final int SELECT_PICTURE = 1;
	public static final int EDITAR_CAMPANHA = 2;
	public static final int VISUALIZAR_CLIENTE = 3;
	public static final int WHATSAPP = 4;
	public static final int RESULT_WHATSAPP = 0;

	private boolean finalizado;
	private boolean showMessage;
	private boolean isWhatsApp;
	private Campanha campanha;
	private ListView gridClientes;
	private int indiceUltimoClienteEnviado;
	private ImageView imgSituacao;
	private ImageView imageView1;
	private ProgressBar progressBar;
	private TextView lblClientes;
	private TextView lblMensagem;
	private TextView txtMensagem;
	private TextView txtDescricao;
	private Uri selectedImageUri;
	private boolean fromEdit;
	private boolean fromDetailClient;

	private int mProgressStatus = 0;
	private Bitmap bitmap = null;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutResId = R.layout.visualizar_campanha;

		super.onCreate(savedInstanceState);
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

	@Override
	protected void inicializarTela() {
		txtDescricao = (TextView) findViewById(R.id.txtDescricao);
		// txtSituacao = (TextView) findViewById(R.id.txtSituacao);
		gridClientes = (ListView) findViewById(R.id.grid_clientes);
		// txtTipoEnvio = (TextView) findViewById(R.id.txtTipoEnvio);
		lblMensagem = (TextView) findViewById(R.id.lblMensagem);
		txtMensagem = (TextView) findViewById(R.id.txtMensagem);
		lblClientes = (TextView) findViewById(R.id.lblClientes);
		imgSituacao = (ImageView) findViewById(R.id.imgSituacao);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	@Override
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

			this.campanha = campanha;

			txtDescricao.setText(campanha.getDescricao());

			imgSituacao.setImageResource(CampanhaAdapter
					.obterImageResId(campanha));
			progressBar.setVisibility(View.GONE);

			if (TipoEnvio.AUTOMATICO.getValue() == campanha.getTipoEnvio()) {
				gridClientes.setAdapter(new ClienteAdapter<CampanhaCliente>(
						this, campanha.getClientes()));
				gridClientes.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent visuzalizarCliente = new Intent(parent
								.getContext(), VisualizarClienteActivity.class);
						CampanhaCliente campanhaCliente = (CampanhaCliente) parent
								.getItemAtPosition(position);
						visuzalizarCliente.putExtra(
								ConsultaClientesActivity.EXTRA_MESSAGE, String
										.valueOf(campanhaCliente.getCliente()
												.getIdentificador()));
						startActivityForResult(visuzalizarCliente,
								VISUALIZAR_CLIENTE);
					}
				});

				lblMensagem.setVisibility(View.GONE);
				txtMensagem.setVisibility(View.GONE);

				obterUltimoEnviado();
			} else {
				if (campanha.getCaminhoImagem() == null) {
					txtMensagem.setText(campanha.getMensagem());
				} else {
					if (selectedImageUri == null) {
						lblMensagem.setVisibility(View.GONE);
						txtMensagem.setVisibility(View.GONE);
						imageView1.setVisibility(View.GONE);
						progressBar.setVisibility(View.VISIBLE);

						Uri uri = Uri.parse(campanha.getCaminhoImagem());

						selectedImageUri = uri;

						new Thread(new Runnable() {
							public void run() {
								while (mProgressStatus < 100) {
									bitmap = carregarImagem(progressBar,
											imageView1, selectedImageUri);
									mProgressStatus = 100;

									// Update the progress bar
									mHandler.post(new Runnable() {
										public void run() {
											progressBar
													.setProgress(mProgressStatus);
											progressBar
													.setVisibility(View.GONE);
											imageView1.setVisibility(View.VISIBLE);
											imageView1.setImageBitmap(bitmap);
											imageView1
													.setAdjustViewBounds(true);
											imageView1
													.setScaleType(ScaleType.CENTER_CROP);
										}
									});
								}
							}
						}).start();
					}
				}

				lblClientes.setVisibility(View.GONE);
				gridClientes.setVisibility(View.GONE);
			}

			if (this.campanha.getClientes() == null
					|| this.campanha.getClientes().isEmpty()) {
				indiceUltimoClienteEnviado = -1;
			}

			isWhatsApp = false;
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
			if (SituacaoCampanha.ENVIADO.getValue() == this.campanha
					.getSituacao().getIdentificador()) {
				finalizado = true;
				this.showConfirmationMessage(
						this.getApplicationContext(),
						"Reenviar campanha",
						this.getApplicationContext().getString(
								R.string.msg_erro_campanha_ja_enviada),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								finalizado = false;
								campanha.getSituacao()
										.setIdentificador(
												SituacaoCampanha.NAO_ENVIADO
														.getValue());

								CampanhaController campanhaController = new CampanhaController(
										true, getApplicationContext());

								try {
									campanhaController
											.atualizarSituacaoCampanha(campanha);
								} catch (BusinessException e) {
									e.printStackTrace();
								}

								enviarCampanha();
							}
						}, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
			} else {
				enviarCampanha();
			}
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
		startActivityForResult(editarCampanha, EDITAR_CAMPANHA);
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
			String intentAction = Intent.ACTION_SEND;
			Intent i = new Intent(intentAction);
			showMessage = true;

			if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				obterUltimoEnviado();

				CampanhaCliente campanhaCliente = this.campanha.getClientes()
						.get(indiceUltimoClienteEnviado);
				Uri uri = Uri.parse("smsto:"
						+ campanhaCliente.getCliente().getTelefone());
				intentAction = Intent.ACTION_SENDTO;
				i = new Intent(intentAction, uri);
				i.setPackage("com.whatsapp");
				startActivityForResult(Intent.createChooser(i, ""), WHATSAPP);
			} else if (campanha.getTipoEnvio() == TipoEnvio.MANUAL.getValue()) {
				if (campanha.getMensagem() != null
						&& campanha.getMensagem().length() > 0) {
					i.setType("text/plain");
					String text = campanha.getMensagem();
					i.putExtra(Intent.EXTRA_TEXT, text);
					i.setPackage("com.whatsapp");
					startActivityForResult(Intent.createChooser(i, ""),
							WHATSAPP);
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
						startActivityForResult(Intent.createChooser(i, ""),
								WHATSAPP);
					}
				}
			}
		} catch (Exception ex) {
			Log.println(0, "Enviar Campanha", ex.getMessage());
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (!finalizado && isWhatsApp) {
			if (!fromEdit && !fromDetailClient) {
				CampanhaController campanhaController = new CampanhaController(
						true, getApplicationContext());

				if (campanha.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()
						&& SituacaoCampanha.ENVIADO.getValue() != campanha
								.getSituacao().getIdentificador()) {

					indiceUltimoClienteEnviado = campanhaController
							.campanhaEnviadaCliente(campanha,
									indiceUltimoClienteEnviado);

					if (indiceUltimoClienteEnviado < this.campanha
							.getClientes().size()) {
						enviarCampanha();
					} else {
						finalizado = true;
						super.showMessage(getApplicationContext().getString(
								R.string.msg_operacao_sucesso));
						super.onBackPressed();
					}
				} else if (campanha.getTipoEnvio() == TipoEnvio.MANUAL
						.getValue()) {
					showConfirmation();
				}
			}
		}

		carregarTela();
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
			} else if (requestCode == EDITAR_CAMPANHA
					|| requestCode == VISUALIZAR_CLIENTE) {
				fromEdit = data
						.hasExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE_EDITAR);
				fromDetailClient = data
						.hasExtra(VisualizarCampanhaActivity.EXTRA_MESSAGE_VISUALIZAR);
				isWhatsApp = false;
			}
		} else if (resultCode == RESULT_WHATSAPP) {
			isWhatsApp = true;
			fromDetailClient = false;
			fromEdit = false;
		}
	}

	private void obterUltimoEnviado() {
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
		} else if (SituacaoCampanha.ENVIADO.getValue() == campanha
				.getSituacao().getIdentificador()) {
			indiceUltimoClienteEnviado = campanha.getClientes().size();
		} else if (SituacaoCampanha.NAO_ENVIADO.getValue() == campanha
				.getSituacao().getIdentificador()) {
			indiceUltimoClienteEnviado = 0;
		}
	}
}