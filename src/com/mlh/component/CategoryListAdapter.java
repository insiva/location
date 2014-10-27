package com.mlh.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mlh.model.Category;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CategoryListAdapter extends BaseAdapter {
	//private LayoutInflater lifCateItem;
	public ArrayList<Category> aryCate = null;
	private Context ctxActivity;
	public CategoryListAdapter(Context context,RefreshListView v) {
		//this.lifPicItem = LayoutInflater.from(context);
		this.aryCate = new ArrayList<Category>();
		this.ctxActivity=context;
	}

	public void readXML(String xs)  {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser=null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CategoryHandler handler = new CategoryHandler();
		StringReader read = new StringReader(xs);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		try {
			parser.parse(source, handler);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.aryCate.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Category pc=this.aryCate.get(position);
		LinearLayout line=new LinearLayout(this.ctxActivity);
		line.setOrientation(LinearLayout.HORIZONTAL);
		TextView tvCate=new TextView(this.ctxActivity);
		tvCate.setText(pc.getDescription());
		tvCate.setPadding(10, 10, 0, 10);
		tvCate.setTextSize(25);
		line.addView(tvCate,MTLayoutParams.GetCateItem());
		return line;
	}

	public class CategoryHandler extends DefaultHandler {
		// private ArrayList<Picture> aryPics;

		public CategoryHandler() {
			if (aryCate == null) {
				aryCate = new ArrayList<Category>();
			}
		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (!localName.equals("node")) {
				return;
			}
			Category pc = new Category(atts);
			aryCate.add(pc);
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			if (!localName.equals("picture")) {
				return;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

		}
	}
	
	public void clearArray(){
		this.aryCate.clear();
	}

}
