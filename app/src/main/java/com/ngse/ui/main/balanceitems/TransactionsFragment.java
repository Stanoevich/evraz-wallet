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

import com.bitshares.bitshareswallet.BaseFragment;

import com.bitshares.bitshareswallet.viewmodel.TransactionViewModel;

import org.evrazcoin.evrazwallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TransactionsFragment extends BaseFragment {

    public static final String TAG = TransactionsFragment.class.getName();
    private OnFragmentInteractionListener mListener;

    private TranactionsAdapter mTranactionsAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.layoutChartLoading)
    View mLoadingChartView;
    @BindView(R.id.layoutLoadingError)
    View mLoadingErrorView;
    private MenuItem backMenuItem;
    private static final int SHOW_HISTORY_VIEW_LODING = 1;
    private static final int SHOW_HISTORY_VIEW_LODING_FAIL = 2;
    private static final int SHOW_HISTORY_VIEW_READY = 3;

    @Override
    public void onResume() {
        super.onResume();
        onShow();
    }

    @Override
    public void onShow() {
        super.onShow();

        TransactionViewModel viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        viewModel.getOperationHistory().observe(
                this,
                operationHistoryWrapperResource -> {
                    switch (operationHistoryWrapperResource.status) {
                        case ERROR:
//                            changeShowedView(SHOW_HISTORY_VIEW_LODING_FAIL);
                            break;
                        case SUCCESS:
                            changeShowedView(SHOW_HISTORY_VIEW_READY);
                            mTranactionsAdapter.notifyTransactionDataChanged(operationHistoryWrapperResource.data);
                            break;
                        case LOADING:
                            if (operationHistoryWrapperResource.data != null) {
                                changeShowedView(SHOW_HISTORY_VIEW_READY);
                                mTranactionsAdapter.notifyTransactionDataChanged(operationHistoryWrapperResource.data);
                            } else {
                                changeShowedView(SHOW_HISTORY_VIEW_LODING);
                            }
                            break;
                    }
                });
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

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_transactions, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTranactionsAdapter = new TranactionsAdapter(this, getActivity());
        mRecyclerView.setAdapter(mTranactionsAdapter);

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


    private void changeShowedView(int nShowView) {
        switch (nShowView) {
            case SHOW_HISTORY_VIEW_LODING:
                mLoadingChartView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
            case SHOW_HISTORY_VIEW_LODING_FAIL:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mLoadingErrorView.setVisibility(View.VISIBLE);
                break;
            case SHOW_HISTORY_VIEW_READY:
                mLoadingChartView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mLoadingErrorView.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
