package com.singular_dreams.pux;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.singular_dreams.pux.databinding.ActivityPuxBinding;
import com.singular_dreams.pux.model.PuxContextDataPoint;

/**
 * Base Activity class of the Pux library.
 */
public class PuxBaseActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;

    private static final float NS2S = 1.0f / 1000000000.0f;

    private long previousEventTime;
    private final float[] accelerometerReading = new float[3];
    private final float[] previousAccelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];


    /**
     * Binding instance of the activity
     */
    ActivityPuxBinding dataBinding;
    /**
     * Journey manager instance
     */
    private PuxJourneyManager journeyManager = new PuxJourneyManager();

    private int currentStepIndex = 0;

    private boolean isContainer1Visible = true;

    private FrameLayout fragmentContainer1;
    private FrameLayout fragmentContainer2;

    private PuxBaseFragment currentFragment;


    public PuxJourneyManager getJourneyManager() {
        return journeyManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sensors initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Journey: handling databinding
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_pux);
        dataBinding.setJourney(journeyManager);
        // Journey: step management
        fragmentContainer1 = (FrameLayout) findViewById(R.id.fragment_container_1);
        fragmentContainer2 = (FrameLayout) findViewById(R.id.fragment_container_2);
        goToFirstStep();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        if (magneticFieldSensor != null) {
            sensorManager.registerListener(this, magneticFieldSensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }

    //region Sensor management

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     * <p>
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Create the new datapoint
        PuxContextDataPoint point = new PuxContextDataPoint();
        point.creationTime = event.timestamp;

        if (event.sensor == accelerometerSensor) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        }
        else if (event.sensor == magneticFieldSensor) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        // Set Orientation angles
        System.arraycopy(orientationAngles, 0, point.orientationAngles, 0, orientationAngles.length);
        // Compute speed vector
        long deltaT = (previousEventTime - event.timestamp);
        if (deltaT != 0) {
            point.speedVector[0] = (accelerometerReading[0] - previousAccelerometerReading[0]) / deltaT;
            point.speedVector[1] = (accelerometerReading[1] - previousAccelerometerReading[1]) / deltaT;
            point.speedVector[2] = (accelerometerReading[2] - previousAccelerometerReading[2]) / deltaT;
        }


        // Save values for speed computation
        previousEventTime = event.timestamp;
        System.arraycopy(accelerometerReading, 0, previousAccelerometerReading,
                0, accelerometerReading.length);

        if (currentFragment != null) {
            currentFragment.onNewContextDataPoint(point);
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    //endregion

    //region Journey management

    protected void goToStep(int stepIndex) {

        // Check that the goto operation is possible
        if (stepIndex >= 0 && stepIndex < journeyManager.getStepsCount()) {

            // Start the fragment transaction
            currentFragment = journeyManager.getStepInstance(stepIndex);

            if (currentFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(getNextStepContainerId(), currentFragment);
                transaction.commit();

                // Swap the visible containers
                getNextStepContainer().setVisibility(View.VISIBLE);
                getCurrentStepContainer().setVisibility(View.GONE);
                isContainer1Visible = !isContainer1Visible;
                currentStepIndex = stepIndex;

                // Clear the previous container
                getNextStepContainer().removeAllViews();
            }
        }
    }

    public Fragment getCurrentFragment() { return currentFragment; }

    public void goToFirstStep() {
        goToStep(0);
    }

    public void goToNextStep() {
        goToStep(currentStepIndex + 1);
    }

    public void goToPreviousStep() {
        goToStep(currentStepIndex - 1);
    }

    private FrameLayout getCurrentStepContainer() {
        return isContainer1Visible ? fragmentContainer1 : fragmentContainer2;
    }

    private FrameLayout getNextStepContainer() {
        return isContainer1Visible ? fragmentContainer2 : fragmentContainer1;
    }

    private int getNextStepContainerId() {
        return isContainer1Visible ? R.id.fragment_container_2 : R.id.fragment_container_1;
    }

    //endregion

}
