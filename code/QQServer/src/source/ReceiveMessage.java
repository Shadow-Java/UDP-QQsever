package source;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import util.Fileup;
import util.Message;
import util.Translate;
import util.User;

//���������պͷ�����Ϣ�߳�
public class ReceiveMessage extends Thread{
	private DatagramSocket serverSocket;//�������׽���
	private DatagramPacket packet;//����
	public static List<User> userList=new ArrayList<User>();//�û��б�
	public static List<User> lineuserList=new ArrayList<User>();//�����û��б�
	public static List<Fileup> upfileList=new ArrayList<Fileup>();//�ļ��б�
	private byte[] data=new byte[8096];//8k�ֽ�����
	private ServerUI parentUI;//��Ϣ����
	
	//���캯��
	public ReceiveMessage(DatagramSocket socket,ServerUI parentUI)
	{
		this.serverSocket=socket;
		this.parentUI=parentUI;
	}
	//�߳��������
	public void run()
	{
		while(true)
		{
			try {
				System.out.println("�������߳���������....");
				packet=new DatagramPacket(data,data.length);//�������ձ���
				serverSocket.receive(packet);
				//���յ�������ת��Ϊ��Ϣ����
				Message msg=new Message();
				msg=(Message)Translate.ByteToObject(packet.getData());
				String userId=msg.getUserId();//�ӵ�ǰ���յ�����Ϣ����û�ID
				String userpasswd=msg.getPassword();//�ӵ�ǰ���յ�����Ϣ����û�����
				System.out.println("�������յ���Ϣ����"+msg.getType());
				Message backMsg=new Message();
				if(msg.getType().equalsIgnoreCase("M_REGISTER"))//������û�ע����Ϣ
				{
					User user=new User();
					user.setUserId(userId);//�����û�id
					user.setPasswd(userpasswd);//�����û�����
					user.setPacket(packet);//�����û�����
					//System.out.println(user.getUserId());
					if(userList.contains(user))//����Ѵ��ڴ��û�
					{
						//System.out.println("������������ϢM_RFAILURE");
						backMsg.setType("M_RFAILURE");
						parentUI.printtext.append(userId+"�û���ע���!\n");
					}
					else//������û�δ��ע��
					{
						backMsg.setType("M_RSUCCESS");
						//System.out.println("������������ϢM_RSUCCESS");
						userList.add(user);//���û������û��б�
						//System.out.println(userList.get(0).getUserId());
						parentUI.printtext.append(userId+"�û�ע��ɹ��������û��б�!\n");
					}
					byte[] buf=Translate.ObjectToByte(backMsg);
                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//��ע���û����͵ı���
                    serverSocket.send(backPacket); //����   
				}
				
				else if(msg.getType().equalsIgnoreCase("M_LOGIN"))//������û���¼��Ϣ
				{
					User user=new User();
					user.setUserId(userId);//�����û�id
					user.setPasswd(userpasswd);//�����û�����
					user.setPacket(packet);//�����û�����
					int containindex=-1;
					int linecontain=-1;
					for(int i=0;i<userList.size();i++)//�ҵ��û��б��е��û�
					{
						if(userList.get(i).getUserId().equalsIgnoreCase(userId))
						{
							containindex=i;
						}						
					}
					//System.out.println(containindex);
					for(int i=0;i<lineuserList.size();i++)//�ҵ������û��б��е��û�
					{
						if(lineuserList.get(i).getUserId().equalsIgnoreCase(userId))
						{
							linecontain=i;
						}						
					}
					if(linecontain!=-1)//����û�������
					{
						backMsg.setType("M_FAILURE");
						parentUI.printtext.append(userId+"�û������ߣ������ظ���¼!\n");
						byte[] buf=Translate.ObjectToByte(backMsg);
	                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//���¼�û����͵ı���
	                    serverSocket.send(backPacket); //����
					}
					else if(containindex!=-1)//����Ѵ��ڴ��û�
					{
						User compareuser=new User();
						compareuser=userList.get(containindex);
						//System.out.println(compareuser.getUserId()+"passwd:"+compareuser.getPasswd());
						if(compareuser.getUserId().equals(userId) && compareuser.getPasswd().equals(userpasswd))//����˺�����ƥ��
						{
							//System.out.println("������������ϢM_SUCCESS");
							backMsg.setType("M_SUCCESS");
							parentUI.printtext.append(userId+"�û���¼�ɹ�������������!\n");
							lineuserList.add(user);
							byte[] buf=Translate.ObjectToByte(backMsg);
		                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//���¼�û����͵ı���
		                    serverSocket.send(backPacket); //����  
							//�����������û�����M_LOGIN��Ϣ�����µ�¼�߷��������û��б�
							for(int i=0;i<lineuserList.size();i++)//�������������û��б�
							{
								if(userId.equalsIgnoreCase(lineuserList.get(i).getUserId())==false)//���������û�
								{
									//M_LOGIN��Ϣ
									DatagramPacket oldPacket=lineuserList.get(i).getPacket();
									//�����������û����͵ı��ġ�������������û���ַ�Ͷ˿�,data���ǵ�ǰ�û��ı�����Ϣ
									DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort());
									serverSocket.send(newPacket);//����
								}
								//��ǰ��¼�û�����M_ACK��Ϣ,����i���û����뵱ǰ�û����û��б�,���������û��б�
								Message other=new Message();
								other.setUserId(lineuserList.get(i).getUserId());
								other.setType("M_ACK");
								byte[] buffer=Translate.ObjectToByte(other);
		                        DatagramPacket newPacket=new DatagramPacket(buffer,buffer.length,packet.getAddress(),packet.getPort());
		                        serverSocket.send(newPacket);							
							}
						}
						else//�˺����벻ƥ��
						{
							backMsg.setType("M_FAILURE");
							parentUI.printtext.append(userId+"�û���¼ʧ�ܣ��˺ź����벻ƥ��!\n");
							byte[] buf=Translate.ObjectToByte(backMsg);
		                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//���¼�û����͵ı���
		                    serverSocket.send(backPacket); //����  
						}
					}
					else//�û�������
					{
						backMsg.setType("M_FAILURE");
						parentUI.printtext.append(userId+"�û���¼ʧ�ܣ��û�������!\n");
						byte[] buf=Translate.ObjectToByte(backMsg);
	                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//���¼�û����͵ı���
	                    serverSocket.send(backPacket); //����  
					} 
				}
				
				else if(msg.getType().equalsIgnoreCase("M_MSG"))//����ǻỰ��Ϣ
				{
					parentUI.printtext.append(userId+"˵��"+msg.getText()+"\n");
					//ת����ϢM_MSG
	                for (int i=0;i<lineuserList.size();i++) { //�����û�
	                    DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
	                    serverSocket.send(newPacket); //����
	                }
				}
				else if(msg.getType().equalsIgnoreCase("M_PRIVATE"))//�����˽����Ϣ
				{
					String targetId=msg.getTargetId();
					parentUI.printtext.append(userId+"��"+targetId+"˽��˵��"+msg.getText()+"\n");
					//ת����ϢM_PRIVATE
					for(int i=0;i<lineuserList.size();i++)
					{
						if(targetId.equalsIgnoreCase(lineuserList.get(i).getUserId()))//������û�����
						{
							DatagramPacket oldPacket=lineuserList.get(i).getPacket();
		                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
		                    serverSocket.send(newPacket); //����
		                    //parentUI.printtext.append(targetId+"˽����Ϣ���ͳɹ�!\n");	
		                    break;
						}
					}
				}
				else if(msg.getType().equalsIgnoreCase("M_QUIT"))//������Ϣ
				{
					parentUI.printtext.append(userId+"�û�����\n");
					//ɾ���û�
	                for(int i=0;i<lineuserList.size();i++)
	                {
	                    if (lineuserList.get(i).getUserId().equalsIgnoreCase(userId)) 
	                    {
	                        lineuserList.remove(i);
	                        break;
	                    }
	                }
	               //�������û�ת��������Ϣ
	                for (int i=0;i<lineuserList.size();i++) {
	                    DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort());
	                    serverSocket.send(newPacket);
	                }
				}
				else if(msg.getType().equalsIgnoreCase("M_FILE"))//�ļ�����
				{
					//�����ļ��б�
					Fileup file=new Fileup();
					file.setUserId(userId);
					file.setFilename(msg.getFilename());
					file.setFileId(msg.getFileId());
					file.setFilecontent(msg.getText());
					file.setPacket(packet);
					upfileList.add(file);
					
					parentUI.printtext.append(userId+"�ɹ��ϴ����ļ�\n");
					//ת����ϢM_FILE
                    for(int i=0;i<lineuserList.size();i++)//���������û�
                    {
                    	DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
	                    serverSocket.send(newPacket); //����
                    }
				}
				else if(msg.getType().equalsIgnoreCase("M_REQFILE"))//���������ļ�
				{
					String fileId=msg.getFileId();
					String filecontent = null;
					String filename=null;
					for(int i=0;i<upfileList.size();i++)//���������ļ�
					{
						if(fileId.equalsIgnoreCase(upfileList.get(i).getFileId()))
						{
							filecontent=upfileList.get(i).getFilecontent();
							filename=upfileList.get(i).getFilename();
							parentUI.printtext.append(userId+"���������ļ�"+fileId+"*"+filename+"\n");
							break;
						}
					}
					backMsg.setType("M_REQFILE");//���������ļ��ظ���Ϣ
					backMsg.setText(filecontent);
					backMsg.setUserId(userId);
					//System.out.println(filecontent);
					backMsg.setFilename(filename);
					backMsg.setFileId(fileId);
					byte[] buf=Translate.ObjectToByte(backMsg);
					for(int j=0;j<lineuserList.size();j++)//���������û�
					{
						if(userId.equalsIgnoreCase(lineuserList.get(j).getUserId()))
						{
							DatagramPacket oldPacket=lineuserList.get(j).getPacket();
		                    DatagramPacket newPacket=new DatagramPacket(buf,buf.length,oldPacket.getAddress(),oldPacket.getPort()); 
		                    serverSocket.send(newPacket); //����
		                    break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}//���տͻ��˱���
		}
	}
}
