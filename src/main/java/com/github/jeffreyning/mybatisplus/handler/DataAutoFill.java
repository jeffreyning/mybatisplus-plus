package com.github.jeffreyning.mybatisplus.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.util.PlusACUtils;
import com.github.jeffreyning.mybatisplus.util.ReadValueUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.jeffreyning.mybatisplus.anno.InsertFill;
import com.github.jeffreyning.mybatisplus.anno.UpdateFill;

/**
 * @author ninghao
 */
@Component
public class DataAutoFill implements MetaObjectHandler {
    private static final Logger logger = LoggerFactory.getLogger(DataAutoFill.class);
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
                        SqlSessionTemplate sqlSessionTemplate=PlusACUtils.getBean(SqlSessionTemplate.class);
                        SqlSession sqlSession = null;
                        try {
                            sqlSession = SqlSessionUtils.getSqlSession(
                                    sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(),
                                    sqlSessionTemplate.getPersistenceExceptionTranslator());
                            ResultSet resultSet = sqlSession.getConnection().createStatement().executeQuery(sql);
                            if (resultSet != null) {
                                if (resultSet.next()) {
                                    Class fieldType=field.getType();
                                    Object fieldVal = ReadValueUtil.readValue(resultSet,fieldType);
                                    this.fillStrategy(metaObject,fieldName,fieldVal);
                                }
                            }
                        } catch (SQLException e) {
                            logger.error("insert fill error", e);
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
                        SqlSessionTemplate sqlSessionTemplate=PlusACUtils.getBean(SqlSessionTemplate.class);
                        SqlSession sqlSession = null;
                        try {
                            sqlSession = SqlSessionUtils.getSqlSession(
                                    sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(),
                                    sqlSessionTemplate.getPersistenceExceptionTranslator());
                            ResultSet resultSet = sqlSession.getConnection().createStatement().executeQuery(sql);
                            if (resultSet != null) {
                                if (resultSet.next()) {
                                    Class fieldType=field.getType();
                                    Object fieldVal = ReadValueUtil.readValue(resultSet,fieldType);
                                    this.fillStrategy(metaObject,fieldName,fieldVal);
                                }
                            }
                        } catch (SQLException e) {
                            logger.error("update fill error", e);
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
