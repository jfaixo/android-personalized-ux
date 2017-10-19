package com.singular_dreams.pux.service;

import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singular_dreams.pux.model.PuxSettings;

import java.io.File;
import java.io.IOException;

public class SettingsService implements PuxSettings.PuxSettingsChangeListener {

    /**
     * Settings file path
     */
    private static final String SETTINGS_FILE_PATH = "pux";
    /**
     * Settings file name
     */
    private static final String SETTINGS_FILE_NAME = "settings.json";

    /**
     * Singleton instance
     */
    private static SettingsService instance;
    /**
     * Jackson mapper used to read/write settings
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Private constructor for singleton
     */
    private SettingsService() {
        // Empty constructor
    }

    /**
     * Returns a settings object instance
     * @return
     */
    public static PuxSettings getSettings() {
        if (instance != null) {
            instance = new SettingsService();
        }

        return instance.readSettings();
    }

    private PuxSettings readSettings() {
        PuxSettings settings;

        try {
            settings = mapper.readValue(getFileHandle(), PuxSettings.class);
        } catch (IOException e) {
            settings = new PuxSettings();
            e.printStackTrace();
        }
        // Registering the service instance as the change listener
        settings.setListener(this);
        return settings;
    }

    private void writeSettings(PuxSettings settings) {
        try {
            mapper.writeValue(getFileHandle(), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFileHandle() {

        File puxSettingsDir = new File(Environment.getDataDirectory(), SETTINGS_FILE_PATH);
        // Create directory if needed
        puxSettingsDir.mkdirs();
        // Open settings file in read only mode
        return new File(puxSettingsDir, SETTINGS_FILE_NAME);
    }

    @Override
    public void onChanged(PuxSettings settings) {
        writeSettings(settings);
    }
}
