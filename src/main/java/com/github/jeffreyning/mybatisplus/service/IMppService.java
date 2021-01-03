package com.github.jeffreyning.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;

/**
 * @author ninghao
 */
public interface IMppService<T> extends IService<T> {
    MppBaseMapper<T> getBaseMapper();
    default boolean updateByMultiId(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().updateByMultiId(entity));
    }

    default boolean deleteByMultiId(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().deleteByMultiId(entity));
    }

    default T selectByMultiId(T entity) {
        return this.getBaseMapper().selectByMultiId(entity);
    }

}
