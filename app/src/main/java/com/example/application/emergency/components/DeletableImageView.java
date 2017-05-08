package com.example.application.emergency.components;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.application.emergency.R;

/**
 * class แสดงผลแทน imageview หน้าแสดงรูปภาพการแจ้งเหตุ มีปุ่มลบเมื่อเปิดโหมดลบ
 */
public class DeletableImageView extends RelativeLayout {

    private ImageView imageView;
    private CheckBox checkBox;
    private Uri imageUri;
    private int imageId;

    public DeletableImageView(Context context, Uri imageUri, int imageId) {
        super(context);
        this.imageUri = imageUri;
        this.imageId = imageId;
        initializeViews(context);
    }

    public DeletableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public DeletableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.deletable_imageview, this);

        imageView = (ImageView) v.findViewById(R.id.imageView);
        checkBox = (CheckBox) v.findViewById(R.id.checkBox);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean isRemoved() {
        return checkBox.isChecked();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public boolean isLocal() {
        return imageId == 0;
    }

    public int getImageId() {
        return imageId;
    }

    public void setEdittingMode(boolean edittingMode) {
        checkBox.setVisibility(edittingMode ? VISIBLE : GONE);
    }
}
