package manu.apps.lucemtrader.interfaces;

import android.graphics.drawable.Drawable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public interface ToolbarInterface {

    void setToolbarTitle(String toolbarTitle);

    void setOverflowIcon(Drawable drawable);

    void showSearchView();

    TextInputEditText returnEtSearch();

    TextInputLayout returnTilSearch();

}
