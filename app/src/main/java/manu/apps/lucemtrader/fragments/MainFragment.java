package manu.apps.lucemtrader.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.adapters.CryptoAdapter;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Crypto;
import manu.apps.lucemtrader.dao.FirebaseDao;
import manu.apps.lucemtrader.interfaces.ToolbarInterface;

public class MainFragment extends Fragment implements View.OnClickListener {

    List<Crypto> cryptoList;

    TextView tvNoData;
    SwipeRefreshLayout srlCrypto;
    RecyclerView rvCrypto;

    NestedScrollView mainNsv;

    MaterialButton btnRetrySwipeDownToRefresh;

    RelativeLayout rlNoData;

    ProgressDialog progressDialog;

    CryptoAdapter cryptoAdapter;

    FloatingActionButton fabFilter, fabAdd, fabSearch;

    MaterialCardView mcvCryptoTopDetails;

    BottomSheetBehavior<View> filterBottomSheetBehavior;

    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

//        new FirebaseDao().returnCryptoListWithValueEventListener();
        new FirebaseDao().returnInvestorsListWithValueEventListener();
        new FirebaseDao().returnCryptoListWithQuery();
        new FirebaseDao().returnInvestorsListWithQuery();


        if (returnFragmentLaunchType().equalsIgnoreCase("fromAdmin")) {

            //fromAdminBackPressed();

        }


        Glide.with(this)
                .asBitmap()
                .load("https://www.witdesignkenya.com/wp-content/uploads/2019/08/tta-lawyers-profile-design-01-1460x880.png")
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        if (Config.returnUserType(requireActivity()).equalsIgnoreCase("AdminClickedInvestor")){

                            ((ToolbarInterface) requireActivity()).setOverflowIcon(
                                    new BitmapDrawable(requireActivity().getResources(),
                                            Config.getCroppedBitmap(Bitmap.createScaledBitmap(resource,
                                                    80, 80, true),120)));

                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        tvNoData = view.findViewById(R.id.tv_no_data);
        srlCrypto = view.findViewById(R.id.srl_crypto);
        rvCrypto = view.findViewById(R.id.rv_crypto);
        mainNsv = view.findViewById(R.id.main_nsv);
        fabFilter = view.findViewById(R.id.fab_filter);
        fabAdd = view.findViewById(R.id.fab_add);
        fabSearch = view.findViewById(R.id.fab_search);

        mcvCryptoTopDetails = view.findViewById(R.id.mcv_crypto_top_details);

        rlNoData = view.findViewById(R.id.rl_no_data);
        btnRetrySwipeDownToRefresh = view.findViewById(R.id.btn_retry_swipe_down_to_refresh);

        Config.swipeRefreshLayoutColorScheme(requireActivity(), srlCrypto);

        btnRetrySwipeDownToRefresh = view.findViewById(R.id.btn_retry_swipe_down_to_refresh);

        btnRetrySwipeDownToRefresh.setOnClickListener(this);

        new Config().hideFabOnScrollDown(mainNsv, fabFilter, fabSearch, fabAdd);

        Config.hideFabOnRecyclerViewScrollDown(requireActivity(), rvCrypto, fabFilter, fabSearch, fabAdd);

        if (Config.returnUserType(requireActivity()).equalsIgnoreCase("Investor")) {

            mcvCryptoTopDetails.setVisibility(View.VISIBLE);

            fabAdd.setVisibility(View.GONE);

            fabSearch.setVisibility(View.VISIBLE);

        } else if (Config.returnUserType(requireActivity()).equalsIgnoreCase("Admin")) {

            mcvCryptoTopDetails.setVisibility(View.GONE);

            fabAdd.setVisibility(View.VISIBLE);

            fabSearch.setVisibility(View.GONE);

        }
        /* *else if (Config.returnUserType(requireActivity()).equalsIgnoreCase("AdminClickedInvestor")){

            mcvCryptoTopDetails.setVisibility(View.VISIBLE);

            fabAdd.setVisibility(View.GONE);

            fabSearch.setVisibility(View.VISIBLE);

            fromAdminBackPressed();

        }*/

        View filterOptionsView = view.findViewById(R.id.filter_bottom_sheet);
        filterBottomSheetBehavior = BottomSheetBehavior.from(filterOptionsView);
        filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        populateCryptos();

        fabFilter.setOnClickListener(this);

    }

    private String returnFragmentLaunchType() {

        Log.wtf("FragmentLaunchTypeInMainFragment",
                MainFragmentArgs.fromBundle(getArguments()).getMainFragmentLaunchType());

        return MainFragmentArgs.fromBundle(getArguments()).getMainFragmentLaunchType();

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.fab_filter) {

            filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

    }

    private void populateCryptos() {

        cryptoList = new ArrayList<>();

        cryptoList.add(new Crypto(1, "BTC", 2.932011, "Bitcoin",
                19.000, "2021-08-07 13:04", R.drawable.ic_bitcoin));

        cryptoList.add(new Crypto(1, "ETH", 2.932011, "Ethereum",
                1.32, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoList.add(new Crypto(1, "ETH", 2.932011, "Ethereum",
                1.32, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoList.add(new Crypto(1, "ETH", 2.932011, "Ethereum",
                1.32, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoList.add(new Crypto(1, "ETH", 2.932011, "Ethereum",
                1.32, "2021-08-07 17:04", R.drawable.ic_ethereum));

        cryptoAdapter = new CryptoAdapter(requireActivity(), cryptoList, "Last Update", false,
                (crypto, pos) -> {

                });

        rvCrypto.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvCrypto.setAdapter(cryptoAdapter);

    }

    // on log out clear preferences
//     new PreferenceManager(requireActivity()).clearPreferences();


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