package util;

import java.net.DatagramPacket;
//文件类，用于服务器文件存储，文件记录
public class Fileup {
	private String userId;//用户ID
	private String filename;//文件路径
	private String filecontent;//文件内容
	private String fileId;//文件Id
	private DatagramPacket packet=null;//用户报文	
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
