package com.github.jeffreyning.mybatisplus.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.jeffreyning.mybatisplus.anno.InsertFill;
import com.github.jeffreyning.mybatisplus.anno.UpdateFill;


@Component
public class DataAutoFill implements MetaObjectHandler {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public void insertFill(MetaObject metaObject) {
        Object classObj = metaObject.getOriginalObject();
        Field[] declaredFields = classObj.getClass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {

                InsertFill insertFill = field.getAnnotation(InsertFill.class);
                if (insertFill != null) {
                    String fieldName = field.getName();
                    String sql = insertFill.value();
                    if (sql != null && !"".equals(sql)) {
                        SqlSession sqlSession = null;
                        try {
                            sqlSession = SqlSessionUtils.getSqlSession(
                                    sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(),
                                    sqlSessionTemplate.getPersistenceExceptionTranslator());
                            ResultSet resultSet = sqlSession.getConnection().createStatement().executeQuery(sql);
                            if (resultSet != null) {
                                if (resultSet.next()) {
                                    Object fieldVal = resultSet.getObject(1);
                                    this.setFieldValByName(fieldName, fieldVal, metaObject);
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException("insert fill error", e);
                        } finally {
                            if (sqlSession != null) {
                                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionTemplate.getSqlSessionFactory());
                            }
                        }
                    }

                }

            }
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object classObj = metaObject.getOriginalObject();
        Field[] declaredFields = classObj.getClass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                UpdateFill updateFill = field.getAnnotation(UpdateFill.class);
                if (updateFill != null) {
                    String fieldName = field.getName();
                    String sql = updateFill.value();
                    if (sql != null && !"".equals(sql)) {
                        SqlSession sqlSession = null;
                        try {
                            sqlSession = SqlSessionUtils.getSqlSession(
                                    sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(),
                                    sqlSessionTemplate.getPersistenceExceptionTranslator());
                            ResultSet resultSet = sqlSession.getConnection().createStatement().executeQuery(sql);
                            if (resultSet != null) {
                                if (resultSet.next()) {
                                    Object fieldVal = resultSet.getObject(1);
                                    this.setFieldValByName(fieldName, fieldVal, metaObject);
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException("update fill error", e);
                        } finally {
                            if (sqlSession != null) {
                                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionTemplate.getSqlSessionFactory());
                            }
                        }
                    }
                }
            }
        }
    }
}
