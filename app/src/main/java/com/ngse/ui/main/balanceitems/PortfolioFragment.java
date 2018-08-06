package com.ngse.ui.main.balanceitems;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.room.BitsharesBalanceAsset;
import com.bitshares.bitshareswallet.viewmodel.WalletViewModel;

import org.evrazcoin.evrazwallet.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PortfolioFragment extends BaseFragment {

    public static final String TAG = PortfolioFragment.class.getName();
    private BalancesAdapter mBalancesAdapter;

    private OnFragmentInteractionListener mListener;
    private MenuItem backMenuItem;

    @BindView(R.id.textUSDBalance)
    TextView textUSDBalance;
    @BindView(R.id.usdRateTitle)
    TextView usdRateTitle;
    @BindView(R.id.usdCostTitle)
    TextView usdCostTitle;


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

    public static PortfolioFragment newInstance() {
        return new PortfolioFragment();
    }


    @Override
    public void onResume() {
        super.onResume();

        WalletViewModel walletViewModel = ViewModelProviders.of(getActivity()).get(WalletViewModel.class);
        walletViewModel.getBalanceData().observe(
                this, resourceBalanceList -> {
                    switch (resourceBalanceList.status) {
                        case SUCCESS:
                            processShowdata(resourceBalanceList.data);
                            mBalancesAdapter.notifyBalancesDataChanged(resourceBalanceList.data);
                            break;
                        case LOADING:
                            if (resourceBalanceList.data != null) {
                                mBalancesAdapter.notifyBalancesDataChanged(resourceBalanceList.data);
                            }
                            break;
                    }
                });
    }

    @Override
    public void onShow() {
        super.onShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_portfolio, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBalancesAdapter = new BalancesAdapter();
        recyclerView.setAdapter(mBalancesAdapter);

        ButterKnife.bind(this, view);
        return view;
    }

    void processShowdata(List<BitsharesBalanceAsset> bitsharesBalanceAssetList) {
//        long totalBTS = 0;
        long totalBalance = 0;
        for (BitsharesBalanceAsset bitsharesBalanceAsset : bitsharesBalanceAssetList) {
//            totalBTS += bitsharesBalanceAsset.total;
            totalBalance += bitsharesBalanceAsset.balance;
        }

        if (!bitsharesBalanceAssetList.isEmpty()) {
            BitsharesBalanceAsset bitsharesBalanceAsset = bitsharesBalanceAssetList.get(0);
           /* double exchangeRate = (double) totalBalance / bitsharesBalanceAsset.currency_precision / totalBTS * bitsharesBalanceAsset.base_precision;
            totalBTS /= bitsharesBalanceAssetList.get(0).base_precision;*/
            totalBalance /= bitsharesBalanceAssetList.get(0).currency_precision;

            String strTotalCurrency = getString(R.string.total_usd, totalBalance,
                    bitsharesBalanceAsset.currency);


            textUSDBalance.setText(strTotalCurrency);
            usdRateTitle.setText(getString(R.string.usd_rate, bitsharesBalanceAsset.currency));
            usdCostTitle.setText(getString(R.string.usd_cost, bitsharesBalanceAsset.currency));
        }

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
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
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

    @Override
    public void notifyUpdate() {

    }

    class BalanceItemViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView viewNumber;
        public TextView viewUnit;
        //        public TextView viewEqual;
        public TextView viewUSBBalance;
        public TextView viewExchangeRate;

        public BalanceItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            viewNumber = (TextView) itemView.findViewById(R.id.textViewNumber);
            viewUnit = (TextView) itemView.findViewById(R.id.textViewUnit);
//            viewEqual = (TextView) itemView.findViewById(R.id.textViewEqual);
            viewUSBBalance = (TextView) itemView.findViewById(R.id.textUSDBalance);
            viewExchangeRate = (TextView) itemView.findViewById(R.id.textExchangeRate);
        }
    }

    class BalancesAdapter extends RecyclerView.Adapter<BalanceItemViewHolder> {
        private List<BitsharesBalanceAsset> bitsharesBalanceAssetList;

        @Override
        public BalanceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_recyclerview_item_balances, parent, false);
            return new BalanceItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BalanceItemViewHolder holder, int position) {
            BitsharesBalanceAsset bitsharesBalanceAsset = bitsharesBalanceAssetList.get(position);
            float balance = (float) bitsharesBalanceAsset.amount / bitsharesBalanceAsset.quote_precision;
            float usdBalance = (float) bitsharesBalanceAsset.balance / bitsharesBalanceAsset.currency_precision;
            float usdExchangeRate = usdBalance / balance;
            String strBalances = String.format(Locale.ENGLISH, "%.2f", balance);
            String strUsdBalance = String.format(Locale.ENGLISH, "%.4f", usdBalance);
            String strUsdExchangeRate = String.format(Locale.ENGLISH, "%.4f", usdExchangeRate);
            holder.viewNumber.setText(strBalances);

            holder.viewUnit.setText(bitsharesBalanceAsset.quote);

            holder.viewExchangeRate.setText(strUsdExchangeRate);
            holder.viewUSBBalance.setText(strUsdBalance);

        }

        @Override
        public int getItemCount() {
            if (bitsharesBalanceAssetList == null) {
                return 0;
            } else {
                return bitsharesBalanceAssetList.size();
            }
        }

        public void notifyBalancesDataChanged(List<BitsharesBalanceAsset> bitsharesBalanceAssetList) {
            this.bitsharesBalanceAssetList = bitsharesBalanceAssetList;

            notifyDataSetChanged();
        }
    }

}
