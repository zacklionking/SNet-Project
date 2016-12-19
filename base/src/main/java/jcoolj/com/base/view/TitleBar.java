package jcoolj.com.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import jcoolj.com.base.R;
import jcoolj.com.base.anim.DrawerAnimator;
import jcoolj.com.base.utils.PixelUtils;

public class TitleBar extends LinearLayout {

    private static final int TYPE_ACTIONBAR = 0;
    private static final int TYPE_TAB = 1;
    private static final int TYPE_CUSTOM = 2;

    private int type = TYPE_ACTIONBAR;

    private LayoutInflater layoutInflater;

    private DrawerAnimator drawerAnimator;

    private int statusBarHeight;

    private ViewGroup titleContainer;
    private LinearLayout actionLeftContainer;
    private LinearLayout actionRightContainer;
    private TextView title;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setFitsSystemWindows(true);
//        setClipToPadding(true);
        layoutInflater = LayoutInflater.from(context);
        drawerAnimator = new DrawerAnimator();
        setOrientation(VERTICAL);
        inflate(context, R.layout.view_title_bar, this);
        initDefault();
        title = (TextView) findViewById(R.id.bar_title);
    }

    private void initDefault(){
        Resources res = getResources();
//        setBackgroundColor(res.getColor(R.color.colorActionBar));
        setBackgroundResource(R.drawable.bg_title_bar);
        titleContainer = (ViewGroup) findViewById(R.id.title_bar_container);
        actionLeftContainer = (LinearLayout) findViewById(R.id.title_bar_left_actions);
        actionRightContainer = (LinearLayout) findViewById(R.id.title_bar_right_actions);
        statusBarHeight = PixelUtils.getStatusBarHeight(getContext());
        LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, res.getDimensionPixelSize(R.dimen.title_bar_height) - statusBarHeight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            params.topMargin = statusBarHeight;
        titleContainer.setLayoutParams(params);
    }

    public void setTitle(CharSequence title){
        this.title.setText(title);
    }

    public void setGoback(){
        View v = findViewById(R.id.back);
        v.setVisibility(View.VISIBLE);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).onBackPressed();
            }
        });
    }

    public void addView(View view){

    }

    public void setViewPager(final ViewPager viewPager){
        type = TYPE_TAB;
        Resources res = getResources();
        SmartTabLayout tabLayout = (SmartTabLayout) findViewById(R.id.pager_tab);
        tabLayout.setVisibility(VISIBLE);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                res.getDimensionPixelSize(R.dimen.tab_bar_height)));
        tabLayout.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCustomView(View view){
        type = TYPE_CUSTOM;
        titleContainer.setVisibility(GONE);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        addView(view);
    }

    public void addActionLeft(View v){
        actionLeftContainer.addView(v);
    }

    public void addActionLeft(@DrawableRes int res, OnClickListener listener){
        actionLeftContainer.addView(createImageActionButton(res, listener));
    }

    public View addActionRight(View v){
        actionRightContainer.addView(v);
        return v;
    }

    public Button addActionRight(CharSequence name, OnClickListener listener){
        Button actionBtn = createActionButton(name, listener);
        actionRightContainer.addView(actionBtn);
        return actionBtn;
    }

    public ImageButton addActionRight(@DrawableRes int res, OnClickListener listener){
        ImageButton actionBtn = createImageActionButton(res, listener);
        actionRightContainer.addView(actionBtn);
        return actionBtn;
    }

    private Button createActionButton(CharSequence name, OnClickListener listener){
        Button btn = (Button) layoutInflater.inflate(R.layout.view_action_button, actionRightContainer, false);
        btn.setText(name);
        btn.setOnClickListener(listener);
        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return btn;
    }

    private ImageButton createImageActionButton(@DrawableRes int res, OnClickListener listener){
        ImageButton btn = new ImageButton(getContext());
        btn.setImageResource(res);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.title_bar_action_btn_width), ViewGroup.LayoutParams.MATCH_PARENT));
        btn.setOnClickListener(listener);
        return btn;
    }

    public void setOnTabClickListener(SmartTabLayout.OnTabClickListener listener){
        SmartTabLayout tabLayout = (SmartTabLayout) findViewById(R.id.pager_tab);
        tabLayout.setOnTabClickListener(listener);
    }

    public CharSequence getTitle(){
        return title.getText();
    }

    public void show(){
        if(getVisibility() == GONE)
            drawerAnimator.toggle(this, true);
    }

    public void hide(){
        if(getVisibility() != GONE)
            drawerAnimator.toggle(this, false);
    }

    public int getTitleBarHeight(){
        Resources res = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return res.getDimensionPixelSize(R.dimen.title_bar_height) + res.getDimensionPixelSize(R.dimen.tab_bar_height);
        else
            return res.getDimensionPixelSize(R.dimen.title_bar_height) + res.getDimensionPixelSize(R.dimen.tab_bar_height) - statusBarHeight;
    }

}
