package source;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import util.Message;
import util.Translate;

//�ͻ����߳̽��շ�������Ϣ�߳�
public class ReceiveMessage extends Thread{
	private DatagramSocket clientSocket;//�ͻ��˻Ự�׽���
	private ClientUI parentUI;//����
	private byte[]data=new byte[8096];//8k�ֽ�����
	private DefaultListModel userlistModel = new DefaultListModel();
	private DefaultListModel filelistModel = new DefaultListModel();
	private Boolean stopMe=true;//����ֹͣ�߳�
	//���캯��
	public ReceiveMessage(DatagramSocket socket,ClientUI parentUI)
	{
		this.clientSocket=socket;
		this.parentUI=parentUI;
	}
	//�߳����
	public void run()
	{
		while(true)
		{
			try {
				System.out.println("�ͻ����߳���������....");
				//�������ձ���
				DatagramPacket packet=new DatagramPacket(data,data.length);
				clientSocket.receive(packet);
				Message msg=new Message();
				msg=(Message)Translate.ByteToObject(data);//��ԭ��Ϣ����
				String userId=msg.getUserId();//��ȡ�û�ID
				System.out.println("�ͻ����յ���Ϣ����"+msg.getType());
				//������Ϣ���ͽ��з��ദ��
				if(msg.getType().equalsIgnoreCase("M_LOGIN"))//�����û���¼
				{
					//���������û�������ʾ��
					playSound("./sound/cough.wav");
					//������Ϣ����
					parentUI.sessiontext.append(userId+"�û�����������---/��ӭ/����\n");
					//�������û������б�
					userlistModel.addElement(userId);
					parentUI.userlist.setModel(userlistModel);
				}
				else if(msg.getType().equalsIgnoreCase("M_ACK"))//�Լ�����
				{
					playSound("./sound/fadeIn.wav");
					//��¼�ɹ������Լ������û��б�
					userlistModel.addElement(userId);
					parentUI.userlist.setModel(userlistModel);
				}
				else if(msg.getType().equalsIgnoreCase("M_MSG"))//�Ự��Ϣ
				{
					playSound("./sound/msg.wav");//������Ϣ��ʾ��
					//������Ϣ����
					parentUI.sessiontext.append(userId+"�û�˵��"+msg.getText()+"\n");
				}
				else if(msg.getType().equalsIgnoreCase("M_PRIVATE"))//˽����Ϣ
				{
					playSound("./sound/msg.wav");//������Ϣ��ʾ��
					//JOptionPane.showMessageDialog(null, "���û�����˽��","��Ϣ��ʾ",JOptionPane.INFORMATION_MESSAGE);
					parentUI.sessiontext.append(userId+"�û�˽�Ķ���˵��"+msg.getText()+"\n");
				}
				else if(msg.getType().equalsIgnoreCase("M_QUIT"))//�����û��˳�����
				{
					playSound("./sound/leave.wav");//������Ϣ��ʾ��
					//������Ϣ����
					parentUI.sessiontext.append(userId+"�뿪��������-----/�ټ�\n");
					//���ߣ��������û��б�ɾ��
					userlistModel.remove(userlistModel.indexOf(userId));
					parentUI.userlist.setModel(userlistModel);
					//clientSocket.close();
					Thread.sleep(5000);
					//stopMe=false;
					//Thread.yield();//�ó�CPU
				}
				else if(msg.getType().equalsIgnoreCase("M_FILE"))
				{
					playSound("./sound/msg.wav");//������Ϣ��ʾ��
					String filename=msg.getFilename();
					String fileId=msg.getFileId();
					parentUI.sessiontext.append("----"+userId+"�û��ϴ����ļ�"+filename+",�������ļ��б����鿴��----\n");
					filelistModel.addElement(fileId+"*"+filename+"--�ϴ��ߣ�"+userId);
					parentUI.filelist.setModel(filelistModel);					
				}
				else if(msg.getType().equalsIgnoreCase("M_REQFILE"))//�����������ӦM_REQFILE
				{
					//������ձ������ݣ�д���ļ�����
					parentUI.sessiontext.append(msg.getUserId()+"----�����ļ�"+msg.getFileId()+"*"+msg.getFilename()+"----\n");
					String filecontent=msg.getText();
					JFileChooser chooser=new JFileChooser();
					int option=chooser.showSaveDialog(new JLabel("������"));
					if(option==JFileChooser.APPROVE_OPTION)//�����ļ��������
					{
						File file=chooser.getSelectedFile();
						FileOutputStream fs=new FileOutputStream(file);//�ļ������
						fs.write(filecontent.getBytes());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(),"������ʾ",JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//����ָ��·����Ч
	public void playSound(String filename) {
		URL url = null;
		try {
			File f=new File(filename); //����·��
	        URI uri = f.toURI();
			url = uri.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}//����·��
		AudioClip aau; 
		aau = Applet.newAudioClip(url);
		aau.play();//����һ��
     }
}

