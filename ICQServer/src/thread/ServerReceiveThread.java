package thread;


import javax.swing.*;

import entity.Node;
import entity.UserLinkList;

import java.io.*;
import java.net.*;

/*
 * ������������Ϣ����
 */
public class ServerReceiveThread extends Thread {
	JTextArea textarea;
	JTextField textfield;
	JComboBox combobox;
	Node client;
	UserLinkList userLinkList;//�û�����
	
	public boolean isStop;
	
	public ServerReceiveThread(JTextArea textarea,JTextField textfield,
		JComboBox combobox,Node client,UserLinkList userLinkList){

		this.textarea = textarea;
		this.textfield = textfield;
		this.client = client;
		this.userLinkList = userLinkList;
		this.combobox = combobox;
		
		isStop = false;
	}
	
	public void run(){
		//�������˷����û����б�
		sendUserList();
		
		while(!isStop && !client.getSocket().isClosed()){
			try{
				String type = (String)client.getInput().readObject();
				
				if(type.equalsIgnoreCase("������Ϣ")){
					String toSomebody = (String)client.getInput().readObject();
					String status  = (String)client.getInput().readObject();
					String action  = (String)client.getInput().readObject();
					String message = (String)client.getInput().readObject();
					
					String msg = client.getUserName()
							+" "+ action
							+ "�� "
							+ toSomebody 
							+ " ˵ : "
							+ message
							+ "\n";
					if(status.equalsIgnoreCase("���Ļ�")){
						msg = " [���Ļ�] " + msg;
					}
					textarea.append(msg);
					
					if(toSomebody.equalsIgnoreCase("������")){
						sendToAll(msg);//�������˷�����Ϣ
					}
					else{
						try{
							client.getOutput().writeObject("������Ϣ");
							client.getOutput().flush();
							client.getOutput().writeObject(msg);
							client.getOutput().flush();
						}
						catch (Exception e){
							//System.out.println("###"+e);
						}
						
						Node node = userLinkList.findUser(toSomebody);
						
						if(node != null){
							node.getOutput().writeObject("������Ϣ"); 
							node.getOutput().flush();
							node.getOutput().writeObject(msg);
							node.getOutput().flush();
						}
					}
				}
				else if(type.equalsIgnoreCase("�û�����")){
					Node node = userLinkList.findUser(client.getUserName());
					userLinkList.delUser(node);
					
					String msg = "�û� " + client.getUserName() + " ����\n";
					int count = userLinkList.getCount();

					combobox.removeAllItems();
					combobox.addItem("������");
					int i = 0;
					while(i < count){
						node = userLinkList.findUser(i);
						if(node == null) {
							i ++;
							continue;
						} 
			
						combobox.addItem(node.getUserName());
						i++;
					}
					combobox.setSelectedIndex(0);

					textarea.append(msg);
					textfield.setText("�����û�" + userLinkList.getCount() + "��\n");
					
					sendToAll(msg);//�������˷�����Ϣ
					sendUserList();//���·����û��б�,ˢ��
					
					break;
				}
			}
			catch (Exception e){
				//System.out.println(e);
			}
		}
	}
	
	/*
	 * �������˷�����Ϣ
	 */
	public void sendToAll(String msg){
		int count = userLinkList.getCount();
		
		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			try{
				node.getOutput().writeObject("������Ϣ");
				node.getOutput().flush();
				node.getOutput().writeObject(msg);
				node.getOutput().flush();
			}
			catch (Exception e){
				//System.out.println(e);
			}
			
			i++;
		}
	}
	
	/*
	 * �������˷����û����б�
	 */
	public void sendUserList(){
		String userlist = "";
		int count = userLinkList.getCount();

		int i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			}
			
			userlist += node.getUserName();
			userlist += '\n';
			i++;
		}
		
		i = 0;
		while(i < count){
			Node node = userLinkList.findUser(i);
			if(node == null) {
				i ++;
				continue;
			} 
			
			try{
				node.getOutput().writeObject("�û��б�");
				node.getOutput().flush();
				node.getOutput().writeObject(userlist);
				node.getOutput().flush();
			}
			catch (Exception e){
				//System.out.println(e);
			}
			i++;
		}
	}
}
