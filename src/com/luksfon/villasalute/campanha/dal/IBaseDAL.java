package com.luksfon.villasalute.campanha.dal;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.exception.BusinessException;

public interface IBaseDAL<E extends EntityBase> {
	int insert(E entity) throws BusinessException, IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException;

	int delete(E entity) throws BusinessException, IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException, InvocationTargetException;

	int update(E entity) throws BusinessException, IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException, InvocationTargetException;

	E get(E entity) throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, BusinessException, NoSuchMethodException,
			NoSuchFieldException, InvocationTargetException;

	List<E> getAll() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, BusinessException, NoSuchMethodException,
			NoSuchFieldException, InvocationTargetException;
}
