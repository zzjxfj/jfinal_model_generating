package cn.reaee.util.orm;

/**
 * 使用模版
 * @author Administrator
 *
 */
public class Demo {
	
	/**
	 * @category 表映射Model文件生成工具演示
	 * 该工具需要javaSE-1.7以上版本支持
	 * 该工具需要外部jar包支持
	 * 开发时引入的jar包有:
	 * 1.final-3.3
	 * 2.druid-1.1.10
	 * 3.mysql-connector-java-8.0.11
	 */
	public static void main(String[] args) throws Exception{
		//数据库连接地址
		String jdbcURL="jdbc:mysql://127.0.0.1/test";
		
		//数据库用户名
		String username="root";
		
		//数据库密码
		String password="root";
		
		TableToModel start=new TableToModel(jdbcURL, username, password);
		
		/**
			设定生成的包名 默认为:"com.jfinal.core.entitys"
			start.setPackageName("cn.reaee.core.entitys");
			
			只生成表名包含shop的表  默认不过滤
			start.setOnlyCreateStatus(true);//打开过滤
			start.setOnlyCreateRegex(".*shop.*");//设定规则(正则表达式)
			
			不生成表名中有member的表  默认不过滤
			start.setNotCreateStatus(true);//打开过滤
			start.setNotCreateRegex(".*member.*");//设定规则(正则表达式)
			
			设定基础类的继承 ,默认继承自:com.jfinal.plugin.activerecord.Model
			start.setBaseExtends("aa.bb.cc.Aaa");
			
			这里不一一演示,里有有详细的备注,stat.set就会自动提示出设定的做用了!
		*/
		start.start();
	}
}
