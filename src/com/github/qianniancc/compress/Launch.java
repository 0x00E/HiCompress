package com.github.qianniancc.compress;

import javax.swing.UIManager;

import com.github.qianniancc.compress.frame.Main;

public class Launch {
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		Main.getInstance();
	}

}
