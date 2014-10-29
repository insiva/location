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
 *继承自ListView，通过下拉动作能够动态刷新ListView
 */
public class RefreshListView extends ListView implements OnScrollListener {
	private LinearLayout llFooter;
	private TextView tvRefreshDown,tvRefreshUp;
	private ProgressBar pbRefresh;
	private int FooterHeight;
	private Context ctxParent;
	private int CurrentScrollState;
	private final static int NONE_PULL_REFRESH = 0;   //正常状态
	private final static int ENTER_PULL_REFRESH = 1;  //进入上拉刷新状态
	private final static int OVER_PULL_REFRESH = 2;   //进入松手刷新状态
	private final static int EXIT_PULL_REFRESH = 3;     //松手后反弹后加载状态
	private int PullRefreshState = 0;                         //记录刷新状态
	private int PPullRefreshState = 0;                         //记录刷新状态
	private float DownY,MoveY;
	private final static int REFRESH_BACKING = 0;      //反弹中
	private final static int REFRESH_BACED = 1;        //达到刷新界限，反弹结束后
	private final static int REFRESH_RETURN = 2;       //没有达到刷新界限，返回
	public final static int REFRESH_DONE = 3;         //加载数据结束
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
		//Footer开始显示，但是还未显示完全
		if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && lastVisibleItem == totalItemCount
	            && (this.llFooter.getTop() > 0 && this.llFooter.getTop()<this.getBottom() && this.llFooter.getBottom() > this.getBottom())) {
	        //进入且仅进入上拉刷新状态
	        if (this.PullRefreshState == NONE_PULL_REFRESH) {
	        	this.PullRefreshState = ENTER_PULL_REFRESH;
	        }
	    } 
		//Footer显示完全
		else if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && lastVisibleItem == totalItemCount
	            && (this.llFooter.getBottom() <= this.getBottom())) {
	        //上拉达到界限，进入松手刷新状态
	        if (this.PullRefreshState == ENTER_PULL_REFRESH || this.PullRefreshState == NONE_PULL_REFRESH) {
	        	this.PullRefreshState = OVER_PULL_REFRESH;
	            //下面是进入松手刷新状态需要做的一个显示改变
	            this.DownY = this.MoveY;//用于后面的下拉特殊效果
	            this.SetRefreshOverText();
	        }
	    }
		//Footer完全不显示
		else if (this.CurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL && lastVisibleItem < totalItemCount) {
	        //不刷新了
	        if (this.PullRefreshState == ENTER_PULL_REFRESH) {
	        	this.PullRefreshState = NONE_PULL_REFRESH;
	        }
	    }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            //记下按下位置
	            //改变
	            this.DownY = ev.getY();
	            break;
	        case MotionEvent.ACTION_MOVE:
	            //移动时手指的位置
	        	this.MoveY = ev.getY();
	            if (this.PullRefreshState == OVER_PULL_REFRESH){//||(this.llFooter.getBottom()>0&&this.llFooter.getBottom()<=this.getBottom())) {
	                //注意下面的mDownY在onScroll的第二个else中被改变了
	            	this.llFooter.setPadding(this.llFooter.getPaddingLeft(),
	                        this.llFooter.getPaddingTop(), //1/3距离折扣
	                        this.llFooter.getPaddingRight(),
	                        (int)((this.DownY-this.MoveY)/3));
	            }else if(this.PullRefreshState==NONE_PULL_REFRESH && this.MoveY<this.DownY
	            		&& this.llFooter.getBottom()<=this.getBottom() && this.llFooter.getBottom()>0){
	            	this.PullRefreshState=OVER_PULL_REFRESH;
	            	this.SetRefreshOverText();
	            	this.llFooter.setPadding(this.llFooter.getPaddingLeft(),
	                        this.llFooter.getPaddingTop(), //1/3距离折扣
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
	                                sleep(15);//慢一点反弹，别一下子就弹回去了
	                            } catch (InterruptedException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                        msg = mHandler.obtainMessage();
	                        if (PullRefreshState == OVER_PULL_REFRESH) {
	                            msg.what = REFRESH_BACED;//开始加载数据，然后返回
	                        } else {
	                            msg.what = REFRESH_RETURN;//未达到刷新界限，直接返回
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
	        case REFRESH_BACED://开始加载数据
	        	SetRegreshingText();
	            PullRefreshState = EXIT_PULL_REFRESH;
	            cbActivity.refresh(mHandler);
	            break;
	        case REFRESH_RETURN:
	            //未达到刷新界限，返回
	        	SetRefreshNoneText();
	        	llFooter.setPadding(llFooter.getPaddingLeft(),
	                    llFooter.getPaddingTop(),
	                    llFooter.getPaddingRight(),
	                    0);
	            PullRefreshState = NONE_PULL_REFRESH;
	            //setSelection(1);
	            break;
	        case REFRESH_DONE:
	            //刷新结束后，恢复原始默认状态
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
        tvRefreshDown.setText("正在加载...");
        this.pbRefresh.setVisibility(View.VISIBLE);
        this.tvRefreshUp.setVisibility(View.INVISIBLE);
	}
	
	private void SetRefreshNoneText(){
    	tvRefreshDown.setText("下拉刷新");
    	this.tvRefreshDown.setVisibility(View.VISIBLE);
    	pbRefresh.setVisibility(View.INVISIBLE);
	}
	
	private void SetRefreshDoneText(){
    	tvRefreshDown.setText("下拉刷新");
    	pbRefresh.setVisibility(View.INVISIBLE);
    
    	tvRefreshUp.setText(DateFormat.format(Config.DEFAULT_DATEFORMAT, new Date()).toString());
    	this.tvRefreshUp.setVisibility(View.VISIBLE);
    	this.tvRefreshDown.setVisibility(View.VISIBLE);
	}

	private void SetRefreshOverText()
	{
        this.tvRefreshUp.setText("松手刷新");
        this.tvRefreshUp.setVisibility(View.VISIBLE);
        this.tvRefreshDown.setVisibility(View.INVISIBLE);
	}
}
