package com.singular_dreams.pux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuxJourneyManager {

    private List<Class<? extends PuxBaseFragment>> fragmentsClassList = new ArrayList<>();

    private Map<Integer, PuxBaseFragment> instantiatedFragmentsMap = new HashMap<>();

    public void addStep(Class<? extends PuxBaseFragment> fragmentClass) {
        fragmentsClassList.add(fragmentClass);
    }

    public void addSteps(Collection<Class<? extends PuxBaseFragment>> fragmentClassList) {
        fragmentsClassList.addAll(fragmentClassList);
    }

    public void addSteps(Class<? extends PuxBaseFragment>[] fragmentClassArray) {
        for (Class<? extends PuxBaseFragment> fragmentClass: fragmentClassArray) {
            fragmentsClassList.add(fragmentClass);
        }
    }

    public void removeStep(int fragmentIndex) {
        fragmentsClassList.remove(fragmentIndex);

        if (instantiatedFragmentsMap.containsKey(fragmentIndex)) {
            instantiatedFragmentsMap.remove(fragmentIndex);
        }
    }

    public int getStepsCount() {
        return fragmentsClassList.size();
    }

    public PuxBaseFragment getStepInstance(int index) {

        // If already instantiated, return the same instance
        if (instantiatedFragmentsMap.containsKey(index)) {
            return instantiatedFragmentsMap.get(index);
        }
        // Create a new fragment instance, register it, and return it
        else {
            try {
                PuxBaseFragment stepInstance = fragmentsClassList.get(index).newInstance();
                instantiatedFragmentsMap.put(index, stepInstance);
                return stepInstance;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
