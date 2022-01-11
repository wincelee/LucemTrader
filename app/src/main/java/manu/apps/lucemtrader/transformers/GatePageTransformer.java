package manu.apps.lucemtrader.transformers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class GatePageTransformer implements ViewPager.PageTransformer{

    @Override
    public void transformPage(@NonNull View page, float position) {

        page.setTranslationX(-position*page.getWidth());

        if (position<-1){

            page.setAlpha(0);

        }else if (position<=0){

            page.setAlpha(1);
            page.setPivotX(0);
            page.setRotationY(90*Math.abs(position));

        }else if (position<=1){

            page.setAlpha(1);
            page.setPivotX(page.getWidth());
            page.setRotationY(-90*Math.abs(position));

        }else{

            page.setAlpha(0);
        }
    }
}
