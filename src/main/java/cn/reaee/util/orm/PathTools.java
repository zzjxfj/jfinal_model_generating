package cn.reaee.util.orm;

import java.io.File;

public class PathTools {
	private final static String mavenPath=new File("src/main/java").getAbsolutePath()+"/";
	private final static String antPath=new File("src").getAbsolutePath()+"/";
	private final static boolean isMaven=new File("pom.xml").isFile();

	public static String getPath(){
		return isMaven ? mavenPath:antPath;
	}
}
