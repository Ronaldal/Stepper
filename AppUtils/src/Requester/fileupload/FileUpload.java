package Requester.fileupload;

import okhttp3.Request;

import java.io.File;

public interface FileUpload {
    Request fileUploadRequest(File file);
    Request getFilePath();
    Request isStepperIn();
}
