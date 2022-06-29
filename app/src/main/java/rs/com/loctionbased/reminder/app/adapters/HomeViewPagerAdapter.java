package rs.com.loctionbased.reminder.app.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import rs.com.loctionbased.reminder.app.fragments.HomeListFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    //DATA
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;                                                   //Holds all of the fragments
    private SparseArray<HomeListFragment> mRegisteredFragmentList = new SparseArray<>();    //Holds only registered fragments


    public HomeViewPagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        mTitleList = titleList;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    public HomeListFragment getRegisteredFragment(int position) {
        return mRegisteredFragmentList.get(position);
    }

    public SparseArray<HomeListFragment> getRegisteredFragments() {
        return mRegisteredFragmentList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragmentList.put(position, (HomeListFragment) fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragmentList.remove(position);
        super.destroyItem(container, position, object);
    }
}
