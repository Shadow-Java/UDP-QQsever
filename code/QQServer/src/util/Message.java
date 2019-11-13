package util;

import java.io.Serializable;
import java.net.InetAddress;

//��Ϣ�࣬������Ϣ�ṹ���涨��ϢЭ��
public class Message implements Serializable{//���л���Ϣ����ʵ��Serializable�ӿ�
	private String userId=null;//�û�ID��Ψһ��ʶ
	private String password=null;//�û�����
	private String filename=null;//�ļ���
	private String fileId=null;//�ļ�Id
	/*��Ϣ����
	 * M_LOGIN:�û���¼��Ϣ
	 * M_REGISTER:�û�ע����Ϣ
	 * M_SUCCESS:��¼�ɹ���Ϣ
	 * M_RSUCCESS:ע��ɹ���Ϣ
	 * M_FAILUER:��¼ʧ����Ϣ
	 * M_RFAILURE:ע��ʧ����Ϣ
	 * M_ACK:�������Կͻ��˵Ļ�Ӧ��Ϣ
	 * M_MSG:�Ự��Ϣ
	 * M_PRIVATE:�û�˽����Ϣ
	 * M_QUIT:�û��˳���Ϣ
	 * M_FILE���ϴ��ļ�
	 * M_REQFILE:���������ļ�
	 * */
	private String type=null;//��Ϣ����
	private String text=null;//��Ϣ����
	private InetAddress toAddr=null;//Ŀ���û���ַ
	private int toPort;//Ŀ���û��˿�
	private String targetId=null;//Ŀ���û�ID
	
	//get��set����
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public InetAddress getToAddr() {
		return toAddr;
	}
	public void setToAddr(InetAddress toAddr) {
		this.toAddr = toAddr;
	}
	public int getToPort() {
		return toPort;
	}
	public void setToPort(int toPort) {
		this.toPort = toPort;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}		
}
