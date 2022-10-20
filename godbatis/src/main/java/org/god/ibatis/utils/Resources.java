package org.god.ibatis.utils;

import java.io.InputStream;

/**
 * godbatis框架提供的一个工具类
 *   这个工具类专门完成类路径中资源的加载
 *   @author llh
 *   @since 1.0
 *   @version 1.0
 */
public class Resources {

    private Resources(){}

    /**
     * 从类路径中加载资源
     * @param resource
     * @return
     */
    public static InputStream getResourceAsStream(String resource){
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }



}
