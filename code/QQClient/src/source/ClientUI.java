package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.Message;
import util.Translate;

public class ClientUI {
	public JFrame jf;
	private JPanel panel;
	private GroupLayout layout;	
	private JLabel sessionlabel;
	public JTextArea sessiontext;
	private JScrollPane sessionscroll;
	private JLabel sendlabel;
	private JTextArea sendtext;
	private JScrollPane sendscroll;
	public JLabel upfilelabel;
	private JButton choosefilebutton;
	private JTextField upfiletext;
	private JButton upfilebutton;
	private JButton sendbutton;	
	private JLabel userlabel;
	public JList<String> userlist;
	private JScrollPane userscroll;
	private JLabel filelabel;
	public JList<String> filelist;	
	private JScrollPane filescroll;
	private JButton dnldbutton;
	private JButton exitbutton;
	public static int filecount=1;//文件ID
	
	private DatagramSocket clientSocket; //客户机套接字
	private Message msg; //消息对象
	private byte[] data=new byte[8096]; //8K字节数组
	public String IdFile=null;//文件Id
	 
	public ClientUI()
	{
		jf=new JFrame("客户端");
		initComponent();
		jf.setVisible(true);
	}
	public ClientUI(DatagramSocket socket,Message msg)//构造函数2,参数交流
	{
		this();//调用无参构造函数
		this.clientSocket=socket;
		this.msg=msg;
		 //创建客户机消息接收和处理线程
        Thread recvThread=new ReceiveMessage(clientSocket,this);
        recvThread.start();//启动消息线程 		
	}
	public void initComponent()
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// 自动创建组件之间的间隙
		layout.setAutoCreateContainerGaps(true);// 自动创建容器与触到容器边框的组件之间的间隙
		
		sessionlabel=new JLabel("会话消息");
		sessiontext=new JTextArea(15,35);
		sessionscroll=new JScrollPane(sessiontext);
		sendlabel=new JLabel("发送消息");
		sendtext=new JTextArea(5,35);
		//sendtext.setCaretColor(Color.pink);
		sendscroll=new JScrollPane(sendtext);
		sendbutton=new JButton("发送");
		SendButton listener0=new SendButton();
		sendbutton.addActionListener(listener0);
		upfilelabel=new JLabel("上传文件");
		choosefilebutton=new JButton("选择文件");
		ChoosefileButton listener2=new ChoosefileButton();
		choosefilebutton.addActionListener(listener2);
		upfiletext=new JTextField(35);
		upfilebutton=new JButton("上传文件");
		UpfileButton listener3=new UpfileButton();
		upfilebutton.addActionListener(listener3);
		
		userlabel=new JLabel("在线用户列表");
		userlist=new JList<String>();
//		userlist.setFixedCellWidth(150);
//		userlist.setFixedCellHeight(20);
//		userlist.setVisibleRowCount(10);
		userscroll=new JScrollPane(userlist);
		userlist.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SelectClicklist(e);				
			}
		});
		
		filelabel=new JLabel("文件列表");
		filelist=new JList<String>();
		filescroll=new JScrollPane(filelist);
		filelist.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				SelectClickfilelist();
			}			
		});
		dnldbutton=new JButton("下载文件");
		DownloadButton listener4=new DownloadButton();
		dnldbutton.addActionListener(listener4);		
		
		exitbutton=new JButton("退出客户端");	
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//水平组（仅确定x方向）
		//上下排列
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(sessionlabel).addComponent(sessionscroll).addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton);
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addComponent(userlabel).addComponent(userscroll).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//左右排列
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(upfilelabel).addComponent(choosefilebutton);
		GroupLayout.SequentialGroup group1_5=layout.createSequentialGroup().addComponent(upfiletext).addComponent(upfilebutton);
		GroupLayout.ParallelGroup group1_6=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_4).addGroup(group1_5);
		GroupLayout.SequentialGroup group1_3=layout.createSequentialGroup().addGroup(group1_6).addGroup(group1_2);
		layout.setHorizontalGroup(group1_3);
		
		//垂直组（仅确定y方向）
		//上下排列
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(sessionlabel).addComponent(sessionscroll).addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton);
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addComponent(userlabel).addComponent(userscroll).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//左右排列
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(upfilelabel).addComponent(choosefilebutton);
		GroupLayout.ParallelGroup group2_5=layout.createParallelGroup().addComponent(upfiletext).addComponent(upfilebutton);
		GroupLayout.SequentialGroup group2_6=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_4).addGroup(group2_5);
		GroupLayout.ParallelGroup group2_3=layout.createParallelGroup().addGroup(group2_6).addGroup(group2_2);
		layout.setVerticalGroup(group2_3);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);		
	}
	//发送按钮
	public class SendButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				msg.setText(sendtext.getText());//获取输入内容
				msg.setType("M_MSG");//设置消息类型
				data=Translate.ObjectToByte(msg);
				//构建发送报文
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
				sendtext.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
	//退出按钮
	public class ExitButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				msg.setType("M_QUIT");//退出消息
				msg.setText(null);
				data=Translate.ObjectToByte(msg);
				//构建发送报文
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			 //clientSocket.close(); //关闭套接字
			 jf.setVisible(false);
		}		
	}
	//点击在线用户列表
	public void SelectClicklist(ListSelectionEvent e)
	{
		String targetId=userlist.getSelectedValue();
		System.out.print(targetId);
		String userId=msg.getUserId();//本用户
		if(targetId.equalsIgnoreCase(userId))//如果点击了自己
		{
			JOptionPane.showMessageDialog(null, "您不可以给自己发送消息哦！\n","私聊信息",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			SessionUI privatesession=new SessionUI(userId,targetId);
			privatesession.jf.setVisible(true);
		}
	}
	//选择文件按钮
	public class ChoosefileButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser=new JFileChooser();	
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//设置文件选择模式，可选择文件和目录
			chooser.showDialog(new JLabel(),"请选择要上传的文件");//打开对话框
			File file=chooser.getSelectedFile();
			upfiletext.setText(file.getAbsolutePath().toString());//将文件路径设置于上传框内	
		}
	}
	//上传文件按钮
	public class UpfileButton implements ActionListener
	{
		private BufferedReader br;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String filepath=upfiletext.getText();
				File file=new File(filepath);
				msg.setFilename(file.getName());
				br = new BufferedReader(new FileReader(file));//读文件
				StringBuffer sb=new StringBuffer();
				String str=null;
				while((str=br.readLine())!=null)
				{
					sb.append(str);
				}
				msg.setText(sb.toString());
				//System.out.println(msg.getText());
				msg.setType("M_FILE");//文件上传类型
				msg.setFileId(Integer.toString(filecount));
				filecount++;
				data=Translate.ObjectToByte(msg);
				//构建发送报文
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
				upfiletext.setText("");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//点击文件列表,获取文件Id
	public void SelectClickfilelist()
	{
		String Flistinformation=filelist.getSelectedValue();
		//System.out.println(Flistinformation);
		IdFile=Flistinformation.substring(0,Flistinformation.indexOf("*"));
	}
	//点击下载按钮,发送下载请求
	public class DownloadButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				//发送请求文件信息
				msg.setType("M_REQFILE");//请求下载消息
				msg.setFileId(IdFile);//通过fileId找到对应file信息
				msg.setText(null);
				data=Translate.ObjectToByte(msg);
				//clientSocket.setSoTimeout(6000);//设置超时时间
				//构建发送报文
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
//	public static void main(String args[])
//	{
//		ClientUI client=new ClientUI();
//		//动态添加用户列表
//		DefaultListModel dlm = new DefaultListModel();
//		for(int i=0;i<20;i++)
//		{
//			dlm.addElement(i+"i");
//		}
//		client.userlist.setModel(dlm);
		
//		try {
//			File file=new File("C:/Users/Administrator/Desktop/test1.txt");
//			BufferedReader br;
//			br = new BufferedReader(new FileReader(file));//读文件
//			StringBuffer sb=new StringBuffer();
//			String str=null;
//			while((str=br.readLine())!=null)
//			{
//				sb.append(str);
//			}
//			System.out.println(sb.toString());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
