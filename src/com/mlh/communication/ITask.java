package com.mlh.communication;

/**
 * @author Matteo
 *一个与上传任务有关的操作，只应用于UploadTask中，只被Picture和Location继承
 */
public interface ITask {
public void upload();
}
