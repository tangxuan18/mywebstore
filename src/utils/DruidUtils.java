package utils;



import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Druid连接池的工具类
 */
public class DruidUtils {

    /**
     *  定义成员变量 DataSource
     */
    private static DataSource dataSource;

    /**
     * threadLocal用于线程中的数据隔离
     * 此时就是Connection隔离
     */
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 静态代码块初始化datasource
     */
    static{
        try {
            Properties pro = new Properties();
            // 1 使用getClassLoader()加载druid.properties配置文件
            pro.load(DruidUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            // 2 初始化DataSource
            dataSource = DruidDataSourceFactory.createDataSource(pro);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接池方法
     */
    public static DataSource getDataSource(){
        return dataSource;
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 开启事务用的getConnection
     * @param flag 表示是否开启事务
     *
     * */
    public static Connection getConnection(boolean flag) throws SQLException {
        if(flag){
            Connection connection = threadLocal.get();
            if(connection == null){
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            }
            return connection;
        }else {
            return dataSource.getConnection();
        }
    }

    /**
     * 释放资源
     */
    public static void close(Statement stmt,Connection conn){
       /* if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();//归还连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
       close(null,stmt,conn);
    }


    public static void close(ResultSet rs , Statement stmt, Connection conn){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();//归还连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
