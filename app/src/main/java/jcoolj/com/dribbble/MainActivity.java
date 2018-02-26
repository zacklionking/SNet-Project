package jcoolj.com.dribbble;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jcoolj.com.base.BaseActivity;
import jcoolj.com.base.PageFragment;
import jcoolj.com.base.anim.DrawerAnimator;
import jcoolj.com.base.utils.PixelUtils;
import jcoolj.com.base.view.FragmentPagerAdapter;
import jcoolj.com.base.view.actionbar.SmartActionBar;
import jcoolj.com.base.view.scrollable.ScrollBehavior;
import jcoolj.com.base.view.scrollable.ScrollState;
import jcoolj.com.dribbble.fragment.FavoritesFragment;
import jcoolj.com.dribbble.fragment.PopularFragment;
import jcoolj.com.dribbble.fragment.RecentFragment;
import jcoolj.com.base.OnLoadListener;
import jcoolj.com.dribbble.oauth.OAuthHelper;
import jcoolj.com.dribbble.view.WaveLoadingView;

public class MainActivity extends BaseActivity implements OnLoadListener, View.OnClickListener, ScrollBehavior {

    private View view_actionBar;

    private WaveLoadingView view_loadingView;

    private DrawerAnimator drawerAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        final ViewPager pager = (ViewPager) findViewById(R.id.fragment_pager);
        final List<PageFragment> fragments = new ArrayList<>();
        PopularFragment popularFragment = new PopularFragment();
        popularFragment.setOnLoadListener(this);
        fragments.add(popularFragment);
        RecentFragment recentFragment = new RecentFragment();
        recentFragment.setOnLoadListener(this);
        fragments.add(recentFragment);
        fragments.add(new FavoritesFragment());
        pager.setAdapter(new FragmentPagerAdapter(this, getSupportFragmentManager(), fragments));

        view_loadingView = (WaveLoadingView) findViewById(R.id.loading_view);
        view_loadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_loadingView.isError())
                    fragments.get(pager.getCurrentItem()).load();
            }
        });

        Resources res = getResources();
        view_actionBar = findViewById(R.id.action_bar);
        ViewGroup titleBarContainer = (ViewGroup) findViewById(R.id.title_bar_container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = PixelUtils.getStatusBarHeight(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, res.getDimensionPixelSize(R.dimen.title_bar_height) - statusBarHeight);
            params.topMargin = statusBarHeight;
            titleBarContainer.setLayoutParams(params);
        }

        SmartTabLayout tabLayout = (SmartTabLayout) findViewById(R.id.pager_tab);
        tabLayout.setViewPager(pager);
        drawerAnimator = new DrawerAnimator();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(view_actionBar.getVisibility() == View.GONE)
                    drawerAnimator.toggle(view_actionBar, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                if (position == pager.getCurrentItem()) {
                    PageFragment page = fragments.get(position);
                    page.refresh();
                }
            }
        });

        StoredCredential credential = null;
        try {
            credential = new OAuthHelper(this).getCredential();
        } catch (IOException ignored) { }
        ImageView btnAccount = (ImageView) findViewById(R.id.btn_account);
        btnAccount.setOnClickListener(this);
        btnAccount.setVisibility(credential == null ? View.GONE : View.VISIBLE);
        TextView btnLogin = (TextView) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnLogin.setVisibility(credential == null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoadStarted() {
        view_loadingView.start();
    }

    @Override
    public void onLoadFailed() {
        view_loadingView.error();
    }

    @Override
    public void onLoadFinished() {
        view_loadingView.stop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_login){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if(id == R.id.btn_account) {

        }
    }

    @Override
    public void onScrollChanged(ScrollState direction, int position) {

    }

    @Override
    public void onReachEnd() {
        view_loadingView.start();
    }

    @Override
    public void onScrollDirectionChanged(ScrollState direction) {
        if (direction == ScrollState.UP && view_actionBar.getVisibility() != View.GONE) {
            drawerAnimator.toggle(view_actionBar, false);
        } else if (direction == ScrollState.DOWN && view_actionBar.getVisibility() == View.GONE) {
            drawerAnimator.toggle(view_actionBar, true);
        }
    }

}
