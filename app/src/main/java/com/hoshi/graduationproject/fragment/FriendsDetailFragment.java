package com.hoshi.graduationproject.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoshi.graduationproject.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsDetailFragment extends AttachDialogFragment {

  private ViewPager viewPager;
  private int page = 0, friend_id = 0, friend_sex = 0;
  private String friend_birthday, friend_profile;
  private String[] title;

  public static final FriendsDetailFragment newInstance(int page, String[] title, int friend_id,
                                                        String friend_birthday, String friend_profile, int sex) {
    FriendsDetailFragment f = new FriendsDetailFragment();
    Bundle bdl = new Bundle(1);
    bdl.putInt("page_number", page);
    bdl.putInt("friend_id", friend_id);
    bdl.putInt("friend_sex", sex);
    bdl.putString("friend_birthday", friend_birthday);
    bdl.putString("friend_profile", friend_profile);
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
      friend_id = getArguments().getInt("friend_id");
      friend_sex = getArguments().getInt("friend_sex");
      friend_birthday = getArguments().getString("friend_birthday");
      friend_profile = getArguments().getString("friend_profile");
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

    final TabLayout tabLayout = rootView.findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabTextColors(R.color.text_color, getResources().getColor(R.color.theme_color));
    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.theme_color));
    /*tabLayout.setTabTextColors(R.color.text_color, ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color).getDefaultColor());
    tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(mContext, R.color.theme_color).getDefaultColor());*/
    return rootView;
  }

  private void setupViewPager(ViewPager viewPager) {
    Bundle trendsBundle = new Bundle();
    Bundle aboutBundle = new Bundle();
    trendsBundle.putInt("friend_id", friend_id);
    aboutBundle.putString("friend_birthday", friend_birthday);
    aboutBundle.putString("friend_profile", friend_profile);
    aboutBundle.putInt("friend_sex", friend_sex);
    FriendsTrendsFragment mFriendsTrendsFragment = new FriendsTrendsFragment();
    FriendsAboutFragment mFriendsAboutFragment = new FriendsAboutFragment();
    FriendsMusicFragment mFriendsMusicFragment = new FriendsMusicFragment();
    mFriendsMusicFragment.setArguments(trendsBundle); // 传入参数bundle
    mFriendsAboutFragment.setArguments(aboutBundle);
    mFriendsTrendsFragment.setArguments(trendsBundle);

    Adapter adapter = new Adapter(getChildFragmentManager());
    adapter.addFragment(mFriendsMusicFragment, title[0]);
    adapter.addFragment(mFriendsTrendsFragment, title[1]);
    adapter.addFragment(mFriendsAboutFragment, title[2]);

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
