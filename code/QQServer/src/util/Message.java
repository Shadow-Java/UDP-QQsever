package util;

import java.io.Serializable;
import java.net.InetAddress;

//消息类，定义消息结构，规定消息协议
public class Message implements Serializable{//序列化消息必须实现Serializable接口
	private String userId=null;//用户ID，唯一标识
	private String password=null;//用户密码
	private String filename=null;//文件名
	private String fileId=null;//文件Id
	/*消息类型
	 * M_LOGIN:用户登录消息
	 * M_REGISTER:用户注册消息
	 * M_SUCCESS:登录成功消息
	 * M_RSUCCESS:注册成功消息
	 * M_FAILUER:登录失败消息
	 * M_RFAILURE:注册失败消息
	 * M_ACK:服务器对客户端的回应消息
	 * M_MSG:会话消息
	 * M_PRIVATE:用户私聊消息
	 * M_QUIT:用户退出消息
	 * M_FILE：上传文件
	 * M_REQFILE:请求下载文件
	 * */
	private String type=null;//消息类型
	private String text=null;//消息内容
	private InetAddress toAddr=null;//目标用户地址
	private int toPort;//目标用户端口
	private String targetId=null;//目标用户ID
	
	//get、set方法
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
