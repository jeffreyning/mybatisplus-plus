package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;

/**
 * @author ninghao
 */
public interface MppBaseMapper<T> extends BaseMapper<T> {

    int deleteByMultiId(T entityId);
    int updateByMultiId(T entity, T entityId);
    T selectByMultiId(T entityId);
}
