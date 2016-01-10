package carleton150.edu.carleton.carleton150.MainFragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by haleyhinze on 10/8/15.
 *
 * Adapter to manage the tabs for the main tab view and set the appropriate Fragment for each
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    MainFragment curFragment = null;

    public MyFragmentPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public MainFragment getItem(int position) {

        switch (position) {
            case 0:
                HistoryFragment tab1 = new HistoryFragment();
                return tab1;
            case 1:
                EventsFragment tab2 = new EventsFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {

        if (curFragment != object) {
            curFragment = (MainFragment) object;
        }

        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public MainFragment getCurFragment(){
        return this.curFragment;
    }
}
