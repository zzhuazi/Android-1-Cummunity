package com.ljh.community.adapter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.R;
import com.ljh.community.model.Section;
import com.ljh.community.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {
    private static final String TAG = SectionsAdapter.class.getSimpleName();
    private List<Section> mSectionList;
    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色

    public List<Boolean> getIsClicks() {
        return isClicks;
    }

    public void setIsClicks(List<Boolean> isClicks) {
        this.isClicks = isClicks;
    }

    public SectionsAdapter(List<Section> mSectionList) {
        this.mSectionList = mSectionList;
        isClicks = new ArrayList<>();
        //置所有元素为false，未点击
        for (int i = 0; i < mSectionList.size(); i++) {
            isClicks.add(false);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickListener = null;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    @Override
    public SectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_section_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SectionsAdapter.ViewHolder holder, int position) {
        if (mSectionList.size() != 0) {
            Section section = mSectionList.get(position);
            holder.sectionName.setText(section.getName());
            if (isClicks.get(position)) {
                holder.sectionName.setTextColor(ContextCompat.getColor(holder.sectionName.getContext(), R.color.ActicleFragmentAdapter_tv_red));
            } else {
                holder.sectionName.setTextColor(ContextCompat.getColor(holder.sectionName.getContext(), R.color.ActicleFragmentAdapter_tv_black));
            }
        }
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.sectionName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.sectionName, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSectionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;
        View sectionView;

        public ViewHolder(View itemView) {
            super(itemView);
            sectionView = itemView;
            sectionName = itemView.findViewById(R.id.tv_sectionName);
        }
    }

}
