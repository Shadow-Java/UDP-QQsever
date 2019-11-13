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
	public static int filecount=1;//�ļ�ID
	
	private DatagramSocket clientSocket; //�ͻ����׽���
	private Message msg; //��Ϣ����
	private byte[] data=new byte[8096]; //8K�ֽ�����
	public String IdFile=null;//�ļ�Id
	 
	public ClientUI()
	{
		jf=new JFrame("�ͻ���");
		initComponent();
		jf.setVisible(true);
	}
	public ClientUI(DatagramSocket socket,Message msg)//���캯��2,��������
	{
		this();//�����޲ι��캯��
		this.clientSocket=socket;
		this.msg=msg;
		 //�����ͻ�����Ϣ���պʹ����߳�
        Thread recvThread=new ReceiveMessage(clientSocket,this);
        recvThread.start();//������Ϣ�߳� 		
	}
	public void initComponent()
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// �Զ��������֮��ļ�϶
		layout.setAutoCreateContainerGaps(true);// �Զ����������봥�������߿�����֮��ļ�϶
		
		sessionlabel=new JLabel("�Ự��Ϣ");
		sessiontext=new JTextArea(15,35);
		sessionscroll=new JScrollPane(sessiontext);
		sendlabel=new JLabel("������Ϣ");
		sendtext=new JTextArea(5,35);
		//sendtext.setCaretColor(Color.pink);
		sendscroll=new JScrollPane(sendtext);
		sendbutton=new JButton("����");
		SendButton listener0=new SendButton();
		sendbutton.addActionListener(listener0);
		upfilelabel=new JLabel("�ϴ��ļ�");
		choosefilebutton=new JButton("ѡ���ļ�");
		ChoosefileButton listener2=new ChoosefileButton();
		choosefilebutton.addActionListener(listener2);
		upfiletext=new JTextField(35);
		upfilebutton=new JButton("�ϴ��ļ�");
		UpfileButton listener3=new UpfileButton();
		upfilebutton.addActionListener(listener3);
		
		userlabel=new JLabel("�����û��б�");
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
		
		filelabel=new JLabel("�ļ��б�");
		filelist=new JList<String>();
		filescroll=new JScrollPane(filelist);
		filelist.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				SelectClickfilelist();
			}			
		});
		dnldbutton=new JButton("�����ļ�");
		DownloadButton listener4=new DownloadButton();
		dnldbutton.addActionListener(listener4);		
		
		exitbutton=new JButton("�˳��ͻ���");	
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//ˮƽ�飨��ȷ��x����
		//��������
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(sessionlabel).addComponent(sessionscroll).addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton);
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addComponent(userlabel).addComponent(userscroll).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//��������
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(upfilelabel).addComponent(choosefilebutton);
		GroupLayout.SequentialGroup group1_5=layout.createSequentialGroup().addComponent(upfiletext).addComponent(upfilebutton);
		GroupLayout.ParallelGroup group1_6=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_4).addGroup(group1_5);
		GroupLayout.SequentialGroup group1_3=layout.createSequentialGroup().addGroup(group1_6).addGroup(group1_2);
		layout.setHorizontalGroup(group1_3);
		
		//��ֱ�飨��ȷ��y����
		//��������
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(sessionlabel).addComponent(sessionscroll).addComponent(sendlabel).addComponent(sendscroll).addComponent(sendbutton);
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addComponent(userlabel).addComponent(userscroll).addComponent(filelabel).addComponent(filescroll).addComponent(dnldbutton).addComponent(exitbutton);
		//��������
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(upfilelabel).addComponent(choosefilebutton);
		GroupLayout.ParallelGroup group2_5=layout.createParallelGroup().addComponent(upfiletext).addComponent(upfilebutton);
		GroupLayout.SequentialGroup group2_6=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_4).addGroup(group2_5);
		GroupLayout.ParallelGroup group2_3=layout.createParallelGroup().addGroup(group2_6).addGroup(group2_2);
		layout.setVerticalGroup(group2_3);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);		
	}
	//���Ͱ�ť
	public class SendButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				msg.setText(sendtext.getText());//��ȡ��������
				msg.setType("M_MSG");//������Ϣ����
				data=Translate.ObjectToByte(msg);
				//�������ͱ���
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
				sendtext.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
	//�˳���ť
	public class ExitButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				msg.setType("M_QUIT");//�˳���Ϣ
				msg.setText(null);
				data=Translate.ObjectToByte(msg);
				//�������ͱ���
				DatagramPacket packet=new DatagramPacket(data,data.length,msg.getToAddr(),msg.getToPort());
				clientSocket.send(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			 //clientSocket.close(); //�ر��׽���
			 jf.setVisible(false);
		}		
	}
	//��������û��б�
	public void SelectClicklist(ListSelectionEvent e)
	{
		String targetId=userlist.getSelectedValue();
		System.out.print(targetId);
		String userId=msg.getUserId();//���û�
		if(targetId.equalsIgnoreCase(userId))//���������Լ�
		{
			JOptionPane.showMessageDialog(null, "�������Ը��Լ�������ϢŶ��\n","˽����Ϣ",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			SessionUI privatesession=new SessionUI(userId,targetId);
			privatesession.jf.setVisible(true);
		}
	}
	//ѡ���ļ���ť
	public class ChoosefileButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser=new JFileChooser();	
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//�����ļ�ѡ��ģʽ����ѡ���ļ���Ŀ¼
			chooser.showDialog(new JLabel(),"��ѡ��Ҫ�ϴ����ļ�");//�򿪶Ի���
			File file=chooser.getSelectedFile();
			upfiletext.setText(file.getAbsolutePath().toString());//���ļ�·���������ϴ�����	
		}
	}
	//�ϴ��ļ���ť
	public class UpfileButton implements ActionListener
	{
		private BufferedReader br;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String filepath=upfiletext.getText();
				File file=new File(filepath);
				msg.setFilename(file.getName());
				br = new BufferedReader(new FileReader(file));//���ļ�
				StringBuffer sb=new StringBuffer();
				String str=null;
				while((str=br.readLine())!=null)
				{
					sb.append(str);
				}
				msg.setText(sb.toString());
				//System.out.println(msg.getText());
				msg.setType("M_FILE");//�ļ��ϴ�����
				msg.setFileId(Integer.toString(filecount));
				filecount++;
				data=Translate.ObjectToByte(msg);
				//�������ͱ���
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
	
	//����ļ��б�,��ȡ�ļ�Id
	public void SelectClickfilelist()
	{
		String Flistinformation=filelist.getSelectedValue();
		//System.out.println(Flistinformation);
		IdFile=Flistinformation.substring(0,Flistinformation.indexOf("*"));
	}
	//������ذ�ť,������������
	public class DownloadButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				//���������ļ���Ϣ
				msg.setType("M_REQFILE");//����������Ϣ
				msg.setFileId(IdFile);//ͨ��fileId�ҵ���Ӧfile��Ϣ
				msg.setText(null);
				data=Translate.ObjectToByte(msg);
				//clientSocket.setSoTimeout(6000);//���ó�ʱʱ��
				//�������ͱ���
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
//		//��̬����û��б�
//		DefaultListModel dlm = new DefaultListModel();
//		for(int i=0;i<20;i++)
//		{
//			dlm.addElement(i+"i");
//		}
//		client.userlist.setModel(dlm);
		
//		try {
//			File file=new File("C:/Users/Administrator/Desktop/test1.txt");
//			BufferedReader br;
//			br = new BufferedReader(new FileReader(file));//���ļ�
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
