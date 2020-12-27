package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * @author ninghao
 */
public interface MppBaseMapper<T> extends BaseMapper<T> {

    int deleteByMultiId(T entityId);
    int updateByMultiId(@Param("et") T entity);
    T selectByMultiId(T entityId);
}
