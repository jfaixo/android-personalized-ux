package com.singular_dreams.pux.debug;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.singular_dreams.pux.PuxBaseFragment;
import com.singular_dreams.pux.R;
import com.singular_dreams.pux.databinding.FragmentPuxDebugBinding;
import com.singular_dreams.pux.model.PuxContextDataPoint;
import com.singular_dreams.pux.model.PuxPrediction;
import com.singular_dreams.pux.service.PuxDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PuxDebugFragment extends PuxBaseFragment implements PuxDataService.EventsListener {

    private PuxDebugViewModel viewModel = new PuxDebugViewModel(this);

    private long lastUpdateTime = 0;

    private boolean isCollectingActivated = false;
    private List<PuxContextDataPoint> collectedList;
    private Map<String, List<PuxContextDataPoint>> collectedData = new HashMap<>();

    public PuxDebugViewModel getViewModel() { return viewModel; }

    public PuxDebugFragment() {
        // Required empty public constructor
    }

    @Override
    public void onNewContextDataPoint(PuxContextDataPoint point) {
        viewModel.setUpdateDelay(point.creationTime - lastUpdateTime);
        lastUpdateTime = point.creationTime;

        viewModel.setOrientationAngles(point.orientationAngles);
        viewModel.setSpeedVector(point.speedVector);

        if (isCollectingActivated) {
            collectedList.add(point);
        }

        // Use the classifier to predicate the current device usage context
        PuxPrediction prediction = PuxDataService.getInstance().predict(point);
        if (prediction != null) {
            viewModel.setPredictedClass(prediction.getPredictedLabel());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPuxDebugBinding binding = FragmentPuxDebugBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        View rootView = binding.getRoot();

        ((TextView)rootView.findViewById(R.id.console_text)).setMovementMethod(new ScrollingMovementMethod());
        return rootView;
    }

    public void collectData() {
        if (!isCollectingActivated) {

            final String label = ((Spinner)getView().findViewById(R.id.labels_spinner)).getSelectedItem().toString();

            collectedList = new ArrayList<>(5000);

            isCollectingActivated = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isCollectingActivated = false;
                    collectedData.put(label, collectedList);

                    viewModel.onCollectEnded(collectedList.size());
                }
            }, 5000);
        }
    }

    public void train() {
        PuxDataService.getInstance().train(collectedData, Double.parseDouble(viewModel.getC()), Double.parseDouble(viewModel.getGamma()), this);
    }

    @Override
    public void onTrainingEnded() {
        viewModel.onTrainingEnded(0);
    }
}
