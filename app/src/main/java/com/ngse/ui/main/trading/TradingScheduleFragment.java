package com.ngse.ui.main.trading;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.BtsFragmentPageAdapter;
import com.bitshares.bitshareswallet.MainActivity;

import com.bitshares.bitshareswallet.data.HistoryPrice;
import com.bitshares.bitshareswallet.viewmodel.QuotationViewModel;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ngse.ui.NewMainActivity;
import com.ngse.ui.main.balanceitems.OpenOrdersFragment;

import org.evrazcoin.evrazwallet.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TradingScheduleFragment extends BaseFragment {
    public static final String TAG = TradingScheduleFragment.class.getName();


    private OnFragmentInteractionListener mListener;
    private MenuItem backMenuItem;
    private BtsFragmentPageAdapter mExchangeFragmentPageAdapter;

    @BindView(R.id.chart)
    CombinedChart mChart;
    @BindView(R.id.layoutChartLoading)
    View mLoadingChartView;
    @BindView(R.id.layoutLoadingError)
    View mLoadingErrorView;
    @BindView(R.id.layoutSelected)
    View mviewLayoutSelected;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.viewPagerSwitcher)
    RadioGroup viewPagerSwitcher;

    private static final int SHOW_CHART_VIEW_NO_DATA = 0;
    private static final int SHOW_CHART_VIEW_LODING = 1;
    private static final int SHOW_CHART_VIEW_LODING_FAIL = 2;
    private static final int SHOW_CHART_VIEW_READY = 3;
    private static final int SHOW_CHART_VIEW_NOT_SUPPORT = 4;

    public TradingScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        backMenuItem = menu.add(R.string.back)
                .setIcon(R.drawable.abc_ic_ab_back_material)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == backMenuItem)
            getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public static TradingScheduleFragment newInstance() {
        return new TradingScheduleFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        onShow();
        ((NewMainActivity) getActivity()).setTitleVisible(true);

    }

    @Override
    public void onDestroyView() {
        ((NewMainActivity) getActivity()).setTitleVisible(false);
        super.onDestroyView();
    }

    @Override
    public void onShow() {
        super.onShow();
        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        viewModel.getHistoryPrice().observe(this, historyPriceListResource -> {
            switch (historyPriceListResource.status) {
                case ERROR:
                    changeShowedView(SHOW_CHART_VIEW_LODING_FAIL);
                    break;
                case LOADING:
                    changeShowedView(SHOW_CHART_VIEW_LODING);
                    break;
                case SUCCESS:
                    changeShowedView(SHOW_CHART_VIEW_READY);
                    updateChartData(historyPriceListResource.data);
                    break;
            }
        });

    }

    @Override
    public void onHide() {
        super.onHide();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_trading_schedule, container, false);
        ButterKnife.bind(this, view);
        Resources res = getResources();
        mExchangeFragmentPageAdapter = new BtsFragmentPageAdapter(getFragmentManager());
        mExchangeFragmentPageAdapter.addFragment(TransactionSellBuyFragment.newInstance(TransactionSellBuyFragment.TRANSACTION_BUY), "");
        mExchangeFragmentPageAdapter.addFragment(TransactionSellBuyFragment.newInstance(TransactionSellBuyFragment.TRANSACTION_SELL), "");
        mExchangeFragmentPageAdapter.addFragment(TradingOrdersFragment.newInstance(),"");
        mExchangeFragmentPageAdapter.addFragment(OpenOrdersFragment.newInstance(), "");
//        mWalletFragmentPageAdapter.addFragment(TransactionsFragment.newInstance("", ""), res.getString(R.string.tab_transactions));


        mViewPager.setAdapter(mExchangeFragmentPageAdapter);
        initPager(mViewPager, mExchangeFragmentPageAdapter);
        viewPagerSwitcher.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.buyButton:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.sellButton:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.ordersButton:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.myOrdersButton:
                    mViewPager.setCurrentItem(3);
                    break;
            }
        });
        viewPagerSwitcher.check(R.id.buyButton);
        /////////////////////
        initChart();

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof BarEntry) {
                    return;
                }

                CandleEntry candleEntry = (CandleEntry) e;
                mviewLayoutSelected.setVisibility(View.VISIBLE);
                TextView textViewDate = (TextView) mviewLayoutSelected.findViewById(R.id.textViewDate);
                TextView textViewHigh = (TextView) mviewLayoutSelected.findViewById(R.id.textViewHigh);
                TextView textViewLow = (TextView) mviewLayoutSelected.findViewById(R.id.textViewLow);
                TextView textViewChange = (TextView) mviewLayoutSelected.findViewById(R.id.textViewChange);

                HistoryPrice historyPrice = (HistoryPrice) candleEntry.getData();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
                textViewDate.setText(simpleDateFormat.format(historyPrice.date));

                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                textViewHigh.setText(decimalFormat.format(candleEntry.getHigh()));
                textViewLow.setText(decimalFormat.format(candleEntry.getHigh()));

                float fChange = (candleEntry.getClose() - candleEntry.getOpen()) / candleEntry.getOpen();
                if (!MainActivity.rasingColorRevers) {
                    if (fChange < 0) {
                        textViewChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.quotation_top_red));
                        textViewChange.setText(
                                String.format(
                                        Locale.ENGLISH, "%.2f%%",
                                        fChange)
                        );

                    } else {
                        textViewChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.quotation_top_green));
                        textViewChange.setText(
                                String.format(
                                        Locale.ENGLISH, "+%.2f%%",
                                        fChange)
                        );
                    }
                } else {
                    if (fChange < 0) {
                        textViewChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.quotation_top_green));
                        textViewChange.setText(
                                String.format(
                                        Locale.ENGLISH, "%.2f%%",
                                        fChange)
                        );
                    } else {
                        textViewChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.quotation_top_red));
                        textViewChange.setText(
                                String.format(
                                        Locale.ENGLISH, "+%.2f%%",
                                        fChange)
                        );
                    }
                }


            }

            @Override
            public void onNothingSelected() {
                mviewLayoutSelected.setVisibility(View.INVISIBLE);
            }
        });


        return view;
    }

    private void initChart() {
        int colorHomeBg = getResources().getColor(android.R.color.transparent);
        int colorLine = getResources().getColor(R.color.common_divider);
        int colorText = getResources().getColor(R.color.text_grey_light);

        mChart.setDescription(null);
        mChart.setDrawGridBackground(true);
        mChart.setBackgroundColor(colorHomeBg);
        mChart.setGridBackgroundColor(colorHomeBg);
        mChart.setScaleYEnabled(false);
        mChart.setPinchZoom(true);

        mChart.setNoDataText(getString(R.string.main_activity_loading));
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(colorLine);
        xAxis.setTextColor(colorText);
        xAxis.setLabelCount(3);
        //xAxis.setSpaceBetweenLabels(4);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(4, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setGridColor(colorLine);
        leftAxis.setTextColor(colorText);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextSize(8);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setGridColor(colorLine);
        rightAxis.setTextColor(colorText);
        rightAxis.setAxisMinimum(0);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateChartData(List<HistoryPrice> historyPriceList) {
        class xAxisValueFormater implements IAxisValueFormatter {
            private List<HistoryPrice> mListPrices;

            public xAxisValueFormater(List<HistoryPrice> listPrices) {
                mListPrices = listPrices;
            }
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int nValue = (int)value;
                if (nValue < mListPrices.size()) {
                    Date date = mListPrices.get(nValue).date;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");

                    return simpleDateFormat.format(date);
                } else {
                    return "";
                }
            }
        }

        initializeData(historyPriceList);
        IAxisValueFormatter xvalueFormater = new xAxisValueFormater(historyPriceList);
        mChart.getXAxis().setValueFormatter(xvalueFormater);
    }

    private void initializeData(List<HistoryPrice> listHistoryPrice) {
        mviewLayoutSelected.setVisibility(View.INVISIBLE);
        List<CandleEntry> candleEntryList = new ArrayList<>();
        List<BarEntry> barEntryList = new ArrayList<>();
        int i = 0;
        for (HistoryPrice price : listHistoryPrice) {
            int nPosition = i++;
            CandleEntry candleEntry = new CandleEntry(nPosition, (float)price.high, (float)price.low, (float)price.open, (float)price.close, price);
            candleEntryList.add(candleEntry);

            BarEntry barEntry = new BarEntry((float)nPosition, (float)price.volume);
            barEntryList.add(barEntry);
        }

        CandleDataSet set = new CandleDataSet(candleEntryList, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(0.7f);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        int nColorGreen = ContextCompat.getColor(getActivity(), R.color.candle_green);
        int nColorRed = ContextCompat.getColor(getActivity(), R.color.quotation_top_red);
        if (!MainActivity.rasingColorRevers) {
            set.setDecreasingColor(nColorRed);
            set.setIncreasingColor(nColorGreen);
            set.setNeutralColor(nColorGreen);
        } else {
            set.setDecreasingColor(nColorGreen);
            set.setIncreasingColor(nColorRed);
            set.setNeutralColor(nColorRed);
        }
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.5f);
        set.setHighLightColor(Color.WHITE);
        set.setDrawValues(false);
        set.setForm(Legend.LegendForm.EMPTY);

        BarDataSet barDataSet = new BarDataSet(barEntryList, "");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setColor(getResources().getColor(R.color.grey), 25);
        barDataSet.setDrawValues(false);
        barDataSet.setForm(Legend.LegendForm.EMPTY);

        CombinedData combinedData = mChart.getCombinedData();
        if (combinedData == null) {
            combinedData = new CombinedData();
        }

        CandleData candleData = combinedData.getCandleData();
        if (candleData == null) {
            candleData = new CandleData();
        }
        if (candleData.getDataSetCount() > 0) {
            candleData.removeDataSet(0);
            candleData.notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        candleData.addDataSet(set);
        candleData.notifyDataChanged();
        combinedData.setData(candleData);

        BarData barData = combinedData.getBarData();
        if (barData == null) {
            barData = new BarData();
        }

        if (barData.getDataSetCount() > 0) {
            barData.removeDataSet(0);
            barData.notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        barData.addDataSet(barDataSet);
        barData.notifyDataChanged();
        combinedData.setData(barData);

        mChart.setData(combinedData);
        mChart.notifyDataSetChanged();
        mChart.fitScreen();
        mChart.setVisibleXRangeMaximum(48);
        mChart.moveViewToX(mChart.getXChartMax() - 48);
    }

    private void changeShowedView(int nShowView) {
        switch (nShowView) {
            case SHOW_CHART_VIEW_LODING:
                mLoadingChartView.setVisibility(View.VISIBLE);
                mChart.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
            case SHOW_CHART_VIEW_LODING_FAIL:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mChart.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.VISIBLE);
                break;
            case SHOW_CHART_VIEW_READY:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mChart.setVisibility(View.VISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
