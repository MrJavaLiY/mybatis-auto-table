package com.auto.table.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("information_schema.columns")
public class TableColumn {
    @TableField("column_name")
    private String columnName;
    @TableField("data_type")
    private String columnType;
}
