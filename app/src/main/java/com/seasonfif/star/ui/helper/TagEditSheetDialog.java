package com.seasonfif.star.ui.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.seasonfif.star.R;
import com.seasonfif.star.utils.DataUtil;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.utils.ToastUtils;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import java.util.List;

/**
 * Created by zhangqiang on 2018/2/6.
 */

public class TagEditSheetDialog {

  private Context context;
  private final float screenW;
  private BottomSheetDialog sheetDialog;
  private ImageView addTag;
  private SwipeMenuRecyclerView recyclerview;
  private LoadingView loadingView;
  private TagAdapter adapter;
  private List<String> datas;

  public TagEditSheetDialog(Context context){
    this.context = context;
    screenW = context.getResources().getDisplayMetrics().widthPixels;
    sheetDialog = new BottomSheetDialog(context);
    View root = LayoutInflater.from(context).inflate(R.layout.layout_tag_edit, null);
    datas = DataUtil.getTagsData();
    initRootView(root);
    sheetDialog.setContentView(root);
  }

  private void initRootView(View view) {
    loadingView = view.findViewById(R.id.loading);
    int color1 = ThemeUtil.getColor(context, R.attr.app_accent_color);
    int color2 = ThemeUtil.getColor(context, R.attr.app_primary_color);
    int color3 = ThemeUtil.getColor(context, R.attr.app_primary_dark_color);
    loadingView.setCircleColors(color1, color2, color3);

    addTag = view.findViewById(R.id.add_tag);
    addTag.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
          @Override public void onAnimationStart(Animation animation) {

          }

          @Override public void onAnimationEnd(Animation animation) {
            dismiss();
          }

          @Override public void onAnimationRepeat(Animation animation) {

          }
        });
        addTag.startAnimation(animation);
      }
    });

    recyclerview = view.findViewById(R.id.recyclerview);
    recyclerview.setLayoutManager(new LinearLayoutManager(context));
    recyclerview.addItemDecoration(new DefaultItemDecoration(
        ContextCompat.getColor(context, R.color.divider_color)));

    recyclerview.setSwipeItemClickListener(mSwipeItemClickListener);
    adapter = new TagAdapter();
    recyclerview.setAdapter(adapter);
  }

  private SwipeItemClickListener mSwipeItemClickListener = new SwipeItemClickListener() {
    @Override public void onItemClick(View itemView, int position) {
      ToastUtils.with(context).show(datas.get(position));
    }
  };

  public void show(){
    if (sheetDialog != null && !sheetDialog.isShowing()){
      sheetDialog.show();
    }
  }

  public void dismiss(){
    if (sheetDialog != null && sheetDialog.isShowing()){
      sheetDialog.dismiss();
    }
  }

  private class TagAdapter extends RecyclerView.Adapter{
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, parent, false);
      TagHolder holder = new TagHolder(view);
      return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
      String s = datas.get(position);
      final TagHolder tagHolder = (TagHolder) holder;
      tagHolder.tv.setText(s);

      final ImageView eb = tagHolder.editBtn;
      eb.setTag(false);
      eb.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          boolean hasChange = (boolean) eb.getTag();
          if (hasChange){
            triggleRight(eb, tagHolder);
          }else{
            triggleLeft(eb, tagHolder);
          }
        }
      });
    }

    @Override
    public int getItemCount() {
      return datas.size();
    }
  }

  private void triggleRight(final ImageView eb, final TagHolder holder) {
    int right = eb.getRight();
    final float delta = screenW/2 - context.getResources().getDimension(R.dimen.dimen_20);
    Animation animation = new TranslateAnimation(0, delta, 0, 0);
    animation.setDuration(200);
    animation.setFillEnabled(true);
    animation.setFillAfter(true);
    animation.setInterpolator(new AccelerateInterpolator());
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        eb.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_edit, Color.BLACK));
        holder.edittext.setVisibility(View.GONE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        RelativeLayout.LayoutParams params =
            (RelativeLayout.LayoutParams) eb.getLayoutParams();
        params.rightMargin -= delta;
        eb.setLayoutParams(params);
        eb.clearAnimation();
        eb.setTag(false);
        holder.tv.setText(holder.edittext.getText().toString().trim());
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    eb.startAnimation(animation);
  }

  private void triggleLeft(final ImageView eb, final TagHolder holder) {
    int right = eb.getRight();
    final float delta = right - screenW/2;
    Animation animation = new TranslateAnimation(0, -delta, 0, 0);
    animation.setDuration(200);
    animation.setFillEnabled(true);
    animation.setFillAfter(true);
    animation.setInterpolator(new AccelerateInterpolator());
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        eb.setImageDrawable(ThemeUtil.tintDrawable(R.drawable.ic_sure, ThemeUtil.getColor(context, R.attr.app_accent_color)));
      }

      @Override public void onAnimationEnd(Animation animation) {
        RelativeLayout.LayoutParams params =
            (RelativeLayout.LayoutParams) eb.getLayoutParams();
        params.rightMargin += delta;
        eb.setLayoutParams(params);
        eb.clearAnimation();
        eb.setTag(true);
        holder.edittext.setVisibility(View.VISIBLE);
        holder.edittext.setText(holder.tv.getText());
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    eb.startAnimation(animation);
  }

  static class TagHolder extends RecyclerView.ViewHolder{

    public TextView tv;
    public ImageView editBtn;
    private EditText edittext;
    public TagHolder(View itemView) {
      super(itemView);
      tv = itemView.findViewById(R.id.name);
      editBtn = itemView.findViewById(R.id.edit_btn);
      edittext = itemView.findViewById(R.id.edittext);
    }
  }
}
