package com.team.androidpos.ui.sale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.team.androidpos.R;
import com.team.androidpos.databinding.SaleReceiptBinding;
import com.team.androidpos.model.entity.SaleProduct;
import com.team.androidpos.ui.MainActivity;

public class SaleReceiptFragment extends Fragment {

    static final String KEY_SALE_ID = "sale_id";
    static final String KEY_NAV_BACk = "nav_back";

    static final int NAV_SALE_PRODUCT = 1;
    static final int NAV_SALE_HISTORY = 2;

    private SaleReceiptBinding saleReceiptBinding;
    private SaleReceiptViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(this).get(SaleReceiptViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        int nav = getArguments() != null ? getArguments().getInt(KEY_NAV_BACk, 0) : 1;
        if(nav == NAV_SALE_PRODUCT) {
            inflater.inflate(R.menu.menu_finish, menu);
            MainActivity activity =(MainActivity) requireActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_finish) {
            Navigation.findNavController(getView()).popBackStack(R.id.saleProductFragment, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.saleReceiptBinding = SaleReceiptBinding.inflate(inflater, container, false);
        saleReceiptBinding.setLifecycleOwner(this);
        saleReceiptBinding.setViewModel(viewModel);
        return saleReceiptBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity) requireActivity();
        activity.switchToggle(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       long saleId = getArguments() != null ? getArguments().getLong(KEY_SALE_ID, 0) : 0;
       if(saleId > 0) viewModel.saleId.setValue(saleId);

       viewModel.saleProductLiveData.observe(this,list -> {

           saleReceiptBinding.linearLayoutItems.removeAllViews();

           for(SaleProduct sp : list) {
               View itemView = getLayoutInflater().inflate(R.layout.layout_recepit_items, saleReceiptBinding.linearLayoutItems, false);

               TextView tvQty = itemView.findViewById(R.id.tvQty);
               TextView tvItemDesc = itemView.findViewById(R.id.tvItemDesc);
               TextView tvPrice = itemView.findViewById(R.id.tvPrice);

               tvQty.setText(String.valueOf(sp.getQuantity()));
               tvItemDesc.setText(sp.getProductDescription());
               double total = sp.getTotalPrice();
               int abs = (int) total;
               if(total - abs > 0.0) {
                   tvPrice.setText(String.valueOf(total));
               } else {
                   tvPrice.setText(String.valueOf(abs));
               }


               saleReceiptBinding.linearLayoutItems.addView(itemView);
           }

           saleReceiptBinding.linearLayoutItems.invalidate();
        });

    }


}
