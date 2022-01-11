package manu.apps.lucemtrader.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.AdminPreferenceManager;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.InvestorPreferenceManager;
import manu.apps.lucemtrader.classes.LoginLogoutManager;
import manu.apps.lucemtrader.classes.SessionManager;

public class SplashFragment extends Fragment implements View.OnClickListener {

    NavController navController;

    BottomSheetBehavior<View> userTypeBottomSheetBehavior;

    MaterialCardView mcvInvestor, mcvAdmin;

    SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireActivity());

        navController = Navigation.findNavController(view);

        ImageView imvSplash = view.findViewById(R.id.imv_splash);
        mcvInvestor = view.findViewById(R.id.mcv_investor);
        mcvAdmin = view.findViewById(R.id.mcv_admin);

        View userTypView = view.findViewById(R.id.select_user_type_bottom_sheet);
        userTypeBottomSheetBehavior = BottomSheetBehavior.from(userTypView);
        userTypeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        imvSplash.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in_fragment));

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (new LoginLogoutManager(requireActivity()).checkPreference()){

                if (TextUtils.isEmpty(Config.returnUserType(requireActivity()))){

                    userTypeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else if (Config.returnUserType(requireActivity()).equalsIgnoreCase("Investor")){

                    navController.navigate(R.id.action_splash_to_investor_main_fragment);

                }else if (Config.returnUserType(requireActivity()).equalsIgnoreCase("Admin")){

                    navController.navigate(R.id.action_splash_to_admin_main_fragment);

                }else if (Config.returnUserType(requireActivity()).equalsIgnoreCase("AdminClickedInvestor")){

                    sessionManager.editor.clear();
                    sessionManager.editor.commit();
                    sessionManager.createSession("Admin");

                    navController.navigate(R.id.action_splash_to_admin_main_fragment);

                }

            }else{

                userTypeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }

        }, 5000);

        mcvInvestor.setOnClickListener(this);
        mcvAdmin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.mcv_investor){

            sessionManager.editor.clear();
            sessionManager.editor.commit();
            sessionManager.createSession("Investor");

            Log.wtf("InvestorPreferenceManager",
                    String.valueOf(new AdminPreferenceManager(requireActivity()).checkPreference()));

            if (new InvestorPreferenceManager(requireActivity()).checkPreference()){

                navController.navigate(R.id.action_splash_to_investor_main_fragment);

            }else{

                navController.navigate(R.id.action_splash_to_slides_fragment);

            }

        }

        if (viewId == R.id.mcv_admin){

            sessionManager.editor.clear();
            sessionManager.editor.commit();
            sessionManager.createSession("Admin");

            Log.wtf("AdminPreferenceManager",
                    String.valueOf(new AdminPreferenceManager(requireActivity()).checkPreference()));

            if (new AdminPreferenceManager(requireActivity()).checkPreference()){

                navController.navigate(R.id.action_splash_to_admin_main_fragment);

            }else{

                navController.navigate(R.id.action_splash_to_phone_no_fragment);

            }

        }
    }
}