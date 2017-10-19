package com.singular_dreams.pux.firstuse;

import com.singular_dreams.pux.PuxBaseActivity;

public class PuxFirstUseActivity extends PuxBaseActivity {

    public PuxFirstUseActivity() {
        super();

        getJourneyManager().addSteps(new Class[] { FirstUseWelcomeStepBaseFragment.class});
    }
}
