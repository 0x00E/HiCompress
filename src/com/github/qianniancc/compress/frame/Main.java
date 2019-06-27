package com.github.qianniancc.compress.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import com.github.qianniancc.compress.dialog.About;
import com.github.qianniancc.compress.dialog.Open;
import com.github.qianniancc.compress.dialog.Unzip;
import com.github.qianniancc.compress.interf.Start;
import com.github.qianniancc.compress.util.DirWatch;
import com.github.qianniancc.compress.util.FileUtil;
import com.github.qianniancc.compress.util.RarUtil;
import com.github.qianniancc.compress.util.StringUtil;
import com.github.qianniancc.compress.util.ZipUtil;

public class Main extends JFrame implements ActionListener {

	private static File temp=new File("temp");
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static Main instance;
	private static JList<Object> list;
	private static String titleLeft;
	private static File srcZipFile;
	private static DefaultListModel<Object> dlm=new DefaultListModel<>();
	private static File f=FileSystemView.getFileSystemView().getHomeDirectory();
	private static JLabel down = new JLabel(f.getAbsolutePath());
	private static JMenuItem openWindowsDir;
	private static JMenuItem createZip;
	
	public static Main getInstance() {
		if(instance==null)instance=new Main();
		return instance;
	}
	
	public static File getF() {
		return f;
	}
	
	public static void reload(){
		if(f.isDirectory())
			changeDir(f);
		else{
			open(f);
		}
	}
	
	public void drag(){
       new DropTarget(this,DnDConstants.ACTION_LINK,new DropTargetAdapter(){
           @SuppressWarnings("unchecked")
           @Override
           public void drop(DropTargetDropEvent dtde){
               try{
                   if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                       dtde.acceptDrop(DnDConstants.ACTION_LINK);
                       List<File>list=(List<File>)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                      if(list.size()==1){
                    	  open(list.get(0));
                      }else{
                    	  File pf=list.get(0).getParentFile();
                    	  createZip(pf.getAbsoluteFile()+File.separator+pf.getName()+".zip",list);
                      }
                      dtde.dropComplete(true);
                    }
                   else dtde.rejectDrop();   
               }catch(Exception e){e.printStackTrace();} 
           }
        });
    }
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title+" - "+Start.NAME);
	}
	
	public static void startWatch(File dir){
		DirWatch.stopAllWatch();
		DirWatch.createWatch(dir);
	}
	
	public static void changeDir(File dir){
		FileUtil.deleteAllFilesOfDir(temp);
		startWatch(dir);
		f=dir;
		if(f.isDirectory()){
			createZip.setText("使用选中的文件创建"+f.getName()+".zip");
			createZip.setEnabled(true);
		}else{
			createZip.setEnabled(false);
		}
		dlm.clear();
		if(dir.list()!=null)
		for(String file:dir.list()){
			dlm.addElement(file);
		}
		list.setModel(dlm);
		getInstance().setTitle(dir.getName());
		down.setText(dir.getAbsolutePath());
	}
	
	private static void changeDir(List<String> entryList) {
		DirWatch.stopAllWatch();
		dlm.clear();
		for(String file:entryList){
			dlm.addElement(file);
		}
		list.setModel(dlm);
	}
	
	public static void changeDir(File[] dirs){
		DirWatch.stopAllWatch();
		dlm.clear();
		for(File file:dirs){
			dlm.addElement(file);
		}
		list.setModel(dlm);
		getInstance().setTitle("根目录");
		down.setText("根目录");
	}

	public Main() {
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FileUtil.deleteAllFilesOfDir(temp);
			}
		});

		setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.ico"));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("文件(F)");
		menuBar.add(mnNewMenu);
		
		JMenuItem openZIP = new JMenuItem("打开压缩文件(O)");
		openZIP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Open();
			}
		});
		mnNewMenu.add(openZIP);
		
		JMenuItem exit = new JMenuItem("退出(E)");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnNewMenu.add(exit);
		
		JMenu command = new JMenu("命令(C)");
		menuBar.add(command);
		
		JMenuItem unzip = new JMenuItem("解压到");
		unzip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Unzip(getSrcZipFile());
			}
		});
		command.add(unzip);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("上级目录");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					if(new File(down.getText()).getAbsolutePath().equals(f.getAbsolutePath()))
						changeDir(new File(f.getParent()));
					else
						changeDir(f);
				} catch (Exception e1) {
					changeDir(File.listRoots());
				}
			}
		});
		command.add(mntmNewMenuItem);
		
		JMenu handle = new JMenu("操作(O)");
		menuBar.add(handle);
		
		JMenuItem delete = new JMenuItem("删除");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Object> selectList=list.getSelectedValuesList();
				int k=JOptionPane.showConfirmDialog(instance, "确定要将选中的文件/文件夹删除吗？", "确认信息", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(k==JOptionPane.OK_OPTION){
					for(Object obj:selectList){
						new File(f+File.separator+obj.toString()).delete();
					}
				}
			}
		});
		handle.add(delete);
		
		JMenuItem clear = new JMenuItem("手动清理缓存");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileUtil.deleteAllFilesOfDir(temp);
			}
		});
		handle.add(clear);
		
		JMenu help = new JMenu("帮助(V)");
		menuBar.add(help);
		
		JMenuItem about = new JMenuItem("关于");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new About();
			}
		});
		help.add(about);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		titleLeft=f.getName();
		list = new JList<Object>(f.list());
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(list.getSelectedValuesList().size()==0 || f.isFile()){
					createZip.setEnabled(false);
				}else{
					createZip.setEnabled(true);
				}
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					if(f.isFile())f=f.getParentFile();
					File theFile = null;
					File downFile=new File(down.getText());
					if(down.getText().equals("根目录")){
						theFile=new File(list.getSelectedValue().toString());
					}
					else if(downFile.isFile() ){
						File path=new File("temp"+File.separator+downFile.getName());
						String name=list.getSelectedValue().toString();
						if(!path.exists()){
							path.mkdir();
							
						}
						if(downFile.getName().endsWith(".zip")){
							ZipUtil.unzipFile(downFile, "temp"+File.separator+downFile.getName());
						}else if(downFile.getName().endsWith(".rar")){
							try {
								RarUtil.unrar(downFile, "temp"+File.separator+downFile.getName(), null);
							} catch (IOException e1) {
								if(!e1.getMessage().equals("exit"))
								JOptionPane.showMessageDialog(null, e1.getMessage(),"提示",JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						if(name.endsWith(" *"))name=name.substring(0, name.length()-2);
						FileUtil.openFile(path+File.separator+name);
						return;
					}else{
						theFile=new File(f+File.separator+list.getSelectedValue().toString());
					}
					String extraName=theFile.getName().substring(theFile.getName().lastIndexOf(".")+1).toLowerCase();
					
					if(theFile.isDirectory()){
						changeDir(theFile);
					}else{
						if(StringUtil.equals(extraName,"zip","rar")){
							open(theFile);
						}
						else FileUtil.openFile(theFile);
						
					}
					
				}
			}
		});
		
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.SOUTH);
		
		
		toolBar.add(down);
		
		JScrollPane scrollPane = new JScrollPane(list);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);
		
		openWindowsDir = new JMenuItem("打开文件存放目录");
		openWindowsDir.addActionListener(this);
		popupMenu.add(openWindowsDir);
		
		createZip = new JMenuItem("使用选中的文件创建"+f.getName()+".zip");
		createZip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<File> fileList=new ArrayList<>();
				for(Object obj:list.getSelectedValuesList()){
					fileList.add(new File(Main.getF()+File.separator+obj.toString()));
				}
				String zipPath=Main.getF().getAbsolutePath()+File.separator+Main.getF().getName()+".zip";
				createZip(zipPath,fileList);
			}
		});
		createZip.setEnabled(false);
		popupMenu.add(createZip);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		setTitle(titleLeft);
		
		DirWatch.createWatch(f);
		
		drag();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}
	
	public static void createZip(String zipPath, List<File> fileList){
		ZipUtil.zipFile(zipPath, fileList);
		try {
			Runtime.getRuntime().exec("explorer /select, "+zipPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void openRar(File srcFile) {
		f=srcFile;
		setSrcZipFile(srcFile);
		List<String> entryList = null;
		try {
			entryList = RarUtil.openrar(srcZipFile, null);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(),"提示",JOptionPane.ERROR_MESSAGE);
		}
		if(entryList==null)return;
		changeDir(entryList);
		setFileInfo(srcFile.getName(),srcFile.getAbsolutePath());
	}

	public static void openZip(File srcFile) {
		f=srcFile;
		setSrcZipFile(srcFile);
		List<String> entryList=ZipUtil.openzipFile(srcFile);
		changeDir(entryList);
		setFileInfo(srcFile.getName(),srcFile.getAbsolutePath());
	}
	
	public static void setFileInfo(String title,String downText){
		instance.setTitle(title);
		down.setText(downText);
	}

	

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src.equals(openWindowsDir)){
			try {  

		           Runtime.getRuntime().exec("explorer /root," + down.getText());  
	       } catch (IOException ioe) {  
	           ioe.printStackTrace();  
	       }  
		}
	}

	public static File getSrcZipFile() {
		return srcZipFile;
	}

	public static void setSrcZipFile(File srcZipFile) {
		Main.srcZipFile = srcZipFile;
	}

	public static void open(File srcFile) {
		if(srcFile.getName().endsWith(".zip")){
			openZip(srcFile);
		}else if(srcFile.getName().endsWith(".rar")){
			openRar(srcFile);
		}
	}
}
