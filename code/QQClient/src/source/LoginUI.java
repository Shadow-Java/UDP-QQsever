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

//�û�ע�ᡢ��¼ϵͳ
public class LoginUI{
	public JDialog dg;
	private JLabel Logolabel;//logoͼƬ
	private JPanel panel;
	private GroupLayout layout;	
	private JLabel idlabel;
	private JTextField idtext;
	private JTextField passwdtext;
	private JLabel passwdlabel;
	private JButton loginbutton;
	private JButton registerbutton;
	
	public LoginUI()//���캯��
	{
		dg=new JDialog();
		initComponents();
		dg.setTitle("�û���¼");
		//this.setBounds(100, 100, 400, 300);
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
		
		idlabel=new JLabel("�˺�");
		idtext=new JTextField(20);
		passwdlabel=new JLabel("����");
		passwdtext=new JTextField(20);
		
		registerbutton=new JButton("ע��");
		RegisterButton listener0=new RegisterButton();
		registerbutton.addActionListener(listener0);
		
		loginbutton=new JButton("��¼");
		LoginButton listener1=new LoginButton();
		loginbutton.addActionListener(listener1);
		//ˮƽ�飨��ȷ��x����
		//��������
		GroupLayout.ParallelGroup group1_1=layout.createParallelGroup().addComponent(idlabel)
																		.addComponent(idtext)
																		.addComponent(passwdlabel)
																		.addComponent(passwdtext);
		//����
		GroupLayout.SequentialGroup group1_2=layout.createSequentialGroup().addComponent(registerbutton).addComponent(loginbutton);
		//����
		GroupLayout.ParallelGroup group1_3=layout.createParallelGroup().addGroup(group1_1).addGroup(group1_2);
		//��������
		GroupLayout.SequentialGroup group1_4=layout.createSequentialGroup().addComponent(Logolabel).addGroup(group1_3);
		layout.setHorizontalGroup(group1_4);
		
		//��ֱ�飨��ȷ��y����
		//��������
		GroupLayout.SequentialGroup group2_1=layout.createSequentialGroup().addComponent(idlabel).addComponent(idtext).addComponent(passwdlabel).addComponent(passwdtext);
		//����
		GroupLayout.ParallelGroup group2_2=layout.createParallelGroup().addComponent(registerbutton).addComponent(loginbutton);
		//����
		GroupLayout.SequentialGroup group2_3=layout.createSequentialGroup().addGroup(group2_1).addGroup(group2_2);
		//��������
		GroupLayout.ParallelGroup group2_4=layout.createParallelGroup().addComponent(Logolabel).addGroup(group2_3);
		layout.setVerticalGroup(group2_4);
		
		dg.setContentPane(panel);
		dg.pack();	
		dg.setLocationRelativeTo(null);		
	}	
	public class RegisterButton implements ActionListener//ע�ᰴť
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			dg.setVisible(false);
			RegisterUI rt=new RegisterUI();	//���û�ע�ᴰ��
			String str=Integer.toString(1111+RegisterUI.count);
			rt.idtext.setText(str);	
			rt.idtext.setEditable(false);
		}		
	}
	public class LoginButton implements ActionListener//��¼��ť
	{
		private DatagramSocket clientSocket;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(idtext.getText().equals(""))//����˺�Ϊ��
			{
				JOptionPane.showMessageDialog(null,"�����������˺ţ�","�˺�Ϊ��",JOptionPane.ERROR_MESSAGE);
			}
			else if(passwdtext.getText().equals(""))//����Ϊ��
			{
				JOptionPane.showMessageDialog(null,"�������������룡","����Ϊ��",JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try {
					//���÷�������ַ�Ͷ˿ں�
					String remoteName="127.0.0.1";
					InetAddress remoteAddr=InetAddress.getByName(remoteName);
					int remotePort=50000;
					clientSocket=new DatagramSocket();
					clientSocket.setSoTimeout(6000);//���ó�ʱʱ��
					//������¼��Ϣ
					Message msg=new Message();
					msg.setUserId(idtext.getText());//��ȡ�û�id
					msg.setPassword(passwdtext.getText());//��ȡ�û�����
					msg.setType("M_LOGIN");//�û���¼��Ϣ
					msg.setToAddr(remoteAddr);//Ŀ���ַ
					msg.setToPort(remotePort);//Ŀ��˿�
					byte[] data=Translate.ObjectToByte(msg);//��Ϣ���л�
					//�����¼����
					DatagramPacket packet=new DatagramPacket(data,data.length,remoteAddr,remotePort);
					//�������ݱ���
					clientSocket.send(packet);
					//�������ݻ��ͱ���
					DatagramPacket backPacket=new DatagramPacket(data,data.length);
					clientSocket.receive(backPacket);
					clientSocket.setSoTimeout(0);//ȡ����ʱ
					Message backMsg=new Message();
					backMsg=(Message)Translate.ByteToObject(data);
					//��¼���
					System.out.println("�ͻ����յ���Ϣ����"+msg.getType());
					if(backMsg.getType().equalsIgnoreCase("M_SUCCESS"))//��¼�ɹ�
					{
						System.out.println("��½�ɹ�");
						JOptionPane.showMessageDialog(null, "��ϲ�㣬���ڿ��Կ�ʼ��������","��¼�ɹ�",JOptionPane.INFORMATION_MESSAGE);
						dg.setVisible(false);
						ClientUI chat=new ClientUI(clientSocket,msg);//���������ҿͻ���
						chat.jf.setTitle(msg.getUserId()+"�ͻ���");
						chat.jf.setVisible(true);
					}
					else if(backMsg.getType().equalsIgnoreCase("M_FAILURE"))
					{
						JOptionPane.showMessageDialog(null, "�����˺Ż����������\n�����������Ƿ�������","��¼ʧ��",JOptionPane.ERROR_MESSAGE);
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
