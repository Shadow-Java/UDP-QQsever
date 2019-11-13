package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//对象序列化和反序列化
public class Translate {
	//序列化：对象转化为字节数组形式obj->oo->bo->buffer
	public static byte[] ObjectToByte(Object obj)
	{
		byte[] buffer=null;
		try{
			ByteArrayOutputStream bo=new ByteArrayOutputStream();//字节数组输出流
			ObjectOutputStream oo=new ObjectOutputStream(bo);//对象输出流
			oo.writeObject(obj);//obj写入输出流
			buffer=bo.toByteArray();//对象序列化为字节数组
		}catch(IOException ex){}
		return buffer;
	}
	
	//反序列化：字节数组转换为对象形式buffer->bi->oi->obj
	public static Object ByteToObject(byte[] buffer)
	{
		Object obj=null;
		try{
			ByteArrayInputStream bi=new ByteArrayInputStream(buffer);//字节数组输入流
			ObjectInputStream oi=new ObjectInputStream(bi);//对象输入流
			obj=oi.readObject();//从对象输入流中读出对象
		}catch(IOException | ClassNotFoundException ex){}
		return obj;
	}
}
