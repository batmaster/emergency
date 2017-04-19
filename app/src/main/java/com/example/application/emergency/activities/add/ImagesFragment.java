package com.example.application.emergency.activities.add;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/15/2017 AD.
 */

public class ImagesFragment extends Fragment {

    private static EmergencyApplication app;

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

        View v = inflater.inflate(R.layout.fragment_add_images, container, false);

        layoutGallery = (LinearLayout) v.findViewById(R.id.layoutGallery);

        imageViewCamera = (ImageView) v.findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, RESULT_CAMERA);
            }
        });

        imageViewAlbum = (ImageView) v.findViewById(R.id.imageViewAlbum);
        imageViewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(intent, RESULT_GALLERY);
            }
        });

        imageUris = new ArrayList<Uri>();

        imageView = (ImageView) v.findViewById(R.id.imageView);

        return v;
    }

    public ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {


            if (requestCode == RESULT_CAMERA || requestCode == RESULT_GALLERY) {
                final Uri imageUri = data.getData();

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
                layoutGallery.addView(i, layoutGallery.getChildCount() - 2);

                Glide.with(getContext()).load(imageUri).centerCrop().into(i);
                imageUris.add(imageUri);
            }
        }
    }

}
