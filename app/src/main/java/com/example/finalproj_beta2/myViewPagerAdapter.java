package com.example.finalproj_beta2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class myViewPagerAdapter extends FragmentStateAdapter {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * an adapter to help expand the fragments in "my_school" activity.
     */

    public myViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new student_fragment();
        }
        else{
            return new teacher_fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
