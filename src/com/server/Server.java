package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.Skeleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.alibaba.fastjson.JSON;
import com.bean.UserInfoBean;
import com.google.gson.Gson;

public class Server extends Thread {
	private UserInfoBean userInfo;
	//存放用户名的Map容器
	private Map<String,UserInfoBean> users=new HashMap<String, UserInfoBean>();
	private List userList=new ArrayList();
	public static void main(String[] args) {
		Server server=new Server();
		server.start();
	}
	@Override
	public void run() {
		try {
			System.out.println("服务器已开启");
			ServerSocket server=new ServerSocket(8823);
			while(true) {
				Socket socket=server.accept();
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				BufferedWriter bw=null;
				//收到字符串
				String receive=br.readLine();
				//deseralizable
				userInfo=JSON.parseObject(receive,UserInfoBean.class);
				if(userInfo.getSendType().equals("login")) {
					//如果不重名
					if(!users.containsKey(userInfo.getSendUser())) {
						//登陆成功 处理为line
						userInfo.setSendType("line");
						//清空List
						if(userList.size()!=0) {
							userList.clear();
						}
						users.put(userInfo.getSendUser(),userInfo);
						//用于添加{String userName,Map users}对象
						userList.add(userInfo.getSendUser());
						userList.add(users);
						
						System.out.println("新增用户成功");
						System.out.println("名字是"+userInfo.getSendUser());
						//传递回QQLogin 用于判断是否是line 再跳转
						bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
						Gson gson=new Gson();
						String sendUsers=gson.toJson(userList);
						System.out.println("sendUsers是"+sendUsers);
						bw.write(sendUsers);
						bw.flush();
					}else {//重名
						userInfo.setSendType("0");
						//清空List
						if(userList.size()!=0) {
							userList.clear();
						}
						users.put(userInfo.getSendUser(),userInfo);
						//用于添加{String userName,Map users}对象
						userList.add(userInfo.getSendUser());
						userList.add(users);
						System.out.println("重名啦");
						//重名则移除
						users.remove(userInfo.getSendUser());
						System.out.println("名字是"+userInfo.getSendUser());
						//传递回QQLogin 
						bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
						Gson gson=new Gson();
						String sendUsers=gson.toJson(userList);
						System.out.println("sendUsers是"+sendUsers);
						bw.write(sendUsers);
						bw.flush();
					}
				}else if(userInfo.getSendType().equals("line")) {
					//直接传输Map users
					bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
					Gson gson=new Gson();
					String json=gson.toJson(users);
					bw.write(json);
					bw.flush();
				}else if(userInfo.getSendType().equals("send")) {
					userInfo.setSendType("line");
					//获取发送者和接收者的信息
					Map receiveUserMap=userInfo.getReceiveUser();
					System.out.println("111111"+receiveUserMap);
					String receiveUserName=(String) receiveUserMap.get("sendUser");
					String sendIP=userInfo.getSendIP();
					String sendPort=userInfo.getSendPort();
					String sendMessage=userInfo.getMessageContent()+"\n";
					String sendUserName=(String)userInfo.getSendUser();
					String receiveIP=(String)receiveUserMap.get("sendIP");
					String receivePort=(String)receiveUserMap.get("sendPort");
//					//如果是发给自己
//					
					//开启一个socket传给接收者的ChatFram接收消息的服务器
					Socket userSendUser=new Socket(receiveIP,Integer.parseInt(receivePort));
					bw=new BufferedWriter(new OutputStreamWriter(userSendUser.getOutputStream(),"utf-8"));
//					String json=JSON.toJSONString(userInfo);
					String str="【"+receiveUserName+"】对你说："+sendMessage;
					bw.write(str);
					bw.newLine();
					bw.flush();
					userSendUser.close();
					
				}
				br.close();
				bw.close();
				socket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
