package com.auto.table.tes;

import com.auto.table.AutoTable;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

@Data
@TableName("test_entity")
@AutoTable
public class TestEntity {
    @TableId(value = "id")
    private Long id;
    @TableField("name")
    private String name;
    @TableField("enable")
    private Boolean enable;
    @TableField("create_time")
    private Date createTime;

    @TableField("test1")
    private String test1;
//    @TableField("test21")
//    private String test21;

}
