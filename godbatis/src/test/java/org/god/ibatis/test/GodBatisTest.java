package org.god.ibatis.test;

import org.dom4j.DocumentException;
import org.god.ibatis.core.SqlSession;
import org.god.ibatis.core.SqlSessionFactory;
import org.god.ibatis.core.SqlSessionFactoryBuilder;
import org.god.ibatis.pojo.User;
import org.god.ibatis.utils.Resources;
import org.junit.Test;

public class GodBatisTest {
    @Test
    public void testSelectOne() throws Exception {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        SqlSession session = sqlSessionFactory.openSession();
        Object o = session.selectOne("org.mybatis.example.Mapper.selectUserById", "2");
        session.close();
        System.out.println(o.toString());
    }
    @Test
    public void testSqlSessionFactory() throws DocumentException {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        System.out.println(sqlSessionFactory);
    }
    @Test
    public void testInsertUser() throws DocumentException {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        SqlSession session = sqlSessionFactory.openSession();
        User user = new User("2","李四无","22");
        session.insert("org.mybatis.example.Mapper.insertUser",user);
        session.commit();
        session.close();
    }
}
