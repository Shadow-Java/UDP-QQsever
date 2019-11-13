package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;

import util.Message;
import util.Translate;

public class SessionUI {
	public String userId;//本用户
	public String targetId;//发送给的用户ID
	public JFrame jf;
	private JPanel panel;
	private GroupLayout layout;	
	private JLabel sendlabel;
	public JTextArea sendtext;
	private JScrollPane sendscroll;
	private JButton sendbutton;
//	private JLabel upfilelabel;
//	private JTextField upfiletext;
//	private JButton choosefilebutton;
//	private JButton upfilebutton;
//	private JLabel filelabel;
//	private JList<String> filelist;
//	private JScrollPane filescroll;
//	private JButton dnldbutton;
	private JButton exitbutton;
	
	public SessionUI()//无参构造函数
	{
		jf=new JFrame("私聊窗口");
		initComponent();
		jf.setVisible(true);
	}
	public SessionUI(String userId,String targetId)//构造函数2
	{
		jf=new JFrame(userId+"对"+targetId+"私聊窗口");
		initComponent();
		jf.setVisible(true);
		this.userId=userId;
		this.targetId=targetId;
	}	
	public void initComponent() {
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// 自动创建组件之间的间隙
		layout.setAutoCreateContainerGaps(true);// 自动创建容器与触到容器边框的组件之间的间隙
		
		sendlabel=new JLabel("发送消息");
		sendtext=new JTextArea(5,30);
		sendscroll=new JScrollPane(sendtext);
		sendbutton=new JButton("发送");
		SendButton listener0=new SendButton();
		sendbutton.addActionListener(listener0);
		
//		upfilelabel=new JLabel("上传文件");
//		upfiletext=new JTextField(20);
//		choosefilebutton=new JButton("选择文件");
//		ChoosefileButton listener2=new ChoosefileButton();
//		choosefilebutton.addActionListener(listener2);
//		upfilebutton=new JButton("上传文件");
//		filelabel=new JLabel("文件列表");
//		filelist=new JList<String>();
//		filescroll=new JScrollPane(filelist);
//		dnldbutton=new JButton("下载文件");
		exitbutton=new JButton("关闭会话");
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//水平组（仅确定x方向）
		//上下
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton).addComponent(exitbutton);
		//GroupLayout.SequentialGroup group1_2=layout.createSequentialGroup().addComponent(choosefilebutton).addComponent(upfilebutton);
		//GroupLayout.ParallelGroup group1_3=layout.createParallelGroup().addComponent(upfilelabel).addComponent(upfiletext).addGroup(group1_2).addComponent(upfilebutton).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addGroup(group1_1).addGroup(group1_3);
		layout.setHorizontalGroup(group1_1);
		
		//垂直组（仅确定y方向）
		//上下
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton).addComponent(exitbutton);
		//GroupLayout.ParallelGroup group2_2=layout.createParallelGroup().addComponent(choosefilebutton).addComponent(upfilebutton);
		//GroupLayout.SequentialGroup group2_3=layout.createSequentialGroup().addComponent(upfilelabel).addComponent(upfiletext).addGroup(group2_2).addComponent(upfilebutton).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addGroup(group2_1).addGroup(group2_3);
		layout.setVerticalGroup(group2_1);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);	
	}
	//关闭按钮
	public class ExitButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			jf.setVisible(false);
		}
		
	}
	//发送按钮
	public class SendButton implements ActionListener
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				//设置服务器地址和端口号
				String remoteName="127.0.0.1";
				InetAddress remoteAddr=InetAddress.getByName(remoteName);
				int remotePort=50000;
				clientSocket = new DatagramSocket();
				//构建私聊消息
				Message msg=new Message();
				msg.setUserId(userId);//设置用户ID
				msg.setType("M_PRIVATE");//私聊消息
				msg.setText(sendtext.getText());//设置发送消息内容
				msg.setToAddr(remoteAddr);//目标地址
				msg.setToPort(remotePort);//目标端口
				msg.setTargetId(targetId);//设置目标用户ID
				byte[] data=Translate.ObjectToByte(msg);
				data=Translate.ObjectToByte(msg);
				//构建发送报文
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);//发送
				sendtext.setText("");
				//JOptionPane.showMessageDialog(null, "消息已发送，您可以选择继续私聊或者关闭窗口\n","私聊信息",JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
//	//选择文件按钮
//	public class ChoosefileButton implements ActionListener
//	{
//		@Override
//		public void actionPerformed(ActionEvent arg0) {
//			JFileChooser chooser=new JFileChooser();	
//			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//设置文件选择模式，可选择文件和目录
//			chooser.showDialog(new JLabel(),"请选择要上传的文件");//打开对话框
//			File file=chooser.getSelectedFile();
//			upfiletext.setText(file.getAbsolutePath().toString());//将文件路径设置于上传框内	
//		}
//	}
	
//	public static void main(String args[])
//	{
//		SessionUI s=new SessionUI();
//	}	
}
