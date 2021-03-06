package com.example.chen.wanandroiddemo.main.coin;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chen.wanandroiddemo.R;
import com.example.chen.wanandroiddemo.adapter.CoinRecordsAdapter;
import com.example.chen.wanandroiddemo.base.fragment.BaseFragment;
import com.example.chen.wanandroiddemo.core.DataManager;
import com.example.chen.wanandroiddemo.core.bean.Coin;
import com.example.chen.wanandroiddemo.core.bean.CoinRecord;
import com.example.chen.wanandroiddemo.main.coin.contract.MyCoinContract;
import com.example.chen.wanandroiddemo.main.coin.presenter.MyCoinPresenter;
import com.example.chen.wanandroiddemo.utils.NetUtil;
import com.example.chen.wanandroiddemo.utils.OpenActivityUtil;
import com.example.chen.wanandroiddemo.widget.RefreshRecyclerView;
import com.example.statelayout_lib.StateLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author chenshuaiyu
 */
public class MyCoinFragment extends BaseFragment<MyCoinPresenter> implements MyCoinContract.View {

    @BindView(R.id.refresh_recycler_view)
    RefreshRecyclerView mRefreshRecyclerView;

    private TextView mCoinCountTv;
    private TextView mRankTv;
    private TextView mLvTv;

    private List<CoinRecord> mCoinRecordList = new ArrayList<>();
    private CoinRecordsAdapter mCoinRecordsAdapter;

    @Override
    protected MyCoinPresenter getPresenter() {
        return new MyCoinPresenter(DataManager.getInstance());
    }

    @Override
    protected StateLayoutManager getStateLayoutManager() {
        return new StateLayoutManager.Builder()
                .setContentLayoutResId(R.layout.fragment_my_coin)
                .setErrorLayoutResId(R.layout.not_login)
                .setErrorReLoadViewResId(R.id.tv_not_login)
                .setOnReLoadListener(() -> {
                    OpenActivityUtil.openLoginActivity(Objects.requireNonNull(getContext()));
                    Objects.requireNonNull(getActivity()).finish();
                })
                .build();
    }

    @Override
    protected void initView() {
        View infoView = LayoutInflater.from(getContext()).inflate(R.layout.my_coin, null);
        mCoinCountTv = infoView.findViewById(R.id.tv_coin_count);
        mRankTv = infoView.findViewById(R.id.tv_rank);
        mLvTv = infoView.findViewById(R.id.tv_lv);

        mRefreshRecyclerView.setFirstPage(1);
        mRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCoinRecordsAdapter = new CoinRecordsAdapter(R.layout.item_coin_record, mCoinRecordList);
        mCoinRecordsAdapter.addHeaderView(infoView);
        mRefreshRecyclerView.setAdapter(mCoinRecordsAdapter);

        mRefreshRecyclerView.setCallback(new RefreshRecyclerView.Callback() {
            @Override
            public void refresh(int firstPage) {
                mPresenter.getCoin();
                mPresenter.getCoinRecords(firstPage);
            }

            @Override
            public void loadMore(int page) {
                mPresenter.getCoinRecords(page);
            }
        });
    }

    @Override
    public void showCoin(Coin coin) {
        mCoinCountTv.setText(String.valueOf(coin.getCoinCount()));
        mRankTv.setText(String.valueOf(coin.getRank()));
        mLvTv.setText(String.valueOf(coin.getLevel()));
    }

    @Override
    public void showCoinRecords(List<CoinRecord> coinRecords) {
        if (mRefreshRecyclerView.isFirstPage()) {
            mRefreshRecyclerView.addCurPage();
            mCoinRecordList.clear();
        }
        mCoinRecordList.addAll(coinRecords);
        mCoinRecordsAdapter.notifyDataSetChanged();
    }

    @Override
    public void reLoad() {
        if (!NetUtil.isNetworkConnected()) {
            showNetErrorView();
        } else if (mPresenter.getLoginStatus()) {
            mRefreshRecyclerView.reLoad();
        } else {
            showErrorView();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "我的积分";
    }
}
