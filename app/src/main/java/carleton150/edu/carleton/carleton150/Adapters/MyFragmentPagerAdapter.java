package carleton150.edu.carleton.carleton150.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import carleton150.edu.carleton.carleton150.ExtraFragments.AddMemoryFragment;
import carleton150.edu.carleton.carleton150.Interfaces.ViewFragmentChangedListener;
import carleton150.edu.carleton.carleton150.MainFragments.EventsFragment;
import carleton150.edu.carleton.carleton150.MainFragments.HistoryFragment;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MainFragments.QuestFragment;
import carleton150.edu.carleton.carleton150.MainFragments.QuestInProgressFragment;

/**
 *
 * Adapter to manage the tabs for the main tab view and set the appropriate Fragment for each
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    MainFragment curFragment = null;
    FragmentManager fragmentManager;
    MainFragment tab3 = null;
    ViewFragmentChangedListener viewFragmentChangedListener;

    public MyFragmentPagerAdapter(FragmentManager fm, int NumOfTabs, ViewFragmentChangedListener viewFragmentChangedListener) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        fragmentManager = fm;
        this.viewFragmentChangedListener = viewFragmentChangedListener;
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
            case 2:
                //Different because tab3 may be QuestInProgressFragment()
                if(tab3 == null) {
                    tab3 = new QuestFragment();
                }
                return tab3;
            default:
                return null;
        }
    }

    /**
     * If the object is not already in view, calls MainFragment.fragmentInView()
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if(curFragment != null) {
            if (curFragment != object) {
                curFragment = (MainFragment) object;
                curFragment.fragmentInView();
                viewFragmentChangedListener.viewFragmentChanged(curFragment);
            }
        }
        if (curFragment != object) {
            curFragment = (MainFragment) object;
        }


    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    /**
     *
     * @return fragment currently in view
     */
    public MainFragment getCurFragment(){
        return this.curFragment;
    }

    public void setCurFragment(MainFragment curFragment){
        this.curFragment = curFragment;
    }

    /**
     * Replaces the QuestFragment with a QuestInProgressFragment
     * @param fragment fragment that will be replacing the current fragment
     */
    public void replaceFragment(MainFragment fragment) {
        if(tab3 != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(tab3).detach(tab3).commit();
            tab3 = fragment;
            curFragment = tab3;
            notifyDataSetChanged();
            curFragment.fragmentInView();
        }
    }

    /**
     * replaces the QuestInProgressFragment with a QuestFragment
     */
    public void replaceFragment(){
        if(tab3 != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(tab3).detach(tab3).commit();
            tab3 = new QuestFragment();
            curFragment = tab3;
            tab3.fragmentInView();
            notifyDataSetChanged();
        }
    }

    /**
     * This is overridden because tab3 could contain either a QuestInProgressFragment
     * or a QuestFragment. Therefore, the position of whichever one is not currently
     * being used should be none.
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        if(object instanceof QuestFragment && tab3 instanceof QuestInProgressFragment){
            return POSITION_NONE;
        } if(object instanceof QuestInProgressFragment && tab3 instanceof QuestFragment){
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="History";
                break;
            case 1:
                title="Events";
                break;
            case 2:
                title="Quests";
                break;
        }

        return title;
    }
}
