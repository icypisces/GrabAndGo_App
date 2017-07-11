package com.example.ntut.grabandgo.orders_daily;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ntut.grabandgo.Order;
import com.example.ntut.grabandgo.OrderItem;
import com.example.ntut.grabandgo.R;

import java.util.List;

public class PaidOrderFragment extends Fragment {
    private static final String TAG = "PaidOrderFragment";
    private RecyclerView recyclerView;

    private List<Order> orderList = null;
    private List<OrderItem> orderitemList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_paid, container, false);  //取得layout檔
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
        public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(
                    R.layout.order_item_view, parent, false);
            return new OrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderAdapter.MyViewHolder holder, int position) {
            final Order order = orderList.get(position);
            holder.tvOrderStatus.setHeight(0);
//            holder.tvOrderStatus.setText(orderStatus);
            holder.tvPickerName.setText(String.valueOf(order.getM_pickupname()));
            holder.tvPhone.setText(String.valueOf(order.getOrd_tel()));
            holder.tvPicktime.setText(String.valueOf(order.getOrd_pickuptime()));
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),PaidOrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }
}
