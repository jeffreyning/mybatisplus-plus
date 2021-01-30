package com.github.jeffreyning.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

    boolean saveOrUpdateByMultiId(T entity);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean saveOrUpdateBatchByMultiId(Collection<T> entityList) {
        return this.saveOrUpdateBatchByMultiId(entityList, 1000);
    }

    boolean saveOrUpdateBatchByMultiId(Collection<T> entityList, int batchSize);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean updateBatchByMultiId(Collection<T> entityList) {
        return this.updateBatchByMultiId(entityList, 1000);
    }

    boolean updateBatchByMultiId(Collection<T> entityList, int batchSize);
}
