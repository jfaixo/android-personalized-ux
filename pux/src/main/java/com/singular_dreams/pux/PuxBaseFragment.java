package com.singular_dreams.pux;

import android.support.v4.app.Fragment;

import com.singular_dreams.pux.model.PuxContextDataPoint;

public abstract class PuxBaseFragment extends Fragment {

    public PuxBaseFragment() {
        // Required empty public constructor
    }

    public abstract void onNewContextDataPoint(PuxContextDataPoint point);
}
