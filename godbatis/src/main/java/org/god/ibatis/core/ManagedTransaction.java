package org.god.ibatis.core;

import java.sql.Connection;

/**
 * MANAGED事务管理器
 */
public class ManagedTransaction implements Transaction{
    @Override
    public void commit() {

    }

    @Override
    public void close() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void openConnection() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
