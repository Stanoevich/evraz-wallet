package com.ngse.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.BtsFragmentPageAdapter;
import com.bitshares.bitshareswallet.TransactionsFragment;

import org.evrazcoin.evrazwallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainWalletFragment extends BaseFragment {
    public static final String TAG = MainWalletFragment.class.getName();


    private OnFragmentInteractionListener mListener;

    private BtsFragmentPageAdapter mWalletFragmentPageAdapter;

    private SendFragment mSendFragment;

    @BindView(R.id.fw_viewPager)
    ViewPager mViewPager;
    @BindView(R.id.viewPagerSwitcher)
    RadioGroup viewPagerSwitcher;


    public MainWalletFragment() {
        // Required empty public constructor
    }

    public void onNewIntent(Intent intent) {
        String strAction = intent.getStringExtra("action");
        if (TextUtils.isEmpty(strAction) == false) {
//            mViewPager.setCurrentItem(1);
            viewPagerSwitcher.check(R.id.sendButton);
            String strName = intent.getStringExtra("name");
            int nAmount = Integer.valueOf(intent.getStringExtra("amount"));
            String strUnit = intent.getStringExtra("unit");
            mSendFragment.processDonate(strName, nAmount, strUnit);
        }
    }

    public static MainWalletFragment newInstance() {
        return new MainWalletFragment();
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onShow() {
        super.onShow();
    }

    @Override
    public void onHide() {
        super.onHide();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_wallet, container, false);
        ButterKnife.bind(this, view);
        Resources res = getResources();
        mWalletFragmentPageAdapter = new BtsFragmentPageAdapter(getFragmentManager());
        mWalletFragmentPageAdapter.addFragment(BalanceFragment.newInstance(), res.getString(R.string.tab_balances));
        mSendFragment = SendFragment.newInstance("", "");
        mWalletFragmentPageAdapter.addFragment(mSendFragment, res.getString(R.string.tab_send));
        mWalletFragmentPageAdapter.addFragment(ExchangeFragment.newInstance(), res.getString(R.string.tab_transactions));
//        mWalletFragmentPageAdapter.addFragment(TransactionsFragment.newInstance("", ""), res.getString(R.string.tab_transactions));


        mViewPager.setAdapter(mWalletFragmentPageAdapter);
        initPager(mViewPager, mWalletFragmentPageAdapter);
        viewPagerSwitcher.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.balanceButton:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.sendButton:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.exchangeButton:
                    mViewPager.setCurrentItem(2);
                    break;
            }
        });
        return view;
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
