package com.example.application.emergency.services;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.System.gc;

/**
 * class สำหรับฟังก์ชั่นที่ใช้ติดต่อสื่อสารกับ server
 */
public class HTTPService {

    /** ประกาศตัวแปร **/
    private Context context;
    private RequestQueue queue;
    private String BASE_URL = "http://188.166.180.204:8888/emergency.php";

    public HTTPService(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    /** ฟังก์ชั่นสำหรับเรียกอัพโหลดไฟล์ภาพ **/
    public void upload(ArrayList<Uri> files, String aid) {
        for (int i = 0; i < files.size(); i++) {
            upload(files.get(i), aid);
        }

    }

    /** ฟังก์ชั่นอัพโหลดไฟล์ภาพขึ้น server **/
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

                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    try {
                        File file = new File(sourceFileUri);
                        if (!file.exists()) {
                            return null;
                        }

                        String newName = aid + "_" + DateFormat.format("yyyy_MM_dd_HH_mm_" + file.getName(), new Date()).toString();

//                        FileInputStream fileInputStream = new FileInputStream(new File(sourceFileUri));
                        ByteArrayInputStream fileInputStream = getReducedImage(sourceFileUri);

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
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"filUpload\";filename=\"" + newName + "\"" + lineEnd);
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
                        }
                        else {
                            upload(uri, aid);
                        }

                        fileInputStream.close();
                        outputStream.flush();
                        outputStream.close();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

        }.execute();
    }

    /** ฟังก์ชั่นสำหรับเรียกใช้ ฟังก์ชั่นที่อยู่บน server **/
    public void callPHP(HashMap<String, String> params, final OnResponseCallback<JSONObject> responseCallback) {
        Log.d("HTTP", params.toString());

        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("HTTP", "onResponse " + s);
                checkin();

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

    private void checkin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "check_in");
            params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
            StringRequest request = new StringRequest(Request.Method.POST, BASE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.d("HTTP", "onResponse " + s);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("HTTP", "onErrorResponse " + volleyError);
                }
            });

            request.setParams(params);
            queue.add(request);
        }
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

    // ฟังก์ชั่นใช้ลดขนาดไฟล์ภาพก่อนอัพโหลด
    private ByteArrayInputStream getReducedImage(String sourceFileUri) {
        Bitmap b = BitmapFactory.decodeFile(sourceFileUri);
        int w = b.getWidth();
        int h = b.getHeight();

        Log.d("imageaa", "before " + b.getWidth() + " " + b.getHeight());
        Bitmap out = null;
        if (h > 1920 && w < h) {
            out = Bitmap.createScaledBitmap(b, w * 1920 / h, 1920, false);
        }
        else if (w > 1920 && w > h) {
            out = Bitmap.createScaledBitmap(b, 1920, h * 1920 / w, false);
        }
        else {
            out = Bitmap.createScaledBitmap(b, w, h, false);
        }
        b = null;

        Log.d("imageaa", "after " + out.getWidth() + " " + out.getHeight());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        out.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] bitmapdata = bos.toByteArray();
        bos = null;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bitmapdata);

        bitmapdata = null;

        return byteArrayInputStream;
    }
}
