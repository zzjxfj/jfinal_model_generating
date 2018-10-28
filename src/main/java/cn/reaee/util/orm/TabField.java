package cn.reaee.util.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 表字段拼装类
 */
public class TabField{

	private static final String LONG="java.lang.Long";
	private static final String BYTES="byte[]";
	private static final String BOOLEAN="java.lang.Boolean";
	private static final String BYTE="java.lang.Byte";
	private static final String INTEGER="java.lang.Integer";
	private static final String BIGDECIMAL="java.math.BigDecimal";
	private static final String STRING="java.lang.String";
	
	private static final String _LONG="getLong";
	private static final String _BYTES="getBytes";
	private static final String _BOOLEAN="getBoolean";
	private static final String _BYTE="getByte";
	private static final String _INTEGER="getInt";
	private static final String _BIGDECIMAL="getBigDecimal";
	private static final String _STRING="getStr";
	
	
	private String field;
	private String type;
	private String canNull;
	private String key;
	private String defaultVal;
	private String extra;
	private String comment;
	private String className;
	
	
	/**
	 * 备注: string
	 * 类型: varchar(8)
	 * |
	 * 备注: string
	 * 类型: char(8)
	 * 
	 * 备注: string
	 * 类型: text
	 * 
	 */
	public String getTypeCase(){
		if(type.contains("unsigned"))return LONG;
		if(type.contains("blob"))return BYTES;
		if(type.contains("bit(1)"))return BOOLEAN;
		if(type.contains("bigint"))return LONG;
		if(type.contains("tinyint"))return BYTE;
		if(type.contains("int"))return INTEGER;
		if(type.contains("decimal"))return BIGDECIMAL;
		if(type.contains("char"))return STRING;
		if(type.contains("text"))return STRING;
		return Object.class.getName();
	}

	public StringBuffer getBaseGetBody(){
		switch (getTypeCase()) {
			case LONG:
				return getBaseBody(LONG,_LONG);
			case BYTES:
				return getBaseBody(BYTES,_BYTES);
			case BOOLEAN:
				return getBaseBody(BOOLEAN,_BOOLEAN);
			case BYTE:
				return getBaseBody(BYTE,_BYTE);
			case INTEGER:
				return getBaseBody(INTEGER,_INTEGER);
			case BIGDECIMAL:
				return getBaseBody(BIGDECIMAL,_BIGDECIMAL);
			case STRING:
				return getBaseBody(STRING,_STRING);
			default:
				return getBaseGetMothedBody();
		}
	}
	
	/**
	 * 说明
	 * @author JACK_ZHANG
	 * 创建时间  2018年5月3日 下午3:11:52	
	 */
	private StringBuffer getBaseBody(String type,String mothedName) {
		return getCommentBody()
				.append("\tpublic ").append(type).append(" get").append(firstCaps(getField())).append("(){\r\n")
				.append("\t\treturn ").append(mothedName).append("(\"").append(field).append("\");\r\n")
				.append("\t}\r\n");
	}

	public StringBuffer getDuangGetBody(){
		switch (getTypeCase()) {
			case LONG:
				return getGetBody(LONG,_LONG);
			case BYTES:
				return getGetBody(BYTES,_BYTES);
			case BOOLEAN:
				return getGetBody(BOOLEAN,_BOOLEAN);
			case BYTE:
				return getGetBody(BYTE,_BYTE);
			case INTEGER:
				return getGetBody(INTEGER,_INTEGER);
			case BIGDECIMAL:
				return getGetBody(BIGDECIMAL,_BIGDECIMAL);
			case STRING:
				return getGetBody(STRING,_STRING);
			default:
				return getGetMothedBody();
		}
	}

	/**
	 * 说明
	 * @author JACK_ZHANG
	 * 创建时间  2018年5月3日 下午3:11:52	
	 */
	private StringBuffer getGetBody(String type,String mothedName) {
		return getCommentBody()
				.append("\tpublic ").append(type).append(" get").append(firstCaps(getField())).append("(").append(type).append(" defaultVal){\r\n")
				.append("\t\t").append(type).append(" _v=").append(mothedName).append("(\"").append(field).append("\");\r\n")
				.append("\t\treturn _v==null? defaultVal:_v;\r\n")
				.append("\t}\r\n");
	}

	public StringBuffer getSetMothedBodys(){
		switch (getTypeCase()) {
			case LONG:
				return getSetBody(LONG);
			case BYTES:
				return getSetBody(BYTES);
			case BOOLEAN:
				return getSetBody(BOOLEAN);
			case BYTE:
				return getSetBody(BYTE);
			case INTEGER:
				return getSetBody(INTEGER);
			case BIGDECIMAL:
				return getSetBody(BIGDECIMAL);
			case STRING:
				return getSetBody(STRING);
			default:
				return getSetMothedBody();
		}
	}
	
	/**
	 * 说明	生成Setter方法体
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	private StringBuffer getSetBody(String type){
		return getCommentBody()
		.append("\tpublic ").append("M").append(" set").append(firstCaps(getField())).append("(").append(type).append(" val){\r\n")
		.append("\t\tset(\"").append(field).append("\", val);\r\n")
		.append("\t\treturn (M)this;\r\n")
		.append("\t}\r\n");
	}
	
	/**
	 * 说明	生成字段常量
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	private StringBuffer getCommentBody(){
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("\r\n")
		.append("\t/**\r\n");
		if(getComment()!=null&&getComment().length()>0){
			sBuffer.append("\t * ").append("备注: ").append(getComment()).append("\r\n");
		}
		if(getType()!=null&&getType().length()>0){
			sBuffer.append("\t * ").append("类型: ").append(getType()).append("\r\n");
		}
		if(getDefaultVal()!=null&&getDefaultVal().length()>0){
			sBuffer.append("\t * ").append("默认: ").append(getDefaultVal()).append("\r\n");
		}
		if(getCanNull()!=null&&getCanNull().length()>0){
			sBuffer.append("\t * ").append("为空: ").append(getCanNull()).append("\r\n");
		}
		if(getExtra()!=null&&getExtra().length()>0){
			sBuffer.append("\t * ").append("自增: ").append(getExtra()).append("\r\n");
		}
		if(getKey()!=null&&getKey().length()>0){
			sBuffer.append("\t * ").append("主键: ").append(getKey()).append("\r\n");
		}
		sBuffer.append("\t */\r\n");
		return sBuffer;
	}

	/**
	 * 说明	生成Setter方法体
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	private StringBuffer getSetMothedBody(){
		return getCommentBody()
		.append("\tpublic ").append("M").append(" set").append(firstCaps(getField())).append("(Object val){\r\n")
		.append("\t\tset(\"").append(field).append("\", val);\r\n")
		.append("\t\treturn (M)this;\r\n")
		.append("\t}\r\n");
	}
	
	/**
	 * 说明	生成字段常量
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	public StringBuffer getColumnBody(){
		return getCommentBody()
		.append("\tpublic static final String ")
		.append(getField().toUpperCase())
		.append(" = \"").append(getField()).append("\" ;\r\n");
	}
	

	/**
	 * 说明	生成无参简单的Get方法体
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	private StringBuffer getBaseGetMothedBody(){
		return getCommentBody()
		.append("\tpublic <T> T get").append(firstCaps(getField())).append("(){\r\n")
		.append("\t\treturn (T)(get(\"").append(field).append("\"));\r\n")
		.append("\t}\r\n");
	}
	

	/**
	 * 说明	生成有默认参的Get方法体
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午2:47:30
	 */
	private StringBuffer getGetMothedBody(){
		return getCommentBody()
		.append("\tpublic <T> T get").append(firstCaps(getField())).append("(Object defaultVal){\r\n")
		.append("\t\tT _v=get(\"").append(field).append("\");\r\n")
		.append("\t\treturn (T)(_v==null? defaultVal:_v);\r\n")
		.append("\t}\r\n");
	}
	
	@Override
	public String toString() {
		return 	"field: "+field+" ;  "
				+" comment: "+comment+" ;  "
				+" type: "+type+" ;  "
				+" canNull: "+canNull+" ;  "
				+" key: "+key+" ;  "
				+" defaultVal: "+defaultVal+" ;  "
				+" extra: "+extra+" ;  ";
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCanNull() {
		return canNull;
	}
	public void setCanNull(String canNull) {
		this.canNull = canNull;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * 说明
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午1:26:28	
	 * @throws SQLException 
	 */
	public TabField(ResultSet rs,String className) throws SQLException {
		this.field =rs.getString("Field");
		this.type =rs.getString("Type");
		this.canNull =rs.getString("Null");
		this.key =rs.getString("Key");
		this.defaultVal =rs.getString("Default");
		this.extra =rs.getString("Extra");
		this.comment =rs.getString("Comment");
		this.className=className;
	}
	
	/**
	 * 说明
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午1:08:34	
	 */
	public TabField(String field, String type, String canNull, String key, String defaultVal, String extra,
			String comment,String className) {
		super();
		this.field = field;
		this.type = type;
		this.canNull = canNull;
		this.key = key;
		this.defaultVal = defaultVal;
		this.extra = extra;
		this.comment = comment;
		this.className=className;
	}
	/**
	 * 说明
	 * @author JACK_ZHANG
	 * 创建时间  2018年4月24日 上午1:08:41	
	 */
	public TabField() {
	}
	
	/**
	 * 说明 字符串首字母大写
	 * @author zzjxfj@163.com
	 * @creattime 创建时间 2018年1月10日 下午11:37:59 说明 字符串首字母大写
	 */
	private String firstCaps(String string) {
		if (string.length() <= 1)
			return string.toUpperCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
	}

	/**  
	* @Title: isKey   
	* @param @return    参数  
	* @return boolean    返回类型  
	* @throws  
	* 说明：
	*/ 
	public boolean isKey() {
		// TODO Auto-generated method stub
		return getKey()!=null&&getKey().length()>0;
	}
}