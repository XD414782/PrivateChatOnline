package com.bean;

import java.util.HashMap;
import java.util.Map;


public class UserInfoBean {
	/*
	 * 包括发送协议、发送用户名、发送用户IP地址、发送用户端口号，用户名Map容器，消息接收用户名，发送消息内容
	 */
	//上线协议
	private String sendType="";
	//本机IP
	private String sendIP="";
	//本机端口
	private String sendPort;
	//用户名
	private String sendUser="";
	//消息接收用户名
	private Map receiveUser=null;
	//发送消息内容
	private String messageContent="";
	
	
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
	public String getSendIP() {
		return sendIP;
	}
	public void setSendIP(String sendIP) {
		this.sendIP = sendIP;
	}
	public String getSendPort() {
		return sendPort;
	}
	public void setSendPort(String sendPort) {
		this.sendPort = sendPort;
	}
	public String getSendUser() {
		return sendUser;
	}
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
	public Map getReceiveUser() {
		return receiveUser;
	}
	public void setReceiveUser(Map receiveUser) {
		this.receiveUser = receiveUser;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	@Override
	public String toString() {
		return "UserInfoBean [sendType=" + sendType + ", sendIP=" + sendIP + ", sendPort=" + sendPort + ", sendUser="
				+ sendUser + ", receiveUser=" + receiveUser + ", messageContent=" + messageContent + "]";
	}
	
	
	
}