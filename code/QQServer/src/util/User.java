package util;

import java.net.DatagramPacket;

//用户对象，用于记录用户ID和用户报文
public class User {
	private String UserId;//用户ID
	private String Passwd;//用户密码
	private DatagramPacket packet=null;//用户报文
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
