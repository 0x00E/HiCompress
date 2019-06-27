package com.github.qianniancc.compress.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.qianniancc.compress.frame.Main;
import com.github.qianniancc.compress.util.RarUtil;
import com.github.qianniancc.compress.util.ZipUtil;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Unzip extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private File srcZipFile1;

	public Unzip(File srcZipFile) {
		srcZipFile1=srcZipFile;
		setModal(true);
		setTitle("解压到");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("解压到：");
		lblNewLabel.setBounds(50, 58, 54, 15);
		contentPanel.add(lblNewLabel);
		if(srcZipFile1==null){
			new Open();
			srcZipFile1=Main.getSrcZipFile();
		}
		textField = new JTextField(srcZipFile1.getAbsolutePath().substring(0, srcZipFile1.getAbsolutePath().lastIndexOf(".")));
		textField.setBounds(50, 82, 300, 21);
		contentPanel.add(textField);
		textField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("确定");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(srcZipFile1.getName().endsWith(".zip"))
							ZipUtil.unzipFile(srcZipFile1, textField.getText());
						else if(srcZipFile1.getName().endsWith(".rar"))
							try {
								RarUtil.unrar(srcZipFile1, textField.getText(),null);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("取消");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setLocationRelativeTo(Main.getInstance());
		setVisible(true);
	}
}
