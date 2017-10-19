package com.singular_dreams.puxdemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.singular_dreams.pux.PuxBaseFragment;
import com.singular_dreams.pux.model.PuxContextDataPoint;
import com.singular_dreams.pux.model.PuxPrediction;
import com.singular_dreams.pux.service.PuxDataService;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends PuxBaseFragment {

    ViewGroup mSceneRoot;
    Scene mHandAgnosticScene;
    Scene mRightHandScene;
    Scene mLeftHandScene;
    Transition transition;
    Scene currentScene;

    private String predictedLabel = "";
    private long steadyLabelDuration = 0;

    public DemoFragment() {
        // Required empty public constructor
        transition = new ChangeBounds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.hand_agnostic_layout, container, false);

        // Create the scene root for the scenes in this app
        mSceneRoot = (ViewGroup) rootView.findViewById(R.id.scene_root);

        // Create the scenes
        mHandAgnosticScene = Scene.getSceneForLayout(mSceneRoot, R.layout.hand_agnostic_layout, this.getContext());
        mRightHandScene = Scene.getSceneForLayout(mSceneRoot, R.layout.right_hand_scene, this.getContext());
        mLeftHandScene = Scene.getSceneForLayout(mSceneRoot, R.layout.left_hand_scene, this.getContext());
        currentScene = mHandAgnosticScene;

        return rootView;
    }

    @Override
    public void onNewContextDataPoint(PuxContextDataPoint point) {
        PuxPrediction prediction = PuxDataService.getInstance().predict(point);

        if (prediction != null && prediction.getPredictedLabel().equals(predictedLabel) == false) {
            predictedLabel = prediction.getPredictedLabel();
            steadyLabelDuration = point.creationTime;
        }
        else if (prediction != null && point.creationTime - steadyLabelDuration > 200000) {
            switch (predictedLabel) {
                case "Right hand":
                    if (currentScene.equals(mRightHandScene) == false) {
                        TransitionManager.go(mRightHandScene, transition);
                        currentScene = mRightHandScene;
                    }
                    break;
                case "Left hand":
                    if (currentScene.equals(mLeftHandScene) == false) {
                        TransitionManager.go(mLeftHandScene, transition);
                        currentScene = mLeftHandScene;
                    }
                    break;
                case "Hand agnostic":
                    if (currentScene.equals(mHandAgnosticScene) == false) {
                        TransitionManager.go(mHandAgnosticScene, transition);
                        currentScene = mHandAgnosticScene;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
