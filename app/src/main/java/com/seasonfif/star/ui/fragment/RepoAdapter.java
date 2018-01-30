package com.seasonfif.star.ui.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seasonfif.star.MyApplication;
import com.seasonfif.star.R;
import com.seasonfif.star.model.Repository;
import com.seasonfif.star.utils.DateUtils;
import com.seasonfif.star.utils.SettingShared;
import com.seasonfif.star.widget.CircleImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lxy on 2018/1/27.
 */

public class RepoAdapter extends BaseAdapter<Repository, RepoAdapter.ViewHolder> {

    private Context mContext;
    public List<Repository> mDataList;

    public RepoAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Repository getItem(int position) {
        return mDataList.get(position);
    }

    public void notifyDataSetChanged(List<Repository> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.repository_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Repository repository = mDataList.get(position);
        final ViewHolder holder = (ViewHolder) viewHolder;
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
            MyApplication.INSTANCE.loadAvatar(repository.owner.avatar_url, holder.avatar);
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, language, description, star_fork, created_at, updated_at;
        public CircleImageView avatar;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            language = (TextView) view.findViewById(R.id.language);
            description = (TextView) view.findViewById(R.id.description);
            star_fork = (TextView) view.findViewById(R.id.star_fork);
            created_at = (TextView) view.findViewById(R.id.created_at);
            updated_at = (TextView) view.findViewById(R.id.update_at);
            avatar = (CircleImageView) view.findViewById(R.id.avatar);
        }
    }

}
