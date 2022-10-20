package org.god.ibatis.core;

import java.util.HashMap;
import java.util.Map;

/**
 *  一个数据库一般对应一个SqlSessionFactory对象
 * 通过SqlSessionFactory对象获取SqlSession对象（开启会话）
 * 可以开启多个SqlSession会话
 */
public class SqlSessionFactory {
    public SqlSessionFactory(){}

    /**
     * 事务管理器属性
     * 事务管理器是可以灵活切换的，所以SqlSessionFactory类中的事务管理器应该是面向接口编程
     */
    private Transaction transaction;


    /**
     * 存放Sql对象的Mapper集合
     */
    private Map<String,MappedStatement> mappedStatementMaps = new HashMap<>();

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Map<String, MappedStatement> getMappedStatementMaps() {
        return mappedStatementMaps;
    }

    public void setMappedStatementMaps(Map<String, MappedStatement> mappedStatementMaps) {
        this.mappedStatementMaps = mappedStatementMaps;
    }

    /**
     * 获取会话对象
     * @return
     */
    public SqlSession openSession(){
        //开启会话的前提是开启连接
         transaction.openConnection();
         SqlSession session = new SqlSession(this);
         return session;
    }

    public SqlSessionFactory(Transaction transaction, Map<String, MappedStatement> mappedStatementMaps) {
        this.transaction = transaction;
        this.mappedStatementMaps = mappedStatementMaps;
    }
}
