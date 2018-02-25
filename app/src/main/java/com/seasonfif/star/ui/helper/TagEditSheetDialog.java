package com.seasonfif.star.ui.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.seasonfif.star.database.DBEngine;
import com.seasonfif.star.model.RepoTag;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.utils.DataUtil;
import com.seasonfif.star.utils.ThemeUtil;
import com.seasonfif.star.utils.ToastUtils;
import com.yanzhenjie.loading.LoadingView;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemLongClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.List;

/**
 * Created by zhangqiang on 2018/2/6.
 */

public class TagEditSheetDialog {

  /**
   * tag名称字数限制
   */
  private static final int NAME_LENGTH = 8;

  private Context context;
  private final float screenW;
  private BottomSheetDialog sheetDialog;
  private ImageView addTag;
  private SwipeMenuRecyclerView recyclerview;
  private LoadingView loadingView;
  private TagAdapter adapter;
  private List<RepoTag> datas;

  public TagEditSheetDialog(Context context){
    this.context = context;
    screenW = context.getResources().getDisplayMetrics().widthPixels;
    sheetDialog = new BottomSheetDialog(context);
    View root = LayoutInflater.from(context).inflate(R.layout.layout_tag_edit, null);
    datas = DataUtil.getRepoTags();
    initRootView(root);
    sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialog) {
        DataUtil.clearRepoTags();
        DataUtil.getRepoTags();
        EventManager.getInstanse().notifyAll(DataObserver.MULTI, null);
      }
    });
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
            handleAddTag();
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
    recyclerview.setSwipeItemLongClickListener(mSwipeItemLongClickListener);
    adapter = new TagAdapter();
    recyclerview.setAdapter(adapter);
  }

  private void handleAddTag() {
    if (hasNewTag()){
      new AlertDialog.Builder(context)
          .setTitle("友情提示：")
          .setMessage("您已创建一个空标签，请完善")
          .setCancelable(false)
          .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {

            }
          }).show();
    }else{
      RepoTag tag = new RepoTag("输入名称", true);
      datas.add(0, tag);
      adapter.notifyItemInserted(0);
    }
  }

  private boolean hasNewTag() {
    for (int i = 0; i < datas.size(); i++) {
      RepoTag tag = datas.get(i);
      if (tag.isNew){
        return true;
      }
    }
    return false;
  }

  private SwipeItemClickListener mSwipeItemClickListener = new SwipeItemClickListener() {
    @Override public void onItemClick(View itemView, int position) {
      final RepoTag repoTag = datas.get(position);
      ToastUtils.with(context).show(repoTag.name);
    }
  };

  private SwipeItemLongClickListener mSwipeItemLongClickListener = new SwipeItemLongClickListener() {
    @Override public void onItemLongClick(final View itemView, final int position) {
      boolean needsOpen = false;
      final RepoTag repoTag = datas.get(position);
      //如果正在编辑状态，需要关闭编辑，并在取消时恢复编辑,否则编辑时删除 会在添加时UI异常
      if (repoTag.isEdit){
         needsOpen = true;
         triggleCollapseDryRun(position);
      }
      final boolean finalNeedsOpen = needsOpen;
      new AlertDialog.Builder(context)
          .setTitle("友情提示：")
          .setMessage("您将删除该标签")
          .setCancelable(false)
          .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
              datas.remove(position);
              adapter.notifyItemRemoved(position);
              clearTagWithThis(repoTag);
            }
          }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {
          if (finalNeedsOpen){
            triggleExpand(repoTag, (TagHolder) recyclerview.getChildViewHolder(itemView));
          }
        }
      }).show();
    }
  };

  /**
   * 删除该标签
   * 清除该tag标记的仓库标签
   * @param tag
   */
  private void clearTagWithThis(RepoTag tag) {
    DBEngine.deleteTag(tag);
    changeRepoTag(tag.name, "");
  }

  /**
   * 替换repo的标签
   * @param oldTag
   * @param newTag
   */
  private void changeRepoTag(String oldTag, String newTag){
    List<Repository> repositories = DBEngine.loadByTag(Repository.class, oldTag);
    if (repositories != null && repositories.size() > 0){
      for (Repository repo: repositories) {
        repo.group = newTag;
      }
      DBEngine.insertOrReplaceTx(repositories);
    }
  }

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

  private class TagAdapter extends RecyclerView.Adapter<TagHolder>{
    @Override
    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, parent, false);
      TagHolder holder = new TagHolder(view);
      return holder;
    }

    @Override
    public void onBindViewHolder(final TagHolder holder, int position) {
      final RepoTag repoTag = datas.get(position);
      final TagHolder tagHolder = holder;
      tagHolder.tv.setText(repoTag.name);
      final ImageView eb = tagHolder.editBtn;
      eb.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (repoTag.isEdit){
            triggleCollapse(repoTag, tagHolder, false);
          }else{
            checkEditMode();
            triggleExpand(repoTag, tagHolder);
          }
        }
      });
    }

    @Override
    public int getItemCount() {
      return datas.size();
    }

    public RepoTag getItem(int position){
      return datas.get(position);
    }
  }

  private void checkEditMode() {
    for (int i = 0; i < datas.size(); i++) {
      RepoTag tag = datas.get(i);
      if (tag.isEdit){
        triggleCollapseDryRun(i);
        return;
      }
    }
  }

  private void triggleCollapseDryRun(int position){
    RepoTag repoTag = adapter.getItem(position);
    TagHolder holder = (TagHolder) recyclerview.findViewHolderForAdapterPosition(position);
    triggleCollapse(repoTag, holder, true);
  }

  private void triggleCollapse(final RepoTag tag, final TagHolder holder, final boolean dryrun) {
    final ImageView eb = holder.editBtn;
    final EditText et = holder.edittext;
    final String tagName = et.getText().toString().trim();

    if (!dryrun){
      String tip = checkNameValid(tag, tagName);
      if (!TextUtils.isEmpty(tip)){
        new AlertDialog.Builder(context)
            .setTitle("友情提示：")
            .setMessage(tip)
            .setCancelable(false)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {

              }
            }).show();
        return;
      }
    }

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
        tag.isEdit = false;

        if (!dryrun){
          if (tag.isNew){
            tag.isNew = false;
          }
          holder.tv.setText(tagName);
          String oldTag = tag.name;
          tag.name = tagName;
          DBEngine.insertOrReplace(tag);
          changeRepoTag(oldTag, tag.name);
        }
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    eb.startAnimation(animation);
  }

  private String checkNameValid(RepoTag tag, String tagName) {
    boolean isValid = true;
    String validTip = "";

    //校验标签名是否合法
    if (!TextUtils.isEmpty(tagName)){

      int nameLength = tagName.length();
      if (nameLength > NAME_LENGTH){
        isValid = false;
        validTip = "标签名长度限制8个字符";
      }else{
        for (RepoTag repoTag : datas) {
          if (tag != repoTag && tagName.equals(repoTag.name)){
            isValid = false;
            validTip = "该标签名已存在";
            break;
          }
        }
      }
    }else{
      isValid = false;
      validTip = "标签名不能为空";
    }
    return validTip;
  }

  private void triggleExpand(final RepoTag tag, final TagHolder holder) {
    final ImageView eb = holder.editBtn;
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
        holder.edittext.setVisibility(View.VISIBLE);
        holder.edittext.setText(holder.tv.getText());
        tag.isEdit = true;

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
