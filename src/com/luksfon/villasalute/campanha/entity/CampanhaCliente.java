package com.luksfon.villasalute.campanha.entity;

import com.luksfon.villasalute.campanha.annotation.Column;
import com.luksfon.villasalute.campanha.annotation.ForeignKey;
import com.luksfon.villasalute.campanha.annotation.PrimaryKey;
import com.luksfon.villasalute.campanha.annotation.Table;

@Table(name = "tCampanhaCliente")
public class CampanhaCliente extends ListViewEntityBase {

	@Column(name = "IdCampanhaCliente", dbType = "INTEGER")
	@PrimaryKey
	private int identificador;
	
	@ForeignKey(column="IdCampanha")
	private Campanha campanha;
	
	@ForeignKey(column="IdCliente")
	private Cliente cliente;
	
	@ForeignKey(column="IdSituacao")
	private Situacao situacao;

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public Campanha getCampanha() {
		return campanha;
	}

	public void setCampanha(Campanha campanha) {
		this.campanha = campanha;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}
	
	@Override
	public String getTitle() {
		return getCliente().getNome();
	}

	@Override
	public String getSubTitle() {
		return getSituacao().getDescricao();
	}
	
	@Override
	public String getLabelTitle() {
		return "Nome:";
	}

	@Override
	public String getLabelSubTitle() {
		return "Situação:";
	}

	@Override
	public int getId() {
		return getIdentificador();
	}
}
