package com.ngse.ui.main.balanceitems;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.CancelOrderDialog;
import com.bitshares.bitshareswallet.TransactionSellBuyPasswordDialog;
import com.bitshares.bitshareswallet.market.MarketStat;
import com.bitshares.bitshareswallet.market.OpenOrder;
import com.bitshares.bitshareswallet.wallet.BitsharesWalletWraper;
import com.bitshares.bitshareswallet.wallet.Broadcast;
import com.bitshares.bitshareswallet.wallet.exception.NetworkStatusException;
import com.bitshares.bitshareswallet.wallet.graphene.chain.utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.evrazcoin.evrazwallet.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class OpenOrdersFragment extends BaseFragment
        implements MarketStat.OnMarketStatUpdateListener {

    public static final String TAG = OpenOrdersFragment.class.getName();
    private static final long MARKET_STAT_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(10);

    private RecyclerView listOrders;
    private OrderListAdapter adapterOrders;
    private TextView txtNone;

    private MarketStat marketStat;
    private String baseAsset;
    private String quoteAsset;
    private KProgressHUD mProcessHud;
//    private MenuItem backMenuItem;

    private BroadcastReceiver currencyUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            marketStat.unsubscribe(baseAsset, quoteAsset);
            updateCurrency();
            marketStat.subscribe(baseAsset, quoteAsset, MarketStat.STAT_MARKET_OPEN_ORDER,
                    MARKET_STAT_INTERVAL_MILLIS, OpenOrdersFragment.this);
        }
    };

    private void refresh() {
        marketStat.updateImmediately(baseAsset, quoteAsset);
    }

    public OpenOrdersFragment() {
        marketStat = new MarketStat();
    }

  /*  @Override
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
    }*/

    public static OpenOrdersFragment newInstance() {
        OpenOrdersFragment fragment = new OpenOrdersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_fragment_orders, container, false);

        adapterOrders = new OrderListAdapter();

        listOrders = (RecyclerView) view.findViewById(R.id.fo_list);
        listOrders.setAdapter(adapterOrders);
        listOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        listOrders.setItemAnimator(null);

        txtNone = (TextView) view.findViewById(R.id.fo_txt_none);

        mProcessHud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onShow();
    }

    @Override
    public void onShow() {
        super.onShow();
        updateCurrency();
        marketStat.subscribe(baseAsset, quoteAsset, MarketStat.STAT_MARKET_OPEN_ORDER,
                MARKET_STAT_INTERVAL_MILLIS, this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcast.CURRENCY_UPDATED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(currencyUpdateReceiver, intentFilter);
    }

    @Override
    public void onHide() {
        super.onHide();
        marketStat.unsubscribe(baseAsset, quoteAsset);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(currencyUpdateReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMarketStatUpdate(MarketStat.Stat stat) {
        if (stat.openOrders != null) {
            List<OpenOrder> orderList = stat.openOrders;
            if (orderList.size() == 0) {
                txtNone.setVisibility(View.VISIBLE);
            } else {
                txtNone.setVisibility(View.GONE);
            }
            adapterOrders.setListOrders(orderList);
        }
    }

    private void updateCurrency() {
        Context ctx = getContext();
        if (ctx == null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String strAssetPair = prefs.getString("quotation_currency_pair", "BTS:USD");
        String strAsset[] = strAssetPair.split(":");
        baseAsset = strAsset[1];
        quoteAsset = strAsset[0];
    }

    class OrderListAdapter extends RecyclerView.Adapter<OrderViewHolder> {
        List<OpenOrder> listOrders;

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.new_view_my_order_item, parent, false);
            return new OrderViewHolder(view);
        }

        public void setListOrders(List<OpenOrder> list) {
            listOrders = list;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {
            holder.update(listOrders.get(position));
        }

        @Override
        public int getItemCount() {
            return listOrders != null ? listOrders.size() : 0;
        }
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCacel;
        TextView txtOperation;
        TextView txtPrice;
//        TextView txtPriceUsd;

        OpenOrder order;

        public OrderViewHolder(View itemView) {
            super(itemView);
            imageCacel = itemView.findViewById(R.id.voi_cacel);

            txtOperation = (TextView) itemView.findViewById(R.id.voi_txt_operation);
            txtPrice = (TextView) itemView.findViewById(R.id.voi_txt_price);
//            txtPriceUsd = (TextView) itemView.findViewById(R.id.voi_txt_price_usd);


            imageCacel.setOnClickListener(onCancelClickListener);
        }

        public void update(OpenOrder order) {

            String operation;
            String firstAmount;
            String secondAmount;

            this.order = order;
            String baseCurrency = utils.getAssetSymbolDisply(order.base.symbol);
            String quoteCurrency = utils.getAssetSymbolDisply(order.quote.symbol);
            if (order.limitOrder.sell_price.quote.asset_id.equals(order.quote.id)) {
                operation = txtOperation.getContext().getString(R.string.label_buy);
                double buyAmount = utils.get_asset_amount(order.limitOrder.sell_price.quote.amount, order.quote);
                firstAmount = String.format(Locale.ENGLISH, "%.2f %s", buyAmount, quoteCurrency);
                double sellAmount = utils.get_asset_amount(order.limitOrder.sell_price.base.amount, order.base);
                secondAmount = String.format(Locale.ENGLISH, "%.2f %s", sellAmount, baseCurrency);

              /*  txtOperation.setText(R.string.label_buy);
                txtTargetCoin.setText(String.format("%.6f", buyAmount));
                txtSrcCoin.setText(String.format("%.6f", sellAmount));*/
            } else {
                operation = txtOperation.getContext().getString(R.string.label_sell);
                double buyAmount = utils.get_asset_amount(order.limitOrder.sell_price.quote.amount, order.base);
                secondAmount = String.format(Locale.ENGLISH, "%.2f %s", buyAmount, baseCurrency);
                double sellAmount = utils.get_asset_amount(order.limitOrder.sell_price.base.amount, order.quote);
                firstAmount = String.format(Locale.ENGLISH, "%.2f %s", sellAmount, quoteCurrency);


               /* txtOperation.setText(R.string.label_sell);
                txtSrcCoin.setText(String.format("%.6f", buyAmount));
                txtTargetCoin.setText(String.format("%.6f", sellAmount));*/
            }
            txtOperation.setText(txtOperation.getContext().getString(R.string.buy_string, operation, firstAmount, secondAmount));
            txtPrice.setText(String.format(Locale.ENGLISH, "%.4f", order.price));
//            txtPriceUsd.setText();
           /* SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            txtExpiration.setText(formatter.format(order.limitOrder.expiration));*/
        }

        private View.OnClickListener onCancelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BitsharesWalletWraper.getInstance().is_locked()) {
                    showPasswordDialog();
                } else {
                    showConfirmDialog();
                }

            }
        };

        private void showConfirmDialog() {
            CancelOrderDialog dialog = new CancelOrderDialog(getActivity(), order);
            dialog.setListener(new CancelOrderDialog.OnDialogInterationListener() {
                @Override
                public void onConfirm() {
                    new AsyncTask<Integer, Integer, Boolean>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            Log.i("OrderFragment", "1111111111");
                            mProcessHud.show();
                            Log.i("OrderFragment", "2222222222");
                        }

                        @Override
                        protected void onPostExecute(Boolean success) {
                            super.onPostExecute(success);
                            mProcessHud.dismiss();
                            if (success) {
                                refresh();
                            } else {
                                Toast.makeText(getContext(), getContext().getString(R.string.import_activity_connect_failed), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        protected Boolean doInBackground(Integer... params) {
                            try {
                                BitsharesWalletWraper.getInstance().cancel_order(order.limitOrder.id);
                                return true;
                            } catch (NetworkStatusException e) {
                                return false;
                            }
                        }
                    }.execute();
                }

                @Override
                public void onReject() {

                }
            });
            dialog.show();
        }

        private void showPasswordDialog() {
            TransactionSellBuyPasswordDialog builder = new TransactionSellBuyPasswordDialog(getActivity());
            builder.setListener(new TransactionSellBuyPasswordDialog.OnDialogInterationListener() {
                @Override
                public void onConfirm(AlertDialog dialog, String passwordString) {
                    if (BitsharesWalletWraper.getInstance().unlock(passwordString) == 0) {
                        dialog.dismiss();
                        showConfirmDialog();
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.password_invalid), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onReject(AlertDialog dialog) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}
