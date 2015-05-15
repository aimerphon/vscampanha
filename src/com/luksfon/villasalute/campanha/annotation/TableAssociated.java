package com.luksfon.villasalute.campanha.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.luksfon.villasalute.campanha.entity.EntityBase;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableAssociated {
	Class<? extends EntityBase> classTableAssociated();
}
