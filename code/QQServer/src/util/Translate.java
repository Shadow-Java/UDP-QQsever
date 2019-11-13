package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//�������л��ͷ����л�
public class Translate {
	//���л�������ת��Ϊ�ֽ�������ʽobj->oo->bo->buffer
	public static byte[] ObjectToByte(Object obj)
	{
		byte[] buffer=null;
		try{
			ByteArrayOutputStream bo=new ByteArrayOutputStream();//�ֽ����������
			ObjectOutputStream oo=new ObjectOutputStream(bo);//���������
			oo.writeObject(obj);//objд�������
			buffer=bo.toByteArray();//�������л�Ϊ�ֽ�����
		}catch(IOException ex){}
		return buffer;
	}
	
	//�����л����ֽ�����ת��Ϊ������ʽbuffer->bi->oi->obj
	public static Object ByteToObject(byte[] buffer)
	{
		Object obj=null;
		try{
			ByteArrayInputStream bi=new ByteArrayInputStream(buffer);//�ֽ�����������
			ObjectInputStream oi=new ObjectInputStream(bi);//����������
			obj=oi.readObject();//�Ӷ����������ж�������
		}catch(IOException | ClassNotFoundException ex){}
		return obj;
	}
}
