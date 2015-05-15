package com.luksfon.villasalute.campanha.dal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.luksfon.villasalute.campanha.R;
import com.luksfon.villasalute.campanha.annotation.Column;
import com.luksfon.villasalute.campanha.annotation.ForeignKey;
import com.luksfon.villasalute.campanha.annotation.HasNullableColumn;
import com.luksfon.villasalute.campanha.annotation.Nullable;
import com.luksfon.villasalute.campanha.annotation.PrimaryKey;
import com.luksfon.villasalute.campanha.annotation.Table;
import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.exception.BusinessException;

@SuppressLint("DefaultLocale")
public abstract class BaseDAL<E extends EntityBase> implements IBaseDAL<E> {

	private Context context;
	private String tableName;
	// private String[] columnsName;
	private String[] pkValues;
	private String nullableColumns;
	private String conditionPK;
	private Class<E> classEntity;

	public BaseDAL(Context context, Class<E> classEntity) {
		this.context = context;
		this.classEntity = classEntity;
		Table table = classEntity.getAnnotation(Table.class);
		this.tableName = table.name();
	}

	private void verifyReturned(int rowsAffected) throws BusinessException {
		if (rowsAffected == -1) {
			throw new BusinessException(
					context.getString(R.string.msg_erro_insertUpdate));
		}
	}

	private static SQLiteDatabase createOrOpenDataBase(Context context) {
		return context.openOrCreateDatabase(
				context.getString(R.string.database_name),
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
	}

	private <T> void fillEntity(T entity, Cursor cursor)
			throws NoSuchFieldException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException,
			InstantiationException {
		Method method = null;
		Column column = null;
		Table table = entity.getClass().getAnnotation(Table.class);

		for (Field field : entity.getClass().getDeclaredFields()) {
			column = field.getAnnotation(Column.class);
			method = entity.getClass().getDeclaredMethod(
					this.context.getString(R.string.prefix_method_set)
							+ field.getName().substring(0, 1).toUpperCase()
							+ field.getName().substring(1), field.getType());

			if (field.getType().isPrimitive()) {
				method.invoke(
						entity,
						cursor.getInt(cursor.getColumnIndex(table.name() + "_"
								+ column.name())));
			} else {
				if (field.getDeclaringClass().isInstance(EntityBase.class)) {
					// TODO: Fazer o carregando da entidade filha
					EntityBase entityChild = (EntityBase) field
							.getDeclaringClass().newInstance();

					fillEntity(entityChild, cursor);

					method.invoke(entity, entityChild);

				} else if (field.getDeclaringClass().isInstance(List.class)) {
					// TODO: Fazer o carregando da entidade filha
					// method.invoke(entity,
					// cursor.getString(cursor.getColumnIndex(column)));
				} else {
					method.invoke(
							entity,
							cursor.getString(cursor.getColumnIndex(table.name()
									+ "_" + column.name())));
				}
			}

		}
	}

	// private String[] getColumns(E entity) {
	// if (columnsName == null) {
	// columnsName = new String[entity.getClass().getDeclaredFields().length];
	// int index = 0;
	// Column column = null;
	//
	// for (Field field : entity.getClass().getDeclaredFields()) {
	//
	// column = field.getAnnotation(Column.class);
	//
	// columnsName[index++] = column.name();
	// }
	// }
	//
	// return columnsName;
	// }

	// @SuppressWarnings("unchecked")
	// private <T> Class<T> getType(Column column) {
	// Class<T> classReturn = null;
	//
	// if (column.dbType().equals(SQLiteDbType.TEXT)) {
	// classReturn = ((Class<T>) String.class);
	// } else if (column.dbType().equals(SQLiteDbType.INTEGER)) {
	// classReturn = ((Class<T>) int.class);
	// } else if (column.dbType().equals(SQLiteDbType.REAL)) {
	// classReturn = ((Class<T>) Float.class);
	// } else if (column.dbType().equals(SQLiteDbType.BLOB)) {
	// classReturn = ((Class<T>) byte[].class);
	// }
	//
	// return classReturn;
	// }

	protected String getNullColumn(E entity) throws IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException {
		if (entity.getClass().isAnnotationPresent(HasNullableColumn.class)) {
			if (nullableColumns == null) {
				Column column = null;
				boolean isNullableColumn = false;
				Method method = null;
				String methodName = null;
				Class<?>[] parameters = null;
				Object[] args = null;

				for (Field field : entity.getClass().getDeclaredFields()) {

					if (field.isAnnotationPresent(Nullable.class)) {
						column = field.getAnnotation(Column.class);

						if (column.dbType().equals(SQLiteDbType.TEXT)) {
							methodName = this.context
									.getString(R.string.prefix_method_get)
									+ field.getName().substring(0, 1)
											.toUpperCase()
									+ field.getName().substring(1);
							method = entity.getClass().getDeclaredMethod(
									methodName, parameters);

							if (method.invoke(entity, args) == null) {
								if (nullableColumns == null) {
									nullableColumns = this.context
											.getString(R.string.string_empty);
								}
								nullableColumns += ", " + column.name();
								isNullableColumn = true;
							}
						}
					}
				}

				if (isNullableColumn) {
					nullableColumns = nullableColumns.substring(1);
				}
			}
		}

		return nullableColumns;
	}

	protected abstract ContentValues getParameters(E entity);

	protected abstract String getDefaultOrberBy();

	protected String[] getValueKey(E entity) throws IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		if (pkValues == null) {
			Method method = null;
			boolean hasPrimaryKey = false;
			String valuesPk = this.context.getString(R.string.string_empty);
			Class<?>[] parameters = null;
			Object[] args = null;

			for (Field field : entity.getClass().getDeclaredFields()) {

				if (field.isAnnotationPresent(PrimaryKey.class)) {
					hasPrimaryKey = true;
					method = entity.getClass().getDeclaredMethod(
							this.context.getString(R.string.prefix_method_get)
									+ field.getName().substring(0, 1)
											.toUpperCase()
									+ field.getName().substring(1), parameters);
					valuesPk += this.context
							.getString(R.string.string_separator)
							+ String.valueOf(method.invoke(entity, args));
				}

				if (hasPrimaryKey) {
					pkValues = valuesPk.substring(1).split(
							this.context.getString(R.string.string_separator));
				}
			}
		}

		return pkValues;
	}

	protected String getConditionKey(E entity) {
		if (conditionPK == null) {
			Column column = null;
			boolean isPrimaryKey = false;
			Table table = entity.getClass().getAnnotation(Table.class);

			for (Field field : entity.getClass().getDeclaredFields()) {

				if (field.isAnnotationPresent(PrimaryKey.class)) {
					if (conditionPK == null) {
						conditionPK = this.context
								.getString(R.string.string_empty);
					}
					column = field.getAnnotation(Column.class);
					conditionPK += " AND " + table.name() + "." + column.name()
							+ " = ?";
					isPrimaryKey = true;
				}
			}

			if (isPrimaryKey) {
				conditionPK = " 1=1 " + conditionPK;
			}
		}

		return conditionPK;
	}

	protected void executeSQL(String query) {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);

			db.execSQL(query);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int insert(E entity) throws BusinessException,
			IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);
			
			int rowsAffected = (int) db.insert(getTableName(),
					getNullColumn(entity), getParameters(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static void beginTransaction(Context context) {
		SQLiteDatabase db = null;

		db = createOrOpenDataBase(context);
		db.beginTransaction();
	}

	public static void saveChanges(Context context) {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(context);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();

			if (db != null) {
				db.close();
			}
		}
	}

	public static void rollBack(Context context) {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(context);
		} finally {
			db.endTransaction();

			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public int delete(E entity) throws BusinessException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);
			int rowsAffected = (int) db.delete(getTableName(),
					getConditionKey(entity), getValueKey(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public int update(E entity) throws BusinessException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);
			int rowsAffected = (int) db.update(getTableName(),
					getParameters(entity), getConditionKey(entity),
					getValueKey(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public E getByKey(E entity) throws ClassNotFoundException,
	// IllegalAccessException, InstantiationException, BusinessException,
	// NoSuchMethodException, NoSuchFieldException,
	// InvocationTargetException {
	// SQLiteDatabase db = null;
	//
	// try {
	// db = createOrOpenDataBase(this.context);
	// E entityReturn = null;
	//
	// Cursor cursor = db.query(getTableName(), getColumns(entity),
	// getConditionKey(entity), getValueKey(entity), null, null,
	// null);
	//
	// if (cursor.getCount() == 0) {
	// throw new BusinessException(
	// this.context
	// .getString(R.string.msg_erro_registro_inexistente));
	// } else {
	// cursor.moveToFirst();
	//
	// Class<E> classReturn = (Class<E>) Class.forName(entity
	// .getClass()
	// .toString()
	// .replace("class ",
	// this.context.getString(R.string.string_empty)));
	// entityReturn = classReturn.newInstance();
	//
	// fillEntity(entityReturn, cursor);
	// }
	//
	// return entityReturn;
	// } finally {
	// if (db != null) {
	// db.close();
	// }
	// }
	// }

	private void buildSql(Class<?> entity, String[] sql, String tableNameFK)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Column column = null;
		boolean hasAdded = false;
		String tbName = null;

		if (sql[1] == null || sql[1].isEmpty()) {
			sql[1] = getTableName();
			tbName = getTableName();
		} else {
			Table table = (Table) entity.getAnnotation(Table.class);
			tbName = table.name();
			sql[1] += " INNER JOIN " + table.name() + " ON ";
		}

		for (Field field : entity.getDeclaredFields()) {
			column = field.getAnnotation(Column.class);

			if (field.isAnnotationPresent(ForeignKey.class)
					&& (field.getType().isInstance(List.class))) {
				buildSql(field.getType(), sql, tbName);
			} else {
				sql[0] += this.context.getString(R.string.string_separator)
						+ tbName + "." + column.name() + " AS " + tbName + "_"
						+ column.name();

				if ((tableNameFK != null) && (!tableNameFK.isEmpty())) {
					if (field.isAnnotationPresent(PrimaryKey.class)) {
						if (hasAdded) {
							sql[1] += " AND ";
						}
						sql[1] += tableNameFK + "." + column.name() + " = "
								+ tbName + "." + column.name();
						hasAdded = true;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(E entity) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, BusinessException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);
			E entityReturn = null;

			String[] sql = new String[] {
					this.context.getString(R.string.string_empty),
					this.context.getString(R.string.string_empty) };

			buildSql(entity.getClass(), sql, null);

			String query = "SELECT " + sql[0].substring(1) + " FROM " + sql[1]
					+ " WHERE " + getConditionKey(entity);
			Cursor cursor = db.rawQuery(query, getValueKey(entity));

			if (cursor.getCount() == 0) {
				throw new BusinessException(
						this.context
								.getString(R.string.msg_erro_registro_inexistente));
			} else {
				cursor.moveToFirst();

				entityReturn = (E) entity.getClass().newInstance();

				fillEntity(entityReturn, cursor);
			}

			return entityReturn;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public List<E> getAll() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, BusinessException,
			NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase(this.context);
			List<E> lista = new ArrayList<E>();
			E entity = null;

			String[] sql = new String[] {
					this.context.getString(R.string.string_empty),
					this.context.getString(R.string.string_empty) };

			buildSql(classEntity, sql, null);

			String query = "SELECT " + sql[0].substring(1) + " FROM " + sql[1]
					+ getDefaultOrberBy();
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.getCount() == 0) {
				throw new BusinessException(
						this.context
								.getString(R.string.msg_erro_registro_inexistente));
			} else {

				while (cursor.moveToNext()) {
					entity = (E) classEntity.newInstance();

					fillEntity(entity, cursor);

					lista.add(entity);
				}
			}

			return lista;

		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void createTable() {
		String createTable = "CREATE TABLE IF NOT EXISTS " + getTableName()
				+ " ( ";
		Column column = null;
		PrimaryKey primaryKey = null;
		String columns = "";

		for (Field field : classEntity.getDeclaredFields()) {
			column = field.getAnnotation(Column.class);
			columns += R.string.string_separator + column.name() + " "
					+ column.dbType();

			if (field.isAnnotationPresent(PrimaryKey.class)) {
				primaryKey = field.getAnnotation(PrimaryKey.class);
				columns += " PRIMARY KEY";
				if (primaryKey.autoIncrement()) {
					columns += " AUTOINCREMENT";
				}
			}
		}

		createTable += columns.substring(1) + ")";

		executeSQL(createTable);
	}

	public void deleteTable() {
		String dropTable = "DROP TABLE " + getTableName();

		executeSQL(dropTable);
	}

	public String getTableName() {
		return tableName;
	}
}