package com.luksfon.villasalute.campanha.query;

import java.lang.reflect.InvocationTargetException;

public final class Restriction {
	private final String column;
	private final String table;
	private final LogicComparator comparator;
	private final String value;
	private String logicalOperator;

	public Restriction(String column, String table, LogicComparator comparator,
			String value) {
		this.column = column;
		this.table = table;
		this.comparator = comparator;
		this.value = value;
	}

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getLogicalComparator() {
		String logicalComparator = "";

		if (LogicComparator.Equals.equals(comparator)) {
			logicalComparator = " = ? ";
		} else if (LogicComparator.GreaterEqualsThan.equals(comparator)) {
			logicalComparator = " >= ? ";
		} else if (LogicComparator.GreaterThan.equals(comparator)) {
			logicalComparator = " > ? ";
		} else if (LogicComparator.IsNotNull.equals(comparator)) {
			logicalComparator = " IS NOT NULL ";
		} else if (LogicComparator.IsNull.equals(comparator)) {
			logicalComparator = " IS NULL ";
		} else if (LogicComparator.Equals.equals(comparator)) {
			logicalComparator = " = ?";
		} else if (LogicComparator.LessEqualsThan.equals(comparator)) {
			logicalComparator = " <= ?";
		} else if (LogicComparator.LessThan.equals(comparator)) {
			logicalComparator = " < ? ";
		} else if (LogicComparator.Like.equals(comparator)) {
			logicalComparator = " like ? ";
		}

		return logicalComparator;
	}

	public String[] toSqlString() throws NoSuchFieldException,
			NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String[] sql = new String[2];

		sql[0] = logicalOperator + " " + table + "." + column
				+ getLogicalComparator();

		if (LogicComparator.IsNotNull.equals(comparator)
				|| LogicComparator.IsNull.equals(comparator)) {
			sql[1] = "";
		} else if (LogicComparator.Like.equals(comparator)) {
			sql[1] = "%" + value + "%";
		} else {
			sql[1] = value;
		}

		return sql;
	}
}