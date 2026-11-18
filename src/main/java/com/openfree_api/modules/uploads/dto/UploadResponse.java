package com.openfree_api.modules.uploads.dto;

public class UploadResponse {

    private String fileName;
    private String url;
    private String contentType;
    private long size;

    public UploadResponse() {
    }

    public UploadResponse(
            String fileName,
            String url,
            String contentType,
            long size
    ) {
        this.fileName = fileName;
        this.url = url;
        this.contentType = contentType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}