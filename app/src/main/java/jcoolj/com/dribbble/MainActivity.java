package jcoolj.com.dribbble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jcoolj.com.base.BaseActivity;
import jcoolj.com.base.PageFragment;
import jcoolj.com.base.view.FragmentPagerAdapter;
import jcoolj.com.base.view.TitleBar;
import jcoolj.com.core.utils.Logger;
import jcoolj.com.dribbble.fragment.FavoritesFragment;
import jcoolj.com.dribbble.fragment.PopularFragment;
import jcoolj.com.dribbble.fragment.RecentFragment;
import jcoolj.com.base.OnLoadListener;
import jcoolj.com.dribbble.oauth.OAuthHelper;
import jcoolj.com.dribbble.view.WaveView;

public class MainActivity extends BaseActivity implements OnLoadListener {

    private WaveView view_loadingView;

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

        view_loadingView = (WaveView) findViewById(R.id.loading_view);
        view_loadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_loadingView.isError())
                    fragments.get(pager.getCurrentItem()).load();
            }
        });

        TitleBar titleBar = getTitleBar();
        titleBar.setViewPager(pager);
        titleBar.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                if (position == pager.getCurrentItem()) {
                    PageFragment page = fragments.get(position);
                    page.refresh();
                }
            }
        });

        try {
            StoredCredential credential = new OAuthHelper(this).getCredential();
            if(credential == null)
                titleBar.addActionRight("Sign in", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
        } catch (IOException e) {
            titleBar.addActionRight("Sign in", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

    @Override
    public void onLoadStarted() {
        Logger.d("wave onLoadStarted");
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

}
