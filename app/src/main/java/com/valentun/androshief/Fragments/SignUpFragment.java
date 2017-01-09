package com.valentun.androshief.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.Helper;
import com.valentun.androshief.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.valentun.androshief.Constants.GALLERY_REQUEST;
import static com.valentun.androshief.Helper.encodeBitMap;
import static com.valentun.androshief.Helper.getDisplayMetrics;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    private OnSignUpFragmentListener mListener;
    private View view;
    private Drawable avatar;
    private AppCompatEditText inputEmail, inputPassword, inputName;
    private AppCompatImageButton inputImage;
    private AppCompatButton register;

    public interface OnSignUpFragmentListener {
        void onSignUpButtonSelected(String email, String password, String name, String image, SignUpFragment fragment);

    }

    public SignUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mListener = (OnSignUpFragmentListener) getActivity();

        inputImage = (AppCompatImageButton) view.findViewById(R.id.sign_up_image);

        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        inputEmail = (AppCompatEditText) view.findViewById(R.id.sign_up_email);
        inputPassword = (AppCompatEditText) view.findViewById(R.id.sign_up_password);
        inputName = (AppCompatEditText) view.findViewById(R.id.sign_up_name);

        register = (AppCompatButton) view.findViewById(R.id.sign_up_submit);
        register.setTextColor(Color.WHITE);
        register.getBackground().setColorFilter(0xFFFF6D00, PorterDuff.Mode.MULTIPLY);
        register.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    avatar = Helper.getCroppedBitMap(bitmap, getDisplayMetrics(getActivity()), getActivity().getResources());
                    inputImage.setImageDrawable(avatar);
                }
        }
    }

    @Override
    public void onClick(View view) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String name = inputName.getText().toString();

        RoundedBitmapDrawable drawable = (RoundedBitmapDrawable) inputImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String base64Bitmap = encodeBitMap(bitmap);

        mListener.onSignUpButtonSelected(email, password, name, base64Bitmap, this);
    }

}
