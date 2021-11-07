package com.supermap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialize.MaterializeBuilder;
import com.supermap.onlineservices.OnlineCallBack;
import com.supermap.onlineservices.OnlineService;
import com.supermap.onlineservices.utils.GroupDeleteListener;
import com.supermap.onlineservices.utils.GroupOrderBy;
import com.supermap.onlineservices.utils.JoinTypes;
import com.supermap.onlineservices.utils.OrderType;
import com.supermap.supermap.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 删除群组
 */
public class DeleteGroupActivity extends AppCompatActivity {

    private static final String TAG = DeleteGroupActivity.class.getSimpleName();

    //save our FastAdapter
    private FastItemAdapter<CheckBoxSampleItem> mFastItemAdapter;

    private GroupListBean mGroupListBean;

    private RelativeLayout rl_progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);

        initView(savedInstanceState);

        refresh();
    }

    private void initView(Bundle savedInstanceState) {
        rl_progress = findViewById(R.id.rl_progress);

        findViewById(R.id.cancel_delete).setOnClickListener(v -> finish());
        findViewById(R.id.delete_group).setOnClickListener(v -> {
            runOnUiThread(() -> rl_progress.setVisibility(View.VISIBLE));

            Set<CheckBoxSampleItem> selectedItems = mFastItemAdapter.getSelectedItems();
            ArrayList<String> groupids = new ArrayList<>();
            Iterator<CheckBoxSampleItem> iterator = selectedItems.iterator();
            while(iterator.hasNext()) {
                CheckBoxSampleItem item = iterator.next();
                groupids.add(item.groupId.toString());
            }
            OnlineService.deleteGroup(groupids, new GroupDeleteListener() {
                @Override
                public void onError(String errorInfo) {
                    Log.e(TAG, "删除失败：" + errorInfo);
                    runOnUiThread(() -> rl_progress.setVisibility(View.GONE));
                }

                @Override
                public void onComplete(String responseBody) {
                    Log.e(TAG, "删除成功：" + responseBody);

                    finish();
                }
            });
        });

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        mFastItemAdapter = new FastItemAdapter<>();
        mFastItemAdapter.withSelectable(true);

        //configure our fastAdapter
        mFastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
            Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
            return false;
        });

        mFastItemAdapter.withOnPreClickListener((v, adapter, item, position) -> {
            // consume otherwise radio/checkbox will be deselected
            return true;
        });
        mFastItemAdapter.withEventHook(new CheckBoxSampleItem.CheckBoxClickEvent());

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.group_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mFastItemAdapter);

        //fill with some sample data
        List<CheckBoxSampleItem> items = new ArrayList<>();
        mFastItemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        mFastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void refresh() {
        JoinTypes[] joinTypes = new JoinTypes[1];
        joinTypes[0] = JoinTypes.CREATE;
        OnlineService.getGroupsInfo(1, 100, GroupOrderBy.CREATETIME,
                null, joinTypes, OrderType.DESC, new OnlineCallBack.CallBackString() {
                    @Override
                    public void onSucceed(String response) {
//                        Log.e(TAG, "getGroupsList: " + response);
                        if (response != null) {
                            mGroupListBean = new Gson().fromJson(response, GroupListBean.class);
                            List<GroupListBean.ContentBean> content = mGroupListBean.getContent();
                            List<CheckBoxSampleItem> items = new ArrayList<>();
                            for (int i = 0; i < content.size(); i++) {
                                items.add(new CheckBoxSampleItem()
                                        .withGroupId(String.valueOf(content.get(i).getId()))
                                        .withName(content.get(i).getGroupName())
                                        .withDescription("创建者：" + content.get(i).getNickname())
                                        .withIdentifier(10086 + i));
                            }
                            mFastItemAdapter.setNewList(items);
                            runOnUiThread(() -> rl_progress.setVisibility(View.GONE));
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        Log.e(TAG, "getGroupsList-errorInfo: " + errorInfo);
                        runOnUiThread(() -> {
                            Toast.makeText(DeleteGroupActivity.this, "网络错误: " + errorInfo, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }



}
