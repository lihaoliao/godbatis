package org.god.ibatis.core;

/**
 * 封装了一个SQL标签中的所有信息
 * 面向对象编程的思想
 */
public class MappedStatement {
    /**
     * SQL语句
     */
    private String sql;
    /**
     * 封装的结果集类型，resultType可以为null
     */
    private String resultType;

    @Override
    public String toString() {
        return "MappedStatement{" +
                "sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public MappedStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }
}
