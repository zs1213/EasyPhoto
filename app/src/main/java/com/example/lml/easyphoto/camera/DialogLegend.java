package com.example.lml.easyphoto.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lml.easyphoto.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DialogLegend {
    private AlertDialog.Builder dialog = null;

    /**
     *
     * @param context
     */
    public void build(Context context){
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle("图例");
        View view = LayoutInflater.from(context).inflate(R.layout.legend_view,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.legend_image);
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.tuli,imageView);
        dialog.setView(view);
        dialog.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

    }

    /**
     *
     */
    public void show(){
        if(dialog != null){
            dialog.create().show();
        }
    }
}
