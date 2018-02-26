package com.hoshi.graduationproject.utils;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class ClickManager {

  public static void init(Activity root, OnClickListener listener, int...viewIds) {
    init(root, sActivityRootViewFinder, listener, viewIds);
  }

  public static void init(View root, OnClickListener listener, int...viewIds) {
    init(root, sViewRootViewFinder, listener, viewIds);
  }

  private static <T> void init(T root, ViewFinder<T> viewFinder, OnClickListener listener, int...viewIds) {
    if (root == null || listener == null || viewIds == null) {
      return;
    }
    View v;

    for (int viewId : viewIds) {
      v = viewFinder.findView(root, viewId);

      if (v == null) {
        continue;
      }
      v.setOnClickListener(listener);
    }
  }

  private ClickManager() {}

  private static interface ViewFinder<T> {
    View findView(T root, int viewId);
  }

  private static ViewFinder<View> sViewRootViewFinder = new ViewFinder<View>() {
    @Override
    public View findView(View root, int viewId) {
      return root.findViewById(viewId);
    }
  };

  private static ViewFinder<Activity> sActivityRootViewFinder = new ViewFinder<Activity>() {
    @Override
    public View findView(Activity root, int viewId) {
      return root.findViewById(viewId);
    }
  };

}
