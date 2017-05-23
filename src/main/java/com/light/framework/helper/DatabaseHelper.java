package com.light.framework.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.light.framework.util.CollectionUtil;
import com.light.framework.util.PropsUtil;

/**
 *  数据库操作助手类
 */
public final class DatabaseHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
	
	private static final QueryRunner QUERY_RUNNER;
	
	private static final BasicDataSource DATA_SOURCE;
	
	//利用ThreadLocal来存储connection
	private static final ThreadLocal<Connection> CONNECTION_HOLDER;
	
	/**
	 * 数据库相关配置信息
	 */
	private static final String DRIVER;
	private static final String URL;
	private static final String USERNAME;
	private static final String PASSWORD;
	
	static{
		CONNECTION_HOLDER = new ThreadLocal<Connection>(); 
		QUERY_RUNNER = new QueryRunner();
		Properties conf = PropsUtil.LoadProps("light.properties");
		DRIVER = conf.getProperty("light.framework.jdbc.driver");
		URL = conf.getProperty("light.framework.jdbc.url");
		USERNAME = conf.getProperty("light.framework.jdbc.username");
		PASSWORD = conf.getProperty("light.framework.jdbc.password");
		
		DATA_SOURCE = new BasicDataSource();
		DATA_SOURCE.setDriverClassName(DRIVER);
		DATA_SOURCE.setUrl(URL);
		DATA_SOURCE.setUsername(USERNAME);
		DATA_SOURCE.setPassword(PASSWORD);
		
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			LOGGER.error("can not load jdbc driver",e);
		}
	}
	
	/**
	 * 获取数据库连接 
	 */
	public static Connection getConnection(){
		Connection conn = CONNECTION_HOLDER.get();
		if(conn == null){
			try {
				//conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
				conn = DATA_SOURCE.getConnection();
			} catch (SQLException e) {
				LOGGER.error("get connection fail",e);
				throw new RuntimeException(e);
			} finally {
				CONNECTION_HOLDER.set(conn);
			}
		}
		return conn;
	}
	
	/**
	 * 关闭数据库连接 
	 */
	@Deprecated
	public static void closeConnection(){
		Connection conn = CONNECTION_HOLDER.get();
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("close connection fail",e);
				throw new RuntimeException(e);
			} finally{
				CONNECTION_HOLDER.remove();
			}
		}
	}
	
	/**
	 * 开启事务 
	 */
	public static void beginTransaction(){
		Connection conn = getConnection();
		if(conn != null){
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				LOGGER.error("begin transaction fail",e);
				throw new RuntimeException(e);
			} finally{
				CONNECTION_HOLDER.set(conn);
			}
		}
	}
	
	/**
	 * 提交事务 
	 */
	public static void commitTransaction(){
		Connection conn = getConnection();
		if(conn != null){
			try {
				conn.commit();
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("commit transaction fail",e);
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 事务回滚 
	 */
	public static void rollbackTransaction(){
		Connection conn = getConnection();
		if(conn != null){
			try {
				conn.rollback();
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("rollback transaction fail",e);
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 查询实例列表 
	 */
	public static <T> List<T> queryEntityList(Class<T> entityClass, String sql,Object... params) {
		List<T> entityList;
		try {
			Connection conn = getConnection();
			entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
		} catch (SQLException e) {
			LOGGER.error("query entity list failure", e);
			throw new RuntimeException(e);
		} 
		return entityList;
	}
	
	/**
	 * 查询单个实体对象 
	 */
	public static <T> T queryEntity(Class<T> entityClass,String sql,Object... params){
		T entity;
		try{
			Connection conn = getConnection();
			entity = QUERY_RUNNER.query(conn, sql,new BeanHandler<T>(entityClass),params);
		}catch (SQLException e) {
			LOGGER.error("query entity failure",e);
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	/**
	 * 执行查询语句 
	 */
	public static List<Map<String,Object>> executeQuery(String sql, Object ...params){
		List<Map<String,Object>> result = null;
		try {
			Connection conn = getConnection();
			QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			LOGGER.error("execute query fail");
			throw new RuntimeException(e);
		}
		return result;
	}
	
	/**
	 * 执行更新语句（包括update，insert，delete） 
	 */
	public static int executeUpdate(String sql,Object...objects){
		int rows = 0;
		try{
			Connection conn = getConnection();
			rows = QUERY_RUNNER.update(conn,sql,objects);
		}catch (SQLException e) {
			LOGGER.error("execute update failure",e);
		}
		return rows;
	}
	
	/**
	* @Title: insertEntity
	* @Description: 插入实体
	* @param @param entityClass
	* @param @param fieldMap
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/
	public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
		if(CollectionUtil.isEmpty(fieldMap)){
			LOGGER.error("can not insert entity: fieldMap is empty");
			return false;
		}
		String sql = "insert into " + getTableName(entityClass);
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		for(String fieldName : fieldMap.keySet()){
			columns.append(fieldName).append(", ");
			values.append("?, ");
		}
		//columns 将最后一个, 换成)
		columns.replace(columns.lastIndexOf(", "),columns.length(),")");
		//values 将最后一个, 换成)
		values.replace(values.lastIndexOf(", "), values.length(), ")");
		sql += columns + " values " + values;
		Object[] params = fieldMap.values().toArray();
		return executeUpdate(sql,params) == 1;
	}
	
	/**
	* @Title: updateEntity
	* @Description: 更新实体
	* @param @param entityClass
	* @param @param id
	* @param @param fieldMap
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/
	public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
		if(CollectionUtil.isEmpty(fieldMap)){
			LOGGER.error("can not update entity: fieldMap is empty");
			return false;
		}
		String sql = " update " + getTableName(entityClass) + " set ";
		StringBuilder columns = new StringBuilder();
		for(String fieldName : fieldMap.keySet()){
			columns.append(fieldName).append("=?, ");
		}
		sql += columns.substring(0, columns.lastIndexOf(", ")) + " where id = ?";
		List<Object> paramList = new ArrayList<Object>();
		paramList.addAll(fieldMap.values());
		paramList.add(id);
		Object[] params = paramList.toArray();
		return executeUpdate(sql,params) == 1;
	}
	
	/**
	* @Title: deleteEntity
	* @Description: 删除实体
	* @param @param entityClass
	* @param @param id
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/
	public static <T> boolean deleteEntity(Class<T> entityClass,long id){
		String sql = " delete from " + getTableName(entityClass) + " where id=?";
		return executeUpdate(sql, id) == 1;
	}
	
	/**
	* @Title: getTableName
	* @Description: 获取表名（实体类名，要求数据库表名和实体类名相同）
	* @param @param entityClass
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	*/
	public static String getTableName(Class<?> entityClass){
		return entityClass.getSimpleName();
	}
	
}
