package util;

import java.net.DatagramPacket;
//�ļ��࣬���ڷ������ļ��洢���ļ���¼
public class Fileup {
	private String userId;//�û�ID
	private String filename;//�ļ�·��
	private String filecontent;//�ļ�����
	private String fileId;//�ļ�Id
	private DatagramPacket packet=null;//�û�����	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilecontent() {
		return filecontent;
	}
	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}
	public DatagramPacket getPacket() {
		return packet;
	}
	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
}
