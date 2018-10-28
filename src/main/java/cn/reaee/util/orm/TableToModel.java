package cn.reaee.util.orm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @category 表映射Model文件生成工具,该工具需要外部jar包支持,开发时引入的jar包有:
 * 1.final-3.3
 * 2.druid-1.1.10
 * 3.mysql-connector-java-8.0.11
 */
public class TableToModel {

	/**
	 * 数据库连接地址
	 */
	private String jdbcURL = null;
	
	/**
	 * 数据库用户名
	 */
	private String username = null;
	
	/**
	 * 数据库密码
	 */
	private String password = null;
	
	/**
	 * 数据库连接池大小
	 */
	private int dbPoolSize = 10;
	
	/**
	 * 生成的包名
	 */
	private String packageName = "com.jfinal.core.entitys";
	
	/**
	 * 生成文件的多线程数量,不要大于数据库连接池大小
	 */
	private int createFileThreadSize=10;
	
	/**
	 * 生成文件的工具类
	 */
	private Class<? extends CreatTableFile> creatTableFileClass = CreatTableFileDuang.class;
	
	/**
	 * 基础类自定义继承
	 */
	private String baseExtends = "com.jfinal.plugin.activerecord.Model";
	
	/**
	 * 是否过滤不生成的表
	 */
	boolean notCreateStatus = false;
	
	/**
	 * 过滤不生成的表的 正则表达式 列表
	 */
	private String notCreateRegex = null;
	
	/**
	 * 是否只生成符合指定规则的表
	 */
	private boolean onlyCreateStatus = false;
	
	/**
	 * 表生成规则 的 正则表达式 列表
	 */
	private String  onlyCreateRegex = null;
	
	/**
	 * 进度检查间隔时间
	 */
	private long steepChack = 500;

	/**
	 * @author zzjxfj@163.com
	 * @category	ORM文件生成工具构造方法
	 * @param jdbcURL
	 * @param username
	 * @param password
	 */
	public TableToModel(String jdbcURL, String username, String password) {
		super();
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 开始执行任务
	 * @throws Exception
	 */
	public void start() throws Exception{
		/**
		 * 建立连接
		 */
		DbConnection.start(jdbcURL,username,password,dbPoolSize);
		/**
		 * 连接失败抛出异常
		 */
		if(!DbConnection.isStart()){
			throw new Exception("连接失败!!");
		}
		
		/**
		 * 获取不到连接抛出异常
		 */
		Connection con = DbConnection.getConnection();
		if(con==null){
			throw new Exception("获取不到连接!");
		}
		con.close();
		
		/**
		 * 获取所有表名
		 */
		List<String> tables=getTableNames();
		
		/**
		 * 是否只生成符合指定规则的表
		 */
		if(onlyCreateStatus){
			if(null==onlyCreateRegex)throw new Exception("你设置了只生成符合指定规则的表,但没设置生成规则!");
			tables=likeName(tables,onlyCreateRegex,true);			
		}
		
		/**
		 * 是否过滤不生成的表
		 */
		if(notCreateStatus){
			if(null==notCreateRegex)throw new Exception("你设置了过滤不生成的表,但没设置过滤规则!");
			tables=likeName(tables,notCreateRegex,false);			
		}
		
		/**
		 * 创建多线程文件生成器
		 */
		CreatFileManage cfm = new CreatFileManage(tables, packageName, createFileThreadSize,creatTableFileClass);
		
		/**
		 * 设置基础继承类
		 */
		if(null!=baseExtends){
			cfm.setBaseExtends(baseExtends);
		}
		
		/**
		 * 把线程加入列表执行
		 */
		cfm.start();
				
		/**
		 * 获取执行进度并显示
		 */
		List<String> list = cfm.getFinish();
		int size=0;
		while((list=cfm.getFinish())==null||list.size()<tables.size()){
			if(list==null){
				System.out.println(".....");
			}
			else if(size!=list.size()){
				System.out.println("已完成："+list.size()+"个");
				size=list.size();
			}
			Thread.sleep(steepChack);
		}
		
		System.out.println("已全部完成,映射"+list.size()+"个表,每个表两个文件,一个映射文件工具类!共计个"+(list.size()*2+1)+"文件,请刷新项目!");
		
		/**
		 * 任务完成，关闭线程池
		 */
		cfm.shutdown();
		
		 /**
		  * 任务完成，关闭数据库连接池
		  */
		DbConnection.stop();
		System.out.println("---------任务已完成,线程池已关闭,数据库连接池已关闭!---------");
	}
	
	/**
	 * 过滤字符串列表
	 * @author zzjxfj@163.com
	 * @time 创建时间  2018年1月10日 下午10:30:04	
	 * @param strings 要过滤的字符串列表
	 * @param regexs 要过滤的字符串特征集	(正则表达式)
	 * @param option		true 存在特征的保留，false 存在特征不保留
	 * @return List	返回过滤后的字符串列表
	 */
	public List<String> likeName(List<String> strings, String regex, boolean option) {
		ArrayList<String> list=new ArrayList<String>();
		for(String s:strings){
			boolean like=s.matches(regex);
			if(option==like){
				list.add(s);
			}	
		}
		return list;
	}
	

	/** 
	 * 说明		获取当前连接的数据库所有的表名
	 * @author zzjxfj@163.com
	 * 创建时间  2018年1月10日 下午8:54:56	
	 * @throws SQLException 
	 */
	public List<String> getTableNames() throws SQLException {
		if(!DbConnection.isStart())throw new SQLException("获取数据库连接失败!");
		List<String> list=new ArrayList<String>();
		Connection con = DbConnection.getConnection();
		DatabaseMetaData mata = con.getMetaData();
		String currentCatalog = con.getCatalog(); 
		String[] types=new String[] {"TABLE"};
		ResultSet tables = mata.getTables(currentCatalog, null, null, types);
		while (tables.next()) {
			list.add(tables.getString("TABLE_NAME"));
			System.out.println(tables.getString("TABLE_NAME"));
		}
		con.close();
		return list;
	}

	/**
	 * 数据库连接地址
	 */
	public String getJdbcURL() {
		return jdbcURL;
	}

	/**
	 * 数据库连接地址
	 */
	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	/**
	 * 数据库用户名
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 数据库用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 数据库密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 数据库密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 数据库连接池大小 默认为10
	 */
	public int getDbPoolSize() {
		return dbPoolSize;
	}

	/**
	 * 数据库连接池大小 默认为10
	 */
	public void setDbPoolSize(int dbPoolSize) {
		this.dbPoolSize = dbPoolSize;
	}

	/**
	 * 生成的包名 默认为 com.jfinal.core.entitys
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * 生成的包名 默认为 com.jfinal.core.entitys
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * 生成文件的多线程数量,不要大于数据库连接池大小 默认为10
	 */
	public int getCreateFileThreadSize() {
		return createFileThreadSize;
	}
	
	/**
	 * 生成文件的多线程数量,不要大于数据库连接池大小 默认为10
	 */
	public void setCreateFileThreadSize(int createFileThreadSize) {
		this.createFileThreadSize = createFileThreadSize;
	}


	/**
	 * 生成文件的工具类  默认为:CreatTableFileDuang.class
	 * 也可以根据需要自己写CreatTableFile的实现类
	 */
	public Class<? extends CreatTableFile> getCreatTableFileClass() {
		return creatTableFileClass;
	}

	/**
	 * 生成文件的工具类  默认为:CreatTableFileDuang.class
	 * 也可以根据需要自己写CreatTableFile的实现类
	 */
	public void setCreatTableFileClass(
			Class<? extends CreatTableFile> creatTableFileClass) {
		this.creatTableFileClass = creatTableFileClass;
	}

	/**
	 * 基础类自定义继承 默认为:com.jfinal.plugin.activerecord.Model
	 */
	public String getBaseExtends() {
		return baseExtends;
	}

	/**
	 * 基础类自定义继承 默认为:com.jfinal.plugin.activerecord.Model
	 */
	public void setBaseExtends(String baseExtends) {
		this.baseExtends = baseExtends;
	}

	/**
	 * 是否过滤不生成的表
	 */
	public boolean isNotCreateStatus() {
		return notCreateStatus;
	}

	/**
	 * 是否过滤不生成的表 需要配合setNotCreateRegex(String notCreateRegex)使用
	 */
	public void setNotCreateStatus(boolean notCreateStatus) {
		this.notCreateStatus = notCreateStatus;
	}
	
	/**
	 * 过滤不生成的表的 正则表达式 
	 */
	public String getNotCreateRegex() {
		return notCreateRegex;
	}
	
	/**
	 * 过滤不生成的表的 正则表达式 
	 */
	public void setNotCreateRegex(String notCreateRegex) {
		this.notCreateRegex = notCreateRegex;
	}

	/**
	 * 是否只生成符合指定规则的表
	 */
	public boolean isOnlyCreateStatus() {
		return onlyCreateStatus;
	}

	/**
	 * 是否只生成符合指定规则的表 需要配合setOnlyCreateRegex(String onlyCreateRegex)使用
	 */
	public void setOnlyCreateStatus(boolean onlyCreateStatus) {
		this.onlyCreateStatus = onlyCreateStatus;
	}

	/**
	 * 表生成规则 的 正则表达式 
	 */
	public String getOnlyCreateRegex() {
		return onlyCreateRegex;
	}

	/**
	 * 表生成规则 的 正则表达式 
	 */
	public void setOnlyCreateRegex(String onlyCreateRegex) {
		this.onlyCreateRegex = onlyCreateRegex;
	}

	/**
	 * 进度检查间隔时间 默认 500 (毫秒)
	 */
	public long getSteepChack() {
		return steepChack;
	}

	/**
	 * 进度检查间隔时间 默认 500 (毫秒)
	 */
	public void setSteepChack(long steepChack) {
		this.steepChack = steepChack;
	}
}
