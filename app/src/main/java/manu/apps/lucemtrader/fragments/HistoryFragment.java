package manu.apps.lucemtrader.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.adapters.CryptoAdapter;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Crypto;

public class HistoryFragment extends Fragment implements View.OnClickListener {

    ChipGroup cgHistoric;
    Chip cWithdrawals, cDeposits;

    List<Crypto> cryptoWithdrawalList;
    List<Crypto> cryptoDepositList;

    CryptoAdapter cryptoAdapter;

    RecyclerView rvHistoric;

    SwipeRefreshLayout srlHistoric;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cgHistoric = view.findViewById(R.id.cg_historic);
        cWithdrawals = view.findViewById(R.id.c_withdrawals);
        cDeposits = view.findViewById(R.id.c_deposits);
        rvHistoric = view.findViewById(R.id.rv_historic);
        srlHistoric = view.findViewById(R.id.srl_historic);

        cWithdrawals.setOnClickListener(this);
        cDeposits.setOnClickListener(this);

        Config.swipeRefreshLayoutColorScheme(requireActivity(), srlHistoric);


        populateWithdrawalCrypto();

    }

    private void populateWithdrawalCrypto(){

        srlHistoric.setRefreshing(false);

        cryptoWithdrawalList = new ArrayList<>();

        cryptoWithdrawalList.add(new Crypto(1, "BTC #WT05", 0.321011, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin));

        cryptoWithdrawalList.add(new Crypto(2, "DOGE #WT04", 0.321011, "Dogecoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_dogecoin));

        cryptoWithdrawalList.add(new Crypto(3, "ETH #WT03", 0.321011, "Ethereum",
                1.245, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoWithdrawalList.add(new Crypto(4, "ETH #WT02", 0.321011, "Ethereum",
                1.245, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoWithdrawalList.add(new Crypto(5, "BTC #WT01", 0.321011, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin));


        populateCryptos(cryptoWithdrawalList);

    }

    private void populateDepositCrypto(){

        srlHistoric.setRefreshing(false);

        cryptoDepositList = new ArrayList<>();

        cryptoDepositList.add(new Crypto(1, "BTC #DP05", 0.321011, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin));

        cryptoDepositList.add(new Crypto(2, "ETH #DP04", 0.321011, "Ethereum",
                1.245, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoDepositList.add(new Crypto(3, "ETH #DP03", 0.321011, "Ethereum",
                1.245, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoDepositList.add(new Crypto(4, "BTC #DP02", 0.321011, "Bitcoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_bitcoin));

        cryptoDepositList.add(new Crypto(5, "DOGE #DP01", 0.321011, "Bitcoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_dogecoin));


        populateCryptos(cryptoDepositList);

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.c_withdrawals){

            cWithdrawals.setTextColor(requireActivity().getResources().
                    getColor(R.color.colorWhite));

            cWithdrawals.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.secondaryLightColor)));

            cWithdrawals.setChipStrokeWidth(0f);

            cDeposits.setTextColor(requireActivity().getResources().
                    getColor(R.color.appBlueColor));

            cDeposits.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));

            cDeposits.setChipStrokeWidth(0.5f);

            populateWithdrawalCrypto();

        }

        if (viewId == R.id.c_deposits){

            cDeposits.setTextColor(requireActivity().getResources().
                    getColor(R.color.colorWhite));

            cDeposits.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.secondaryLightColor)));

            cDeposits.setChipStrokeWidth(0f);

            cWithdrawals.setTextColor(requireActivity().getResources().
                    getColor(R.color.appBlueColor));

            cWithdrawals.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));

            cWithdrawals.setChipStrokeWidth(0.5f);

            populateDepositCrypto();

        }
    }

    private void populateCryptos(List<Crypto> cryptoList){

        cryptoAdapter = new CryptoAdapter(requireActivity(), cryptoList, "Date", false,
                (crypto, pos) -> {

        });

        rvHistoric.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvHistoric.setAdapter(cryptoAdapter);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (Config.returnUserType(requireActivity()).equalsIgnoreCase("AdminClickedInvestor")){

            inflater.inflate(R.menu.admin_clicked_investor_overflow_menu, menu);

            menu.findItem(R.id.action_admin_account).setVisible(false);
            menu.findItem(R.id.action_admin_settings).setVisible(false);
            menu.findItem(R.id.action_admin_log_out).setVisible(false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_edit_investor) {

        }else if (item.getItemId() == R.id.action_delete_investor){

        }

        return super.onOptionsItemSelected(item);

    }
}