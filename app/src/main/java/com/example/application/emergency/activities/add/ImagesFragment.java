package com.example.application.emergency.activities.add;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class แสดงผล fragment หน้าแสดงรูปภาพการแจ้งเหตุ
 */
public class ImagesFragment extends Fragment {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private static EmergencyApplication app;

    private int aid;

    private LinearLayout layoutGallery;
    private ImageView imageViewCamera;
    private ImageView imageViewAlbum;
    private ArrayList<Uri> imageUris;

    private ImageView imageView;

    public static final int RESULT_CAMERA = 201;
    public static final int RESULT_GALLERY = 202;

    private static ImagesFragment fragment;

    public static ImagesFragment getInstance() {
        if (fragment == null) {
            fragment = new ImagesFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = (EmergencyApplication) getActivity().getApplication();

        /** ดึงค่า accident id เพื่อตรวจสอบว่า หน้านี้ถูกเปิดโดยการกดปุ่ม เพิ่ม หรือคลิกที่รายการการแจ้งเหตุ **/
        aid = ((AddActivity)getActivity()).getAid();
        ((AddActivity)getActivity()).setImagesFragment(this);

        /** ดึงค่า accident id เพื่อตรวจสอบว่า หน้านี้ถูกเปิดโดยการกดปุ่ม เพิ่ม หรือคลิกที่รายการการแจ้งเหตุ **/
        View v = inflater.inflate(R.layout.fragment_add_images, container, false);

        layoutGallery = (LinearLayout) v.findViewById(R.id.layoutGallery);

        imageViewCamera = (ImageView) v.findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** เปิดแอปกล้อง **/
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, RESULT_CAMERA);
            }
        });

        imageViewAlbum = (ImageView) v.findViewById(R.id.imageViewAlbum);
        imageViewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** เปิดแอปอัลบัมภาพ **/
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(intent, RESULT_GALLERY);
            }
        });

        imageUris = new ArrayList<Uri>();

        imageView = (ImageView) v.findViewById(R.id.imageView);

        /** เปิดแอปอัลบัมภาพ **/

        HashMap<String, String> params2 = new HashMap<String, String>();
        params2.put("function", "get_images");
        params2.put("aid", String.valueOf(aid));
        app.getHttpService().callPHP(params2, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        JSONArray a = data.getJSONArray("array");

                        ArrayList<ListModel> list = new ArrayList<ListModel>();
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);

                            Uri imageUri = Uri.parse(o.getString("image"));

                            Log.d("imageUri", imageUri.toString());

                            ImageView im = getImageView(imageUri);

                            layoutGallery.addView(im, layoutGallery.getChildCount() - 2);

                            Glide.with(getContext()).load(imageUri).centerCrop().placeholder(R.drawable.placeholder).into(im);
                            Glide.with(getContext()).load(imageUri).fitCenter().into(imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return v;
    }

    /** ฟังก์ชั่นสำหรับเรียกใช้ตัวแปรใน class **/
    public ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    /** ฟังก์ชั่นของระบบแอนดรอยด์ สำหรับเรียกใช้หลังการกลับจาก process อื่น **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** ถ้าเป็นการกลับจากกล้องหรืออัลบัม ให้เอาภาพมาแสดง และใส่ไว้ในรายการในตัวแปร **/
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == RESULT_CAMERA || requestCode == RESULT_GALLERY) {
                Uri imageUri = data.getData();

                ImageView i = getImageView(imageUri);

                layoutGallery.addView(i, layoutGallery.getChildCount() - 2);

                Glide.with(getContext()).load(imageUri).centerCrop().into(i);
                Glide.with(getContext()).load(imageUri).fitCenter().into(imageView);
                imageUris.add(imageUri);
            }
        }
    }

    /** ฟังก์ชั่นสำหรับสร้าง component พร้อมรูปภาพมาแสดง **/
    private ImageView getImageView(final Uri imageUri) {
        Resources r = getResources();
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
        int pd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

        final ImageView i = new ImageView(getContext());
        i.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        i.setAdjustViewBounds(true);
        i.setPadding(pd, pd, pd, pd);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getContext()).load(imageUri).fitCenter().into(imageView);
            }
        });

        return i;
    }
}
