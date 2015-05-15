package com.luksfon.villasalute.campanha.entity;

import com.luksfon.villasalute.campanha.annotation.Column;
import com.luksfon.villasalute.campanha.annotation.PrimaryKey;
import com.luksfon.villasalute.campanha.annotation.Table;

@Table(name="tSituacao")
public class Situacao extends EntityBase {
	
	@Column(name="IdSituacao", dbType="INTEGER")
	@PrimaryKey
	private int identificador;
	
	@Column(name="DsSituacao", dbType="TEXT")
	private String descricao;

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
}
