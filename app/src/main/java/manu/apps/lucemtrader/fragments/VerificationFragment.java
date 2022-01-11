package manu.apps.lucemtrader.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.AdminPreferenceManager;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.InvestorPreferenceManager;
import manu.apps.lucemtrader.classes.LoginLogoutManager;
import manu.apps.lucemtrader.classes.SessionManager;

public class VerificationFragment extends Fragment implements View.OnClickListener {

    private TextView tvResendCode;

    private MaterialButton btnResendCode;

    TextInputLayout tilSecurityVerificationCode;

    TextInputEditText etSecurityVerificationCode;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks;

    private String verificationId;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private Dialog errorExceptionDialog;

    NavController navController;

    /* 1. Enable Verification in Google Cloud

     2. Firebase Console Link [https://console.cloud.google.com]

     3. Select Project Under All

     4. Search for Android Device Verification

     5. Enable the API

     6. Navigate to firebase console then Project Overview then project settings
     then add fingerprint then generate and add SHA-256 certificate fingerprint

     */


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verification_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        navController = Navigation.findNavController(view);

        MaterialButton btnVerifyContinue = view.findViewById(R.id.btn_verify_continue);
        TextView tvDisplayVerify = view.findViewById(R.id.tv_display_verify);
        TextView tvVerifyDescription = view.findViewById(R.id.tv_verify_description);
        tvResendCode = view.findViewById(R.id.tv_resend_code);
        btnResendCode = view.findViewById(R.id.btn_resend_code);

        tilSecurityVerificationCode = view.findViewById(R.id.til_security_verification_code);
        etSecurityVerificationCode = view.findViewById(R.id.et_security_verification_code);

        String userType = Config.returnUserType(requireActivity());

        if (userType != null) {

            if (userType.equalsIgnoreCase("Investor")) {

                tvDisplayVerify.setText(requireActivity().getString(R.string.security_code));
                tvVerifyDescription.setText(requireActivity().getString(R.string.enter_your_private_code));

            } else if (userType.equalsIgnoreCase("Admin")) {

                tvDisplayVerify.setText(requireActivity().getString(R.string.verification_code));
                tvVerifyDescription.setText(requireActivity().getString(R.string.please_enter_code_that_was_n_sent_to_your_phone_number));

            }
        }

        btnVerifyContinue.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);

        String validationPhoneNo = VerificationFragmentArgs.fromBundle(getArguments()).getPhoneNo();

        onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                dismissProgressDialog();

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                dismissProgressDialog();

                e.printStackTrace();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    showErrorExceptionDialog("invalidCredentials", e.getMessage(),
                            "We encountered an error while validating your credentials, please retry",
                            R.drawable.ic_error);

                    Log.wtf("VerificationFailException", "InvalidCredentialsException");

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    showErrorExceptionDialog("tooManyRequests", e.getMessage(),
                            "We have received too many verification requests, please try again later",
                            R.drawable.ic_info);

                } else {

                    showErrorExceptionDialog("sendingSmsCodeFailed", e.getMessage(),
                            "We encountered an error while sending code to " + validationPhoneNo + ", please retry",
                            R.drawable.ic_error);
                }

                btnVerifyContinue.setEnabled(false);


            }

            @Override
            public void onCodeSent(@NonNull String verificationIdString, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingTokenOnCodeSent) {
                super.onCodeSent(verificationIdString, forceResendingTokenOnCodeSent);

                dismissProgressDialog();

                startResendCodeTimer();

                verificationId = verificationIdString;

                forceResendingToken = forceResendingTokenOnCodeSent;

                Config.showSnackBar(requireActivity(), "Verification code sent to " + validationPhoneNo,
                        0, Snackbar.LENGTH_LONG);

            }
        };


        if (TextUtils.isEmpty(validationPhoneNo)) {

            navController.navigateUp();

        } else {

            // add this below onVerificationStateChangedCallbacks
             startPhoneNoVerification(validationPhoneNo, false);

            // Code for testing

            /* * new InvestorPreferenceManager(requireActivity()).writePreference();
            new AdminPreferenceManager(requireActivity()).writePreference();

            new LoginLogoutManager(requireActivity()).writePreference();

            navController.navigate(R.id.action_verification_to_main_fragment);*/

        }


    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.btn_verify_continue) {

            if (Objects.requireNonNull(etSecurityVerificationCode.getText()).toString().trim().isEmpty()) {

                tilSecurityVerificationCode.setError("Code has not been entered");
                etSecurityVerificationCode.requestFocus();

            } else {

                verifyPhoneNumber(verificationId, VerificationFragmentArgs.fromBundle(getArguments()).getPhoneNo());

            }

            navController.navigate(R.id.action_verification_to_main_fragment);

        }

        if (viewId == R.id.btn_resend_code) {

            btnResendCode.setVisibility(View.GONE);

            startPhoneNoVerification(VerificationFragmentArgs.fromBundle(getArguments()).getPhoneNo(),
                    true);

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

    private void startResendCodeTimer() {

        btnResendCode.setVisibility(View.GONE);

        tvResendCode.setVisibility(View.VISIBLE);

        new CountDownTimer(25000, 1000) {

            public void onTick(long millisUntilFinished) {

                tvResendCode.setText(String.format("%s %s %s", "Resend Code in",
                        millisUntilFinished / 1000, "seconds"));

            }

            public void onFinish() {

                tvResendCode.setVisibility(View.GONE);

                btnResendCode.setVisibility(View.VISIBLE);

            }
        }.start();

    }

    private void startPhoneNoVerification(String phoneNo, boolean forceResendTokenBoolean) {

        initializeShowProgressDialog();

        PhoneAuthOptions phoneAuthOptions;

        if (forceResendTokenBoolean) {

            phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNo)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(onVerificationStateChangedCallbacks)
                    .setForceResendingToken(forceResendingToken)
                    .build();

        } else {

            phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNo)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(onVerificationStateChangedCallbacks)
                    .build();

        }

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);


    }

    private void verifyPhoneNumber(String verificationId, String phoneNo) {

        initializeShowProgressDialog();

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.
                getCredential(verificationId, phoneNo);

        signInWithPhoneAuthCredential(phoneAuthCredential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> {

                    dismissProgressDialog();

                    // parseToNavigation
                    // saveToSession
                    // Getting firebase user below
//                     FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    new LoginLogoutManager(requireActivity()).writePreference();

                    String userType = Config.returnUserType(requireActivity());

                    if (userType != null) {

                        if (userType.equalsIgnoreCase("Investor")) {

                            new InvestorPreferenceManager(requireActivity()).writePreference();

                        } else if (userType.equalsIgnoreCase("Admin")) {

                            new AdminPreferenceManager(requireActivity()).writePreference();
                        }

                    }

                    String phone = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

                    showErrorExceptionDialog("signInSuccess", "Sign In Verification was a success"
                                    + "\n" + phone,
                            "We have successfully verified you, please proceed",
                            R.drawable.ic_info);

                })
                .addOnFailureListener(e -> {

                    dismissProgressDialog();

                    e.printStackTrace();

                    showErrorExceptionDialog("signInFailure", e.getMessage(),
                            "We encountered an error while signing you in, please retry",
                            R.drawable.ic_error);


                });
    }

    private void showErrorExceptionDialog(String statType, String stat, String tvMessage, int drawableId) {

        if (errorExceptionDialog == null) {

            errorExceptionDialog = new Dialog(requireActivity());
            errorExceptionDialog.setContentView(R.layout.error_exception_dialog_layout);

            errorExceptionDialog.setCancelable(false);

            errorExceptionDialog.show();

        } else if (!errorExceptionDialog.isShowing()) {

            errorExceptionDialog.show();

        }

        // Setting dialog background to transparent
        (errorExceptionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Setting size of the dialog
        errorExceptionDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imvErrorExceptionDialog = errorExceptionDialog.findViewById(R.id.imv_error_exception_dialog);
        TextView tvErrorExceptionDialogMessage = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_message);
        final TextView tvErrorExceptionDialogStatsForNerdsType = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds_type);
        final TextView tvErrorExceptionDialogStatsForNerds = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds);
        MaterialButton btnErrorExceptionCancel = errorExceptionDialog.findViewById(R.id.btn_error_exception_cancel);
        MaterialButton btnErrorExceptionRetry = errorExceptionDialog.findViewById(R.id.btn_error_exception_retry);

        imvErrorExceptionDialog.setImageDrawable(ContextCompat.getDrawable(requireActivity(), drawableId));
        tvErrorExceptionDialogMessage.setText(tvMessage);

        if (!TextUtils.isEmpty(statType)) {

            tvErrorExceptionDialogStatsForNerdsType.setText(statType);
        }

        if (!TextUtils.isEmpty(stat)) {

            tvErrorExceptionDialogStatsForNerds.setText(stat);
        }


        imvErrorExceptionDialog.setOnClickListener(v -> {

            tvErrorExceptionDialogStatsForNerdsType.setVisibility(View.VISIBLE);

            tvErrorExceptionDialogStatsForNerds.setVisibility(View.VISIBLE);

        });


        if (statType.equalsIgnoreCase("signInSuccess")) {

            btnErrorExceptionRetry.setText(requireActivity().getString(R.string.proceed));

            btnErrorExceptionRetry.setOnClickListener(v -> {

                errorExceptionDialog.dismiss();

                navController.navigate(R.id.action_verification_to_main_fragment);

            });

            btnErrorExceptionCancel.setEnabled(false);
            btnErrorExceptionCancel.setVisibility(View.GONE);


        } else if (statType.equalsIgnoreCase("signInFailure") |
                statType.equalsIgnoreCase("sendingSmsCodeFailed") |
                statType.equalsIgnoreCase("tooManyRequests") |
                statType.equalsIgnoreCase("invalidCredentials")) {

            btnErrorExceptionRetry.setOnClickListener(v -> {

                errorExceptionDialog.dismiss();

                startPhoneNoVerification(VerificationFragmentArgs.fromBundle(getArguments()).getPhoneNo(),
                        true);

            });

        }

        btnErrorExceptionCancel.setOnClickListener(v -> {

            navController.navigateUp();

            navController.popBackStack();

            errorExceptionDialog.dismiss();

        });

    }

}