package com.nebulacompanies.nebula.gui;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nebulacompanies.nebula.Config;
import com.nebulacompanies.nebula.R;
import com.nebulacompanies.nebula.fragments.AboutUs;
import com.nebulacompanies.nebula.util.WalkThroughListener;
import com.nebulacompanies.nebula.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;
/*
import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnViewInflateListener;*/

/**
 * Created by Palak Mehta on 5/9/2016.
 */
public class CompanyProfile extends AppCompatActivity implements WalkThroughListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
   /* FancyShowCaseView mFancyShowCaseView;
    ShowcaseView sv;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_profile);

        setActionBar();
        init();
    }
    
    void setActionBar(){
        toolbar = (Toolbar) findViewById(R.id.company_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
    }

    void init(){
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);

        Typeface tf1 = Typeface.createFromAsset(this.getAssets(), Config.FONT_STYLE);

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf1);
                    ((TextView) tabViewChild).setTextSize(16);
                }
            }
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //mFancyShowCaseView.hide();
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AboutUs(), getResources().getString(R.string.about_us));
        adapter.addFragment(new AboutUs(), getResources().getString(R.string.Promoters));
        adapter.addFragment(new AboutUs(), getResources().getString(R.string.projects_));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onMethodCallback(String param) {
        if(param.equals("CompanyProfile")) {
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();

                /*if (j == 1) {
                    mFancyShowCaseView = new FancyShowCaseView.Builder(this)
                            .focusOn(vgTab)
                            .customView(R.layout.layout_my_custom_view, new OnViewInflateListener() {
                                @Override
                                public void onViewInflated(View view) {
                                    view.findViewById(R.id.image).setOnClickListener(mClickListener);
                                }
                            })
                            .dismissListener(new DismissListener() {
                                @Override
                                public void onDismiss(String id) {
                                    viewPager.setCurrentItem(1);
                                }

                                @Override
                                public void onSkipped(String id) {

                                }
                            })
                            .closeOnTouch(false)
                            .build();
                    mFancyShowCaseView.show();
                }*/
            }
        }
        else if(param.equals("CompanyProfile1")) {
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();

               /* if (j == 2) {
                    mFancyShowCaseView = new FancyShowCaseView.Builder(this)
                            .focusOn(vgTab)
                            .customView(R.layout.layout_projects_tab_pointer, new OnViewInflateListener() {
                                @Override
                                public void onViewInflated(View view) {
                                    view.findViewById(R.id.image).setOnClickListener(mClickListener);
                                }
                            })
                            .dismissListener(new DismissListener() {
                                @Override
                                public void onDismiss(String id) {
                                    viewPager.setCurrentItem(2);
                                }

                                @Override
                                public void onSkipped(String id) {

                                }
                            })
                            .closeOnTouch(false)
                            .build();
                    mFancyShowCaseView.show();
                }*/
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new  ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i3 = new Intent(this, MarketingActivity.class);
        startActivity(i3);*/
    }

}
