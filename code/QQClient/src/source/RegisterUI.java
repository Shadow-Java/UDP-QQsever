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

public class RegisterUI{
	public JDialog dg;
	private JLabel Logolabel;//logo图片
	private JPanel panel;
	private GroupLayout layout;	
	private JLabel idlabel;
	public JTextField idtext;
	private JLabel passwdlabel;
	public JTextField passwdtext;
	private JLabel conpasswdlabel;
	public JTextField conpasswdtext;
	private JButton confirmbutton;
	private JButton exitbutton;
	public static int count=0;//用于为注册用户分配账号
	
	public RegisterUI()//构造函数
	{
		dg=new JDialog();
		initComponents();
		dg.setTitle("用户注册");
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
		
		idlabel=new JLabel("您的账号");
		idtext=new JTextField(20);
		passwdlabel=new JLabel("请输入密码");
		passwdtext=new JTextField(20);
		conpasswdlabel=new JLabel("确认密码");
		conpasswdtext=new JTextField(20);
		
		confirmbutton=new JButton("确认");
		RegisterConfirm listener0=new RegisterConfirm();
		confirmbutton.addActionListener(listener0);
		
		exitbutton=new JButton("退出注册");
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//水平组（仅确定x方向）
		//上下排列
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(idlabel)
																		.addComponent(idtext)
																		.addComponent(passwdlabel)
																		.addComponent(passwdtext).addComponent(conpasswdlabel).addComponent(conpasswdtext);
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(confirmbutton).addComponent(exitbutton);
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_4);
		//左右排列
		GroupLayout.SequentialGroup group1_3=layout.createSequentialGroup().addComponent(Logolabel).addGroup(group1_2);
		layout.setHorizontalGroup(group1_3);
		
		//垂直组（仅确定y方向）
		//上下排列
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(idlabel).addComponent(idtext).addComponent(passwdlabel).addComponent(passwdtext).addComponent(conpasswdlabel).addComponent(conpasswdtext);
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(confirmbutton).addComponent(exitbutton);
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_4);
	
		//左右排列
		GroupLayout.ParallelGroup group2_3=layout.createParallelGroup().addComponent(Logolabel).addGroup(group2_2);
		layout.setVerticalGroup(group2_3);
		
		dg.setContentPane(panel);
		dg.pack();	
		dg.setLocationRelativeTo(null);		
	}
	public class ExitButton implements ActionListener//退出注册
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dg.setVisible(false);
			LoginUI login=new LoginUI();//登录聊天室
			login.dg.setVisible(true);
		}
		
	}
	public class RegisterConfirm implements ActionListener//注册界面确认按钮
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(passwdtext.getText().equals(""))//如果输入密码框为空
			{
				JOptionPane.showMessageDialog(null,"请输入您的密码！","密码为空",JOptionPane.ERROR_MESSAGE);
			}
			else if(conpasswdtext.getText().equals(""))//确认密码为空
			{
				JOptionPane.showMessageDialog(null, "请再次输入您的密码！","确认密码为空",JOptionPane.ERROR_MESSAGE);
			}
			else if(passwdtext.getText().equals(conpasswdtext.getText())==false)//两次密码输入不同
			{
				JOptionPane.showMessageDialog(null, "确认密码不正确！","密码错误",JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try {
					//设置服务器地址和端口号
					String remoteName="127.0.0.1";
					InetAddress remoteAddr=InetAddress.getByName(remoteName);
					int remotePort=50000;
					clientSocket = new DatagramSocket();
					clientSocket.setSoTimeout(6000);//设置超时时间
					//构建注册消息
					Message msg=new Message();
					msg.setUserId(idtext.getText());//用户名
					msg.setPassword(passwdtext.getText());//密码
					msg.setType("M_REGISTER");//用户注册消息
					msg.setToAddr(remoteAddr);//目标地址
					msg.setToPort(remotePort);//目标端口
					byte[] data=Translate.ObjectToByte(msg);
					//定义注册报文
					DatagramPacket packet=new DatagramPacket(data,data.length,remoteAddr,remotePort);
					//发送数据报文
					clientSocket.send(packet);
					//接收服务器回送报文
					DatagramPacket backPacket=new DatagramPacket(data,data.length);
					clientSocket.receive(backPacket);
					clientSocket.setSoTimeout(0);//取消超时
					Message backMsg=new Message();
					backMsg=(Message)Translate.ByteToObject(data);
					//处理注册结果
					if(backMsg.getType().equalsIgnoreCase("M_RSUCCESS"))//注册成功
					{
						JOptionPane.showMessageDialog(null, "现在可以登录聊天室啦！","注册成功",JOptionPane.INFORMATION_MESSAGE);
						dg.setVisible(false);
						LoginUI login=new LoginUI();//登录聊天室
						login.dg.setVisible(true);
						count++;//确认用户＋1
					}
					else if(backMsg.getType().equalsIgnoreCase("M_RFAILURE"))//注册失败
					{
						JOptionPane.showMessageDialog(null, "很遗憾服务器未接受您的注册申请！","注册失败",JOptionPane.ERROR_MESSAGE);
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
}
