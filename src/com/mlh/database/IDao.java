package com.mlh.database;

import android.database.Cursor;

/**
 * @author Matteo
 *一个与数据库操作相关的Interface
 * @param <T>
 */
public interface IDao<T> {
	
	public void insert(T t); // 增加操作

	public void update(T t); // 修改操作

	public void delete(long id); // 删除操作

	public T getById(long id); // 按ID查询操作
	
	public T getByCursor(Cursor cur); // 数据库记录初始化
	
	public void updateUploadedFlag(long id);//更新是否已上传字段
}
