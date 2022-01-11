package manu.apps.lucemtrader.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.adapters.SlidesPagerAdapter;
import manu.apps.lucemtrader.transformers.GatePageTransformer;

public class SlidesFragment extends Fragment implements View.OnClickListener {

    private ViewPager vpSlides;
    private final int [] layouts = {R.layout.slide1,R.layout.slide2, R.layout.slide3};

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slides_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        MaterialButton btnNext = view.findViewById(R.id.btn_next);
        vpSlides = view.findViewById(R.id.vp_slides);

        btnNext.setOnClickListener(this);

        SlidesPagerAdapter slidesPagerAdapter = new SlidesPagerAdapter(layouts, requireActivity());

        vpSlides.setPageTransformer(true, new GatePageTransformer());

        vpSlides.setAdapter(slidesPagerAdapter);

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.btn_next){

            loadNextSlide();

        }
    }

    private void loadNextSlide(){

        int nextSlide = vpSlides.getCurrentItem()+1;

        if (nextSlide<layouts.length){

            vpSlides.setCurrentItem(nextSlide);

        }else{

            navController.navigate(R.id.action_slides_to_phone_no_fragment);

        }

    }

}