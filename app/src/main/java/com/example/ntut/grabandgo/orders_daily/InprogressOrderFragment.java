package com.example.ntut.grabandgo.orders_daily;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.BaseFragment;
import com.example.ntut.grabandgo.Common;
import com.example.ntut.grabandgo.MainActivity;
import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;
import com.example.ntut.grabandgo.Restaurant_related.RestValidateNotYetActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InprogressOrderFragment extends BaseFragment {
    private String ServletName = "/AppStoreOrderDailyServlet";
    private static final String TAG = "InprogressOrderFragment";
    private RecyclerView recyclerView;
    private Button btItems;
    private AsyncTask OrderIsReadTask;
    private ProgressDialog progressDialog;
    private String ord_id;
    private int readOrder;

    private List<Order> orderList = null;
    private List<OrderItem> orderitemList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_inprogress,container,false);  //取得layout檔
        findViews(view);

        //取得自Activity送來的資料．
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderList = (List<Order>) bundle.getSerializable("orderList");
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new OrderAdapter(getActivity(), orderList));
        }
        return view;
    }

    private void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        btItems = (Button) view.findViewById(R.id.btItems);
    }

//-------------------------------------RecyclerView.Adapter-----------------------------------------

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
        private Context context;
        private List<Order> orderList;

        public OrderAdapter(Context context, List<Order> orderList) {
            this.context = context;
            this.orderList = orderList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderStatus, tvPickerName, tvPhone, tvPicktime;
            LinearLayout linearLayoutOrder;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvOrderStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
                tvPickerName = (TextView) itemView.findViewById(R.id.tvPickerName);
                tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
                tvPicktime = (TextView) itemView.findViewById(R.id.tvPicktime);
                linearLayoutOrder = (LinearLayout)itemView.findViewById(R.id.linearLayoutOrder);
            }
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(
                    R.layout.order_item_view, parent, false);
            return new OrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Order order = orderList.get(position);
            String orderStatus = "";
            if ( order.getIsRead() == 0 ) {
                holder.linearLayoutOrder.setBackgroundResource(R.drawable.button_pink);
                orderStatus = getResources().getString(R.string.notRead);
            } else {
                holder.tvOrderStatus.setHeight(0);
            }
            holder.tvOrderStatus.setText(String.valueOf(orderStatus));
            holder.tvPickerName.setText(String.valueOf(order.getM_pickupname()));
            holder.tvPhone.setText(String.valueOf(order.getOrd_tel()));
            holder.tvPicktime.setText(String.valueOf(order.getOrd_pickuptime()));
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    ord_id = String.valueOf(order.getOrd_id());
                    readOrder = 0;
                    if (order.getIsRead() == 0) {
                        updateIsRead(order);
                    } else {
                        Intent intent = new Intent(getActivity(),InprogressOrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("order", order);
                        bundle.putSerializable("readOrder", readOrder);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void updateIsRead(Order order) {
        String url = Common.URL + ServletName ;
        if (Common.networkConnected(getActivity())) {
            OrderIsReadTask = new OrderIsReadTask().execute(url, order);
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }


    class OrderIsReadTask extends AsyncTask<Object, Void, List<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<Object> doInBackground(Object... params) {
            List<Object> list = new ArrayList<>();
            readOrder = -1;
            String url = (String)params[0];
            Order order = (Order)params[1];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("param", "OrderIsRead");
            jsonObject.addProperty("ord_id", ord_id);
            try {
                jsonIn = Common.getRemoteData(url, jsonObject.toString(), TAG);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return list;
            }

            Gson gson = new Gson();
            JsonObject joResult = gson.fromJson(jsonIn.toString(),
                    JsonObject.class);
            int readOrder = Integer.parseInt(joResult.get("readOrder").getAsString());
            list.add(readOrder);
            list.add(order);
            return list;
        }

        @Override
        protected void onPostExecute(List<Object> list) {
            super.onPostExecute(list);
            Log.d(TAG, "list = " + list);
            int readOrder = (int) list.get(0);
            Order order = (Order)list.get(1);
            if( readOrder == 1 ){
                Intent intent = new Intent(getActivity(),InprogressOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                bundle.putSerializable("readOrder", readOrder);
                intent.putExtras(bundle);
                startActivity(intent);
                progressDialog.cancel();
            } else if( readOrder == -1 ){
                Common.showToast(getActivity(), "更新訂單為已讀取失敗");
                progressDialog.cancel();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btItems.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),InprogressOrderItemSummaryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void lazyLoad() {

    }
}
