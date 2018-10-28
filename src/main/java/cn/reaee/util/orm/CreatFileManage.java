package cn.reaee.util.orm;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jfinal.plugin.activerecord.Config;

/**
 * @author zzjxfj@163.com
 * 创建时间  2018年1月11日 上午9:55:08	
 * 说明	多线程创建Model管理器
 */
public class CreatFileManage {

	/*
	 * 表名列表
	 */
	private List<String> tables;
	
	/*
	 * 包名
	 */
	private String packageName;
	
	/*
	 * 包路径
	 */
	private String packagePath;
	
	/*
	 *	base类继承 
	 */
	private String baseExtends="com.jfinal.plugin.activerecord.Model";

	/*
	 * 线程结果集
	 */
	private Map<String, Future<Boolean>> map = null;
	
	/*
	 * 实体类生成器
	 */
	private Class<? extends CreatTableFile> creatFileClass=null;

	/*
	 * 线程池
	 */
	private ExecutorService threadpool;
	
	//线程加载完成标识
	private boolean start=false;
	/**
	 * @author zzjxfj@163.com
	 * 创建时间  2018年1月11日 上午10:36:43	
	 * 说明	管理器构造
	 * @param 	tables 表名列表
	 * @param 	packageName 包名
	 * @param	poolsize 线程池大小
	 * @param	creatFileClass	实体类字符串生成器
	 * @throws SQLException 
	 */
	public CreatFileManage(List<String> tables,  TableToModel config) throws SQLException {
		this.tables = tables;
		this.creatFileClass=config.getCreatTableFileClass();
		if(creatFileClass==null )throw new SQLException("CreatTableFile is null!"); 
		this.packageName = config.getPackageName();
		this.packagePath=PathTools.getPath()+packageName.replace('.', '/') + "/";
		File file=new File(this.packagePath+"/base");
		if(!file.exists()){  
		    file.mkdirs(); 
		}
		
		this.threadpool = Executors.newFixedThreadPool(config.getCreateFileThreadSize());
		this.map=new HashMap<String, Future<Boolean>>();
	}
	
	/**
	 * 说明	生成映射类
	 * @author clf_java@163.com
	 * @throws IOException 
	 * @creattime 创建时间  2018年1月11日 下午10:32:28	
	 */
	public void createMapping(List<String> tables,String packageStr) throws IOException {
		String content1 = createMappingBody(tables, packageStr);
		String outputPath1 = packagePath + "_TablePlugins.java";
		File file=new File(outputPath1);
		if(!file.exists()){  
		    file.createNewFile();
		}
		PrintStream pw1 = new PrintStream(file);
		pw1.println(content1);
		pw1.flush();
		pw1.close();
	}
	
	/**
	 * 功能：生成生成映射类主体代码
	 * @author clf_java@163.com
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	public String createMappingBody(List<String> tables,String packageStr) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + packageStr + ";\r\n");
		sb.append("import com.jfinal.plugin.activerecord.ActiveRecordPlugin;\r\n");
		sb.append("\r\n");
		// 实体部分
		sb.append("\r\n\r\npublic class _TablePlugins  {\r\n");
		sb.append("\tpublic void addMapping(ActiveRecordPlugin arp) {\r\n");
		for (String tablename:tables) {
			sb.append("\t\tarp.addMapping(")
			.append(firstCaps(tablename)).append("._TABLENAME,")
			.append(firstCaps(tablename))
			.append(".class);\r\n");
		}
		sb.append("   }\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}
	
	/**
	 * @author 		zzjxfj@163.com
	 * @creattime 	创建时间  2018年1月10日 下午11:37:59	
	 * 说明	   		字符串首字母大写
	 */
	public String firstCaps(String string) {
		if(string.length()<=1)return string.toUpperCase();
		return string.substring(0, 1).toUpperCase()+string.substring(1, string.length());
	}
	
	/**
	 * 创建线程并加载到线程池 创建映射文件
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 下午10:41:04	
	 * @param CreatFileManage
	 * 说明
	 * @throws Exception 
	 */
	public void start() throws Exception{
		if(!DbConnection.isStart())throw new IllegalAccessError("数据库未连接，请检查DbConnection.start(String url,String username,String password,int poolsize)参数是否正确");
		for(String s:tables){
			CreatTableFile cFile=creatFileClass.newInstance();
			cFile.set(s, packagePath, packageName,baseExtends);
			Future<Boolean> future = threadpool.submit(cFile);
			map.put(s,future);
		}
		start=true;
		try {
			createMapping(tables, packageName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取完成的 表 列表
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 下午10:41:49	
	 * @param CreatFileManage
	 * 说明
	 *
	 */
	final public List<String> getFinish(){
		if(!start)return null;
		if(map==null||map.isEmpty())return null;
		List<String> arr=new ArrayList<String>();
		for(Entry<String, Future<Boolean>> m:map.entrySet()){
			if(m.getValue().isDone()){
				arr.add(m.getKey());
			}
		}
		return arr;
	}
	
	/**
	 * 获取完成的表数量
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 下午10:43:10	
	 */
	final public int getFinishCount(){
		if(!start)return 0;
		if(map==null||map.isEmpty())return 0;
		int count=0;
		for(Entry<String, Future<Boolean>> m:map.entrySet()){
			if(m.getValue().isDone())count++;
		}
		return count;
	}
	
	/**
	 * 关闭线程池
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 下午10:43:30	
	 */
	public void shutdown() {
		threadpool.shutdown();
	}
	
	public boolean isTerminated(){
		return threadpool.isTerminated();
	}
	
	/**
	 * 线程池是否已关闭
	 * @author zzjxfj@163.com
	 * @creattime 创建时间  2018年1月11日 下午10:43:41	
	 */
	public boolean isShutdown(){
		return threadpool.isShutdown();
	}

	/**
	 * @author zzjxfj@163.com
	 * @category 设定base类的继承 默认为com.jfinal.plugin.activerecord.Model
	 * @param string
	 */
	public void setBaseExtends(String baseExtends) {
		this.baseExtends=baseExtends;
	}

}
