package com.plugins.advancedhttp.cordovahttp;

import com.plugins.advancedhttp.http.TLSConfiguration;

import java.io.File;
import java.net.URI;

import com.plugins.advancedhttp.http.HttpRequest;

import org.apache.cordova.CallbackContext;
import com.plugins.file.FileUtils;
import org.json.JSONObject;

class CordovaHttpDownload extends CordovaHttpBase {
  private String filePath;

  public CordovaHttpDownload(String url, JSONObject headers, String filePath, int timeout, boolean followRedirects,
                             TLSConfiguration tlsConfiguration, CallbackContext callbackContext) {

    super("GET", url, headers, timeout, followRedirects, "text", tlsConfiguration, callbackContext);
    this.filePath = filePath;
  }

  @Override
  protected void processResponse(HttpRequest request, CordovaHttpResponse response) throws Exception {
    response.setStatus(request.code());
    response.setUrl(request.url().toString());
    response.setHeaders(request.headers());

    if (request.code() >= 200 && request.code() < 300) {
      File file = new File(new URI(this.filePath));
      JSONObject fileEntry = FileUtils.getFilePlugin().getEntryForFile(file);

      request.receive(file);
      response.setFileEntry(fileEntry);
    } else {
      response.setErrorMessage("There was an error downloading the file");
    }
  }
}
