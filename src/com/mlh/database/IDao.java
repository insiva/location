package com.mlh.database;

import android.database.Cursor;

/**
 * @author Matteo
 *һ�������ݿ������ص�Interface
 * @param <T>
 */
public interface IDao<T> {
	
	public void insert(T t); // ���Ӳ���

	public void update(T t); // �޸Ĳ���

	public void delete(long id); // ɾ������

	public T getById(long id); // ��ID��ѯ����
	
	public T getByCursor(Cursor cur); // ���ݿ��¼��ʼ��
	
	public void updateUploadedFlag(long id);//�����Ƿ����ϴ��ֶ�
}
