package com.mlh.component;

import java.util.Date;

import com.mlh.Config;
import com.mlh.R;
import com.mlh.model.ICallBack;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


/**
 * @author Matteo
 *�̳���ListView��ͨ�����������ܹ���̬ˢ��ListView
 */
public class RefreshListView extends ListView implements OnScrollListener {
	private LinearLayout llFooter;
	private TextView tvRefreshDown,tvRefreshUp;
	private ProgressBar pbRefresh;
	private int FooterHeight;
	private Context ctxParent;
	private int CurrentScrollState;
	private final static int NONE_PULL_REFRESH = 0;   //����״̬
	private final static int ENTER_PULL_REFRESH = 1;  //��������ˢ��״̬
	private final static int OVER_PULL_REFRESH = 2;   //��������ˢ��״̬
	private final static int EXIT_PULL_REFRESH = 3;     //���ֺ󷴵������״̬
	private int PullRefreshState = 0;                         //��¼ˢ��״̬
	private int PPullRefreshState = 0;                         //��¼ˢ��״̬
	private float DownY,MoveY;
	private final static int REFRESH_BACKING = 0;      //������
	private final static int REFRESH_BACED = 1;        //�ﵽˢ�½��ޣ�����������
	private final static int REFRESH_RETURN = 2;       //û�дﵽˢ�½��ޣ�����
	public final static int REFRESH_DONE = 3;         //�������ݽ���
	private ICallBack cbActivity;
	
	public RefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ctxParent=context;
		
		this.setOnItemClickListener(new OnItemClickListener(){


			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cbActivity.clickItem(parent, view, position, id);
			}});
		this.addFooter();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.ctxParent=context;
		this.setOnItemClickListener(new OnItemClickListener(){


			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cbActivity.clickItem(parent, view, position, id);
			}});
		this.addFooter();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//if(!this.HasFooter)return;
	    this.CurrentScrollState = scrollState;
	    //Fun.Log("CurrentScrollState"+this.CurrentScrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//if(!this.HasFooter)return;
		int lastVisibleItem=firstVisibleItem+visibleItemCount;
		//Footer��ʼ��ʾ�����ǻ�δ��ʾ��ȫ
		if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && lastVisibleItem == totalItemCount
	            && (this.llFooter.getTop() > 0 && this.llFooter.getTop()<this.getBottom() && this.llFooter.getBottom() > this.getBottom())) {
	        //�����ҽ���������ˢ��״̬
	        if (this.PullRefreshState == NONE_PULL_REFRESH) {
	        	this.PullRefreshState = ENTER_PULL_REFRESH;
	        }
	    } 
		//Footer��ʾ��ȫ
		else if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && lastVisibleItem == totalItemCount
	            && (this.llFooter.getBottom() <= this.getBottom())) {
	        //�����ﵽ���ޣ���������ˢ��״̬
	        if (this.PullRefreshState == ENTER_PULL_REFRESH || this.PullRefreshState == NONE_PULL_REFRESH) {
	        	this.PullRefreshState = OVER_PULL_REFRESH;
	            //�����ǽ�������ˢ��״̬��Ҫ����һ����ʾ�ı�
	            this.DownY = this.MoveY;//���ں������������Ч��
	            this.SetRefreshOverText();
	        }
	    }
		//Footer��ȫ����ʾ
		else if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL && lastVisibleItem < totalItemCount) {
	        //��ˢ����
	        if (this.PullRefreshState == ENTER_PULL_REFRESH) {
	        	this.PullRefreshState = NONE_PULL_REFRESH;
	        }
	    }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            //���°���λ��
	            //�ı�
	            this.DownY = ev.getY();
	            break;
	        case MotionEvent.ACTION_MOVE:
	            //�ƶ�ʱ��ָ��λ��
	        	this.MoveY = ev.getY();
	            if (this.PullRefreshState == OVER_PULL_REFRESH){//||(this.llFooter.getBottom()>0&&this.llFooter.getBottom()<=this.getBottom())) {
	                //ע�������mDownY��onScroll�ĵڶ���else�б��ı���
	            	this.llFooter.setPadding(this.llFooter.getPaddingLeft(),
	                        this.llFooter.getPaddingTop(), //1/3�����ۿ�
	                        this.llFooter.getPaddingRight(),
	                        (int)((this.DownY-this.MoveY)/3));
	            }else if(this.PullRefreshState==NONE_PULL_REFRESH && this.MoveY<this.DownY
	            		&& this.llFooter.getBottom()<=this.getBottom() && this.llFooter.getBottom()>0){
	            	this.PullRefreshState=OVER_PULL_REFRESH;
	            	this.SetRefreshOverText();
	            	this.llFooter.setPadding(this.llFooter.getPaddingLeft(),
	                        this.llFooter.getPaddingTop(), //1/3�����ۿ�
	                        this.llFooter.getPaddingRight(),
	                        (int)((this.DownY-this.MoveY)/3));
	            }
	            break;
	        case MotionEvent.ACTION_UP:
	        	 //when you action up, it will do these:
	            //1. roll back util header topPadding is 0
	            //2. hide the header by setSelection(1)
	            if (this.PullRefreshState == OVER_PULL_REFRESH || this.PullRefreshState == ENTER_PULL_REFRESH) {
	            	new Thread() {
	                    public void run() {
	                        Message msg;
	                        while(llFooter.getPaddingBottom() > 1) {
	                            msg = mHandler.obtainMessage();
	                            msg.what = REFRESH_BACKING;
	                            mHandler.sendMessage(msg);
	                            try {
	                                sleep(15);//��һ�㷴������һ���Ӿ͵���ȥ��
	                            } catch (InterruptedException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                        msg = mHandler.obtainMessage();
	                        if (PullRefreshState == OVER_PULL_REFRESH) {
	                            msg.what = REFRESH_BACED;//��ʼ�������ݣ�Ȼ�󷵻�
	                        } else {
	                            msg.what = REFRESH_RETURN;//δ�ﵽˢ�½��ޣ�ֱ�ӷ���
	                        }
	                        mHandler.sendMessage(msg);
	                    };
	                }.start();
	            }
	            break;
	    }
	    return super.onTouchEvent(ev);
	}
	
	public void addFooter(){
		this.llFooter = (LinearLayout) LayoutInflater.from(this.ctxParent).inflate(R.layout.layout_pic_list_footer, null);
	    this.addFooterView(this.llFooter);
	    this.tvRefreshDown = (TextView) findViewById(R.id.tvRefreshDown);
	    this.tvRefreshUp = (TextView) findViewById(R.id.tvRefreshUp);
	    this.pbRefresh = (ProgressBar) findViewById(R.id.pbRefresh);
		this.SetRefreshDoneText();
	    
	    this.measureView(this.llFooter);
	    this.FooterHeight = this.llFooter.getMeasuredHeight();
	    
	    this.setOnScrollListener(this);
	}
	
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
	    ViewGroup.LayoutParams p = child.getLayoutParams();
	    if (p == null) {
	        p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	    }
	 
	    int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
	    int lpHeight = p.height;
	    int childHeightSpec;
	    if (lpHeight > 0) {
	        childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
	                MeasureSpec.EXACTLY);
	    } else {
	        childHeightSpec = MeasureSpec.makeMeasureSpec(0,
	                MeasureSpec.UNSPECIFIED);
	    }
	    child.measure(childWidthSpec, childHeightSpec);
	}
    
	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case REFRESH_BACKING:
	            llFooter.setPadding(llFooter.getPaddingLeft(),
	                    llFooter.getPaddingTop(),
	                    llFooter.getPaddingRight(),
	                    (int) (llFooter.getPaddingBottom()*0.75f));
	            break;
	        case REFRESH_BACED://��ʼ��������
	        	SetRegreshingText();
	            PullRefreshState = EXIT_PULL_REFRESH;
	            cbActivity.refresh(mHandler);
	            break;
	        case REFRESH_RETURN:
	            //δ�ﵽˢ�½��ޣ�����
	        	SetRefreshNoneText();
	        	llFooter.setPadding(llFooter.getPaddingLeft(),
	                    llFooter.getPaddingTop(),
	                    llFooter.getPaddingRight(),
	                    0);
	            PullRefreshState = NONE_PULL_REFRESH;
	            //setSelection(1);
	            break;
	        case REFRESH_DONE:
	            //ˢ�½����󣬻ָ�ԭʼĬ��״̬
	        	SetRefreshDoneText();
	        	llFooter.setPadding(llFooter.getPaddingLeft(),
	                    0,
	                    llFooter.getPaddingRight(),
	                    llFooter.getPaddingBottom());
	            PullRefreshState = NONE_PULL_REFRESH;
	            //setSelection(1);
	            break;
	        default:
	            break;
	        }
	    }
	};
	
	public void SetCallBack(ICallBack cb){
		this.cbActivity=cb;
	}
	
	private void SetRegreshingText(){
        tvRefreshDown.setText("���ڼ���...");
        this.pbRefresh.setVisibility(View.VISIBLE);
        this.tvRefreshUp.setVisibility(View.INVISIBLE);
	}
	
	private void SetRefreshNoneText(){
    	tvRefreshDown.setText("����ˢ��");
    	this.tvRefreshDown.setVisibility(View.VISIBLE);
    	pbRefresh.setVisibility(View.INVISIBLE);
	}
	
	private void SetRefreshDoneText(){
    	tvRefreshDown.setText("����ˢ��");
    	pbRefresh.setVisibility(View.INVISIBLE);
    
    	tvRefreshUp.setText(DateFormat.format(Config.DEFAULT_DATEFORMAT, new Date()).toString());
    	this.tvRefreshUp.setVisibility(View.VISIBLE);
    	this.tvRefreshDown.setVisibility(View.VISIBLE);
	}

	private void SetRefreshOverText()
	{
        this.tvRefreshUp.setText("����ˢ��");
        this.tvRefreshUp.setVisibility(View.VISIBLE);
        this.tvRefreshDown.setVisibility(View.INVISIBLE);
	}
}
