package com.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.alibaba.fastjson.JSON;
import com.bean.UserInfoBean;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionEvent;

public class QQLogin extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField username;
	private JTextField serverIP;
	private JTextField myPort;
	private UserInfoBean userInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			QQLogin dialog = new QQLogin();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public QQLogin() {
		userInfo=new UserInfoBean();
		InetAddress i;
		try {
			i = InetAddress.getLocalHost();
			//设置本机IP
			userInfo.setSendIP(i.getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblqq = new JLabel("\u6B22\u8FCE\u4F7F\u7528\u804A\u5929\u7CFB\u7EDF");
		lblqq.setFont(new Font("幼圆", Font.PLAIN, 17));
		lblqq.setBounds(144, 40, 187, 21);
		contentPanel.add(lblqq);
		
		JLabel label = new JLabel("\u7528\u6237\u6635\u79F0");
		label.setBounds(93, 95, 54, 15);
		contentPanel.add(label);
		
		JLabel lblip = new JLabel("\u670D\u52A1\u5668IP");
		lblip.setBounds(93, 134, 54, 15);
		contentPanel.add(lblip);
		
		JLabel label_1 = new JLabel("\u7ED1\u5B9A\u7AEF\u53E3");
		label_1.setBounds(93, 173, 54, 15);
		contentPanel.add(label_1);
		
		username = new JTextField();
		username.setBounds(175, 92, 156, 21);
		
		
		
		contentPanel.add(username);
		username.setColumns(10);
		
		serverIP = new JTextField();
		serverIP.setBounds(175, 131, 156, 21);
		contentPanel.add(serverIP);
		serverIP.setColumns(10);
		
		myPort = new JTextField();
		myPort.setColumns(10);
		myPort.setBounds(175, 170, 156, 21);
		contentPanel.add(myPort);
		
		JButton LoginButton = new JButton("\u767B\u5F55");
		LoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//判断输入框是否为空
				if(serverIP.getText().equals("")||myPort.getText().equals("")||username.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "请完整输入！");
				}else {
					//绑定本机用户名
					userInfo.setSendUser(username.getText());
					//上线协议
					userInfo.setSendType("login");
					//绑定服务器
					try {
						Socket socket=new Socket(serverIP.getText(),8823);
						BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
						BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
						//绑定本机端口
						userInfo.setSendPort(myPort.getText());
						String userInfoStr=JSON.toJSONString(userInfo);
						//写出json字符串 在服务器转化为Map
						bw.write(userInfoStr);
						bw.newLine();
						bw.flush();
						//接受服务器处理过后的List users对象
						String list=br.readLine();
						Gson gson=new Gson();
						java.lang.reflect.Type type = new TypeToken<List>(){}.getType();
						List userList=gson.fromJson(list, type);
						//取出List中的元素
						String keyName=(String) userList.get(0);
						/*
						 * ↓这个就是users
						 */
						Map userMap=(Map) userList.get(1);
						System.out.println("keyname是"+keyName+"...userMap是"+userMap);
						//如果users的keySet里面有对应的keyName
						if(userMap.keySet().contains(keyName)) {
							//取出并重新接受自己的userInfo
							Map userMapInfo=(Map) userMap.get(keyName);
							userInfo.setSendUser((String)userMapInfo.get("sendUser"));
							userInfo.setSendIP((String)userMapInfo.get("sendIP"));
							userInfo.setSendPort((String)userMapInfo.get("sendPort"));
							userInfo.setSendType((String)userMapInfo.get("sendType"));
							userInfo.setMessageContent((String)userMapInfo.get("messageContent"));
						}
						//判断有没有被处理成line 如果是 跳转到ChatFrame
						if(userInfo.getSendType().equals("line")) {
							//跳转到聊天主页
							setVisible(false);
							ChatFrame cf=new ChatFrame();
							//把所有的userinfo传给聊天主页 刷新右边列表
							cf.setMyUserInfo(keyName, userInfo);
							cf.setServerIP(serverIP.getText());
							cf.setVisible(true);
						}else if(userInfo.getSendType().equals("0")) {
							JOptionPane.showMessageDialog(null, "重名啦");
						}
						br.close();
						bw.close();
						socket.close();
						
						
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
				
			}
		});
		LoginButton.setBounds(157, 215, 93, 23);
		contentPanel.add(LoginButton);
	}
}
