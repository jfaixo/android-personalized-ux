package com.singular_dreams.pux.debug;

import com.singular_dreams.pux.PuxBaseActivity;

public class PuxDebugActivity extends PuxBaseActivity {

    public PuxDebugActivity() {
        super();

        getJourneyManager().addSteps(new Class[] { PuxDebugFragment.class});
    }
}
