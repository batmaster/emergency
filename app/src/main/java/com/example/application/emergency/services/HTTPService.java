package com.example.application.emergency.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.MultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTTPService {

    private Context context;
    private RequestQueue queue;
    private String BASE_URL = "http://188.166.180.204:8888/emergency.php";

    public HTTPService(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public void upload(ArrayList<Uri> files, String aid) {
        for (int i = 0; i < files.size(); i++) {
            upload(files.get(i), aid);
        }

    }

    private void upload(final Uri uri, final String aid) {
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {

                try {
                    String sourceFileUri = getRealPathFromUri(context, uri);

                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 10 * 1024 * 1024;
                    int resCode = 0;
                    String resMessage = "";

                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    try {
                        File file = new File(sourceFileUri);
                        if (!file.exists()) {
                            return null;
                        }

                        String newName = aid + "_" + DateFormat.format("yyyy_MM_dd_HH_mm_" + file.getName(), new Date()).toString();

                        FileInputStream fileInputStream = new FileInputStream(new File(sourceFileUri));

                        URL url = new URL(BASE_URL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");

                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes(
                                "Content-Disposition: form-data; name=\"filUpload\";filename=\"" + newName + "\"" + lineEnd);
                        outputStream.writeBytes(lineEnd);

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // Read file
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            outputStream.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // Response Code and Message
                        resCode = conn.getResponseCode();
                        if (resCode == HttpURLConnection.HTTP_OK) {
                            InputStream is = conn.getInputStream();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();

                            int read = 0;
                            while ((read = is.read()) != -1) {
                                bos.write(read);
                            }
                            byte[] result = bos.toByteArray();
                            bos.close();

                            resMessage = new String(result);

                        }
                        else {
                            upload(uri, aid);
                        }

                        fileInputStream.close();
                        outputStream.flush();
                        outputStream.close();


                    } catch (Exception ex) {
                        // Exception handling
                    }


                } catch (Exception ex) {
                    // dialog.dismiss();

                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

                Log.d("upload onProgressUpdate", getRealPathFromUri(context, uri) + " " + values.toString());
            }

        }.execute();
    }

    public void callPHP(HashMap<String, String> params, final OnResponseCallback<JSONObject> responseCallback) {
        Log.d("HTTP", params.toString());

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                Log.d("HTTP", "onResponse " + s);

                try {
                    JSONObject json = new JSONObject(s);

                    responseCallback.onResponse(true, null, json);

                } catch (JSONException e) {
                    Log.d("HTTP", "JSONException " + e.getMessage());
                    e.printStackTrace();
                    responseCallback.onResponse(true, e, null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("HTTP", "onErrorResponse " + volleyError);
                responseCallback.onResponse(false, volleyError, null);
            }
        });

        request.setParams(params);
        queue.add(request);
    }


    public interface OnResponseCallback<T> {
        void onResponse(boolean success, Throwable error, T data);
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
