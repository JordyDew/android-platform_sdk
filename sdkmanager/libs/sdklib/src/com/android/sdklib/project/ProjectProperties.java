/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.sdklib.project;

import com.android.sdklib.SdkManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to load and save project properties for both ADT and Ant-based build.
 *
 */
public final class ProjectProperties {
    /** The property name for the project target */
    public final static String PROPERTY_TARGET = "target";
    public final static String PROPERTY_SDK = "sdk-folder";
    
    private final static String PROPERTIES_FILE = "default.properties";
    
    private final static String PROP_HEADER = 
            "# This file is automatically generated by Android Tools.\n" +
            "# Do not modify this file -- YOUR CHANGES WILL BE ERASED!\n" +
            "# For customized properties when using Ant, set new values\n" +
            "# in a \"build.properties\" file.\n\n";

    private final static Map<String, String> COMMENT_MAP = new HashMap<String, String>();
    static {
        COMMENT_MAP.put(PROPERTY_TARGET, "# Project target.\n");
        COMMENT_MAP.put(PROPERTY_SDK, "# location of the SDK. Only used by Ant.\n");
    }
    
    private final String mProjectFolderOsPath;
    private final Map<String, String> mProperties;

    /**
     * Loads a project properties file and return a {@link ProjectProperties} object
     * containing the properties
     * @param projectFolderOsPath the project folder.
     */
    public static ProjectProperties load(String projectFolderOsPath) {
        File projectFolder = new File(projectFolderOsPath);
        if (projectFolder.isDirectory()) {
            File defaultFile = new File(projectFolder, PROPERTIES_FILE);
            if (defaultFile.isFile()) {
                Map<String, String> map = SdkManager.parsePropertyFile(defaultFile, null /* log */);
                if (map != null) {
                    return new ProjectProperties(projectFolderOsPath, map);
                }
            }
        }
        return null;
    }
    
    /**
     * Creates a new project properties file, with no properties.
     * <p/>The file is not created until {@link #save()} is called.
     * @param projectFolderOsPath the project folder.
     */
    public static ProjectProperties create(String projectFolderOsPath) {
        // create and return a ProjectProperties with an empty map.
        return new ProjectProperties(projectFolderOsPath, new HashMap<String, String>());
    }
    
    /**
     * Sets a new properties. If a property with the same name already exists, it is replaced.
     * @param name the name of the property.
     * @param value the value of the property.
     */
    public void setProperty(String name, String value) {
        mProperties.put(name, value);
    }
    
    /**
     * Returns the value of a property.
     * @param name the name of the property.
     * @return the property value or null if the property is not set.
     */
    public String getProperty(String name) {
        return mProperties.get(name);
    }

    /**
     * Saves the property file.
     * @throws IOException
     */
    public void save() throws IOException {
        File toSave = new File(mProjectFolderOsPath, PROPERTIES_FILE);
        
        FileWriter writer = new FileWriter(toSave);
        
        // write the header
        writer.write(PROP_HEADER);
        
        // write the properties.
        for (Entry<String, String> entry : mProperties.entrySet()) {
            String comment = COMMENT_MAP.get(entry.getKey());
            if (comment != null) {
                writer.write(comment);
            }
            writer.write(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }
        
        // close the file to flush
        writer.close();
    }
    
    /**
     * Private constructor.
     * Use {@link #load(String)} or {@link #create(String)} to instantiate.
     * @param projectFolderOsPath
     * @param map
     */
    private ProjectProperties(String projectFolderOsPath, Map<String, String> map) {
        mProjectFolderOsPath = projectFolderOsPath;
        mProperties = map;
    }
}