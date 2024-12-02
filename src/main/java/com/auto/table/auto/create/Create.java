package com.auto.table.auto.create;

import com.auto.table.AutoTable;
import com.auto.table.dao.TableDao;
import com.auto.table.entity.TableColumn;
import com.auto.table.enums.SqlserverEnum;
import com.auto.table.exception.AnnotationException;
import com.auto.table.exception.NotTypeException;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ly
 * @date 2019/11/26 0026 14:06
 */
@Component
@Slf4j
/*
 CREATE TABLE Employees (
 EmployeeID INT PRIMARY KEY IDENTITY(1,1),
 FirstName NVARCHAR(50) NOT NULL,
 LastName NVARCHAR(50) NOT NULL,
 BirthDate DATE NOT NULL,
 Position NVARCHAR(100) NOT NULL
 );
 */
public class Create {
    private static final String CREATE_TABLE_SQL = "CREATE TABLE ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String NULL = " NULL";
    private static final String IDENTITY = " IDENTITY(1,1)";
    @Resource
    JdbcTemplate jdbcTemplate;
    @Autowired
    TableDao tableDao;

    public void execute(Class<?> clazz) {
        Wrapper<TableColumn> wrapper = new EntityWrapper<>();
        String tableName = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(TableName.class)) {
            tableName = clazz.getAnnotation(TableName.class).value();
        }
        wrapper.eq("table_name", tableName);
        List<TableColumn> tableColumns = tableDao.selectList(wrapper);
        String sql;
        if (tableColumns != null && !tableColumns.isEmpty()) {
            sql = alter(clazz, tableColumns);
        } else {
            sql = createTable(clazz);
        }
        if (sql.isEmpty()) {
            return;
        }



















        log.debug("需要执行的语句是：{}", sql);
        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }

    public String alter(Class<?> clazz, List<TableColumn> tableColumns) {
        StringBuilder alterSql = new StringBuilder();
        String tableName = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(TableName.class)) {
            tableName = clazz.getAnnotation(TableName.class).value();
        }
        Field[] files = clazz.getDeclaredFields();
        List<Field> fields = fieldNotTableColumn(Arrays.asList(files), tableColumns);


        for (Field field : fields) {
            String fieldName;
            if (field.isAnnotationPresent(TableField.class)) {
                fieldName = field.getAnnotation(TableField.class).value();
            } else {
                fieldName = field.getName();
            }
            if (tableColumns.stream().anyMatch(c -> fieldName.equals(c.getColumnName()))) {
                //证明已经有数据了，所以不要了 11
                continue;
            }
            String fieldType = getEnum(field.getType().getSimpleName());

            alterSql.append("alter table ")
                    .append(tableName)
                    .append(" add ")
                    .append(fieldName)
                    .append(" ")
                    .append(fieldType)
                    .append(NOT_NULL)
                    .append(";\n");
            log.debug("添加的字段有：{}", fieldName);
        }


        List<TableColumn> tableColumns1 = tableColumnNotField(Arrays.asList(files), tableColumns);
        if (!tableColumns1.isEmpty()) {
            log.debug("需要删除的字段有：{}", tableColumns1.stream().map(TableColumn::getColumnName).collect(Collectors.joining(",")));
            for (TableColumn tableColumn : tableColumns1) {
                alterSql.append("alter table ")
                        .append(tableName)
                        .append(" drop column ")
                        .append(tableColumn.getColumnName())
                        .append(";\n");
            }
        }
        return alterSql.toString();
    }

    public String createTable(Class<?> clazz) {
        AutoTable autoTable = clazz.getAnnotation(AutoTable.class);
        if (autoTable == null) {
            throw new AnnotationException("输入类" + clazz.getName() + "没有@AutoTable注解");
        }
        StringBuilder createSql = new StringBuilder();
        String tableName = clazz.getSimpleName();
        TableName tableNameA = clazz.getAnnotation(TableName.class);
        if (tableNameA != null) {
            tableName = tableNameA.value();
        }
        createSql.append(CREATE_TABLE_SQL).append(tableName).append("(\n");
        Field[] files = clazz.getDeclaredFields();
        for (Field field : files) {
            String fieldName = field.getName();
            if (field.isAnnotationPresent(TableField.class)) {
                fieldName = field.getAnnotation(TableField.class).value();
            }
            String fieldType = getEnum(field.getType().getSimpleName());
            if (field.isAnnotationPresent(TableId.class)) {
                //主键
                createSql.append(field.getAnnotation(TableId.class).value()).append(" ").append(fieldType).append(PRIMARY_KEY);
                IdType idType = field.getAnnotation(TableId.class).type();
                if (idType == IdType.AUTO) {
                    createSql.append(IDENTITY);
                }
                createSql.append(",\n");
            } else {
                createSql.append(fieldName).append(" ").append(fieldType).append(NOT_NULL).append(",\n");
            }
        }
        createSql.deleteCharAt(createSql.length() - 2);
        createSql.append(")");
        return createSql.toString();
    }

    private String getEnum(String type) {
        SqlserverEnum sqlserverEnum = null;
        switch (type) {
            case "int":
            case "Integer":
                sqlserverEnum = SqlserverEnum.INT;
                break;
            case "long":
            case "Long":
                sqlserverEnum = SqlserverEnum.BIGINT;
                break;
            case "String":
                sqlserverEnum = SqlserverEnum.VARCHAR;
                break;
            case "Date":
                sqlserverEnum = SqlserverEnum.DATETIME;
                break;
            case "boolean":
            case "Boolean":
                sqlserverEnum = SqlserverEnum.BIT;
                break;
            default:
                break;
        }
        if (sqlserverEnum == SqlserverEnum.VARCHAR) {
            return sqlserverEnum.getType() + "(" + sqlserverEnum.getLength() + ")";
        } else {
            if (sqlserverEnum != null) {
                return sqlserverEnum.getType();
            } else {
                throw new NotTypeException("没有见过的类型" + type);
            }
        }
    }

    private List<Field> fieldNotTableColumn(List<Field> fields, List<TableColumn> tableColumns) {
        List<Field> fieldFilters = fields.stream().filter(field -> tableColumns.stream().noneMatch(c -> (field.isAnnotationPresent(TableField.class) ? field.getAnnotation(TableField.class).value() : field.getName()).equals(c.getColumnName()))).collect(Collectors.toList());
//        for (Field field : fieldFilters) {
//            if (field.isAnnotationPresent(TableField.class)) {
//                String columnName = field.getAnnotation(TableField.class).value();
//                if (tableColumns.stream().anyMatch(c -> columnName.equals(c.getColumnName()))) {
//                    fieldFilters.remove(field);
//                }
//            }
//        }
        return fieldFilters;
    }

    private List<TableColumn> tableColumnNotField(List<Field> fields, List<TableColumn> tableColumns) {
        return tableColumns.stream().filter(c -> fields.stream().noneMatch(f -> (f.isAnnotationPresent(TableField.class) ? f.getAnnotation(TableField.class).value() : f.getName()).equals(c.getColumnName()))).collect(Collectors.toList());
    }

}
