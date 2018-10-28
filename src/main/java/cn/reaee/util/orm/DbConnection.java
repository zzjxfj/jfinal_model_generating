package cn.reaee.util.orm;

import java.sql.Connection;
import java.sql.SQLException;
import com.jfinal.plugin.druid.DruidPlugin;

/** @author zzjxfj@163.com
 * 创建时间  2018年1月10日 下午8:04:30	
 * 说明	数据库连接和一些工具
 */
public class DbConnection {
	
	private static DruidPlugin druidPlugin=null;
	
	/**
	 * 初始化连接池 只可调用一次
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 上午10:39:17	
	 * @param url	JDBC数据库连接URL
	 * @param username	数据库用户名
	 * @param password	数据库密码
	 * @param poolsize	连接池大小		
	 *
	 */
	final public static synchronized void start(String url,String username,String password,int poolsize) throws IllegalAccessException{
		if(druidPlugin!=null)throw new IllegalAccessError("不能重复启动数库连接，本方法只可执行一次，如要检测连接运行状态，请调用isStart()");
		if(druidPlugin==null)druidPlugin=new DruidPlugin(url, username, password);
		druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(poolsize);
	}
	
	final public static boolean stop(){
		return null==druidPlugin ? false:druidPlugin.stop();
	}
	
	/**
	 * @author zzjxfj@163.com
	 * @category 从线程池获取一个连接
	 * @return Connection
	 * @throws SQLException
	 */
	final public static Connection getConnection() throws SQLException{
		return null==druidPlugin ? null:druidPlugin.getDataSource().getConnection();
	}
	
	/**
	 * @author zzjxfj@163.com
	 * @category 获取连接状态
	 * @return boolean
	 */
	final public static boolean isStart(){
		if(druidPlugin==null)return false;
		if(!druidPlugin.start())return false;
		return true;
	}

}
