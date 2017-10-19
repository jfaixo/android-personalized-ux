package com.singular_dreams.pux.service;

import com.singular_dreams.pux.model.PuxContextDataPoint;
import com.singular_dreams.pux.model.PuxPrediction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libsvm.*;

public class PuxDataService {

    /**
     * Singleton instance
     */
    private static PuxDataService instance;

    private svm_model model;

    private String[] classTranslationArray;

    public static PuxDataService getInstance() {
        if (instance == null) {
            instance = new PuxDataService();
        }
        return instance;
    }

    /**
     * Private constructor for singleton pattern
     */
    private PuxDataService() {
    }

    public void train(Map<String, List<PuxContextDataPoint>> dataset, double C, double gamma, EventsListener listener) {

        svm_problem problem = transformProblem(dataset);

        svm_parameter parameters = new svm_parameter();
        parameters.svm_type = svm_parameter.C_SVC;
        parameters.kernel_type = svm_parameter.RBF;
        parameters.degree = 3;
        parameters.gamma = gamma;	// 1/num_features
        parameters.coef0 = 0;
        parameters.nu = 0.5;
        parameters.cache_size = 100;
        parameters.C = C;
        parameters.eps = 1e-3;
        parameters.p = 0.1;
        parameters.shrinking = 1;
        parameters.probability = 1;
        parameters.nr_weight = 0;
        parameters.weight_label = new int[0];
        parameters.weight = new double[0];

        model = svm.svm_train(problem, parameters);

        if (listener != null) {
            listener.onTrainingEnded();
        }
    }

    private svm_problem transformProblem(Map<String, List<PuxContextDataPoint>> dataset) {
        svm_problem problem = new svm_problem();

        // compute dataset size
        problem.l = 0;
        for (List<PuxContextDataPoint> list : dataset.values())
        {
            problem.l += list.size();
        }

        // Compute target values and data
        classTranslationArray = new String[dataset.keySet().size()];
        dataset.keySet().toArray(classTranslationArray);
        problem.y = new double[problem.l];

        problem.x = new svm_node[problem.l][];
        int index = 0;
        for (Map.Entry<String, List<PuxContextDataPoint>> entry : dataset.entrySet()) {
            int labelIndex = Arrays.asList(classTranslationArray).indexOf(entry.getKey()); // TODO: pas optimisé du tout

            for (PuxContextDataPoint point : entry.getValue()) {
                // Insert label
                problem.y[index] = labelIndex;
                // Insert data
                problem.x[index] = transformDataPoint(point);

                index += 1;
            }
        }

        return problem;
    }

    public PuxPrediction predict(PuxContextDataPoint point) {
        if (model != null) {
            double[] probabilities = new double[classTranslationArray.length];
            double labelIndex = svm.svm_predict_probability(model, transformDataPoint(point), probabilities);

            return new PuxPrediction(classTranslationArray[(int)labelIndex], probabilities);
        }
        else {
            return null;
        }
    }

    private svm_node[] transformDataPoint(PuxContextDataPoint point) {

        svm_node[] node = new svm_node[6];

        // Azimuth
        node[0] = new svm_node();
        node[0].index = 1;
        node[0].value = point.orientationAngles[0];
        // Pitch
        node[1] = new svm_node();
        node[1].index = 2;
        node[1].value = point.orientationAngles[1];
        // Roll
        node[2] = new svm_node();
        node[2].index = 3;
        node[2].value = point.orientationAngles[2];

        // Vx
        node[3] = new svm_node();
        node[3].index = 4;
        node[3].value = point.speedVector[0];
        // Vy
        node[4] = new svm_node();
        node[4].index = 5;
        node[4].value = point.speedVector[1];
        // Vz
        node[5] = new svm_node();
        node[5].index = 6;
        node[5].value = point.speedVector[2];

        return node;
    }

    public interface EventsListener {
        void onTrainingEnded();
    }
}
