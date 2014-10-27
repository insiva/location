package com.mlh.communication;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

public interface IXml<T> {
	public T getByAttributes(Attributes attr);
	public T getByXmlString(String xmlStr);
	public String toXml(T t);
	public void addToXmlDocument(XmlSerializer xs,T t) throws Exception;
}
