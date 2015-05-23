package com.luksfon.villasalute.campanha.entity;

import java.util.ArrayList;

import com.luksfon.villasalute.campanha.annotation.Column;
import com.luksfon.villasalute.campanha.annotation.ForeignKey;
import com.luksfon.villasalute.campanha.annotation.Nullable;
import com.luksfon.villasalute.campanha.annotation.PrimaryKey;
import com.luksfon.villasalute.campanha.annotation.Table;
import com.luksfon.villasalute.campanha.annotation.TableAssociated;

@Table(name = "tCampanha")
public class Campanha extends ListViewEntityBase {

	@Column(name = "IdCampanha", dbType = "INTEGER")
	@PrimaryKey
	private int identificador;
	
	@Column(name = "DsCampanha", dbType = "TEXT")
	private String descricao;
	
	@ForeignKey(column="IdCampanha")
	@TableAssociated(classTableAssociated=CampanhaCliente.class)
	private ArrayList<CampanhaCliente> clientes;
	
	@ForeignKey(column="IdSituacao")
	private Situacao situacao;
	
	@Column(name = "DsMensagem", dbType = "TEXT")
	@Nullable
	private String mensagem;
	
	@Column(name = "DsPathImagem", dbType = "TEXT")
	@Nullable
	private String caminhoImagem;
	
	@Column(name = "StManual", dbType = "INTEGER")
	private int tipoEnvio;
	
	@Column(name = "BlImagem", dbType = "BLOB")
	@Nullable
	private byte[] imagem;
	
	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ArrayList<CampanhaCliente> getClientes() {
		return clientes;
	}

	public void setClientes(ArrayList<CampanhaCliente> clientes) {
		this.clientes = clientes;
	}
	
	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}
	
	@Override
	public String getTitle() {
		return getDescricao();
	}

	@Override
	public String getSubTitle() {
		return getSituacao().getDescricao();
	}

	@Override
	public String getLabelTitle() {
		return "Campanha:";
	}

	@Override
	public String getLabelSubTitle() {
		return "Situação";
	}

	@Override
	public int getId() {
		return getIdentificador();
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getCaminhoImagem() {
		return caminhoImagem;
	}

	public void setCaminhoImagem(String caminhoImagem) {
		this.caminhoImagem = caminhoImagem;
	}

	public int getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(int tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}
}
