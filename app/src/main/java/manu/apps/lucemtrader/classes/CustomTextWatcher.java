package manu.apps.lucemtrader.classes;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class CustomTextWatcher implements TextWatcher {

    private final TextInputLayout textInputLayout;

    public CustomTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textInputLayout.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
