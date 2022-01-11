package manu.apps.lucemtrader.fragments;

import android.app.ProgressDialog;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.adapters.InvestorsAdapter;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Investor;
import manu.apps.lucemtrader.classes.SessionManager;
import manu.apps.lucemtrader.dao.FirebaseDao;
import manu.apps.lucemtrader.interfaces.ToolbarInterface;

public class InvestorsFragment extends Fragment implements View.OnClickListener {

    private TextView tvDisplayInvestorsNo;

    private SwipeRefreshLayout srlInvestors;

    private RecyclerView rvInvestors;

    private NavController navController;

    private ProgressDialog progressDialog;

    NestedScrollView nsvInvestors;

    FloatingActionButton fabFilter, fabAdd, fabSearch;

    private BottomSheetBehavior<View> filterBottomSheetBehavior;

    TextInputLayout tilFilterByShow;

    private SessionManager sessionManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.investors_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireActivity());

        navController = Navigation.findNavController(view);

        tvDisplayInvestorsNo = view.findViewById(R.id.tv_display_investors_no);
        srlInvestors = view.findViewById(R.id.srl_investors);
        rvInvestors = view.findViewById(R.id.rv_investors);
        nsvInvestors = view.findViewById(R.id.nsv_investors);

        fabFilter = view.findViewById(R.id.fab_filter);
        fabAdd = view.findViewById(R.id.fab_add);
        fabSearch = view.findViewById(R.id.fab_search);
        tilFilterByShow = view.findViewById(R.id.til_filter_by_show);

        tilFilterByShow.setVisibility(View.GONE);

        fabFilter.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSearch.setOnClickListener(this);

        View filterOptionsView = view.findViewById(R.id.filter_bottom_sheet);
        filterBottomSheetBehavior = BottomSheetBehavior.from(filterOptionsView);
        filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Config.swipeRefreshLayoutColorScheme(requireActivity(), srlInvestors);

        srlInvestors.setOnRefreshListener(this::populateInvestors);

        populateInvestors();

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.fab_filter) {

            filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

        if (viewId == R.id.fab_add) {

            InvestorsFragmentDirections.ActionInvestorsToAddEditInvestorsFragment actionInvestorsToAddEditInvestorsFragment =
                    InvestorsFragmentDirections.actionInvestorsToAddEditInvestorsFragment();

            actionInvestorsToAddEditInvestorsFragment.setAddEditInvestorsLaunchType("addInvestor");

            navController.navigate(actionInvestorsToAddEditInvestorsFragment);

        }

        if (viewId == R.id.fab_search) {

            ((ToolbarInterface) requireActivity()).showSearchView();

        }
    }

    private void initializeShowProgressDialog() {

        if (progressDialog == null) {

            progressDialog = new ProgressDialog(requireActivity());

            progressDialog.show();

            progressDialog.setContentView(R.layout.custom_progress_dialog_layout);

            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            progressDialog.setCancelable(false);

        } else if (!progressDialog.isShowing()) {

            progressDialog.show();

        }

    }

    private void dismissProgressDialog() {

        progressDialog.dismiss();

    }

    private void setRefreshingFalse() {

        srlInvestors.setRefreshing(false);

    }


    private void populateInvestors() {

        initializeShowProgressDialog();

        List<Investor> investorsList = new ArrayList<>();

        new FirebaseDao().returnInvestorsDatabaseReference().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                setRefreshingFalse();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Investor investor = dataSnapshot.getValue(Investor.class);

                    if (investor != null) {

                        investor.setId(snapshot.getKey());

                    }

                    investorsList.add(investor);

                }

                dismissProgressDialog();

                setUpInvestorsAdapter(investorsList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                dismissProgressDialog();

            }
        });
    }

    private void setUpInvestorsAdapter(List<Investor> investorsList) {

        InvestorsAdapter investorsAdapter = new InvestorsAdapter(requireActivity(), investorsList,
                (investor, pos, clickType) -> {

                    if (clickType.equalsIgnoreCase("editClick")) {

                        InvestorsFragmentDirections.ActionInvestorsToAddEditInvestorsFragment
                                actionInvestorsToAddEditInvestorsFragment =
                                InvestorsFragmentDirections.actionInvestorsToAddEditInvestorsFragment();

                        actionInvestorsToAddEditInvestorsFragment.setAddEditInvestorsLaunchType("editInvestor");
                        actionInvestorsToAddEditInvestorsFragment.setName(investor.getName());
                        actionInvestorsToAddEditInvestorsFragment.setUsername(investor.getUsername());
                        actionInvestorsToAddEditInvestorsFragment.setCountry(investor.getCountry());
                        actionInvestorsToAddEditInvestorsFragment.setPhoneNo(investor.getPhoneNo());
                        actionInvestorsToAddEditInvestorsFragment.setSecurityCode(investor.getSecurityCode());
                        actionInvestorsToAddEditInvestorsFragment.setDateJoined(investor.getDateJoined());
                        actionInvestorsToAddEditInvestorsFragment.setSharedAgreed(investor.getSharedAgreed());
                        actionInvestorsToAddEditInvestorsFragment.setAvatarUrl(investor.getAvatarUrl());

                        navController.navigate(actionInvestorsToAddEditInvestorsFragment);

                    } else if (clickType.equalsIgnoreCase("normalClick")) {

                        InvestorsFragmentDirections.ActionInvestorsToInvestorMainFragmentFromAdmin
                                actionInvestorsToInvestorMainFragmentFromAdmin =
                                InvestorsFragmentDirections.actionInvestorsToInvestorMainFragmentFromAdmin();

                        actionInvestorsToInvestorMainFragmentFromAdmin.setNavInvestorMainFragmentFromAdminUserId("1");
                        actionInvestorsToInvestorMainFragmentFromAdmin.setMainFragmentLaunchType("fromAdmin");
                        actionInvestorsToInvestorMainFragmentFromAdmin.setAvatarUrl(investor.getAvatarUrl());

                        // on back clicked set Admin in session manager
                        sessionManager.editor.clear();
                        sessionManager.editor.commit();
                        sessionManager.createSession("AdminClickedInvestor");

                        navController.navigate(actionInvestorsToInvestorMainFragmentFromAdmin);

                    }

                });

        tvDisplayInvestorsNo.setText(String.format("%s %s%s%s", "Investors", "(", investorsList.size(), ")"));

        rvInvestors.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvInvestors.setAdapter(investorsAdapter);


        ((ToolbarInterface) requireActivity()).returnEtSearch().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isAdded()) {

                    ((ToolbarInterface) requireActivity()).returnTilSearch().setEndIconVisible(false);

                    investorsAdapter.getInvestorFilter().filter(charSequence);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isAdded()) {

                    if (editable.length() == 0) {

                        ((ToolbarInterface) requireActivity()).returnTilSearch().setEndIconVisible(true);

                    }
                }

            }
        });

    }

}