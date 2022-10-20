package org.god.ibatis.core;

import java.sql.Connection;

/**
 * 事务管理器接口
 * 所有的事务管理器都应该遵循该规范
 * JDBC事务管理器
 * MANAGED事务管理器
 * 都应该实现这个接口
 */
public interface Transaction {
    //提供控制事物的方法

    /**
     * 提交事务
     */
    void commit();

    /**
     * 关闭事务
     */
    void close();

    /**
     * 回滚事务
     */
    void rollback();

    /**
     * 开启数据库连接
     */
    void openConnection();

    /**
     * 获取数据库连接对象
     * @return
     */
    Connection getConnection();
}

