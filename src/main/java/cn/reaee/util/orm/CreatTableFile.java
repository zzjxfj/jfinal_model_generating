package cn.reaee.util.orm;

import java.util.concurrent.Callable;

/**
 * @author zzjxfj@163.com
 * 创建时间  2018年1月10日 下午11:33:56	
 * 说明	数据表创建实体类工具
 */
public abstract class CreatTableFile implements Callable<Boolean> {

	protected String tablename;
	protected String pathRoot;
	protected String packageName;
	protected String baseFilePath;
	protected String extFilePath;
	protected String baseExtends;
	
	/**
	 * @author zzjxfj@163.com
	 * 创建时间  2018年1月11日 下午12:03:11	
	 * 备注		未经同意，请勿修改
	 * 说明
	 *
	 */
	public CreatTableFile() {
	}
	
	/**
	 * @author zzjxfj@163.com
	 * @category	设定表名,类路径,包名
	 * @param tablename
	 * @param pathRoot
	 * @param packageName
	 */
	public void set(String tablename, String pathRoot, String packageName,String baseExtends) {
		this.tablename = tablename;
		this.pathRoot = pathRoot;
		this.packageName = packageName;
		baseFilePath=pathRoot+"base/"+firstCaps(tablename)+"_Base.java";
		extFilePath=pathRoot+firstCaps(tablename)+".java";
		this.baseExtends=baseExtends;
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
	 * @author zzjxfj@163.com
	 * 创建时间  2018年1月11日 上午9:44:47	
	 * 说明	线程执行内容,用于生成与表对应的类文件
	 */
	public abstract Boolean call() throws Exception;

}
