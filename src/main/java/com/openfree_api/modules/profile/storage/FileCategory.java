package com.openfree_api.modules.profile.storage;

public enum FileCategory {

    AVATAR("avatars"),
    RESUME("resumes"),
    LOGO("logos");

    private final String directory;

    FileCategory(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}