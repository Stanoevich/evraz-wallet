package com.ngse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.market.MarketTicker;
import com.bitshares.bitshareswallet.viewmodel.QuotationViewModel;
import com.ngse.ui.NewMainActivity;

import org.evrazcoin.evrazwallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeFragment extends BaseFragment {
    private static final String TAG = "QuotationFragment";

    private QuotationCurrencyPairAdapter quotationCurrencyPairAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    public ExchangeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ExchangeFragment newInstance() {
        ExchangeFragment fragment = new ExchangeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_exchange, container, false);
        ButterKnife.bind(this, view);

        quotationCurrencyPairAdapter = new QuotationCurrencyPairAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(quotationCurrencyPairAdapter);
        mRecyclerView.setItemAnimator(null);


        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        quotationCurrencyPairAdapter.setOnItemClickListenr(new QuotationCurrencyPairAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(View view, int position) {
                //mListener.notifyCurrencyPairChange();
                if (quotationCurrencyPairAdapter.getSelectedMarketTicker() != null) {
                    MarketTicker marketTicker = quotationCurrencyPairAdapter.getSelectedMarketTicker().marketTicker;
                    viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                    ((NewMainActivity) getActivity()).showTradingScheduleFragment();
                }

            }
        });

        return view;
    }


    @Override
    public void onShow() {
        super.onShow();

        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        viewModel.getMarketTicker().observe(
                this,
                marketTickerListResource -> {
                    switch (marketTickerListResource.status) {
                        case ERROR:
                            break;
                        case LOADING:
                            if (marketTickerListResource.data != null && marketTickerListResource.data.size() != 0) {
                                quotationCurrencyPairAdapter.notifyDataUpdated(marketTickerListResource.data);
                                MarketTicker marketTicker = quotationCurrencyPairAdapter.getSelectedMarketTicker().marketTicker;
                                viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                            }
                            break;
                        case SUCCESS:
                            quotationCurrencyPairAdapter.notifyDataUpdated(marketTickerListResource.data);
                            MarketTicker marketTicker = quotationCurrencyPairAdapter.getSelectedMarketTicker().marketTicker;
                            viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                            break;
                    }
                });


        viewModel.getSelectedMarketTicker().observe(this, currencyPair -> quotationCurrencyPairAdapter.notifyDataSetChanged());
    }

    @Override
    public void onHide() {
        super.onHide();
    }


}
