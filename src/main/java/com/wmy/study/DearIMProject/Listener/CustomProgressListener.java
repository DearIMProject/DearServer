package com.wmy.study.DearIMProject.Listener;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.ProgressListener;
import org.springframework.stereotype.Component;

public class CustomProgressListener implements ProgressListener {
    private final HttpServletRequest request;

    public CustomProgressListener(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void update(long bytesRead, long contentLength, int items) {
        long progress = (bytesRead * 100) / contentLength;
        request.getSession().setAttribute("uploadProgress", progress);
    }
}
