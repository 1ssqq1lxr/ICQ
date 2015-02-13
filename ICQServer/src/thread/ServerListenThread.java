package thread;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import entity.Node;
import entity.UserLinkList;

import java.io.*;
import java.net.*;

/*
 * ����˵�������
 */
public class ServerListenThread extends Thread {
	ServerSocket server;
	
	JComboBox combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;//�û�����
	
	Node client;
	ServerReceiveThread recvThread;
	
	public boolean isStop;

	/*
	 * �������˵��û�����������������
	 */
	public ServerListenThread(ServerSocket server,JComboBox combobox,
		JTextArea textarea,JTextField textfield,UserLinkList userLinkList){

		this.server = server;
		this.combobox = combobox;
		this.textarea = textarea;
		this.textfield = textfield;
		this.userLinkList = userLinkList;
		
		isStop = false;
	}
	
	public void run(){
		while(!isStop && !server.isClosed()){
			try{
				client = new Node();
				client.setSocket(server.accept());
				client.setOutput(new ObjectOutputStream(client.getSocket().getOutputStream()));
				client.getOutput().flush();
				client.setInput(new ObjectInputStream(client.getSocket().getInputStream()));
				client.setUserName((String)client.getInput().readObject());
				
				//��ʾ��ʾ��Ϣ
				combobox.addItem(client.getUserName());
				userLinkList.addUser(client);
				textarea.append("�û� " + client.getUserName() + " ����" + "\n");
				textfield.setText("�����û�" + userLinkList.getCount() + "��\n");
				
				recvThread = new ServerReceiveThread(textarea,textfield,
					combobox,client,userLinkList);
				recvThread.start();
			}
			catch(Exception e){
			}
		}
	}
}

