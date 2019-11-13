package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;

import util.Message;
import util.Translate;

//用户注册、登录系统
public class LoginUI{
	public JDialog dg;
	private JLabel Logolabel;//logo图片
	private JPanel panel;
	private GroupLayout layout;	
	private JLabel idlabel;
	private JTextField idtext;
	private JTextField passwdtext;
	private JLabel passwdlabel;
	private JButton loginbutton;
	private JButton registerbutton;
	
	public LoginUI()//构造函数
	{
		dg=new JDialog();
		initComponents();
		dg.setTitle("用户登录");
		//this.setBounds(100, 100, 400, 300);
		dg.setVisible(true);
	}
	public void initComponents()//初始化组件
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// 自动创建组件之间的间隙
		layout.setAutoCreateContainerGaps(true);// 自动创建容器与触到容器边框的组件之间的间隙
		
		Logolabel=new JLabel();
		ImageIcon icon=new ImageIcon("./images/Login.png");
		Logolabel.setIcon(icon);
		
		idlabel=new JLabel("账号");
		idtext=new JTextField(20);
		passwdlabel=new JLabel("密码");
		passwdtext=new JTextField(20);
		
		registerbutton=new JButton("注册");
		RegisterButton listener0=new RegisterButton();
		registerbutton.addActionListener(listener0);
		
		loginbutton=new JButton("登录");
		LoginButton listener1=new LoginButton();
		loginbutton.addActionListener(listener1);
		//水平组（仅确定x方向）
		//上下排列
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(idlabel)
																		.addComponent(idtext)
																		.addComponent(passwdlabel)
																		.addComponent(passwdtext);
		//左右
		GroupLayout.SequentialGroup group1_2=layout.createSequentialGroup().addComponent(registerbutton).addComponent(loginbutton);
		//上下
		GroupLayout.ParallelGroup group1_3=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_2);
		//左右排列
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(Logolabel).addGroup(group1_3);
		layout.setHorizontalGroup(group1_4);
		
		//垂直组（仅确定y方向）
		//上下排列
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(idlabel).addComponent(idtext).addComponent(passwdlabel).addComponent(passwdtext);
		//左右
		GroupLayout.ParallelGroup group2_2=layout.createParallelGroup().addComponent(registerbutton).addComponent(loginbutton);
		//上下
		GroupLayout.SequentialGroup group2_3=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_2);
		//左右排列
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(Logolabel).addGroup(group2_3);
		layout.setVerticalGroup(group2_4);
		
		dg.setContentPane(panel);
		dg.pack();	
		dg.setLocationRelativeTo(null);		
	}	
	public class RegisterButton implements ActionListener//注册按钮
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			dg.setVisible(false);
			RegisterUI rt=new RegisterUI();	//打开用户注册窗口
			String str=Integer.toString(1111+RegisterUI.count);
			rt.idtext.setText(str);	
			rt.idtext.setEditable(false);
		}		
	}
	public class LoginButton implements ActionListener//登录按钮
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(idtext.getText().equals(""))//如果账号为空
			{
				JOptionPane.showMessageDialog(null,"请输入您的账号！","账号为空",JOptionPane.ERROR_MESSAGE);
			}
			else if(passwdtext.getText().equals(""))//密码为空
			{
				JOptionPane.showMessageDialog(null,"请输入您的密码！","密码为空",JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try {
					//设置服务器地址和端口号
					String remoteName="127.0.0.1";
					InetAddress remoteAddr=InetAddress.getByName(remoteName);
					int remotePort=50000;
					clientSocket=new DatagramSocket();
					clientSocket.setSoTimeout(6000);//设置超时时间
					//构建登录消息
					Message msg=new Message();
					msg.setUserId(idtext.getText());//获取用户id
					msg.setPassword(passwdtext.getText());//获取用户密码
					msg.setType("M_LOGIN");//用户登录消息
					msg.setToAddr(remoteAddr);//目标地址
					msg.setToPort(remotePort);//目标端口
					byte[] data=Translate.ObjectToByte(msg);//消息序列化
					//定义登录报文
					DatagramPacket packet=new DatagramPacket(data,data.length,remoteAddr,remotePort);
					//发送数据报文
					clientSocket.send(packet);
					//接收数据回送报文
					DatagramPacket backPacket=new DatagramPacket(data,data.length);
					clientSocket.receive(backPacket);
					clientSocket.setSoTimeout(0);//取消超时
					Message backMsg=new Message();
					backMsg=(Message)Translate.ByteToObject(data);
					//登录结果
					System.out.println("客户端收到消息类型"+msg.getType());
					if(backMsg.getType().equalsIgnoreCase("M_SUCCESS"))//登录成功
					{
						System.out.println("登陆成功");
						JOptionPane.showMessageDialog(null, "恭喜你，现在可以开始聊天啦！","登录成功",JOptionPane.INFORMATION_MESSAGE);
						dg.setVisible(false);
						ClientUI chat=new ClientUI(clientSocket,msg);//进入聊天室客户端
						chat.jf.setTitle(msg.getUserId()+"客户端");
						chat.jf.setVisible(true);
					}
					else if(backMsg.getType().equalsIgnoreCase("M_FAILURE"))
					{
						JOptionPane.showMessageDialog(null, "您的账号或者密码错误！\n或者请检查您是否已在线","登录失败",JOptionPane.ERROR_MESSAGE);
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	public static void main(String args[])
	{
		LoginUI login=new LoginUI();
		login.dg.setVisible(true);
	}	
}
