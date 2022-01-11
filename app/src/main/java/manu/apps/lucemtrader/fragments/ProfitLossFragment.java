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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.adapters.CryptoAdapter;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Crypto;

public class ProfitLossFragment extends Fragment implements View.OnClickListener {

    TextView tvDisplayTitle, tvTotalProfit, tvTotalLoss;
    MaterialButton btnDollar, btnPercentage;

    CryptoAdapter cryptoAdapter;

    SwipeRefreshLayout srlBtcPercentage;

    RecyclerView rvBtcPercentage;

    ProgressBar pbStats;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.profit_loss_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDisplayTitle = view.findViewById(R.id.tv_display_title);
        btnDollar = view.findViewById(R.id.btn_dollar);
        btnPercentage = view.findViewById(R.id.btn_percentage);
        srlBtcPercentage = view.findViewById(R.id.srl_btc_percentage);
        rvBtcPercentage = view.findViewById(R.id.rv_btc_percentage);
        pbStats = view.findViewById(R.id.pb_stats);
        tvTotalProfit = view.findViewById(R.id.tv_total_profit);
        tvTotalLoss = view.findViewById(R.id.tv_total_loss);

        btnDollar.setOnClickListener(this);
        btnPercentage.setOnClickListener(this);

        Config.swipeRefreshLayoutColorScheme(requireActivity(), srlBtcPercentage);

        populateBtc();

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_dollar){

            tvDisplayTitle.setText(requireActivity().getString(R.string.btc));

            btnPercentage.setStrokeWidth(1);
            btnPercentage.setIconTint(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.appBlueColor)));
            btnPercentage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));


            btnDollar.setStrokeWidth(0);
            btnDollar.setIconTint(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));
            btnDollar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.secondaryLightColor)));

            populateBtc();


        }

        if (viewId == R.id.btn_percentage){

            tvDisplayTitle.setText(requireActivity().getString(R.string.percentage));

            btnDollar.setStrokeWidth(1);
            btnDollar.setIconTint(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.appBlueColor)));
            btnDollar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));

            btnPercentage.setStrokeWidth(0);
            btnPercentage.setIconTint(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.colorWhite)));
            btnPercentage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.
                    getColor(requireActivity(), R.color.secondaryLightColor)));

            populatePercentage();


        }
    }

    private void populateBtc(){

        srlBtcPercentage.setRefreshing(false);

        List<Crypto> btcList = new ArrayList<>();

        btcList.add(new Crypto(1, "BTC #TTPF05", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, true, 0.321011));

        btcList.add(new Crypto(2, "BTC #TTLS04", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, false, 0.321011));

        btcList.add(new Crypto(3, "BTC #TTPF03", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, true, 0.321011));

        btcList.add(new Crypto(4, "BTC #TTPF02", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, false, 0.321011));

        btcList.add(new Crypto(5, "BTC #TTPF02", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, true, 0.321011));

        btcList.add(new Crypto(6, "BTC #TTPF02", 0, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, true, 0.321011));

        double totalProfitValue = 0;
        double totalLossValue = 0;

        for (Crypto cryptoCalculations: btcList){


            if (cryptoCalculations.isProfit()){

                totalProfitValue += cryptoCalculations.getAmount();

            }else {

                totalLossValue += cryptoCalculations.getAmount();

            }

        }

        pbStats.setProgress((int)(totalLossValue/totalProfitValue * 100));

        tvTotalProfit.setText(String.format("%s %s%s", "▲",  "$", totalProfitValue));
        tvTotalLoss.setText(String.format("%s %s%s", "▼", "$", totalLossValue));

        populateCryptos(btcList);

    }

    private void populatePercentage(){

        srlBtcPercentage.setRefreshing(false);

        List<Crypto> percentageList = new ArrayList<>();

        percentageList.add(new Crypto(1, "BTC #TTPCPF05", 0.3210, "Bitcoin",
                1.245, "2021-08-07 13:04", R.drawable.ic_bitcoin, true, 25));

        percentageList.add(new Crypto(2, "DOGE #TTPCLS04", 0.211, "Dogecoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_dogecoin, false, 25));

        percentageList.add(new Crypto(3, "BTC #TTPCLS03", 0.411, "Bitcoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_bitcoin, false, 25));

        percentageList.add(new Crypto(4, "ETH #TTPCLS02", 0.321011, "Ethereum",
                1.245, "2021-08-07 17:04", R.drawable.ic_bitcoin, true, 15));

        percentageList.add(new Crypto(5, "DOGE #TTPCLS01", 0.321011, "Dogecoin",
                1.245, "2021-08-07 17:04", R.drawable.ic_dogecoin, true, 23));

        double totalProfitPercentage = 0;
        double totalLossPercentage = 0;

        for (Crypto cryptoCalculations: percentageList){


            if (cryptoCalculations.isProfit()){

                totalProfitPercentage += cryptoCalculations.getProfitLossPercentage();

            }else {

                totalLossPercentage += cryptoCalculations.getProfitLossPercentage();

            }

        }

        pbStats.setProgress((int)(totalLossPercentage/totalProfitPercentage * 100));

        tvTotalProfit.setText(String.format("%s %s%s", "▲",  "%", new Config().numberFormatter(totalProfitPercentage)));
        tvTotalLoss.setText(String.format("%s %s%s", "▼", "%", new Config().numberFormatter(totalLossPercentage)));

        populateCryptos(percentageList);

    }

    private void populateCryptos(List<Crypto> cryptoList){

        cryptoAdapter = new CryptoAdapter(requireActivity(), cryptoList, "Date", true,
                (crypto, pos) -> {

                });

        rvBtcPercentage.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvBtcPercentage.setAdapter(cryptoAdapter);


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