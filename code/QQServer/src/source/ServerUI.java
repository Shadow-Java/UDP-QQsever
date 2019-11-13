package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.*;

//����������
public class ServerUI {
	private JFrame jf;
	private JPanel panel;
	private GroupLayout layout;
	private JLabel namelabel;//����������
	private JTextField nametext;
	private JLabel portlabel;//�������˿�
	private JTextField porttext;
	private JButton startbutton;
	public JTextArea printtext;
	private JScrollPane scroll;//��������
	
	public ServerUI()//���캯��
	{
		jf=new JFrame("��������");
		initComponent();
		jf.setVisible(true);
	}
	public void initComponent()
	{
		panel=new JPanel();
		layout=new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);// �Զ��������֮��ļ�϶
		layout.setAutoCreateContainerGaps(true);// �Զ����������봥�������߿�����֮��ļ�϶
		
		namelabel=new JLabel("������������");
		nametext=new JTextField("127.0.0.1",10);
		nametext.setEditable(false);
		portlabel=new JLabel("�˿���");
		porttext=new JTextField("50000",10);
		porttext.setEditable(false);
		startbutton=new JButton("����");
		startbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				StartButton(ev);
				}
			});		
		printtext=new JTextArea(20,50);
		scroll=new JScrollPane(printtext);
		
		//ˮƽ�飨��ȷ��x����
		//��������
		GroupLayout.SequentialGroup group1_1=layout.createSequentialGroup().addComponent(namelabel).addComponent(nametext).addComponent(portlabel).addComponent(porttext).addComponent(startbutton);
		//����
		GroupLayout.ParallelGroup group1_2=layout.createParallelGroup().addGroup(group1_1).addComponent(scroll);
		layout.setHorizontalGroup(group1_2);
		
		//��ֱ�飨��ȷ��y����
		//��������
		GroupLayout.ParallelGroup group2_1=layout.createParallelGroup().addComponent(namelabel).addComponent(nametext).addComponent(portlabel).addComponent(porttext).addComponent(startbutton);
		//����
		GroupLayout.SequentialGroup group2_2=layout.createSequentialGroup().addGroup(group2_1).addComponent(scroll);
		layout.setVerticalGroup(group2_2);
		
		jf.setContentPane(panel);
		jf.pack();	
		jf.setLocationRelativeTo(null);
	}
	public void StartButton(ActionEvent ev)//������������ť
	{
		try {
			//��ȡ��������ַ�Ͷ˿ں�
			//String hostName=nametext.getText();
			int hostPort=Integer.parseInt(porttext.getText());
			//����UDP���ݰ�����ָ���˿ڼ���
			DatagramSocket serverSocket=new DatagramSocket(hostPort);
			printtext.append("��������������ʼ����....\n");
			Thread recvThread=new ReceiveMessage(serverSocket,this);
            recvThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "������ʾ", JOptionPane.ERROR_MESSAGE);
		}
		startbutton.setEnabled(false);//���ɱ��ٴε��
	}
	
	public static void main(String[] args)
	{
		ServerUI server=new ServerUI();
		server.jf.setVisible(true);
	}	
}
