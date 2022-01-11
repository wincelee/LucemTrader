package manu.apps.lucemtrader.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.CustomTextWatcher;

public class PhoneNoFragment extends Fragment implements View.OnClickListener {

    TextInputLayout tilNo;
    TextInputEditText etNo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_no_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilNo = view.findViewById(R.id.til_no);
        etNo = view.findViewById(R.id.et_no);

        CountryCodePicker ccp = view.findViewById(R.id.ccp);

        tilNo.setPrefixText(ccp.getSelectedCountryCodeWithPlus());

        ccp.setOnCountryChangeListener(() -> tilNo.setPrefixText(ccp.getSelectedCountryCodeWithPlus()));

        etNo.addTextChangedListener(new CustomTextWatcher(tilNo));

        etNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().startsWith("0")) {

                    editable.clear();

                }

            }
        });

        MaterialButton btnContinue = view.findViewById(R.id.btn_continue);
        
        btnContinue.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_continue){

            if (Objects.requireNonNull(etNo.getText()).toString().trim().isEmpty()) {

                tilNo.setError("Phone number has not been entered");
                etNo.requestFocus();

            } else {

                String phoneNumber = Objects.requireNonNull(tilNo.getPrefixText()).toString().trim() +
                        etNo.getText().toString().trim();

                PhoneNoFragmentDirections.ActionPhoneNoToVerificationFragment actionPhoneNoToVerificationFragment =
                        PhoneNoFragmentDirections.actionPhoneNoToVerificationFragment();

                actionPhoneNoToVerificationFragment.setPhoneNo(phoneNumber);

                Navigation.findNavController(view).navigate(actionPhoneNoToVerificationFragment);

            }



        }
    }

}