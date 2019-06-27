package com.github.qianniancc.compress.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;

import com.github.qianniancc.compress.frame.Main;

public class DirWatch {
	
	private static HashSet<WatchThread> watchList=new HashSet<>();
	
	public static HashSet<WatchThread> getWatchList() {
		return watchList;
	}
	
	
	public static void createWatch(File f){
		WatchThread wt=new WatchThread();
		watchList.add(wt);
		wt.start(f);
		
	}


	public static void stopAllWatch() {
		for(WatchThread t:watchList){
			t.setStop();
		}
	}
	
}

class WatchThread extends Thread{
	

	File f;
	boolean isStop;
	
	public synchronized void start(File f) {
		this.f=f;
		super.start();
	}
	
	
	public void setStop() {
		isStop=true;
	}


	@SuppressWarnings("unused")
	@Override
	public void run() {
		try {

			WatchService service = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(f.getAbsolutePath());
			path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			WatchKey watchKey;
			out:
			do {
				watchKey = service.take();
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					if(isStop)return;
					Main.reload();
				}

			} while (watchKey.reset());
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}