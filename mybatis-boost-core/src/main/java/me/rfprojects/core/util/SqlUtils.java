package me.rfprojects.core.util;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public abstract class SqlUtils {

    public static StringBuilder appendWhere(StringBuilder sqlBuilder, List<String> columns) {
        sqlBuilder.append(" WHERE ");
        columns.forEach(c -> sqlBuilder.append(c).append(" = ?, "));
        sqlBuilder.setLength(sqlBuilder.length() - 2);
        return sqlBuilder;
    }

    public static String appendLimit(String sql, RowBounds rowBounds) {
        if (rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET || rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT) {
            sql += " LIMIT " + rowBounds.getOffset() + ", " + rowBounds.getLimit();
        }
        return sql;
    }
}
