package com.luksfon.villasalute.campanha.controller;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.luksfon.villasalute.campanha.database.DatabaseManager;
import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.entity.Situacao;
import com.luksfon.villasalute.campanha.exception.BusinessException;

public class SituacaoController<E extends EntityBase> extends DatabaseManager {

	public SituacaoController(boolean buildAllEntities, Context context) {
		super(buildAllEntities, context);
	}

	@Override
	public <T extends EntityBase> void createTable(Class<T> classEntity) {
		super.createTable(classEntity);

		try {
			Situacao situacao = new Situacao();

			situacao.setDescricao("Enviado");

			super.insert(situacao);
			
			situacao.setDescricao("Enviando");

			super.insert(situacao);
			
			situacao.setDescricao("Não enviado");

			super.insert(situacao);

		} catch (IllegalArgumentException e) {
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
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
}
