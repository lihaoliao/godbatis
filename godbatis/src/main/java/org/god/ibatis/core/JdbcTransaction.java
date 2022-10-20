package org.god.ibatis.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC事务管理器
 */
public class JdbcTransaction implements Transaction{
    /**
     * 数据源属性
     * 面向接口编程
     */
    private DataSource dataSource;
    /**
     * 自动提交的标志
     * true表示自动提交
     * false表示非自动提交
     */
    private Boolean autoCommit;

    /**
     * 连接对象
     */
    private Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * 创建事务管理器对象
     * @param dataSource
     * @param autoCommit
     */
    public JdbcTransaction(DataSource dataSource, Boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openConnection(){
        try {
            if (connection == null) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(autoCommit);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
