package com.mlh.activity;

import java.util.ArrayList;

import com.mlh.Config;
import com.mlh.R;
import com.mlh.component.CategoryFragment;
import com.mlh.component.CategoryFragmentPagerAdapter;
import com.mlh.model.Category;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * @author Matteo
 *类别Activity，显示两个列表：日期列表和地点列表
 */
public class CategoryActivity extends FragmentActivity {
	private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTabDay, tvTabSite;

    private int currIndex = 0;
    private int offset = 1000;
    private int position_one;
    private Resources resources;
    Fragment fragSite,fragDay;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
        resources = getResources();
        init();
        initWidth();
        initViewPager();
		Config.logCurrentThreadID("CategoryActivity");
	}
	
	private void init() {
		tvTabDay = (TextView) findViewById(R.id.tvTabDay);
		tvTabSite = (TextView) findViewById(R.id.tvTabSite);
        tvTabDay.setOnClickListener(new MyOnClickListener(0));
        tvTabSite.setOnClickListener(new MyOnClickListener(1));
    }

    private void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        this.fragDay = CategoryFragment.newInstance(Category.DAY_TYPE,this);
        this.fragSite = CategoryFragment.newInstance(Category.SITE_TYPE,this);

        fragmentsList.add(this.fragDay);
        fragmentsList.add(this.fragSite);
        
        mPager.setAdapter(new CategoryFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initWidth() {
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;

        position_one = (int) (screenW / 2.0);
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;
        public MyOnClickListener(int i) {
            index = i;
        }
        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, 0, 0, 0);
                    tvTabSite.setTextColor(resources.getColor(R.color.LightWhite));
                } 
                tvTabDay.setTextColor(resources.getColor(R.color.White));
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_one, 0, 0);
                    tvTabDay.setTextColor(resources.getColor(R.color.LightWhite));
                } 
                tvTabSite.setTextColor(resources.getColor(R.color.White));
                break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    
}
