package com.seasonfif.star.widget.helper;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * 创建时间：2017年05月08日19:21 <br>
 * 作者：zhangqiang <br>
 * 描述：
 */

public class FABBehavior extends FloatingActionButton.Behavior{

  private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
  private boolean mIsAnimatingOut = false;

  public FABBehavior(Context context, AttributeSet attrs) {
    super();
  }

  @Override
  public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                     final View directTargetChild, final View target, final int nestedScrollAxes) {
    // Ensure we react to vertical scrolling
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
        || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
  }

  @Override
  public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                             final View target, final int dxConsumed, final int dyConsumed,
                             final int dxUnconsumed, final int dyUnconsumed) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
      // User scrolled down and the FAB is currently visible -> hide the FAB
      animateOut(child);
    } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
      // User scrolled up and the FAB is currently not visible -> show the FAB
      animateIn(child);
    }
  }

  // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
  private void animateOut(final FloatingActionButton button) {
    if (Build.VERSION.SDK_INT >= 14) {
      ViewCompat.animate(button).translationY(button.getHeight() + getMarginBottom(button)).setInterpolator(INTERPOLATOR).withLayer()
          .setListener(new ViewPropertyAnimatorListener() {
            public void onAnimationStart(View view) {
              FABBehavior.this.mIsAnimatingOut = true;
            }

            public void onAnimationCancel(View view) {
              FABBehavior.this.mIsAnimatingOut = false;
            }

            public void onAnimationEnd(View view) {
              FABBehavior.this.mIsAnimatingOut = false;
              view.setVisibility(View.GONE);
            }
          }).start();
    } else {

    }
  }

  // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
  private void animateIn(FloatingActionButton button) {
    button.setVisibility(View.VISIBLE);
    if (Build.VERSION.SDK_INT >= 14) {
      ViewCompat.animate(button).translationY(0)
          .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
          .start();
    } else {

    }
  }

  private int getMarginBottom(View v) {
    int marginBottom = 0;
    final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
      marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
    }
    return marginBottom;
  }
}
