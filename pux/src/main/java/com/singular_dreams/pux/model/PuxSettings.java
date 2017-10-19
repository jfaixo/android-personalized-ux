package com.singular_dreams.pux.model;


public class PuxSettings {

    /**
     * Change listener
     */
    private PuxSettingsChangeListener listener = null;
    /**
     * Settings JSON definition versioning
     */
    private int schemaVersion = 1;

    /**
     * Change listener setter
     * @param listener listener to use
     */
    public void setListener(PuxSettingsChangeListener listener) {
        this.listener = listener;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {

        this.schemaVersion = schemaVersion;
        onSettingChanged();
    }

    private void onSettingChanged() {
        if (listener != null) {
            listener.onChanged(this);
        }
    }

    public interface PuxSettingsChangeListener {
        void onChanged(PuxSettings settings);
    }
}
