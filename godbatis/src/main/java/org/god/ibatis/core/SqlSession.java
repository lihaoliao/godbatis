package org.god.ibatis.core;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * 专门执行SQL语句的会话对象
 */
public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 执行insert语句，向数据库表种插入记录
     * @param sqlId
     * @param o
     * @return
     */
    public int insert(String sqlId,Object o){
        int count = 0;
        try {
            //JDBC代码
            Connection connection = sqlSessionFactory.getTransaction().getConnection();
            Map<String, MappedStatement> mappedStatementMaps = sqlSessionFactory.getMappedStatementMaps();
            MappedStatement mappedStatement = mappedStatementMaps.get(sqlId);
            String sql = mappedStatement.getSql();
            int fromIndex = 0;
            List<String> propertyList = new ArrayList<>();
            while (fromIndex<=sql.length()) {
                int left = sql.indexOf("{", fromIndex);
                int right = sql.indexOf("}", fromIndex);
                if(left==-1 || right== -1){
                    break;
                }
                String propertyName = sql.substring(left + 1, right).trim();
                //System.out.println(propertyName);
                propertyList.add(propertyName);
                fromIndex = right + 1;
            }
//            propertyList.forEach(property->{
//                System.out.println(property);
//            });
            sql = sql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //给占位符传值
            //问题一：不知道有多少问号
            //问题二：不知道object中的对象该赋值给那一个
            for (int i = 0; i < propertyList.size(); i++) {
                String getMethodName = "get" + propertyList.get(i).toUpperCase().charAt(0) + propertyList.get(i).substring(1);
                Method getMethod = o.getClass().getDeclaredMethod(getMethodName);
                Object propertyValue = getMethod.invoke(o);
                //数据库下标从1开始
                preparedStatement.setString(i+1,propertyValue.toString());
            }
            count = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 执行select语句，返回一个符合条件的对象,只适合返回一条记录的sql语句
     * @param sqlId
     * @param parameter
     * @return
     */
    public Object selectOne(String sqlId,Object parameter) throws Exception {
        Object object = null;
        Connection connection = sqlSessionFactory.getTransaction().getConnection();
        MappedStatement mappedStatement = sqlSessionFactory.getMappedStatementMaps().get(sqlId);
        String sql = mappedStatement.getSql();
        String resultType = mappedStatement.getResultType();
        int fromIndex = 0;
        List<String> propertyList = new ArrayList<>();
        while (fromIndex<=sql.length()) {
            int left = sql.indexOf("{", fromIndex);
            int right = sql.indexOf("}", fromIndex);
            if(left==-1 || right== -1){
                break;
            }
            String propertyName = sql.substring(left + 1, right).trim();
            //System.out.println(propertyName);
            propertyList.add(propertyName);
            fromIndex = right + 1;
        }
        sql = sql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        Class<?> resultTypeClass = Class.forName(resultType);
        for (int i = 0; i < propertyList.size(); i++) {
            String getMethodName = "get" + propertyList.get(i).toUpperCase().charAt(0) + propertyList.get(i).substring(1);
            String setMethodName = "set" + propertyList.get(i).toUpperCase().charAt(0) + propertyList.get(i).substring(1);
            Method getMethod = resultTypeClass.getDeclaredMethod(getMethodName);
            Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName,String.class);
            Object o = resultTypeClass.getDeclaredConstructor().newInstance();
            setMethod.invoke(o,parameter);
            Object propertyValue = getMethod.invoke(o);
            //数据库下标从1开始
            preparedStatement.setString(i+1,propertyValue.toString());
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        //因为只有一条记录，所以直接用if
        if (resultSet.next()){
            //获取resultType的Class
            resultTypeClass = Class.forName(resultType);
            //调用无参构造方法创建对象
            object = resultTypeClass.getDeclaredConstructor().newInstance();
            //给User类的属性赋值
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);
                String setMethodName = "set" + columnName.toUpperCase().charAt(0) + columnName.substring(1);
                Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName,String.class);
                setMethod.invoke(object,resultSet.getString(i+1));
            }
        }
        return object;
    }
    //局部测试
    public static void main(String[] args) {

    }

    public void commit(){
        sqlSessionFactory.getTransaction().commit();
    }
    public void rollback(){
        sqlSessionFactory.getTransaction().rollback();
    }
    public void close(){
        sqlSessionFactory.getTransaction().close();
    }

}
