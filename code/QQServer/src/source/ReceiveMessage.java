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

//服务器接收和发送消息线程
public class ReceiveMessage extends Thread{
	private DatagramSocket serverSocket;//服务器套接字
	private DatagramPacket packet;//报文
	public static List<User> userList=new ArrayList<User>();//用户列表
	public static List<User> lineuserList=new ArrayList<User>();//在线用户列表
	public static List<Fileup> upfileList=new ArrayList<Fileup>();//文件列表
	private byte[] data=new byte[8096];//8k字节数组
	private ServerUI parentUI;//消息窗口
	
	//构造函数
	public ReceiveMessage(DatagramSocket socket,ServerUI parentUI)
	{
		this.serverSocket=socket;
		this.parentUI=parentUI;
	}
	//线程运行入口
	public void run()
	{
		while(true)
		{
			try {
				System.out.println("服务器线程正在运行....");
				packet=new DatagramPacket(data,data.length);//构建接收报文
				serverSocket.receive(packet);
				//将收到的数据转换为消息对象
				Message msg=new Message();
				msg=(Message)Translate.ByteToObject(packet.getData());
				String userId=msg.getUserId();//从当前接收到的消息获得用户ID
				String userpasswd=msg.getPassword();//从当前接收到的消息获得用户密码
				System.out.println("服务器收到消息类型"+msg.getType());
				Message backMsg=new Message();
				if(msg.getType().equalsIgnoreCase("M_REGISTER"))//如果是用户注册消息
				{
					User user=new User();
					user.setUserId(userId);//保存用户id
					user.setPasswd(userpasswd);//保存用户密码
					user.setPacket(packet);//保存用户报文
					//System.out.println(user.getUserId());
					if(userList.contains(user))//如果已存在此用户
					{
						//System.out.println("服务器发出消息M_RFAILURE");
						backMsg.setType("M_RFAILURE");
						parentUI.printtext.append(userId+"用户已注册过!\n");
					}
					else//如果此用户未被注册
					{
						backMsg.setType("M_RSUCCESS");
						//System.out.println("服务器发出消息M_RSUCCESS");
						userList.add(user);//将用户加入用户列表
						//System.out.println(userList.get(0).getUserId());
						parentUI.printtext.append(userId+"用户注册成功，加入用户列表!\n");
					}
					byte[] buf=Translate.ObjectToByte(backMsg);
                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//向注册用户发送的报文
                    serverSocket.send(backPacket); //发送   
				}
				
				else if(msg.getType().equalsIgnoreCase("M_LOGIN"))//如果是用户登录消息
				{
					User user=new User();
					user.setUserId(userId);//保存用户id
					user.setPasswd(userpasswd);//保存用户密码
					user.setPacket(packet);//保存用户报文
					int containindex=-1;
					int linecontain=-1;
					for(int i=0;i<userList.size();i++)//找到用户列表中的用户
					{
						if(userList.get(i).getUserId().equalsIgnoreCase(userId))
						{
							containindex=i;
						}						
					}
					//System.out.println(containindex);
					for(int i=0;i<lineuserList.size();i++)//找到在线用户列表中的用户
					{
						if(lineuserList.get(i).getUserId().equalsIgnoreCase(userId))
						{
							linecontain=i;
						}						
					}
					if(linecontain!=-1)//如果用户已上线
					{
						backMsg.setType("M_FAILURE");
						parentUI.printtext.append(userId+"用户已在线，不必重复登录!\n");
						byte[] buf=Translate.ObjectToByte(backMsg);
	                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//向登录用户发送的报文
	                    serverSocket.send(backPacket); //发送
					}
					else if(containindex!=-1)//如果已存在此用户
					{
						User compareuser=new User();
						compareuser=userList.get(containindex);
						//System.out.println(compareuser.getUserId()+"passwd:"+compareuser.getPasswd());
						if(compareuser.getUserId().equals(userId) && compareuser.getPasswd().equals(userpasswd))//如果账号密码匹配
						{
							//System.out.println("服务器发出消息M_SUCCESS");
							backMsg.setType("M_SUCCESS");
							parentUI.printtext.append(userId+"用户登录成功，进入聊天室!\n");
							lineuserList.add(user);
							byte[] buf=Translate.ObjectToByte(backMsg);
		                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//向登录用户发送的报文
		                    serverSocket.send(backPacket); //发送  
							//向其他在线用户发送M_LOGIN消息，向新登录者发送整个用户列表
							for(int i=0;i<lineuserList.size();i++)//遍历整个在线用户列表
							{
								if(userId.equalsIgnoreCase(lineuserList.get(i).getUserId())==false)//其他在线用户
								{
									//M_LOGIN消息
									DatagramPacket oldPacket=lineuserList.get(i).getPacket();
									//向其他在线用户发送的报文。获得其他在线用户地址和端口,data中是当前用户的报文信息
									DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort());
									serverSocket.send(newPacket);//发送
								}
								//向当前登录用户回送M_ACK消息,将第i个用户加入当前用户的用户列表,发送在线用户列表
								Message other=new Message();
								other.setUserId(lineuserList.get(i).getUserId());
								other.setType("M_ACK");
								byte[] buffer=Translate.ObjectToByte(other);
		                        DatagramPacket newPacket=new DatagramPacket(buffer,buffer.length,packet.getAddress(),packet.getPort());
		                        serverSocket.send(newPacket);							
							}
						}
						else//账号密码不匹配
						{
							backMsg.setType("M_FAILURE");
							parentUI.printtext.append(userId+"用户登录失败，账号和密码不匹配!\n");
							byte[] buf=Translate.ObjectToByte(backMsg);
		                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//向登录用户发送的报文
		                    serverSocket.send(backPacket); //发送  
						}
					}
					else//用户不存在
					{
						backMsg.setType("M_FAILURE");
						parentUI.printtext.append(userId+"用户登录失败，用户不存在!\n");
						byte[] buf=Translate.ObjectToByte(backMsg);
	                    DatagramPacket backPacket=new DatagramPacket(buf,buf.length,packet.getAddress(),packet.getPort());//向登录用户发送的报文
	                    serverSocket.send(backPacket); //发送  
					} 
				}
				
				else if(msg.getType().equalsIgnoreCase("M_MSG"))//如果是会话消息
				{
					parentUI.printtext.append(userId+"说："+msg.getText()+"\n");
					//转发消息M_MSG
	                for (int i=0;i<lineuserList.size();i++) { //遍历用户
	                    DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
	                    serverSocket.send(newPacket); //发送
	                }
				}
				else if(msg.getType().equalsIgnoreCase("M_PRIVATE"))//如果是私聊消息
				{
					String targetId=msg.getTargetId();
					parentUI.printtext.append(userId+"对"+targetId+"私聊说："+msg.getText()+"\n");
					//转发消息M_PRIVATE
					for(int i=0;i<lineuserList.size();i++)
					{
						if(targetId.equalsIgnoreCase(lineuserList.get(i).getUserId()))//如果该用户在线
						{
							DatagramPacket oldPacket=lineuserList.get(i).getPacket();
		                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
		                    serverSocket.send(newPacket); //发送
		                    //parentUI.printtext.append(targetId+"私聊消息发送成功!\n");	
		                    break;
						}
					}
				}
				else if(msg.getType().equalsIgnoreCase("M_QUIT"))//下线消息
				{
					parentUI.printtext.append(userId+"用户下线\n");
					//删除用户
	                for(int i=0;i<lineuserList.size();i++)
	                {
	                    if (lineuserList.get(i).getUserId().equalsIgnoreCase(userId)) 
	                    {
	                        lineuserList.remove(i);
	                        break;
	                    }
	                }
	               //向其他用户转发下线消息
	                for (int i=0;i<lineuserList.size();i++) {
	                    DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort());
	                    serverSocket.send(newPacket);
	                }
				}
				else if(msg.getType().equalsIgnoreCase("M_FILE"))//文件传输
				{
					//保存文件列表
					Fileup file=new Fileup();
					file.setUserId(userId);
					file.setFilename(msg.getFilename());
					file.setFileId(msg.getFileId());
					file.setFilecontent(msg.getText());
					file.setPacket(packet);
					upfileList.add(file);
					
					parentUI.printtext.append(userId+"成功上传了文件\n");
					//转发消息M_FILE
                    for(int i=0;i<lineuserList.size();i++)//遍历所有用户
                    {
                    	DatagramPacket oldPacket=lineuserList.get(i).getPacket();
	                    DatagramPacket newPacket=new DatagramPacket(data,data.length,oldPacket.getAddress(),oldPacket.getPort()); 
	                    serverSocket.send(newPacket); //发送
                    }
				}
				else if(msg.getType().equalsIgnoreCase("M_REQFILE"))//请求下载文件
				{
					String fileId=msg.getFileId();
					String filecontent = null;
					String filename=null;
					for(int i=0;i<upfileList.size();i++)//遍历所有文件
					{
						if(fileId.equalsIgnoreCase(upfileList.get(i).getFileId()))
						{
							filecontent=upfileList.get(i).getFilecontent();
							filename=upfileList.get(i).getFilename();
							parentUI.printtext.append(userId+"请求下载文件"+fileId+"*"+filename+"\n");
							break;
						}
					}
					backMsg.setType("M_REQFILE");//请求下载文件回复消息
					backMsg.setText(filecontent);
					backMsg.setUserId(userId);
					//System.out.println(filecontent);
					backMsg.setFilename(filename);
					backMsg.setFileId(fileId);
					byte[] buf=Translate.ObjectToByte(backMsg);
					for(int j=0;j<lineuserList.size();j++)//遍历所有用户
					{
						if(userId.equalsIgnoreCase(lineuserList.get(j).getUserId()))
						{
							DatagramPacket oldPacket=lineuserList.get(j).getPacket();
		                    DatagramPacket newPacket=new DatagramPacket(buf,buf.length,oldPacket.getAddress(),oldPacket.getPort()); 
		                    serverSocket.send(newPacket); //发送
		                    break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}//接收客户端报文
		}
	}
}
