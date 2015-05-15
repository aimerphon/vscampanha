package com.luksfon.villasalute.campanha.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.content.Context;

import com.luksfon.villasalute.campanha.dal.BaseDAL;
import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.exception.BusinessException;


public class BaseController <D extends BaseDAL<E>, E extends EntityBase> {

	protected D baseDAL;

	public BaseController(Context context, Class<D> classDAL,
			Class<E> classEntity) throws InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		Constructor<D> constructor = classDAL.getConstructor(Context.class);
		this.baseDAL = constructor.newInstance(context);
	}

	public int insert(E entity) throws IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException,
			BusinessException {
		return baseDAL.insert(entity);
	}

	public int delete(E entity) throws BusinessException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		return baseDAL.delete(entity);
	}

	public int update(E entity) throws BusinessException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		return baseDAL.update(entity);
	}

	public E get(E entity) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, BusinessException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException {
		return (E) baseDAL.get(entity);
	}

	public List<E> getAll() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, BusinessException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException {
		return baseDAL.getAll();
	}
}
