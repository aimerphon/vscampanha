package com.luksfon.villasalute.campanha.entity;

import com.luksfon.villasalute.campanha.annotation.Column;
import com.luksfon.villasalute.campanha.annotation.HasNullableColumn;
import com.luksfon.villasalute.campanha.annotation.Nullable;
import com.luksfon.villasalute.campanha.annotation.PrimaryKey;
import com.luksfon.villasalute.campanha.annotation.Table;

@Table(name = "tCliente")
@HasNullableColumn
public class Cliente extends ListViewEntityBase {

	@Column(name = "IdCliente", dbType = "INTEGER")
	@PrimaryKey
	private int identificador;

	@Column(name = "DsNome")
	private String nome;

	@Column(name = "DsTelefone")
	private String telefone;

	@Column(name = "DsEmail")
	@Nullable
	private String email;

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getTitle() {
		return getNome();
	}

	@Override
	public String getSubTitle() {
		return getTelefone();
	}
	
	@Override
	public String getLabelTitle() {
		return "Nome:";
	}

	@Override
	public String getLabelSubTitle() {
		return "Telefone:";
	}

	@Override
	public int getId() {
		return getIdentificador();
	}
}
