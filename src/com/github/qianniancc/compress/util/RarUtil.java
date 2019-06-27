package com.github.qianniancc.compress.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.github.qianniancc.compress.dialog.Encrypted;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * RAR格式压缩文件解压工具类 不支持RAR格式压缩 支持中文,支持RAR压缩文件密码 依赖jar包 commons-io.jar
 * commons-logging.jar java-unrar-decryption-supported.jar gnu-crypto.jar
 * 
 * @author ninemax
 */
public class RarUtil {

	public static final String SEPARATOR = File.separator;

	
	
	/**
	 * 解压指定RAR文件到当前文件夹
	 * 
	 * @param srcRar
	 *            指定解压
	 * @param password
	 *            压缩文件时设定的密码
	 * @throws IOException
	 */
	public static void unrar(String srcRar, String password) throws IOException {
		unrar(srcRar, null, password);
	}

	/**
	 * 解压指定的RAR压缩文件到指定的目录中
	 * 
	 * @param srcRar
	 *            指定的RAR压缩文件
	 * @param destPath
	 *            指定解压到的目录
	 * @param password
	 *            压缩文件时设定的密码
	 * @throws IOException
	 */
	public static void unrar(String srcRar, String destPath, String password) throws IOException {
		File srcFile = new File(srcRar);
		if (!srcFile.exists()) {
			return;
		}
		if (null == destPath || destPath.length() == 0) {
			unrar(srcFile, srcFile.getParent(), password);
			return;
		}
		unrar(srcFile, destPath, password);
	}

	/**
	 * 解压指定RAR文件到当前文件夹
	 * 
	 * @param srcRarFile
	 *            解压文件
	 * @param password
	 *            压缩文件时设定的密码
	 * @throws IOException
	 */
	public static void unrar(File srcRarFile, String password) throws IOException {
		if (null == srcRarFile || !srcRarFile.exists()) {
			throw new IOException("指定文件不存在.");
		}
		unrar(srcRarFile, srcRarFile.getParent(), password);
	}

	/**
	 * 解压指定RAR文件到指定的路径
	 * 
	 * @param srcRarFile
	 *            需要解压RAR文件
	 * @param destPath
	 *            指定解压路径
	 * @param password
	 *            压缩文件时设定的密码
	 * @throws IOException
	 */
	public static void unrar(File srcRarFile, String destPath, String password) throws IOException {
		if (null == srcRarFile || !srcRarFile.exists()) {
			throw new IOException("指定压缩文件不存在.");
		}
		if (!destPath.endsWith(SEPARATOR)) {
			destPath += SEPARATOR;
		}
		Archive archive = null;
		OutputStream unOut = null;
		try {
			archive = new Archive(srcRarFile, password, false);
			if(!archive.isPass()){
				archive.close();
				String p=new Encrypted().createDialog();
				if(p==null || p.equals("")){
					return;
				}else{
					throw new IOException("密码错误");
				}
			}
			FileHeader fileHeader = archive.nextFileHeader();
			while (null != fileHeader) {
				if (!fileHeader.isDirectory()) {
					String destFileName = "";
					String destDirName = "";
					if (SEPARATOR.equals("/")) { 
						destFileName = (destPath + fileHeader.getFileNameString()).replaceAll("\\\\", "/");
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
					} else { 
						destFileName = (destPath + fileHeader.getFileNameString()).replaceAll("/", "\\\\");
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
					}
					
					File dir = new File(destDirName);
					if (!dir.exists() || !dir.isDirectory()) {
						dir.mkdirs();
					}
					
					unOut = new FileOutputStream(new File(destFileName));
					try {
						archive.extractFile(fileHeader, unOut);
					} catch (RarException e) {
						if(e.getMessage().equals("unkownError")){
							archive.close();
							String p=new Encrypted().createDialog();
							if(p==null){
								throw new IOException("exit");
							}else if(p.equals("")){
								throw new IOException("密码为空");
							}else{
								archive=new Archive(srcRarFile, p, false);
								try {
									archive.extractFile(archive.nextFileHeader(), unOut);
								} catch (RarException e1) {
									archive.close();
									throw new IOException("密码错误");
								}
								
							}
						}
					}
					unOut.flush();
					unOut.close();
				}
				fileHeader = archive.nextFileHeader();
			}
			archive.close();
		} catch (RarException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(unOut);
		}
	}
	
	public static List<String> openrar(File srcRarFile, String password) throws IOException {
		List<String> files=new ArrayList<>();
		if (null == srcRarFile || !srcRarFile.exists()) {
			throw new IOException("指定压缩文件不存在.");
		}
		Archive archive = null;
		try {
			archive = new Archive(srcRarFile, password, false);
			if(archive.isEncrypted()){
				archive.close();
				String p=new Encrypted().createDialog();
				if(p==null || p.equals("")){
					return null;
				}else{
					throw new IOException("密码错误");
				}
			}
			FileHeader fileHeader = archive.nextFileHeader();
			while (null != fileHeader) {
				if (!fileHeader.isDirectory()) {
					if(fileHeader.isEncrypted())
						files.add(fileHeader.getFileNameString()+" *");
					else
						files.add(fileHeader.getFileNameString());
				}
				fileHeader = archive.nextFileHeader();
			}
			archive.close();
		} catch (RarException e) {
			e.printStackTrace();
		}
		return files;
	}
	
	
}