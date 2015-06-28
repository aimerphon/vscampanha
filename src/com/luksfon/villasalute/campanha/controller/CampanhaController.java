package com.luksfon.villasalute.campanha.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.content.Context;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.database.DatabaseManager;
import com.luksfon.villasalute.campanha.entity.Campanha;
import com.luksfon.villasalute.campanha.entity.CampanhaCliente;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.entity.Situacao;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.query.LogicComparator;
import com.luksfon.villasalute.campanha.query.Restriction;
import com.luksfon.villasalute.campanha.util.SituacaoCampanha;
import com.luksfon.villasalute.campanha.util.TipoEnvio;

public class CampanhaController extends DatabaseManager {

	public CampanhaController(boolean buildAllEntities, Context context) {
		super(buildAllEntities, context);
	}

	public int insert(Campanha entity, List<Cliente> clientes)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException, NoSuchFieldException {

		int rowsAffected = 0;

		try {
			beginTransaction();

			Situacao situacao = new Situacao();
			situacao.setIdentificador(3);
			entity.setSituacao(situacao);
			rowsAffected = super.insert(entity);
			entity.setIdentificador(rowsAffected);

			CampanhaClienteController<CampanhaCliente> campanhaClienteController = new CampanhaClienteController<CampanhaCliente>(
					true, this.context);
			CampanhaCliente campanhaCliente = null;

			for (Cliente cliente : clientes) {
				campanhaCliente = new CampanhaCliente();
				campanhaCliente.setCampanha(entity);
				campanhaCliente.setCliente(cliente);
				campanhaCliente.setSituacao(situacao);

				campanhaClienteController.insert(campanhaCliente);
			}

			saveChanges();
		} catch (IllegalAccessException ex) {
			rollBack();
		} catch (IllegalArgumentException ex) {
			rollBack();
		} catch (NoSuchMethodException ex) {
			rollBack();
		} catch (InvocationTargetException ex) {
			rollBack();
		} catch (ClassNotFoundException ex) {
			rollBack();
		} catch (BusinessException ex) {
			rollBack();
		}

		return rowsAffected;
	}

	public int editarCampanha(Campanha entity, List<Cliente> clientes)
			throws BusinessException {
		int rowsAffected = 0;

		try {
			beginTransaction();

			Situacao situacao = new Situacao();
			situacao.setIdentificador(3);
			entity.setSituacao(situacao);
			rowsAffected = super.update(entity);

			CampanhaClienteController<CampanhaCliente> campanhaClienteController = new CampanhaClienteController<CampanhaCliente>(
					true, this.context);
			CampanhaCliente campanhaCliente = null;

			if (entity.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				if (entity.getClientes() != null
						&& !entity.getClientes().isEmpty()) {
					super.where(
							new Restriction(
									context.getString(R.string.table_column_identificador_campanha),
									context.getString(R.string.table_campanha_cliente),
									LogicComparator.Equals, String
											.valueOf(entity.getIdentificador())))
							.deleteByRestriction(CampanhaCliente.class);
				}

				for (Cliente cliente : clientes) {
					campanhaCliente = new CampanhaCliente();
					campanhaCliente.setCampanha(entity);
					campanhaCliente.setCliente(cliente);
					campanhaCliente.setSituacao(situacao);

					campanhaClienteController.insert(campanhaCliente);
				}
			}

			saveChanges();
		} catch (IllegalAccessException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (IllegalArgumentException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (NoSuchMethodException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (InvocationTargetException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (ClassNotFoundException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (BusinessException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (NoSuchFieldException e) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		}

		return rowsAffected;
	}

	public int atualizarSituacaoCampanha(Campanha entity)
			throws BusinessException {
		int rowsAffected = 0;

		try {
			beginTransaction();

			Situacao situacao = new Situacao();
			situacao.setIdentificador(SituacaoCampanha.NAO_ENVIADO.getValue());
			entity.setSituacao(situacao);

			rowsAffected = super.update(entity);

			CampanhaClienteController<CampanhaCliente> campanhaClienteController = new CampanhaClienteController<CampanhaCliente>(
					true, this.context);

			if (entity.getTipoEnvio() == TipoEnvio.AUTOMATICO.getValue()) {
				if (entity.getClientes() != null
						&& !entity.getClientes().isEmpty()) {

					for (CampanhaCliente campanhaCliente : entity.getClientes()) {
						campanhaCliente.getSituacao().setIdentificador(
								SituacaoCampanha.NAO_ENVIADO.getValue());

						campanhaClienteController.update(campanhaCliente);
					}
				}
			}

			saveChanges();
		} catch (IllegalAccessException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (IllegalArgumentException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (NoSuchMethodException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (InvocationTargetException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		} catch (BusinessException ex) {
			rollBack();
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_operacao_nao_realizada));
		}

		return rowsAffected;
	}

	public int campanhaEnviadaCliente(Campanha campanha,
			int indiceUltimoClienteEnviado) {
		try {
			beginTransaction();

			if (indiceUltimoClienteEnviado < campanha.getClientes().size()) {
				CampanhaCliente campanhaCliente = campanha.getClientes().get(
						indiceUltimoClienteEnviado);

				campanhaCliente.getSituacao().setIdentificador(
						SituacaoCampanha.ENVIADO.getValue());
				campanhaCliente.getSituacao().setDescricao(
						context.getString(R.string.situacao_enviado_descricao));

				CampanhaClienteController<CampanhaCliente> campanhaClienteController = new CampanhaClienteController<CampanhaCliente>(
						true, context);

				campanhaClienteController.update(campanhaCliente);

				indiceUltimoClienteEnviado++;
			}

			if (campanhaFinalizada(campanha, indiceUltimoClienteEnviado)) {
				campanha.getSituacao().setIdentificador(
						SituacaoCampanha.ENVIADO.getValue());
				campanha.getSituacao().setDescricao(
						context.getString(R.string.situacao_enviado_descricao));
			} else {
				campanha.getSituacao().setIdentificador(
						SituacaoCampanha.ENVIANDO.getValue());
				campanha.getSituacao()
						.setDescricao(
								context.getString(R.string.situacao_enviando_descricao));

			}

			update(campanha);

			saveChanges();
		} catch (IllegalAccessException e) {
			rollBack();
		} catch (NoSuchMethodException e) {
			rollBack();
		} catch (IllegalArgumentException e) {
			rollBack();
		} catch (InvocationTargetException e) {
			rollBack();
		} catch (BusinessException e) {
			rollBack();
		}

		return indiceUltimoClienteEnviado;
	}

	public int excluirCampanha(Campanha campanha) {
		int rowsAffected = 0;
		try {
			beginTransaction();

			if (campanha.getClientes() != null
					&& !campanha.getClientes().isEmpty()) {
				super.where(
						new Restriction(
								context.getString(R.string.table_column_identificador_campanha),
								context.getString(R.string.table_campanha_cliente),
								LogicComparator.Equals, String.valueOf(campanha
										.getIdentificador())))
						.deleteByRestriction(CampanhaCliente.class);
			}

			rowsAffected = super.delete(campanha);

			saveChanges();
		} catch (IllegalAccessException e) {
			rollBack();
		} catch (NoSuchMethodException e) {
			rollBack();
		} catch (IllegalArgumentException e) {
			rollBack();
		} catch (InvocationTargetException e) {
			rollBack();
		} catch (BusinessException e) {
			rollBack();
		} catch (NoSuchFieldException e) {
			rollBack();
		}

		return rowsAffected;
	}

	protected boolean campanhaFinalizada(Campanha campanha,
			int indiceUltimoClienteEnviado) {
		boolean finalizada = true;

		if (SituacaoCampanha.ENVIANDO.getValue() == campanha.getSituacao()
				.getIdentificador()
				|| SituacaoCampanha.NAO_ENVIADO.getValue() == campanha
						.getSituacao().getIdentificador()) {
			if (existeProximoCliente(campanha, indiceUltimoClienteEnviado)) {
				finalizada = false;
			}
		}

		return finalizada;
	}

	protected boolean existeProximoCliente(Campanha campanha,
			int indiceUltimoClienteEnviado) {
		boolean existeProximo = false;

		if (campanha.getClientes() != null && !campanha.getClientes().isEmpty()
				&& indiceUltimoClienteEnviado < campanha.getClientes().size()) {
			existeProximo = true;
		}

		return existeProximo;
	}
}