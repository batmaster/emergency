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
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.components.DeletableImageView;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;

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
    private ImageView imageViewDelete;
    private ImageView imageViewDone;

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

        imageViewDelete = (ImageView) v.findViewById(R.id.imageViewDelete);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEdittingMode(true);
            }
        });

        imageViewDone = (ImageView) v.findViewById(R.id.imageViewDone);
        imageViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEdittingMode(false);

            }
        });

        imageView = (ImageView) v.findViewById(R.id.imageView);

        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
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

                            final Uri imageUri = Uri.parse(o.getString("image"));

                            Log.d("imageUri", imageUri.toString());

                            final DeletableImageView im = getDeletableImageView(imageUri, o.getInt("id"));

                            layoutGallery.addView(im, layoutGallery.getChildCount() - 4);

                            Glide.with(getContext()).load(imageUri).listener(new RequestListener<Uri, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    e.printStackTrace();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                    return false;
                                }
                            }).centerCrop().placeholder(R.drawable.placeholder).into(im.getImageView());
                            Glide.with(getContext()).load(imageUri).listener(new RequestListener<Uri, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    e.printStackTrace();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                    return false;
                                }
                            }).fitCenter().placeholder(R.drawable.placeholder).into(imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        setEdittingMode(false);
        if (aid != -1) {
            imageViewCamera.setVisibility(View.GONE);
            imageViewAlbum.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
        }

        return v;
    }

    /** ฟังก์ชั่นของระบบแอนดรอยด์ สำหรับเรียกใช้หลังการกลับจาก process อื่น **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** ถ้าเป็นการกลับจากกล้องหรืออัลบัม ให้เอาภาพมาแสดง และใส่ไว้ในรายการในตัวแปร **/
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == RESULT_CAMERA || requestCode == RESULT_GALLERY) {
                final Uri imageUri = data.getData();

                final DeletableImageView i = getDeletableImageView(imageUri, 0);

                layoutGallery.addView(i, layoutGallery.getChildCount() - 4);

                Glide.with(getContext()).load(imageUri).centerCrop().into(i.getImageView());
                Glide.with(getContext()).load(imageUri).fitCenter().into(imageView);
            }
        }
    }

    /** ฟังก์ชั่นสำหรับสร้าง component พร้อมรูปภาพมาแสดง **/
    private DeletableImageView getDeletableImageView(final Uri imageUri, int imageId) {
        Resources r = getResources();
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
        int pd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

        DeletableImageView deletableImageView = new DeletableImageView(getContext(), imageUri, imageId);
        ImageView i = deletableImageView.getImageView();
        i.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
        i.setAdjustViewBounds(true);
        i.setPadding(pd, pd, pd, pd);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getContext()).load(imageUri).fitCenter().into(imageView);
            }
        });

        return deletableImageView;
    }

    private void setEdittingMode(boolean editingState) {
        if (editingState) {
            imageViewCamera.setVisibility(View.GONE);
            imageViewAlbum.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
            imageViewDone.setVisibility(View.VISIBLE);

            for (int i = 0; i < layoutGallery.getChildCount() - 4; i++) {
                DeletableImageView im = (DeletableImageView) layoutGallery.getChildAt(i);
                im.setEdittingMode(true);

                layoutGallery.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
        else {
            imageViewCamera.setVisibility(View.VISIBLE);
            imageViewAlbum.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
            imageViewDone.setVisibility(View.GONE);

            for (int i = 0; i < layoutGallery.getChildCount() - 4; i++) {
                DeletableImageView im = (DeletableImageView) layoutGallery.getChildAt(i);
                im.setEdittingMode(false);

                if (im.isRemoved()) {
                    layoutGallery.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }
    }

    public ArrayList<Uri> getNewImageUris() {
        ArrayList<Uri> list = new ArrayList<Uri>();
        for (int i = 0; i < layoutGallery.getChildCount() - 4; i++) {
            DeletableImageView im = (DeletableImageView) layoutGallery.getChildAt(i);
            if (im.isLocal() && !im.isRemoved()) {
                list.add(im.getImageUri());
            }
        }

        return list;
    }

    public ArrayList<Integer> getRemoteRemoves() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < layoutGallery.getChildCount() - 4; i++) {
            DeletableImageView im = (DeletableImageView) layoutGallery.getChildAt(i);
            if (!im.isLocal() && im.isRemoved()) {
                list.add(im.getImageId());
            }
        }

        return list;
    }
}
