package com.github.qianniancc.compress.util;

import java.io.File;

public class FileUtil {
	
	public static void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();  
	}  
	  
	// 用 Java 调用windows系统的exe文件，比如notepad，calc之类  
	public static void openWindowsExe(String command) {  
	    final Runtime runtime = Runtime.getRuntime();  
	    try {  
	        runtime.exec(command);  
	    } catch (final Exception e) {  
	        System.out.println("Error win exec!");  
	    }  
	}  
	  
	// 调用其他的可执行文件，例如：自己制作的exe，或是 下载 安装的软件.  
	public static void openExe(File f) {  
	    final Runtime runtime = Runtime.getRuntime();  
	  
	    try {  
	        runtime.exec(f.getAbsolutePath());  
	  
	    } catch (final Exception e) {  
	        System.out.println("Error exec!");  
	    }  
	}  
	  
	// 打开其他任意格式的文件，比如txt,word等  
	public static void openFile(File f) {  
	    final Runtime runtime = Runtime.getRuntime();   
	    final String cmd = "rundll32 url.dll FileProtocolHandler file://"+f.getAbsolutePath();  
	    try {  
	        runtime.exec(cmd);  
	    } catch (final Exception e) {  
	        System.out.println("Error exec!");  
	    }  
	}

	public static void openFile(String string) {
		openFile(new File(string));
	}  
}

