package com.github.qianniancc.compress.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.qianniancc.compress.frame.Main;
import com.github.qianniancc.compress.interf.Start;

public class About extends JDialog {


	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	public About() {
		setResizable(false);
		setModal(true);
		
		setTitle("关于 \""+Start.NAME+"\"");
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblNewLabel = new JLabel("<html>\r\n<p>\""+Start.NAME+"\"是一款开源压缩软件</p>\r\n<hr>\r\n<font color=red>本软件使用GPL 3.0协议发布<font>\r\n</html>");
			lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
			contentPanel.add(lblNewLabel);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("确定");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		this.setLocationRelativeTo(Main.getInstance());
		this.setVisible(true);
	}

}
