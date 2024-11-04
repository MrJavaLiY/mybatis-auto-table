package com.auto.table.dao;

import com.auto.table.entity.TableColumn;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface TableDao extends BaseMapper<TableColumn> {
}
