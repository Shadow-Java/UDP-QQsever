package source;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import util.Message;
import util.Translate;

//客户端线程接收服务器消息线程
public class ReceiveMessage extends Thread{
	private DatagramSocket clientSocket;//客户端会话套接字
	private ClientUI parentUI;//界面
	private byte[]data=new byte[8096];//8k字节数组
	private DefaultListModel userlistModel = new DefaultListModel();
	private DefaultListModel filelistModel = new DefaultListModel();
	private Boolean stopMe=true;//用于停止线程
	//构造函数
	public ReceiveMessage(DatagramSocket socket,ClientUI parentUI)
	{
		this.clientSocket=socket;
		this.parentUI=parentUI;
	}
	//线程入口
	public void run()
	{
		while(true)
		{
			try {
				System.out.println("客户端线程正在运行....");
				//构建接收报文
				DatagramPacket packet=new DatagramPacket(data,data.length);
				clientSocket.receive(packet);
				Message msg=new Message();
				msg=(Message)Translate.ByteToObject(data);//还原消息对象
				String userId=msg.getUserId();//获取用户ID
				System.out.println("客户端收到消息类型"+msg.getType());
				//根据消息类型进行分类处理
				if(msg.getType().equalsIgnoreCase("M_LOGIN"))//其他用户登录
				{
					//播放其他用户上线提示音
					playSound("./sound/cough.wav");
					//更新消息窗口
					parentUI.sessiontext.append(userId+"用户进入聊天室---/欢迎/撒花\n");
					//新上线用户加入列表
					userlistModel.addElement(userId);
					parentUI.userlist.setModel(userlistModel);
				}
				else if(msg.getType().equalsIgnoreCase("M_ACK"))//自己上线
				{
					playSound("./sound/fadeIn.wav");
					//登录成功，将自己加入用户列表
					userlistModel.addElement(userId);
					parentUI.userlist.setModel(userlistModel);
				}
				else if(msg.getType().equalsIgnoreCase("M_MSG"))//会话消息
				{
					playSound("./sound/msg.wav");//播放消息提示音
					//更新消息窗口
					parentUI.sessiontext.append(userId+"用户说："+msg.getText()+"\n");
				}
				else if(msg.getType().equalsIgnoreCase("M_PRIVATE"))//私聊消息
				{
					playSound("./sound/msg.wav");//播放消息提示音
					//JOptionPane.showMessageDialog(null, "有用户对你私聊","消息提示",JOptionPane.INFORMATION_MESSAGE);
					parentUI.sessiontext.append(userId+"用户私聊对你说："+msg.getText()+"\n");
				}
				else if(msg.getType().equalsIgnoreCase("M_QUIT"))//其他用户退出下线
				{
					playSound("./sound/leave.wav");//播放消息提示音
					//更新消息窗口
					parentUI.sessiontext.append(userId+"离开了聊天室-----/再见\n");
					//下线，从在线用户列表删除
					userlistModel.remove(userlistModel.indexOf(userId));
					parentUI.userlist.setModel(userlistModel);
					//clientSocket.close();
					Thread.sleep(5000);
					//stopMe=false;
					//Thread.yield();//让出CPU
				}
				else if(msg.getType().equalsIgnoreCase("M_FILE"))
				{
					playSound("./sound/msg.wav");//播放消息提示音
					String filename=msg.getFilename();
					String fileId=msg.getFileId();
					parentUI.sessiontext.append("----"+userId+"用户上传了文件"+filename+",可以在文件列表点击查看！----\n");
					filelistModel.addElement(fileId+"*"+filename+"--上传者："+userId);
					parentUI.filelist.setModel(filelistModel);					
				}
				else if(msg.getType().equalsIgnoreCase("M_REQFILE"))//如果服务器回应M_REQFILE
				{
					//处理接收报文内容，写入文件保存
					parentUI.sessiontext.append(msg.getUserId()+"----下载文件"+msg.getFileId()+"*"+msg.getFilename()+"----\n");
					String filecontent=msg.getText();
					JFileChooser chooser=new JFileChooser();
					int option=chooser.showSaveDialog(new JLabel("在那里"));
					if(option==JFileChooser.APPROVE_OPTION)//处理文件保存操作
					{
						File file=chooser.getSelectedFile();
						FileOutputStream fs=new FileOutputStream(file);//文件输出流
						fs.write(filecontent.getBytes());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(),"错误提示",JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//播放指定路径音效
	public void playSound(String filename) {
		URL url = null;
		try {
			File f=new File(filename); //绝对路径
	        URI uri = f.toURI();
			url = uri.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}//解析路径
		AudioClip aau; 
		aau = Applet.newAudioClip(url);
		aau.play();//播放一次
     }
}

