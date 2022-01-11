package manu.apps.lucemtrader;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import manu.apps.lucemtrader.classes.AdminPreferenceManager;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.InvestorPreferenceManager;
import manu.apps.lucemtrader.classes.LoginLogoutManager;
import manu.apps.lucemtrader.classes.SessionManager;
import manu.apps.lucemtrader.interfaces.ToolbarInterface;

public class MainActivity extends AppCompatActivity implements ToolbarInterface {

    AppBarConfiguration appBarConfiguration;

    BottomNavigationView bnvMain;

    NavController navController;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    MaterialToolbar tbMain;

    private static final int BACK_PRESS_TIME_INTERVAL = 2000;
    private long backPressTime;

    SessionManager sessionManager;

    SharedPreferences.OnSharedPreferenceChangeListener userTypeSessionManagerListener;

    ActionBarDrawerToggle navDrawerToggle;

    AppBarLayout appBarLayout;

    private TextView tvToolbarTitle;

    private RelativeLayout rlSearchView;

    private TextInputEditText etSearch;
    private TextInputLayout tilSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sessionManager = new SessionManager(this);

        // Colored drawables blue color 102239
        // Colored drawables green color 05A931

        if (BuildConfig.DEBUG) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            if (Build.MODEL.equalsIgnoreCase("ANE-LX1")){

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }



        tbMain = findViewById(R.id.tb_main);
        etSearch = findViewById(R.id.et_search);
        tilSearch = findViewById(R.id.til_search);
        appBarLayout = findViewById(R.id.app_bar_layout);
        rlSearchView = findViewById(R.id.rl_search_view);
        MaterialButton btnSearchBack = findViewById(R.id.btn_search_back);

        btnSearchBack.setOnClickListener(view ->

                revertToolbar()

        );

        setSupportActionBar(tbMain);

        tbMain.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_hamburger));

        bnvMain = findViewById(R.id.bnv_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);

        tvToolbarTitle = tbMain.findViewById(R.id.tv_toolbar_title);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(navDrawerToggle);
        navDrawerToggle.syncState();

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_splash_fragment,
                R.id.nav_phone_no_fragment, R.id.nav_verification_fragment, R.id.nav_investor_main_fragment,
                R.id.nav_admin_main_fragment, R.id.nav_history_fragment, R.id.nav_profit_loss_fragment,
                R.id.nav_investors_fragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(tbMain, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(bnvMain, navController);
        NavigationUI.setupWithNavController(navigationView, navController);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        populateBottomNavigationView();

        userTypeSessionManagerListener = (sharedPreferences, key) -> {

            if (key.equalsIgnoreCase("USERTYPE")) {

                populateBottomNavigationView();

            }

        };

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.nav_splash_fragment |
                    destination.getId() == R.id.nav_slides_fragment) {

                appBarLayout.setVisibility(View.GONE);

                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                );

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                /* * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                }*/

                disableOnBackPressed();

            } else if (destination.getId() == R.id.nav_phone_no_fragment |
                    destination.getId() == R.id.nav_verification_fragment) {

                appBarLayout.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

               /* * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.whiteColor));

                }*/

                disableOnBackPressed();

            } else if (destination.getId() == R.id.nav_edit_add_investors_fragment) {

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                tbMain.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_back));

                appBarLayout.setVisibility(View.VISIBLE);

                tvToolbarTitle.setVisibility(View.VISIBLE);

                bnvMain.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                disableOnBackPressed();

                hideSearchView();

            } else if (destination.getId() == R.id.nav_investor_main_fragment_from_admin) {

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                tbMain.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_back));

                tbMain.setNavigationOnClickListener(v -> {

                    int previousDestinationId = 0;

                    try {

                        previousDestinationId = Objects.requireNonNull(navController.
                                getPreviousBackStackEntry()).getDestination().getId();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                    if (!(previousDestinationId == R.id.nav_history_fragment_from_admin |
                            previousDestinationId == R.id.nav_profit_loss_fragment_from_admin)) {

                        sessionManager.editor.clear();
                        sessionManager.editor.commit();
                        sessionManager.createSession("Admin");

                    }

                    navController.navigateUp();


                });

                appBarLayout.setVisibility(View.VISIBLE);

                tvToolbarTitle.setVisibility(View.GONE);

                bnvMain.setVisibility(View.VISIBLE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                mainFragmentFromAdminBackPressed();

                hideSearchView();

            } else if (destination.getId() == R.id.nav_history_fragment_from_admin |
                    destination.getId() == R.id.nav_profit_loss_fragment_from_admin) {

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                tbMain.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_back));

                tbMain.setNavigationOnClickListener(v -> navController.navigateUp());

                appBarLayout.setVisibility(View.VISIBLE);

                tvToolbarTitle.setVisibility(View.GONE);

                bnvMain.setVisibility(View.VISIBLE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                historyProfitLossFromAdminBackPressed();

                hideSearchView();

            } else {

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                tbMain.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_app));

                tbMain.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_hamburger));

                appBarLayout.setVisibility(View.VISIBLE);

                tvToolbarTitle.setVisibility(View.GONE);

                bnvMain.setVisibility(View.VISIBLE);

                hideSearchView();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                disableOnBackPressed();

            }

        });

    }


    private void populateBottomNavigationView() {

        String userType = Config.returnUserType(this);

        if (userType != null) {

            if (userType.equalsIgnoreCase("Investor")) {

                bnvMain.getMenu().clear();

                bnvMain.inflateMenu(R.menu.investor_bottom_nav_menu);

            } else if (userType.equalsIgnoreCase("Admin")) {

                bnvMain.getMenu().clear();

                bnvMain.inflateMenu(R.menu.admin_bottom_nav_menu);

            } else if (userType.equalsIgnoreCase("AdminClickedInvestor")) {

                bnvMain.getMenu().clear();

                bnvMain.inflateMenu(R.menu.admin_clicked_investor_bottom_nav_menu);

            }
        }

    }

    private void disableOnBackPressed() {

        MainActivity.this.getOnBackPressedDispatcher().addCallback(MainActivity.this,
                new OnBackPressedCallback(false) {
                    @Override
                    public void handleOnBackPressed() {

                    }
                });
    }

    private void historyProfitLossFromAdminBackPressed() {

        MainActivity.this.getOnBackPressedDispatcher().addCallback(MainActivity.this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        navController.navigateUp();

                    }
                });

    }

    private void mainFragmentFromAdminBackPressed() {

        MainActivity.this.getOnBackPressedDispatcher().addCallback(MainActivity.this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        int previousDestinationId = 0;

                        try {

                            previousDestinationId = Objects.requireNonNull(navController.
                                    getPreviousBackStackEntry()).getDestination().getId();

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                        if (!(previousDestinationId == R.id.nav_history_fragment_from_admin |
                                previousDestinationId == R.id.nav_profit_loss_fragment_from_admin)) {

                            sessionManager.editor.clear();
                            sessionManager.editor.commit();
                            sessionManager.createSession("Admin");

                        }

                        navController.navigateUp();

                    }
                });
    }

    @Override
    public void onBackPressed() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);


        if (navHostFragment != null && navHostFragment.getChildFragmentManager().getBackStackEntryCount() == 0) {

            if (backPressTime + BACK_PRESS_TIME_INTERVAL > System.currentTimeMillis()) {

                super.onBackPressed();

                return;

            } else {

                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            }

            backPressTime = System.currentTimeMillis();

        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        MenuCompat.setGroupDividerEnabled(menu, true);

        String userType = Config.returnUserType(this);

        if (userType != null) {

            if (userType.equalsIgnoreCase("Investor")) {

                menuInflater.inflate(R.menu.investor_overflow_menu, menu);

            } else if (userType.equalsIgnoreCase("Admin")) {

                menuInflater.inflate(R.menu.admin_overflow_menu, menu);

            } else if (userType.equalsIgnoreCase("AdminClickedInvestor")) {

                menuInflater.inflate(R.menu.admin_overflow_menu, menu);

            }
        }

        /* *userTypeSessionManagerListener = (sharedPreferences, key) -> {

            if (key.equalsIgnoreCase("USERTYPE")) {

                String listenerUserType = Config.returnUserType(this);

                if (listenerUserType != null) {

                    if (listenerUserType.equalsIgnoreCase("Investor")) {

                        menuInflater.inflate(R.menu.investor_overflow_menu, menu);

                    } else if (listenerUserType.equalsIgnoreCase("Admin")) {

                        menuInflater.inflate(R.menu.admin_overflow_menu, menu);

                    }else if (listenerUserType.equalsIgnoreCase("AdminClickedInvestor")) {

                        menuInflater.inflate(R.menu.admin_clicked_investor_overflow_menu, menu);
                    }
                }

            }

        };*/

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int menuItemId = item.getItemId();

        if (menuItemId == R.id.action_investor_log_out) {

            sessionManager.editor.clear();
            sessionManager.editor.commit();

            new LoginLogoutManager(this).clearPreference();
            new InvestorPreferenceManager(this).clearPreference();

            navController.popBackStack();
            navController.navigate(R.id.nav_splash_fragment);

        } else if (menuItemId == R.id.action_admin_log_out) {

            sessionManager.editor.clear();
            sessionManager.editor.commit();

            new LoginLogoutManager(this).clearPreference();
            new AdminPreferenceManager(this).clearPreference();

            Log.wtf("AdminLogOutClicked", String.valueOf(true));

            navController.popBackStack();
            navController.navigate(R.id.nav_splash_fragment);

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {

        sessionManager.returnSessionManagerSharedPreferences().
                registerOnSharedPreferenceChangeListener(userTypeSessionManagerListener);

        super.onResume();

    }

    @Override
    protected void onPause() {

        sessionManager.returnSessionManagerSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(userTypeSessionManagerListener);

        super.onPause();
    }

    @Override
    public void setToolbarTitle(String toolbarTitle) {

        tvToolbarTitle.setText(toolbarTitle);

    }

    @Override
    public void setOverflowIcon(Drawable drawable) {

        tbMain.setOverflowIcon(drawable);

    }

    @Override
    public void showSearchView() {

        tbMain.setNavigationIcon(null);

        tbMain.setOverflowIcon(null);

        bnvMain.setVisibility(View.GONE);

        rlSearchView.setVisibility(View.VISIBLE);

    }

    @Override
    public TextInputEditText returnEtSearch(){

        return etSearch;

    }

    @Override
    public TextInputLayout returnTilSearch() {
        return tilSearch;
    }

    private void hideSearchView() {

        rlSearchView.setVisibility(View.GONE);

    }

    private void revertToolbar(){

        rlSearchView.setVisibility(View.GONE);

        tbMain.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_app));

        tbMain.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_hamburger));

        bnvMain.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(Objects.requireNonNull(etSearch.getText()).toString())){

            etSearch.getText().clear();

        }

    }
}