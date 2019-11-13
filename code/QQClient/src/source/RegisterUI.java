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
	private JLabel Logolabel;//logoͼƬ
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
	public static int count=0;//����Ϊע���û������˺�
	
	public RegisterUI()//���캯��
	{
		dg=new JDialog();
		initComponents();
		dg.setTitle("�û�ע��");
		dg.setVisible(true);
	}
	public void initComponents()//��ʼ�����
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// �Զ��������֮��ļ�϶
		layout.setAutoCreateContainerGaps(true);// �Զ����������봥�������߿�����֮��ļ�϶
		
		Logolabel=new JLabel();
		ImageIcon icon=new ImageIcon("./images/Login.png");
		Logolabel.setIcon(icon);
		
		idlabel=new JLabel("�����˺�");
		idtext=new JTextField(20);
		passwdlabel=new JLabel("����������");
		passwdtext=new JTextField(20);
		conpasswdlabel=new JLabel("ȷ������");
		conpasswdtext=new JTextField(20);
		
		confirmbutton=new JButton("ȷ��");
		RegisterConfirm listener0=new RegisterConfirm();
		confirmbutton.addActionListener(listener0);
		
		exitbutton=new JButton("�˳�ע��");
		ExitButton listener1=new ExitButton();
		exitbutton.addActionListener(listener1);
		
		//ˮƽ�飨��ȷ��x����
		//��������
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(idlabel)
																		.addComponent(idtext)
																		.addComponent(passwdlabel)
																		.addComponent(passwdtext).addComponent(conpasswdlabel).addComponent(conpasswdtext);
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(confirmbutton).addComponent(exitbutton);
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_4);
		//��������
		GroupLayout.SequentialGroup group1_3=layout.createSequentialGroup().addComponent(Logolabel).addGroup(group1_2);
		layout.setHorizontalGroup(group1_3);
		
		//��ֱ�飨��ȷ��y����
		//��������
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(idlabel).addComponent(idtext).addComponent(passwdlabel).addComponent(passwdtext).addComponent(conpasswdlabel).addComponent(conpasswdtext);
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(confirmbutton).addComponent(exitbutton);
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_4);
	
		//��������
		GroupLayout.ParallelGroup group2_3=layout.createParallelGroup().addComponent(Logolabel).addGroup(group2_2);
		layout.setVerticalGroup(group2_3);
		
		dg.setContentPane(panel);
		dg.pack();	
		dg.setLocationRelativeTo(null);		
	}
	public class ExitButton implements ActionListener//�˳�ע��
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dg.setVisible(false);
			LoginUI login=new LoginUI();//��¼������
			login.dg.setVisible(true);
		}
		
	}
	public class RegisterConfirm implements ActionListener//ע�����ȷ�ϰ�ť
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(passwdtext.getText().equals(""))//������������Ϊ��
			{
				JOptionPane.showMessageDialog(null,"�������������룡","����Ϊ��",JOptionPane.ERROR_MESSAGE);
			}
			else if(conpasswdtext.getText().equals(""))//ȷ������Ϊ��
			{
				JOptionPane.showMessageDialog(null, "���ٴ������������룡","ȷ������Ϊ��",JOptionPane.ERROR_MESSAGE);
			}
			else if(passwdtext.getText().equals(conpasswdtext.getText())==false)//�����������벻ͬ
			{
				JOptionPane.showMessageDialog(null, "ȷ�����벻��ȷ��","�������",JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try {
					//���÷�������ַ�Ͷ˿ں�
					String remoteName="127.0.0.1";
					InetAddress remoteAddr=InetAddress.getByName(remoteName);
					int remotePort=50000;
					clientSocket = new DatagramSocket();
					clientSocket.setSoTimeout(6000);//���ó�ʱʱ��
					//����ע����Ϣ
					Message msg=new Message();
					msg.setUserId(idtext.getText());//�û���
					msg.setPassword(passwdtext.getText());//����
					msg.setType("M_REGISTER");//�û�ע����Ϣ
					msg.setToAddr(remoteAddr);//Ŀ���ַ
					msg.setToPort(remotePort);//Ŀ��˿�
					byte[] data=Translate.ObjectToByte(msg);
					//����ע�ᱨ��
					DatagramPacket packet=new DatagramPacket(data,data.length,remoteAddr,remotePort);
					//�������ݱ���
					clientSocket.send(packet);
					//���շ��������ͱ���
					DatagramPacket backPacket=new DatagramPacket(data,data.length);
					clientSocket.receive(backPacket);
					clientSocket.setSoTimeout(0);//ȡ����ʱ
					Message backMsg=new Message();
					backMsg=(Message)Translate.ByteToObject(data);
					//����ע����
					if(backMsg.getType().equalsIgnoreCase("M_RSUCCESS"))//ע��ɹ�
					{
						JOptionPane.showMessageDialog(null, "���ڿ��Ե�¼����������","ע��ɹ�",JOptionPane.INFORMATION_MESSAGE);
						dg.setVisible(false);
						LoginUI login=new LoginUI();//��¼������
						login.dg.setVisible(true);
						count++;//ȷ���û���1
					}
					else if(backMsg.getType().equalsIgnoreCase("M_RFAILURE"))//ע��ʧ��
					{
						JOptionPane.showMessageDialog(null, "���ź�������δ��������ע�����룡","ע��ʧ��",JOptionPane.ERROR_MESSAGE);
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
