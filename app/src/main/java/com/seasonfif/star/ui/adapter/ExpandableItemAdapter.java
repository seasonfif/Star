package com.seasonfif.star.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.seasonfif.star.MyApplication;
import com.seasonfif.star.R;
import com.seasonfif.star.model.Level0Item;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.utils.DateUtils;
import com.seasonfif.star.utils.Navigator;
import com.seasonfif.star.utils.SettingShared;
import com.seasonfif.star.utils.ThemeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zhangqiang on 2018/2/1.
 */

public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>{

  private static final String TAG = "ExpandableItemAdapter";

  public static final int TYPE_LEVEL_0 = 0;
  public static final int TYPE_LEVEL_1 = 1;
  public static final int TYPE_PERSON = 2;
  private final Context context;

  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with
   * some initialization data.
   *
   * @param data A new list is created out of this one to avoid mutable list
   */
  public ExpandableItemAdapter(Context context, List<MultiItemEntity> data) {
    super(data);
    this.context = context;
    addItemType(TYPE_LEVEL_0, R.layout.item_menu_sticky);
    addItemType(TYPE_LEVEL_1, R.layout.repository_item);
  }

  @Override protected void convert(final BaseViewHolder holder, MultiItemEntity item) {
      switch(holder.getItemViewType()){
        case TYPE_LEVEL_0:
          final Level0Item lv0 = (Level0Item) item;
          final int subcount = lv0.getSubItems() == null ? 0 : lv0.getSubItems().size();
          holder.setText(R.id.tv_title, lv0.group + " : " + subcount)
              .setTextColor(R.id.tv_title, Color.WHITE);
          if (subcount > 0){
            holder.setImageDrawable(R.id.iv, lv0.isExpanded() ? ThemeUtil.tintDrawable(R.drawable.arrow_b, Color.WHITE) :
                ThemeUtil.tintDrawable(R.drawable.arrow_r, Color.WHITE));
          }else{
            holder.setImageDrawable(R.id.iv, null);
          }
          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
              int pos = holder.getAdapterPosition();
              Log.d(TAG, "Level 0 item pos: " + pos);
              ImageView iv = holder.getView(R.id.iv);
              if (subcount > 0){
                if (lv0.isExpanded()) {
                  //不能做动画 因为holder返回的iv不是同一个对象  holder改变了？？？
                  //rotate(iv, -270, 0);
                  collapse(pos);
                } else {
                  //rotate(iv, 0, 90);
                  expand(pos);
                }
              }
            }
          });
          break;
        case TYPE_LEVEL_1:
          final Repository repo = (Repository) item;
          bindRepo(holder.itemView, repo);
          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Navigator.openRepoProfile((Activity) context, repo);
            }
          });
          break;
      }
  }

  private void bindRepo(View view, Repository repository){
    RepoAdapter.ViewHolder holder = new RepoAdapter.ViewHolder(view);
    holder.name.setText(repository.name);
    holder.language.setText(TextUtils.isEmpty(repository.language) ? "" : repository.language);
    holder.description.setText(TextUtils.isEmpty(repository.description) ? mContext.getResources().getString(R.string.has_no_description) : repository.description);
    holder.star_fork.setText(mContext.getString(R.string.star_fork,
            repository.stargazers_count, repository.forks_count));
    String date_create = "";
    String date_update = "";
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      date_create = DateUtils.sdfyyyy_MM_dd_slash.format(format.parse(repository.created_at));
      date_update = DateUtils.sdfyyyy_MM_dd_slash.format(format.parse(repository.updated_at));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    holder.created_at.setText(mContext.getString(R.string.create_at, date_create));
    holder.updated_at.setText(mContext.getString(R.string.update_at, date_update));

    if (SettingShared.isHideAvatar(mContext)){
      holder.avatar.setVisibility(View.GONE);
      holder.avatar.setOnClickListener(null);
    }else{
      holder.avatar.setVisibility(View.VISIBLE);
      holder.avatar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
      });
      MyApplication.INSTANCE.loadAvatar(repository.avatar, holder.avatar);
    }
  }

  private void rotate(View v, float from, float to){
    Animation animation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    animation.setDuration(1500);
    animation.setFillAfter(true);
    animation.setInterpolator(new AccelerateInterpolator());
    v.startAnimation(animation);
  }
}
