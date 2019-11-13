package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.*;

//服务器界面
public class ServerUI {
	private JFrame jf;
	private JPanel panel;
	private GroupLayout layout;
	private JLabel namelabel;//服务器主机
	private JTextField nametext;
	private JLabel portlabel;//服务器端口
	private JTextField porttext;
	private JButton startbutton;
	public JTextArea printtext;
	private JScrollPane scroll;//滚动窗口
	
	public ServerUI()//构造函数
	{
		jf=new JFrame("服务器端");
		initComponent();
		jf.setVisible(true);
	}
	public void initComponent()
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// 自动创建组件之间的间隙
		layout.setAutoCreateContainerGaps(true);// 自动创建容器与触到容器边框的组件之间的间隙
		
		namelabel=new JLabel("服务器主机名");
		nametext=new JTextField("127.0.0.1",10);
		nametext.setEditable(false);
		portlabel=new JLabel("端口名");
		porttext=new JTextField("50000",10);
		porttext.setEditable(false);
		startbutton=new JButton("启动");
		startbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				StartButton(ev);
				}
			});		
		printtext=new JTextArea(20,50);
		scroll=new JScrollPane(printtext);
		
		//水平组（仅确定x方向）
		//左右排列
		GroupLayout.SequentialGroup group1_1=layout.createSequentialGroup().addComponent(namelabel).addComponent(nametext).addComponent(portlabel).addComponent(porttext).addComponent(startbutton);
		//上下
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addGroup(group1_1).addComponent(scroll);
		layout.setHorizontalGroup(group1_2);
		
		//垂直组（仅确定y方向）
		//左右排列
		GroupLayout.ParallelGroup group2_1=layout.createParallelGroup().addComponent(namelabel).addComponent(nametext).addComponent(portlabel).addComponent(porttext).addComponent(startbutton);
		//上下
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addGroup(group2_1).addComponent(scroll);
		layout.setVerticalGroup(group2_2);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);
	}
	public void StartButton(ActionEvent ev)//启动服务器按钮
	{
		try {
			//获取服务器地址和端口号
			//String hostName=nametext.getText();
			int hostPort=Integer.parseInt(porttext.getText());
			//创建UDP数据包，在指定端口监听
			DatagramSocket serverSocket=new DatagramSocket(hostPort);
			printtext.append("服务器已启动开始监听....\n");
			Thread recvThread=new ReceiveMessage(serverSocket,this);
            recvThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "错误提示", JOptionPane.ERROR_MESSAGE);
		}
		startbutton.setEnabled(false);//不可被再次点击
	}
	
	public static void main(String[] args)
	{
		ServerUI server=new ServerUI();
		server.jf.setVisible(true);
	}	
}
