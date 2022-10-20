package org.god.ibatis.core;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.god.ibatis.utils.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

/**
 * SqlSessionFactoryBuilder的构造器
 * 通过SqlSessionFactoryBuilder的build方法解析godbatis-config.xml文件
 * 然后创建SqlSessionFactory对象
 */
public class SqlSessionFactoryBuilder {
  /**
   * 无参构造方法
   */
  public SqlSessionFactoryBuilder(){}

  /**
   * 解析godbatis-config.xml文件
   * 创建SqlSessionFactory对象
   * @param is
   * @return
   */
  public SqlSessionFactory build(InputStream is) throws DocumentException {
    //解析XML核心配置文件
    SAXReader reader =  new SAXReader();
    Document document = reader.read(is);
    Element environmentsElement = (Element) document.selectSingleNode("/configuration/environments");
    String defaultId = environmentsElement.attributeValue("default");
    Element environmentElement = (Element) document.selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");
    Element transactionManagerElement = environmentElement.element("transactionManager");
    Element dataSourceElement = environmentElement.element("dataSource");
    List<String> sqlMapperXMLPathList = new ArrayList<>();
    List<Node> mapperList = document.selectNodes("//mapper");
    mapperList.forEach(mapper->{
      Element m =(Element) mapper;
      String resource = m.attributeValue("resource");
      //获取所有XMLMapper文件
      sqlMapperXMLPathList.add(resource);
    });
    DataSource dataSource = getDataSource(dataSourceElement);
    Transaction transaction = getTransaction(transactionManagerElement,dataSource);
    Map<String,MappedStatement> mappedStatementMap = getMappedStatements(sqlMapperXMLPathList);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactory(transaction,mappedStatementMap);
    return sqlSessionFactory;
  }

  /**
   * 解析所有的SqlMapperXML文件，构建Map集合
   * @param sqlMapperXMLPathList
   * @return
   */
  private Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperXMLPathList) {
    //key是sql语句的id，值是MappedStatement对象
    Map<String,MappedStatement> mappedStatementMap = new HashMap<>();
    //对每一个Mapper.xml进行一一解析获取
    sqlMapperXMLPathList.forEach(sqlMapper -> {
      SAXReader reader = new SAXReader();
      try {
        Document document = reader.read(Resources.getResourceAsStream(sqlMapper));
        Element mapperElement = (Element) document.selectSingleNode("mapper");
        String namespace = mapperElement.attributeValue("namespace");
        List<Element> sqlElements = mapperElement.elements();
        sqlElements.forEach(sql -> {
          String sqlId = sql.attributeValue("id");
          sqlId = namespace + "." + sqlId;
          String resultType = sql.attributeValue("resultType");
          String sqlText = sql.getTextTrim();
          MappedStatement mappedStatement = new MappedStatement(sqlText,resultType);
          mappedStatementMap.put(sqlId,mappedStatement);
        });
      } catch (DocumentException e) {
        e.printStackTrace();
      }
    });

    return mappedStatementMap;
  }


  /**
   * 获取事务管理器
   * @param transactionManagerElement 事务管理器标签元素
   * @param dataSource 数据源对象
   * @return
   */
  private Transaction getTransaction(Element transactionManagerElement, DataSource dataSource) {
    String transactionManagerType = transactionManagerElement.attributeValue("type").trim().toUpperCase();
    Transaction transaction = null;
    if(Const.JDBC_TRANSACTION.equals(transactionManagerType)){
      transaction = new JdbcTransaction(dataSource,false);
    }
    if(Const.MANAGED_TRANSACTION.equals(transactionManagerType)){
      transaction = new ManagedTransaction();
    }
    return transaction;
  }

  /**
   * 获取数据源对象
   * @param dataSourceElement 数据源标签元素
   * @return
   */
  private DataSource getDataSource(Element dataSourceElement) {
    DataSource dataSource = null;
    Map<String,String> propertyMap = new HashMap<>();
    String dataSourceType = dataSourceElement.attributeValue("type").trim().toUpperCase();
    if(Const.UN_POOLED_DATASOURCE.equals(dataSourceType)){
      List<Element> dataSourcePropertyList = dataSourceElement.elements("property");
      dataSourcePropertyList.forEach(property -> {
        String name = property.attributeValue("name");
        String value = property.attributeValue("value");
        propertyMap.put(name,value);
      });
      dataSource = new UnPooledDataSource(propertyMap.get("driver"),propertyMap.get("url"),propertyMap.get("username"),propertyMap.get("password"));
    }
    if(Const.POOLED_DATASOURCE.equals(dataSourceType)){
      dataSource = new PooledDataSource();
    }
    if(Const.JNDI_DATASOURCE.equals(dataSourceType)){
      dataSource = new JNDIDataSource();
    }

    return dataSource;
  }
}
