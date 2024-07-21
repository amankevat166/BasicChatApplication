package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Fragments.CallsFragment;
import Fragments.ChatFragment;
import Fragments.StautsFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new ChatFragment();
        }
        else if (position==1){
            return new StautsFragment();
        }
        else {
            return new CallsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0){
            return "CHATS";
        }
        else if(position==1) {
            return "STATUS";
        }
        else {
            return "CALLS";
        }
    }
}
