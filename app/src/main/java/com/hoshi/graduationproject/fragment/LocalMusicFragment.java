package com.hoshi.graduationproject.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoshi.graduationproject.R;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicFragment extends AttachDialogFragment {

  private ViewPager viewPager;
  private int page = 0;
  private ActionBar ab;
  private String[] title;

  public static final LocalMusicFragment newInstance(int page, String[] title) {
    LocalMusicFragment f = new LocalMusicFragment();
    Bundle bdl = new Bundle(1);
    bdl.putInt("page_number", page);
    bdl.putStringArray("title", title);
    f.setArguments(bdl);
    return f;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    viewPager.setCurrentItem(page);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      page = getArguments().getInt("page_number");
      title = getArguments().getStringArray("title");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_local_music, container, false);
    viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
    if (viewPager != null) {
      setupViewPager(viewPager);
      viewPager.setOffscreenPageLimit(3);
    }

    final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabTextColors(R.color.text_color, getResources().getColor(R.color.theme_color));
    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.theme_color));
    /*tabLayout.setTabTextColors(R.color.text_color, ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color).getDefaultColor());
    tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color).getDefaultColor());*/
    return rootView;
  }

  private void setupViewPager(ViewPager viewPager) {
    Adapter adapter = new Adapter(getChildFragmentManager());
    adapter.addFragment(new LocalMusicChildFragment(), title[0]);
    adapter.addFragment(new LocalMusicChildFragment(), title[1]);
    adapter.addFragment(new LocalMusicChildFragment(), title[2]);
    adapter.addFragment(new LocalMusicChildFragment(), title[3]);

    viewPager.setAdapter(adapter);
  }

  static class Adapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
      mFragments.add(fragment);
      mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
      if (mFragments.size() > position)
        return mFragments.get(position);

      return null;
    }

    @Override
    public int getCount() {
      return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return mFragmentTitles.get(position);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
      // don't super !
    }
  }
}
