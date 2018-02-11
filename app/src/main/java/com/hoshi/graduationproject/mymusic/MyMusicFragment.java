package com.hoshi.graduationproject.mymusic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoshi.graduationproject.MyApplication;
import com.hoshi.graduationproject.R;
import com.hoshi.graduationproject.activity.PlayingActivity;
import com.hoshi.graduationproject.fragment.BaseFragment;
import com.hoshi.graduationproject.personal.LoginActivity;
import com.hoshi.graduationproject.provider.DownFileStore;
import com.hoshi.graduationproject.recent.TopTracksLoader;
import com.hoshi.graduationproject.util.ClickManager;
import com.hoshi.graduationproject.util.IConstants;
import com.hoshi.graduationproject.util.MusicUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hoshi.graduationproject.R.id.song_list_creator;

public class MyMusicFragment extends BaseFragment implements View.OnClickListener {

  private View rootView = null;
  private NestedExpandaleListView mExpandableListView;
  private List<String> group_list;
  private List<String[]> song_list_item;
  private List<List<String[]>> item_list;

  TextView tv_localMusicNum;
  TextView tv_recentPlayedNum;
  TextView tv_myCollectionNum;
  TextView tv_personalSongListNum;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (rootView == null) {
      rootView = inflater.inflate(R.layout.fragment_mymusic, container, false);
    }
    ViewGroup parent = (ViewGroup) rootView.getParent();
    if (parent != null) {
      parent.removeView(rootView);
    }
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ClickManager.init(getView(), this,
            R.id.local_music,
            R.id.loading_button);

    mExpandableListView = (NestedExpandaleListView) getActivity().findViewById(R.id.expandablelistview);
    tv_localMusicNum = (TextView) getActivity().findViewById(R.id.local_music_num);
    tv_recentPlayedNum = (TextView) getActivity().findViewById(R.id.recent_played_num);
    tv_myCollectionNum = (TextView) getActivity().findViewById(R.id.my_collection_num);
    tv_personalSongListNum = (TextView) getActivity().findViewById(R.id.personal_song_list_num);
    initView();
    loadCount();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.local_music:
        startActivity(new Intent(getActivity(), LocalMusicActivity.class));
        break;
      case R.id.loading_button:
        Intent intent = new Intent(MyApplication.getContext(), PlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
        break;
      default:
        break;
    }
  }

  protected void initView() {
    group_list = new ArrayList<String>();
    item_list = new ArrayList<List<String[]>>();

    String result[][] = {{"日语", "0 首 by 异世界人"}, {"国语", "0 首 by 异世界人"}};

    song_list_item = new ArrayList<String[]>();
    for (int j = 0; j < 5; j++) {
      song_list_item.add(result[0]);
    }
    group_list.add("我创建的歌单");
    item_list.add(song_list_item);

    song_list_item = new ArrayList<String[]>();
    for (int j = 0; j < 5; j++) {
      song_list_item.add(result[1]);
    }
    group_list.add("我收藏的歌单");
    item_list.add(song_list_item);

    mExpandableListView.setAdapter(new MyExpandableListViewAdapter(rootView.getContext()));
    mExpandableListView.setGroupIndicator(null);  //将默认的左箭头去掉

    //点击事件
    mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

      @Override
      public boolean onChildClick(ExpandableListView parent, View v,
                                  int groupPosition, int childPosition, long id) {
        /*Intent pregEncDetailIntent = new Intent(getActivity(), PregEncDetailActivity.class);
        pregEncDetailIntent.putExtra(RequestKeys.ID, bKData[groupPosition].smallData[childPosition].id);
        pregEncDetailIntent.putExtra(RequestKeys.BIGCONTENT, bKData[groupPosition].smallData[childPosition].content);
        startActivity(pregEncDetailIntent);*/
        return true;
      }
    });

    mExpandableListView.setVisibility(View.VISIBLE);
  }

  class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;

    public MyExpandableListViewAdapter(Context context) {
      this.context = context;
    }

    @Override
    public int getGroupCount() {
      return group_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
      return item_list.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
      return group_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
      return item_list.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
      return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
      return childPosition;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
      GroupHolder groupHolder = null;
      if (convertView == null) {
        convertView = (View) getActivity().getLayoutInflater().from(context).inflate(
                R.layout.fragment_mymusic_expendlist_group, null);
        groupHolder = new GroupHolder();
        groupHolder.group_text = (TextView) convertView.findViewById(R.id.group_text);
        groupHolder.group_arrow_right = (ImageView) convertView.findViewById(R.id.group_arrow_right);
        groupHolder.group_arrow_down = (ImageView) convertView.findViewById(R.id.group_arrow_down);
        convertView.setTag(groupHolder);
      } else {
        groupHolder = (GroupHolder) convertView.getTag();
      }
      groupHolder.group_text.setText(group_list.get(groupPosition));

      if (isExpanded) {
        groupHolder.group_arrow_right.setVisibility(View.GONE);
        groupHolder.group_arrow_down.setVisibility(View.VISIBLE);
      } else {
        groupHolder.group_arrow_right.setVisibility(View.VISIBLE);
        groupHolder.group_arrow_down.setVisibility(View.GONE);
      }

      return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
      ItemHolder itemHolder = null;
      if (convertView == null) {
        convertView = (View) getActivity().getLayoutInflater().from(context).inflate(
                R.layout.fragment_mymusic_expendlist_item, null);
        itemHolder = new ItemHolder();
        itemHolder.song_list_name = (TextView) convertView.findViewById(R.id.song_list_name);
        itemHolder.song_list_creator = (TextView) convertView.findViewById(song_list_creator);
        convertView.setTag(itemHolder);
      } else {
        itemHolder = (ItemHolder) convertView.getTag();
      }
      itemHolder.song_list_name.setText(item_list.get(groupPosition).get(
              childPosition)[0]);
      itemHolder.song_list_creator.setText(item_list.get(groupPosition).get(
              childPosition)[1]);
      return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
      return true;
    }
  }

  class GroupHolder {
    public TextView group_text;
    public ImageView group_arrow_right;
    public ImageView group_arrow_down;
  }

  class ItemHolder {
    public TextView song_list_name;
    public TextView song_list_creator;
  }

  //父目录
  private class BigData {
    String content;
    String id;
    SmallData[] smallData;
  }

  //一级子目录
  private class SmallData {
    String content;
    String id;
  }

  private void loadCount() {
    int localMusicCount = 0, recentMusicCount = 0, myCollectionCount = 0, personalSongListCount = 0;
    localMusicCount = MusicUtils.queryMusic(getActivity(), IConstants.START_FROM_LOCAL).size();
    recentMusicCount = TopTracksLoader.getCount(getActivity(), TopTracksLoader.QueryType.RecentSongs);
    myCollectionCount = DownFileStore.getInstance(getActivity()).getDownLoadedListAll().size();
    personalSongListCount = MusicUtils.queryArtist(getActivity()).size();
    tv_localMusicNum.setText("（" + localMusicCount + "）");
    tv_recentPlayedNum.setText("（" + recentMusicCount + "）");
    tv_myCollectionNum.setText("（" + myCollectionCount + "）");
    tv_personalSongListNum.setText("（" + personalSongListCount + "）");
  }
}
