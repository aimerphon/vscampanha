package com.luksfon.villasalute.campanha.controller;

import android.content.Context;

import com.luksfon.villasalute.campanha.database.DatabaseManager;
import com.luksfon.villasalute.campanha.entity.EntityBase;


public class CampanhaClienteController<CampanhaCliente extends EntityBase> extends DatabaseManager {
	
	public CampanhaClienteController(boolean buildAllEntities, Context context) {
		super(buildAllEntities, context);
	}
}