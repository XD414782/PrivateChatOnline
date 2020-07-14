package com.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.alibaba.fastjson.JSON;
import com.bean.UserInfoBean;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatFrame extends JDialog {

	private final JPanel message = new JPanel();
	private JList jList;
	private JTextField username;
	private JTextField messageText;
	private Map userMap;
	private String myName="";
	private String serverIP="";
	private UserInfoBean myUserInfo;
	private String myPort;
	JTextArea textArea;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ChatFrame dialog = new ChatFrame();
			dialog.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ChatFrame() {
		setBounds(100, 100, 753, 560);
		getContentPane().setLayout(new BorderLayout());
		message.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(message, BorderLayout.CENTER);
		message.setLayout(null);
		
		jList = new JList();
		jList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// 获取当前用户选择接收信息的用户
				Object name = jList.getSelectedValue();
				if (username != null) {
					// 选择的用户名设置接收用户文本框中
					username.setText(name + "");
				}
			}
		});
		jList.setBounds(545, 10, 183, 506);
		message.add(jList);
		
		JLabel label = new JLabel("\u63A5\u6536\u7528\u6237\u540D\uFF1A");
		label.setBounds(10, 421, 113, 15);
		message.add(label);
		
		username = new JTextField();
		username.setBounds(104, 417, 165, 21);
		message.add(username);
		username.setColumns(10);
		
		messageText = new JTextField();
		messageText.setBounds(10, 466, 406, 21);
		message.add(messageText);
		messageText.setColumns(10);
		
		JButton sendMessage = new JButton("\u53D1\u9001");
		sendMessage.setBounds(426, 465, 93, 23);
		sendMessage.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				BufferedWriter bw = null;
				Socket socket;
				try {
					socket = new Socket(serverIP,8823);
					bw= new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
					//从username获得要发送的用户名
					String receiveUser=username.getText();
					Map user=(Map) userMap.get(receiveUser);
					//把自己的userinfo的sendType改成send
					myUserInfo.setSendType("send");
					String msg = messageText.getText();
					myUserInfo.setMessageContent(msg);
					myUserInfo.setReceiveUser(user);
					//写出到服务器
					String json=JSON.toJSONString(myUserInfo);
					System.out.println("发送消息内容:"+json);
					//设置文本样式
					SimpleAttributeSet att = new SimpleAttributeSet();
					StyleConstants.setForeground(att, Color.RED);
					StyleConstants.setFontSize(att, 18);
					String msgStr="我对【"+receiveUser+"】说："+msg+"\n";
					// 获取文本域中文档
					Document docs = textArea.getDocument();
					docs.insertString(docs.getLength(), msgStr, att);
					// 更新光标位置
					textArea.setCaretPosition(docs.getLength());
					bw.write(json);
					bw.flush();
					bw.close();
					socket.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		message.add(sendMessage);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 521, 376);
		message.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
	}
	public void setServerIP(String serverIP) {
		this.serverIP=serverIP;
		//调用线程 因为没写构造方法 这个方法所在的是最后调用的方法 在这里调用也一样
		RefreshList thread=new RefreshList();
		thread.start();
		ClientServerSocketThread messageThread=new ClientServerSocketThread();
		messageThread.start();
	}
	public void setMyUserInfo(String myName,UserInfoBean myUserInfo) {
		this.myName=myName;
		//设置标题
		this.setTitle(myName);
		this.myUserInfo=myUserInfo;
		//设置端口
		myPort=this.myUserInfo.getSendPort();
	}
	//刷新消息
	class ClientServerSocketThread extends Thread {

		@Override
		public void run() {
			// 创建serverSocket
			try {
				ServerSocket ssk = new ServerSocket(Integer.parseInt(myPort));
				// 循环等待接收消息
				while (true) {
					Socket sk = ssk.accept();
					// 套接字获取接收的内容
					BufferedReader br = new BufferedReader(
							new InputStreamReader(sk.getInputStream(),"utf-8"));
					// 获取在服务器处理过的消息内容
					String msgStr = br.readLine();
					br.close();
					sk.close();
					//设置字体样式
					SimpleAttributeSet att = new SimpleAttributeSet();
					StyleConstants.setForeground(att, Color.RED);
					StyleConstants.setFontSize(att, 18);
					// 获取文本域中文档
					Document docs = textArea.getDocument();
					docs.insertString(docs.getLength(), msgStr, att);
					// 更新光标位置
					textArea.setCaretPosition(docs.getLength());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	//刷新右侧列表
	class RefreshList extends Thread {
		Socket socket;
		@Override
		public void run() {
			BufferedWriter bw = null;
			BufferedReader br = null;
			while(true) {
				try {
					socket= new Socket(serverIP,8823);
					bw= new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
					br=new BufferedReader(
							new InputStreamReader(socket.getInputStream(),"utf-8"));
					//发送myuserinfo给服务器
					Gson gson=new Gson();
					myUserInfo.setSendType("line");
					String json=gson.toJson(myUserInfo);
					bw.write(json);
					bw.newLine();
					bw.flush();
					
					//从服务器获得字符串
					String receiveStr=br.readLine();
					userMap=JSON.parseObject(receiveStr,Map.class);
					//声明接收容器
					Vector usersList = new Vector();
					//遍历keyset
					Iterator it=userMap.keySet().iterator(); 
					while (it.hasNext()){
						//从userMap获取别的用户的userinfo
						usersList.add(it.next());
					}
					jList.setListData(usersList);
					//每10秒刷新一次
					Thread.sleep(10000);
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally{
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						bw.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						socket.close();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
				}
				
			}
		}
	}
}
