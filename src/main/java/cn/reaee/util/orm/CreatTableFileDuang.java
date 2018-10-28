package cn.reaee.util.orm;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzjxfj@163.com
 * @category CreatTableFile 实现类,生成表对应的类文件
 */
public class CreatTableFileDuang extends CreatTableFile{
	
	public CreatTableFileDuang() {}

    /** 
     * @author zzjxfj@163.com
     * @category获得某表中所有字段的信息对象 
     * @param tableName 
     * @return List<TabField>
     * @throws Exception 
     */  
    public List<TabField> getTabFieldsByTableName(String tableName ,Connection conn) throws SQLException {  
        Statement stmt = conn.createStatement();  
        ResultSet rs = stmt.executeQuery("show full columns from " + tableName);
        List<TabField> list=new ArrayList<TabField>();
            while (rs.next()) {
            	list.add(new TabField(rs,firstCaps(tableName)));
            }   
        rs.close();  
        stmt.close();  
        conn.close();  
        return list;
    }  
	

    /**
     * 说明 生成业务类内容 继承自Base类
     * 创建时间  2018年4月24日 上午2:37:29
     * @throws IOException 
     */
	public void  creatExtClasee() throws IOException{

		// 创建字符串,拼接类实体
		StringBuffer classHead = new StringBuffer();
		// 声明当前拼接类所在的包
		classHead.append("package " + packageName + ";\r\n");
		classHead.append("\r\n");
		classHead.append("import "+ packageName + ".base."+firstCaps(tablename) + "_Base;");
		classHead.append("\r\n");
		// 注释部分,该类对应是数据库的哪张表,生成的时间
		classHead.append("\t/**\r\n");
		classHead.append("\t *\t" + tablename + " 实体类\r\n");
		classHead.append("\t */ \r\n");
		// 实体部分,类的头部
		//public class Aliyunsms_set extends Aliyunsms_set_Base<Aliyunsms_set>{
		String name=firstCaps(tablename);
		classHead.append("\r\npublic class ").append(name).append(" extends ")
		.append(name).append("_Base<").append(name).append(">{\r\n")
		.append("\r\n\t/**\r\n")
		.append("\t * 获取操作表的dao\r\n")
		.append("\t */\r\n")
		.append("\tpublic static final ")
		.append(firstCaps(tablename))
		.append(" dao = new ").append(name).append("();\r\n");
		
		File file=new File(extFilePath);
		if((!file.exists())){
			if(!file.createNewFile()){
				System.err.println("文件不存在，并且创建失败，文路径为："+extFilePath);
			}
			PrintStream out = new PrintStream(file);
			out.println(classHead);
			//类体结束
			out.print("\r\n}");
			out.flush();
			out.close();			
		}
	}

    
    /**
     * 说明 生成Base头部内容
     * 创建时间  2018年4月24日 上午2:37:29
     */
	public StringBuffer  getBaseClassHead(String tablename, String packageName){
		// 创建字符串,拼接类实体
		StringBuffer classHead = new StringBuffer();
		// 声明当前拼接类所在的包
		classHead.append("package " + packageName + ".base;\r\n");
		// 导入jfinal中Model类所在的包
//		classHead.append("import "+baseExtends+";\r\n");
		classHead.append("\r\n");
		// 注释部分,该类对应是数据库的哪张表
		classHead.append("\t/**\r\n");
		classHead.append("\t *\t" + tablename + " 实体类\r\n");
		classHead.append("\t */ \r\n");
		// 实体部分,类的头部
		classHead.append("\r\npublic class " + firstCaps(tablename) + "_Base<M extends "+baseExtends+"> extends "+baseExtends+"<M>{\r\n");
		classHead
		.append("\r\n\t/**\r\n")
		.append("\t * 表名称     ").append(tablename).append("\r\n")
		.append("\t */\r\n")
		.append("\tpublic static final String _TABLENAME=\"").append(tablename).append("\";\r\n");
		return classHead;
	}

	/**
	 * 生成BaseBean
	 * @param out
	 * @param list
	 * @throws IOException 
	 */
	public void creatBaseClasee(List<TabField> list) throws IOException{
		File file=new File(baseFilePath);
		if((!file.exists())&&!file.createNewFile()){
			System.err.println("文件不存在，并且创建失败，文路径为："+baseFilePath);
		}
		PrintStream out = new PrintStream(file);
		//输出文件头
		out.print(getBaseClassHead(tablename, packageName));
		
		StringBuffer primaryKey=new StringBuffer();
		for(TabField tField:list){
			if(tField.isKey())primaryKey.append(tField.getField()).append(",");
		}
		//输出主键
		if(primaryKey.length()>2){
			primaryKey=primaryKey.deleteCharAt(primaryKey.length() - 1);
			out.print(
					new StringBuffer().append("\r\n\t/**\r\n")
					.append("\t * 主键     ").append(primaryKey).append("\r\n")
					.append("\t */\r\n")
					.append("\tpublic static final String _PRIMARY_KEYS=\"").append(primaryKey).append("\";\r\n")
					);
		}
		
		//输出属性列表
		for(TabField tField:list){
			out.print(tField.getColumnBody());
		}
		
		//输出方法列表
		for(TabField tField:list){
			out.print(tField.getBaseGetBody());
			out.print(tField.getDuangGetBody());
			out.print(tField.getSetMothedBodys());
		}
		
		//类体结束
		out.print("\r\n}");
		out.flush();
		out.close();
	}
	
	
	public Boolean call() throws Exception{
		Connection conn = null;
		while(conn==null||!DbConnection.isStart()){
			try{
				conn=DbConnection.getConnection();
			}catch(SQLException e){
				
			}
		}
		List<TabField> list =null;
		try{
			list = getTabFieldsByTableName(tablename, conn);
			System.out.println(tablename+"\t:\t"+conn);
		}catch(Exception e){
			System.err.println("表 "+tablename+" 不存在");
		}
		
		if(list!=null){
			creatBaseClasee(list);
			creatExtClasee();
		}
		
		if(conn!=null){
			conn.close();
		}
		return true;
	}

}


