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
	public String userId;//���û�
	public String targetId;//���͸����û�ID
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
	
	public SessionUI()//�޲ι��캯��
	{
		jf=new JFrame("˽�Ĵ���");
		initComponent();
		jf.setVisible(true);
	}
	public SessionUI(String userId,String targetId)//���캯��2
	{
		jf=new JFrame(userId+"��"+targetId+"˽�Ĵ���");
		initComponent();
		jf.setVisible(true);
		this.userId=userId;
		this.targetId=targetId;
	}	
	public void initComponent() {
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// �Զ��������֮��ļ�϶
		layout.setAutoCreateContainerGaps(true);// �Զ����������봥�������߿�����֮��ļ�϶
		
		sendlabel=new JLabel("������Ϣ");
		sendtext=new JTextArea(5,30);
		sendscroll=new JScrollPane(sendtext);
		sendbutton=new JButton("����");
		SendButton listener0=new SendButton();
		sendbutton.addActionListener(listener0);
		
//		upfilelabel=new JLabel("�ϴ��ļ�");
//		upfiletext=new JTextField(20);
//		choosefilebutton=new JButton("ѡ���ļ�");
//		ChoosefileButton listener2=new ChoosefileButton();
//		choosefilebutton.addActionListener(listener2);
//		upfilebutton=new JButton("�ϴ��ļ�");
//		filelabel=new JLabel("�ļ��б�");
//		filelist=new JList<String>();
//		filescroll=new JScrollPane(filelist);
//		dnldbutton=new JButton("�����ļ�");
		exitbutton=new JButton("�رջỰ");
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//ˮƽ�飨��ȷ��x����
		//����
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton).addComponent(exitbutton);
		//GroupLayout.SequentialGroup group1_2=layout.createSequentialGroup().addComponent(choosefilebutton).addComponent(upfilebutton);
		//GroupLayout.ParallelGroup group1_3=layout.createParallelGroup().addComponent(upfilelabel).addComponent(upfiletext).addGroup(group1_2).addComponent(upfilebutton).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addGroup(group1_1).addGroup(group1_3);
		layout.setHorizontalGroup(group1_1);
		
		//��ֱ�飨��ȷ��y����
		//����
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton).addComponent(exitbutton);
		//GroupLayout.ParallelGroup group2_2=layout.createParallelGroup().addComponent(choosefilebutton).addComponent(upfilebutton);
		//GroupLayout.SequentialGroup group2_3=layout.createSequentialGroup().addComponent(upfilelabel).addComponent(upfiletext).addGroup(group2_2).addComponent(upfilebutton).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addGroup(group2_1).addGroup(group2_3);
		layout.setVerticalGroup(group2_1);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);	
	}
	//�رհ�ť
	public class ExitButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			jf.setVisible(false);
		}
		
	}
	//���Ͱ�ť
	public class SendButton implements ActionListener
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				//���÷�������ַ�Ͷ˿ں�
				String remoteName="127.0.0.1";
				InetAddress remoteAddr=InetAddress.getByName(remoteName);
				int remotePort=50000;
				clientSocket = new DatagramSocket();
				//����˽����Ϣ
				Message msg=new Message();
				msg.setUserId(userId);//�����û�ID
				msg.setType("M_PRIVATE");//˽����Ϣ
				msg.setText(sendtext.getText());//���÷�����Ϣ����
				msg.setToAddr(remoteAddr);//Ŀ���ַ
				msg.setToPort(remotePort);//Ŀ��˿�
				msg.setTargetId(targetId);//����Ŀ���û�ID
				byte[] data=Translate.ObjectToByte(msg);
				data=Translate.ObjectToByte(msg);
				//�������ͱ���
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);//����
				sendtext.setText("");
				//JOptionPane.showMessageDialog(null, "��Ϣ�ѷ��ͣ�������ѡ�����˽�Ļ��߹رմ���\n","˽����Ϣ",JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
//	//ѡ���ļ���ť
//	public class ChoosefileButton implements ActionListener
//	{
//		@Override
//		public void actionPerformed(ActionEvent arg0) {
//			JFileChooser chooser=new JFileChooser();	
//			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//�����ļ�ѡ��ģʽ����ѡ���ļ���Ŀ¼
//			chooser.showDialog(new JLabel(),"��ѡ��Ҫ�ϴ����ļ�");//�򿪶Ի���
//			File file=chooser.getSelectedFile();
//			upfiletext.setText(file.getAbsolutePath().toString());//���ļ�·���������ϴ�����	
//		}
//	}
	
//	public static void main(String args[])
//	{
//		SessionUI s=new SessionUI();
//	}	
}
