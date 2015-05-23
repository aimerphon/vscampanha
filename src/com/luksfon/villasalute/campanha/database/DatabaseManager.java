package com.luksfon.villasalute.campanha.database;

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
import com.luksfon.villasalute.campanha.annotation.TableAssociated;
import com.luksfon.villasalute.campanha.dal.SQLiteDbType;
import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.exception.BusinessException;
import com.luksfon.villasalute.campanha.query.LogicComparator;
import com.luksfon.villasalute.campanha.query.Restriction;

@SuppressLint("DefaultLocale")
public class DatabaseManager {

	protected Context context;
	private String[] pkValues;
	private String nullableColumns;
	private String conditionPK;
	private boolean buildAllEntities;
	private ArrayList<Restriction> restrictions;
	private static SQLiteDatabase database;

	public DatabaseManager(boolean buildAllEntities, Context context) {
		this.context = context;
		this.buildAllEntities = buildAllEntities;
		this.restrictions = new ArrayList<Restriction>();

		if (database == null) {
			database = getInstance(context);
		}
	}

	public synchronized static SQLiteDatabase getInstance(Context context) {
		if (database == null || !database.isOpen()) {
			database = context.openOrCreateDatabase(
					context.getString(R.string.database_name),
					SQLiteDatabase.CREATE_IF_NECESSARY, null);
		}
		return database;
	}

	public SQLiteDatabase createOrOpenDataBase() {
		if (database == null || !database.isOpen()) {
			database = getInstance(context);
		}

		return database;
	}

	public Context getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	public <T extends EntityBase> int insert(T entity)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException, BusinessException {
		try {
			if (database != null && !database.inTransaction())
				database = createOrOpenDataBase();
			Class<T> classEntity = (Class<T>) entity.getClass();
			int rowsAffected = (int) database.insert(getTableName(classEntity),
					getNullColumn(entity), getParameters(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	protected <T extends EntityBase> ContentValues getParameters(T entity)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ContentValues values = new ContentValues();

		PrimaryKey primaryKey = null;
		Column column = null;
		ForeignKey foreignKey = null;

		Class<?>[] parameters = null;
		Object[] args = null;
		Method method = null;

		for (Field field : entity.getClass().getDeclaredFields()) {

			if (field.isAnnotationPresent(Column.class)) {
				column = field.getAnnotation(Column.class);
				if (field.isAnnotationPresent(PrimaryKey.class)) {
					primaryKey = field.getAnnotation(PrimaryKey.class);

					if (!primaryKey.autoIncrement()) {
						values.put(column.name(), getFieldValue(entity, field));
					}
				} else {
					values.put(column.name(), getFieldValue(entity, field));
				}
			} else if ((field.isAnnotationPresent(ForeignKey.class))
					&& (!field.isAnnotationPresent(TableAssociated.class))) {
				foreignKey = field.getAnnotation(ForeignKey.class);

				method = entity.getClass().getDeclaredMethod(
						this.context.getString(R.string.prefix_method_get)
								+ field.getName().substring(0, 1).toUpperCase()
								+ field.getName().substring(1), parameters);
				EntityBase child = (EntityBase) method.invoke(entity, args);

				for (Field fieldChild : child.getClass().getDeclaredFields()) {
					if (fieldChild.isAnnotationPresent(PrimaryKey.class)) {
						values.put(foreignKey.column(),
								getFieldValue(child, fieldChild));
					}
				}
			}
		}

		return values;
	}

	private <T extends EntityBase> String getFieldValue(T entity, Field field)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class<?>[] parameters = null;
		Object[] args = null;
		Method method = entity.getClass().getDeclaredMethod(
				this.context.getString(R.string.prefix_method_get)
						+ field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1), parameters);
		return String.valueOf(method.invoke(entity, args));
	}

	protected <T extends EntityBase> String getNullColumn(T entity)
			throws IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException {
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

	protected <T> String[] getValueKey(T entity) throws IllegalAccessException,
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

	public void beginTransaction() {
		SQLiteDatabase db = null;

		db = createOrOpenDataBase();
		db.beginTransaction();
	}

	public void saveChanges() {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();

			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	public void rollBack() {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase();
		} finally {
			db.endTransaction();

			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	protected <T extends EntityBase> String getConditionKey(T entity) {
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

	protected <T extends EntityBase> String[] getColumnsPK(T entity) {
		Column column = null;
		String columnsPK = "";

		for (Field field : entity.getClass().getDeclaredFields()) {

			if (field.isAnnotationPresent(PrimaryKey.class)) {
				column = field.getAnnotation(Column.class);
				columnsPK += context.getString(R.string.string_separator)
						+ column.name();
			}
		}

		return columnsPK.substring(1).split(
				context.getString(R.string.string_separator));
	}

	@SuppressWarnings("unchecked")
	public <T extends EntityBase> int delete(T entity)
			throws BusinessException, IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		try {
			if (database != null && !database.inTransaction())
				database = createOrOpenDataBase();
			Class<T> classEntity = (Class<T>) entity.getClass();
			String tableName = getTableName(classEntity);
			int rowsAffected = (int) database.delete(tableName,
					getConditionKey(entity), getValueKey(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	public <T extends EntityBase> int deleteByRestriction(Class<T> classEntity)
			throws BusinessException, IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		try {
			if (database != null && !database.inTransaction())
				database = createOrOpenDataBase();
			String tableName = getTableName(classEntity);

			String[] sql = buildConditions();

			String where = sql[0].replace(
					context.getString(R.string.clausula_where), "");
			String[] values = sql[1].substring(1).split(
					context.getString(R.string.string_separator));

			int rowsAffected = (int) database.delete(tableName, where, values);
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends EntityBase> int update(T entity)
			throws BusinessException, IllegalAccessException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		try {
			if (database != null && !database.inTransaction())
				database = createOrOpenDataBase();
			Class<T> classEntity = (Class<T>) entity.getClass();
			String tableName = getTableName(classEntity);
			int rowsAffected = (int) database.update(tableName,
					getParameters(entity), getConditionKey(entity),
					getValueKey(entity));
			verifyReturned(rowsAffected);

			return rowsAffected;
		} finally {
			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends EntityBase> T get(T entity)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, BusinessException, NoSuchMethodException,
			NoSuchFieldException, InvocationTargetException {
		String[] pks = getColumnsPK(entity);
		boolean firstPk = true;
		Class<T> classEntity = (Class<T>) entity.getClass();
		String tableName = getTableName(classEntity);
		String[] values = getValueKey(entity);
		int index = 0;

		for (String pk : pks) {
			if (firstPk) {
				where(new Restriction(pk, tableName, LogicComparator.Equals,
						values[index++]));
			} else {
				and(new Restriction(pk, tableName, LogicComparator.Equals,
						values[index++]));
			}

		}

		T entityReturn = firstOrDefault(classEntity);

		if (entityReturn == null) {
			throw new BusinessException(
					this.context
							.getString(R.string.msg_erro_registro_inexistente));
		}

		return entityReturn;
	}

	public <T extends EntityBase> List<T> getAll(Class<T> classEntity)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, BusinessException, NoSuchMethodException,
			NoSuchFieldException, InvocationTargetException {
		return toList(classEntity);
	}

	private void verifyReturned(int rowsAffected) throws BusinessException {
		if (rowsAffected == -1) {
			throw new BusinessException(
					context.getString(R.string.msg_erro_insertUpdate));
		}
	}

	public <T extends EntityBase> String getTableName(Class<T> classEntity) {
		Table table = classEntity.getAnnotation(Table.class);
		return table.name();
	}

	public DatabaseManager and(Restriction restriction) {
		restriction.setLogicalOperator("AND");
		restrictions.add(restriction);

		return this;
	}

	public DatabaseManager or(Restriction restriction) {
		restriction.setLogicalOperator("OR");
		restrictions.add(restriction);

		return this;
	}

	public DatabaseManager where(Restriction restriction) {
		restriction.setLogicalOperator("WHERE");
		restrictions.add(restriction);

		return this;
	}

	public <T extends EntityBase> T firstOrDefault(Class<T> classEntity)
			throws InstantiationException, IllegalAccessException,
			NoSuchFieldException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException,
			BusinessException {
		ArrayList<T> entities = toList(classEntity);

		T entity = null;

		if (entities != null && entities.size() > 0) {
			entity = entities.get(0);
		}

		return entity;
	}

	public <T> ArrayList<T> toList(Class<T> classEntity)
			throws BusinessException, InstantiationException,
			IllegalAccessException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException,
			ClassNotFoundException {

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {

			db = createOrOpenDataBase();
			ArrayList<T> lista = new ArrayList<T>();
			T entityReturn = null;

			String[] sql = new String[] {
					this.context.getString(R.string.string_empty),
					this.context.getString(R.string.string_empty) };

			buildSql(classEntity, sql, null);

			String[] sqlConditions = buildConditions();

			String query = "SELECT " + sql[0].substring(1) + " FROM " + sql[1];

			String[] values = null;
			String sqlCondition = this.context.getString(R.string.string_empty);

			if ((sqlConditions[0] != null) && (!sqlConditions[0].isEmpty())) {
				sqlCondition = " " + sqlConditions[0];
				values = sqlConditions[1].substring(1).split(
						this.context.getString(R.string.string_separator));
			}

			query += sqlCondition;

			cursor = db.rawQuery(query, values);

			while (cursor.moveToNext()) {
				entityReturn = (T) classEntity.newInstance();

				fillEntity(entityReturn, cursor);

				if (buildAllEntities) {
					fillListEntity(entityReturn, cursor);
				}

				lista.add(entityReturn);
			}

			return lista;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	private String[] buildConditions() throws NoSuchFieldException,
			NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String[] sqlConditions = new String[2];
		String[] sqlRestriction = null;

		sqlConditions[0] = context.getString(R.string.string_empty);
		sqlConditions[1] = context.getString(R.string.string_empty);

		for (Restriction restriction : restrictions) {
			sqlRestriction = restriction.toSqlString();
			sqlConditions[0] += sqlRestriction[0];
			sqlConditions[1] += context.getString(R.string.string_separator)
					+ sqlRestriction[1];
		}

		return sqlConditions;
	}

	private <T> void fillEntity(T entity, Cursor cursor)
			throws NoSuchFieldException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException,
			InstantiationException, IllegalArgumentException,
			ClassNotFoundException, BusinessException {
		Method method = null;
		Column column = null;
		Table table = entity.getClass().getAnnotation(Table.class);

		for (Field field : entity.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				column = field.getAnnotation(Column.class);
				method = entity
						.getClass()
						.getDeclaredMethod(
								this.context.getString(R.string.prefix_method_set)
										+ field.getName().substring(0, 1)
												.toUpperCase()
										+ field.getName().substring(1),
								field.getType());

				if (field.getType().isPrimitive()) {
					method.invoke(
							entity,
							cursor.getInt(cursor.getColumnIndex(table.name()
									+ "_" + column.name())));
				} else {
					if (column.dbType().equals("TEXT")) {
						method.invoke(
								entity,
								cursor.getString(cursor.getColumnIndex(table
										.name() + "_" + column.name())));
					} else if (column.dbType().equals("BLOB")) {
						method.invoke(
								entity,
								cursor.getBlob(cursor.getColumnIndex(table
										.name() + "_" + column.name())));
					}
				}
			} else if (field.isAnnotationPresent(ForeignKey.class)
					&& !field.isAnnotationPresent(TableAssociated.class)) {
				Object entityChild = field.getType().newInstance();

				fillEntity(entityChild, cursor);

				method = entity
						.getClass()
						.getDeclaredMethod(
								this.context.getString(R.string.prefix_method_set)
										+ field.getName().substring(0, 1)
												.toUpperCase()
										+ field.getName().substring(1),
								field.getType());

				method.invoke(entity, entityChild);
			}
		}
	}

	private <T> void fillListEntity(T entity, Cursor cursor)
			throws NoSuchFieldException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException,
			InstantiationException, IllegalArgumentException,
			ClassNotFoundException, BusinessException {
		Method method = null;
		Table table = entity.getClass().getAnnotation(Table.class);
		ForeignKey fk = null;
		TableAssociated tableAssociated = null;

		for (Field field : entity.getClass().getDeclaredFields()) {
			if (buildAllEntities && field.isAnnotationPresent(ForeignKey.class)
					&& field.isAnnotationPresent(TableAssociated.class)) {
				fk = field.getAnnotation(ForeignKey.class);
				DatabaseManager databaseManager = new DatabaseManager(true,
						context);

				String[] pks = getValueKey(entity);
				boolean isAdicionar = false;

				for (String pk : pks) {
					if (!isAdicionar) {
						databaseManager.where(new Restriction(fk.column(),
								table.name(), LogicComparator.Equals, pk));
						isAdicionar = true;
					} else {
						databaseManager.and(new Restriction(fk.column(), table
								.name(), LogicComparator.Equals, pk));
					}
				}
				method = entity
						.getClass()
						.getDeclaredMethod(
								this.context.getString(R.string.prefix_method_set)
										+ field.getName().substring(0, 1)
												.toUpperCase()
										+ field.getName().substring(1),
								field.getType());

				tableAssociated = field.getAnnotation(TableAssociated.class);

				method.invoke(entity, databaseManager.toList(tableAssociated
						.classTableAssociated()));
			}
		}
	}

	private void buildSql(Class<?> entity, String[] sql, String tableNameFK)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Column column = null;
		boolean hasAdded = false;
		String tbName = null;
		String aliasTable = null;

		ArrayList<Field> fields = new ArrayList<Field>();

		Table table = (Table) entity.getAnnotation(Table.class);
		tbName = table.name();
		aliasTable = table.name();

		if (sql[1] == null || sql[1].isEmpty()) {
			sql[1] = table.name() + " AS " + table.name();
		} else if (buildAllEntities) {
			if (sql[1].contains(tbName + " AS ")) {
				int indice = sql[1].split(tbName + " AS ").length;
				aliasTable += String.valueOf(indice);
			}
			sql[1] += " LEFT JOIN " + table.name() + " AS " + aliasTable
					+ " ON ";
		}

		for (Field field : entity.getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				column = field.getAnnotation(Column.class);

				sql[0] += context.getString(R.string.string_separator)
						+ aliasTable + "." + column.name() + " AS "
						+ aliasTable + "_" + column.name();

				if ((tableNameFK != null) && (!tableNameFK.isEmpty())) {
					if (field.isAnnotationPresent(PrimaryKey.class)) {
						if (hasAdded) {
							sql[1] += " AND ";
						}
						sql[1] += tableNameFK + "." + column.name() + " = "
								+ aliasTable + "." + column.name();
						hasAdded = true;
					}
				}
			} else {
				if (buildAllEntities
						&& field.isAnnotationPresent(ForeignKey.class)
						&& !field.isAnnotationPresent(TableAssociated.class)) {
					// buildSql(field.getType(), sql, tbName);
					fields.add(field);
				}
			}
		}

		for (Field fks : fields) {
			buildSql(fks.getType(), sql, tbName);
		}
	}

	public <T extends EntityBase> void createTable(Class<T> classEntity) {
		String createTable = "CREATE TABLE IF NOT EXISTS "
				+ getTableName(classEntity) + " ( ";
		Column column = null;
		Column columnChild = null;
		PrimaryKey primaryKey = null;
		String columns = "";

		for (Field field : classEntity.getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				column = field.getAnnotation(Column.class);
				columns += context.getString(R.string.string_separator)
						+ column.name() + " " + column.dbType();

				if (field.isAnnotationPresent(PrimaryKey.class)) {
					primaryKey = field.getAnnotation(PrimaryKey.class);
					columns += " PRIMARY KEY";
					if (primaryKey.autoIncrement()) {
						columns += " AUTOINCREMENT";
					}
				}
			} else if ((field.isAnnotationPresent(ForeignKey.class))
					&& (!field.isAnnotationPresent(TableAssociated.class))) {
				for (Field fieldChild : field.getType().getDeclaredFields()) {
					if (fieldChild.isAnnotationPresent(PrimaryKey.class)) {
						columnChild = fieldChild.getAnnotation(Column.class);
						columns += context.getString(R.string.string_separator)
								+ columnChild.name() + " "
								+ columnChild.dbType();
					}
				}
			}
		}

		createTable += columns.substring(1) + ")";

		executeSQL(createTable);
	}

	public <T extends EntityBase> void dropTable(Class<T> classEntity) {
		String dropTable = "DROP TABLE " + getTableName(classEntity);

		executeSQL(dropTable);
	}

	public void executeSQL(String query) {
		SQLiteDatabase db = null;

		try {
			db = createOrOpenDataBase();

			db.execSQL(query);
		} finally {
			if (database != null && !database.inTransaction()) {
				database.close();
			}
		}
	}
}
