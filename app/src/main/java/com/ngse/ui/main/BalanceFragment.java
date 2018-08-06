package com.ngse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.room.BitsharesBalanceAsset;
import com.bitshares.bitshareswallet.viewmodel.WalletViewModel;
import com.ngse.ui.NewMainActivity;

import org.evrazcoin.evrazwallet.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BalanceFragment extends BaseFragment {
    public static final String TAG = BalanceFragment.class.getName();

    private OnFragmentInteractionListener mListener;


    @BindView(R.id.textTotalBalance)
    TextView textViewBalances;
    @BindView(R.id.textViewCurrency)
    TextView textViewCurency;


    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }


    @Override
    public void onResume() {
        super.onResume();

        WalletViewModel walletViewModel = ViewModelProviders.of(getActivity()).get(WalletViewModel.class);
        walletViewModel.getBalanceData().observe(
                this, resourceBalanceList -> {
                    switch (resourceBalanceList.status) {
                        case ERROR:
                            processError();
                            break;
                        case SUCCESS:
                            processShowdata(resourceBalanceList.data);
                            break;
                        case LOADING:
                            if (resourceBalanceList.data != null && resourceBalanceList.data.size() != 0) {
                                processShowdata(resourceBalanceList.data);
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
    public void onHide() {
        super.onHide();
    }

    void processShowdata(List<BitsharesBalanceAsset> bitsharesBalanceAssetList) {
        long totalBTS = 0;
        long totalBalance = 0;
        for (BitsharesBalanceAsset bitsharesBalanceAsset : bitsharesBalanceAssetList) {
            totalBTS += bitsharesBalanceAsset.total;
            totalBalance += bitsharesBalanceAsset.balance;
        }

        if (bitsharesBalanceAssetList.isEmpty() == false) {
            BitsharesBalanceAsset bitsharesBalanceAsset = bitsharesBalanceAssetList.get(0);
            double exchangeRate = (double) totalBalance / bitsharesBalanceAsset.currency_precision / totalBTS * bitsharesBalanceAsset.base_precision;
            totalBTS /= bitsharesBalanceAssetList.get(0).base_precision;
            totalBalance /= bitsharesBalanceAssetList.get(0).currency_precision;

            String strTotalCurrency = String.format(
                    Locale.ENGLISH,
                    "= %d %s (%.4f %s/%s)",
                    totalBalance,
                    bitsharesBalanceAsset.currency,
                    exchangeRate,
                    "BTS",
                    bitsharesBalanceAsset.currency
            );

            textViewCurency.setText(strTotalCurrency);
        }

        String strTotalBalance = String.format(Locale.ENGLISH, "%d %s", totalBTS, "BTS");
        textViewBalances.setText(strTotalBalance);
        textViewCurency.setVisibility(View.VISIBLE);
    }

    void processError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        builder.setPositiveButton(R.string.connect_fail_dialog_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WalletViewModel walletViewModel = ViewModelProviders.of(getActivity()).get(WalletViewModel.class);
                walletViewModel.retry();
            }
        });
        builder.setMessage(R.string.connect_fail_message);
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_balance, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.portfolio)
    void portfolioClick() {
        ((NewMainActivity) getActivity()).showPortfolioFragment();
    }

    @OnClick(R.id.openOrders)
    void openOrdersClick() {
        ((NewMainActivity) getActivity()).showOpenOrdersFragment();
    }

    @OnClick(R.id.transactions)
    void transactionsClick() {
        ((NewMainActivity) getActivity()).showTransactionsFragment();
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
}
