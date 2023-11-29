package edu.northeastern.numad22fa_group10.search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.northeastern.numad22fa_group10.search.addingFragments.IngredientFragment;
import edu.northeastern.numad22fa_group10.search.addingFragments.recipeFragment;

public class AddingPagerAdapter extends FragmentStateAdapter {
    public AddingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    //adding ingredients or recipes in different tab
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new recipeFragment();
            default:
                return new IngredientFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
