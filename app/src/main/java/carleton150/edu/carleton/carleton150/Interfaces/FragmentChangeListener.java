package carleton150.edu.carleton.carleton150.Interfaces;

import carleton150.edu.carleton.carleton150.ExtraFragments.AddMemoryFragment;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;

/**
 * Created by haleyhinze on 1/20/16. Interface for communicating
 * between the QuestFragment and the MainActivity when the start quest
 * button is clicked in the QuestFragment so that the MainActivity can
 * swap the QuestFragment with a QuestInProgressFragment
 */
public interface FragmentChangeListener {

    public void replaceFragment(MainFragment fragment);
}
