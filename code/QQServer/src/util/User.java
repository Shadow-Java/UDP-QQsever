package util;

import java.net.DatagramPacket;

//�û��������ڼ�¼�û�ID���û�����
public class User {
	private String UserId;//�û�ID
	private String Passwd;//�û�����
	private DatagramPacket packet=null;//�û�����
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getPasswd() {
		return Passwd;
	}
	public void setPasswd(String passwd) {
		Passwd = passwd;
	}
	public DatagramPacket getPacket() {
		return packet;
	}
	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}
}
