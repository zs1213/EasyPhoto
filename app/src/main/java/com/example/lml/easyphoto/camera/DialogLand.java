package com.example.lml.easyphoto.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.lml.easyphoto.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DialogLand {
    private AlertDialog.Builder dialog = null;

    /**
     *
     * @param context
     * @param land
     */
    public void build(Context context, final LandInfo land){
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示");
        View view = LayoutInflater.from(context).inflate(R.layout.land_view,null);
        final EditText txtName = (EditText) view.findViewById(R.id.land_anme);
        if (land.getName()!=null){
            txtName.setText(land.getName());
        }

        final EditText txtIdCard = (EditText) view.findViewById(R.id.land_idcard);
        if (land.getIdCard()!=null){
            txtIdCard.setText(land.getIdCard());
        }

        final EditText txtDkbm = (EditText) view.findViewById(R.id.land_dkbm);
        if (land.getDkbm()!=null){
            txtDkbm.setText(land.getDkbm());
        }
        final EditText txtAream = (EditText) view.findViewById(R.id.land_area);
        if (land.getAreas()!=0){
            NumberFormat formatter = new DecimalFormat("0.00");
            txtAream.setText(formatter.format(land.getAreas()));
        }
        final EditText txtPlantType = (EditText) view.findViewById(R.id.land_landtype);
        if (land.getPlantType()!=null){
            txtPlantType.setText(land.getPlantType());
        }
        final EditText txtGps = (EditText) view.findViewById(R.id.land_gps);
        if (land.getGps()!=null){
            txtGps.setText(land.getGps());
        }

        final EditText txtAddress = (EditText) view.findViewById(R.id.land_address);
        if (land.getAddress()!=null){
            txtAddress.setText(land.getAddress());
        }

        final EditText txtDkName = (EditText) view.findViewById(R.id.land_dkName);
        if (land.getGroudName()!=null){
            txtDkName.setText(land.getGroudName());
        }
        dialog.setView(view);
        dialog.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                land.setAddress(txtAddress.getText().toString());
            }
        });

    }

    /**
     *
     */
    public void show(){
        if(dialog != null)
            dialog.create().show();
    }
}
