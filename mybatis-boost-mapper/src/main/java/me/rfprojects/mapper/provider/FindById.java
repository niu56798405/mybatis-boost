package me.rfprojects.mapper.provider;

import me.rfprojects.core.Configuration;
import me.rfprojects.core.ConfigurationAware;
import me.rfprojects.core.SqlProvider;
import me.rfprojects.core.util.EntityUtils;
import me.rfprojects.core.util.MyBatisUtils;
import me.rfprojects.core.util.SqlUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindById implements SqlProvider, ConfigurationAware {

    private Configuration configuration;

    @Override
    public void replace(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql) {
        Class<?> type = mappedStatement.getResultMaps().get(0).getType();

        String tableName = EntityUtils.getTableName(type, configuration.getNameAdaptor());
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ").append(tableName);

        List<String> properties = EntityUtils.getProperties(type);
        boolean mapUnderscoreToCamelCase = (boolean)
                metaObject.getValue("delegate.configuration.mapUnderscoreToCamelCase");
        List<String> columns = EntityUtils.getColumns(type, properties, mapUnderscoreToCamelCase);

        List<Integer> idIndexes = EntityUtils.getIdIndexes(type, properties);
        List<String> ids = new ArrayList<>();
        idIndexes.forEach(i -> ids.add(columns.get(i)));

        SqlUtils.appendWhere(sqlBuilder, ids);
        List<ParameterMapping> parameterMappings = MyBatisUtils.getParameterMapping
                ((org.apache.ibatis.session.Configuration)
                        metaObject.getValue("delegate.configuration"), ids);
        metaObject.setValue("delegate.boundSql.parameterMappings", parameterMappings);

        Map parameterMap = (Map) boundSql.getParameterObject();
        Object[] parameterArray = (Object[]) parameterMap.get("array");
        parameterMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            //noinspection unchecked
            parameterMap.put(ids.get(0), parameterArray[i]);
        }
        metaObject.setValue("delegate.boundSql.parameterObject", parameterMap);
        metaObject.setValue("delegate.parameterHandler.parameterObject", parameterMap);

        metaObject.setValue("delegate.boundSql.sql", sqlBuilder.toString());
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
