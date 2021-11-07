package com.supermap.imobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.InsightBaseBean;
import com.supermap.imobile.bean.InsightsBean;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InsightsAdapter extends BaseAdapter {

    private ArrayList<InsightBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public InsightsAdapter(Context context, InsightsBean insightsBean) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        formate(insightsBean);
    }

    public void setData(InsightsBean insightsBean) {
        formate(insightsBean);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_insights_listview, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_insight_title);
            holder.count = (TextView) convertView.findViewById(R.id.tv_visit_count);
            holder.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
            holder.createTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.description = (TextView) convertView.findViewById(R.id.tv_description);
            holder.url = (TextView) convertView.findViewById(R.id.tv_url);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        InsightBaseBean baseBean = mList.get(position);

        Glide.with(mContext)
                .load(baseBean.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.map)
                .into(holder.imageView);
        holder.title.setText(baseBean.getTitle());
        holder.count.setText(String.valueOf(baseBean.getCount()));
        holder.updateTime.setText(Utils.parseTime(baseBean.getUpdateTime()));
        holder.createTime.setText(Utils.parseTime(baseBean.getCreateTime()));
        holder.description.setText(baseBean.getDescription());
        holder.url.setText(baseBean.getURL());

        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView count;
        public TextView updateTime;
        public TextView createTime;
        public TextView description;
        public TextView url;
    }

    private void formate(InsightsBean insightsBean) {
        mList.clear();
        int pageSize = insightsBean.getPageSize();
        int total = insightsBean.getTotal();
        int counts = 0;
        if (total >= pageSize) {
            counts = pageSize;
        } else {
            counts = total;
        }
        for (int i = 0; i < counts; i++) {
            InsightsBean.ContentBean contentBean = insightsBean.getContent().get(i);
            int ID = contentBean.getId();
            String title = contentBean.getName();
            String thumbnail = contentBean.getThumbnail();
            int count = contentBean.getVisitCount();
            long updateTime = contentBean.getUpdateTime();
            long createTime = contentBean.getCreateTime();
            String description = contentBean.getDescription();
            InsightBaseBean baseBean = new InsightBaseBean.Builder(title,ID,thumbnail)
                    .setCount(count)
                    .setcreateTime(createTime)
                    .setupdateTime(updateTime)
                    .setdescription(description)
                    .build();
            mList.add(baseBean);
        }
        //排序
        Collections.sort(mList, new SortByTime());
    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            InsightBaseBean b1 = (InsightBaseBean) o1;
            InsightBaseBean b2 = (InsightBaseBean) o2;
            if (b1.getUpdateTime() < b2.getUpdateTime()){
                return 1;
            }
            return -1;
        }
    }

}
