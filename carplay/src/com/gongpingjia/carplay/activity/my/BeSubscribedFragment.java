package com.gongpingjia.carplay.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gongpingjia.carplay.ILoadSuccess;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseFragment;
import com.gongpingjia.carplay.adapter.BeSubscribedAdapter2;
import com.gongpingjia.carplay.adapter.SubscribeListener;
import com.gongpingjia.carplay.api.API2;
import com.gongpingjia.carplay.bean.User;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

/**
 * Created by Administrator on 2015/10/16.
 * 关注我的人
 */
public class BeSubscribedFragment extends CarPlayBaseFragment implements ILoadSuccess {
    private PullToRefreshListView mListView;
    private BeSubscribedAdapter2 beSubscribeAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mListView = (PullToRefreshListView) inflater.inflate(R.layout.fragment_follow_each_other, container, false);
        setOnLoadSuccess(this);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }
        });
        initView();
        return mListView;

    }


    private void initView() {
        beSubscribeAdapter = new BeSubscribedAdapter2(getActivity());
        beSubscribeAdapter.setSubscribeListener(new SubscribeListener() {
            @Override
            public void onSubscribe(String targetId) {
                DhNet dhNet = new DhNet(API2.getFollowPerson(User.getInstance().getUserId(), User.getInstance().getToken()));
                dhNet.addParam("targetUserId", targetId);
                dhNet.doPostInDialog(new NetTask(getActivity()) {
                    @Override
                    public void doInUI(Response response, Integer transfer) {
                        if (response.isSuccess()) {
                            //取消关注成功
                            refresh();
                        }
                    }
                });
            }
        });
        mListView.setAdapter(beSubscribeAdapter);
        setUrl(API2.getSubscribeMy(User.getInstance().getUserId(), User.getInstance().getToken()));
        fromWhat("data.beSubscribed");
        refresh();
    }


    @Override
    public void loadSuccess() {
        beSubscribeAdapter.setData(mVaules);
        mListView.onRefreshComplete();
    }

    @Override
    public void loadSuccessOnFirst() {

    }
}
