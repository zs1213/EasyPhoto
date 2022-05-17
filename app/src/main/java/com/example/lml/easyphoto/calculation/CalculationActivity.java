package com.example.lml.easyphoto.calculation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.StringUtil;

import cn.hutool.core.util.StrUtil;

public class CalculationActivity extends Activity implements View.OnClickListener {
    private TextView tv_yumi;
    private TextView tv_shuidao;
    private TextView tv_dadou;

    private View view_yumi;
    private View view_shuidao;
    private View view_dadou;

    private EditText et_area;
    private EditText et_weater;
    private EditText et_lizl;
    private EditText et_zazhi;
    private EditText et_zhuNumber;
    private EditText et_suiNumber;
    private EditText et_ybzl;

    private LinearLayout rel_area;
    private LinearLayout rel_weater;
    private LinearLayout rel_lizl;
    private LinearLayout rel_zazhi;
    private LinearLayout rel_zhuNumber;
    private LinearLayout rel_suiNumber;
    private LinearLayout rel_ybzl;

    private View view_area;
    private View view_weater;
    private View view_lizl;
    private View view_zazhi;
    private View view_zhuNumber;
    private View view_suiNumber;
    private View view_ybzl;

    private Button btn_calculation;
    private TextView tv_result;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        initView();
        initData();
    }

    private void initView() {
        tv_yumi = findViewById(R.id.calculation_tv_yumi);
        tv_shuidao = findViewById(R.id.calculation_tv_shuidao);
        tv_dadou = findViewById(R.id.calculation_tv_dadou);
        view_yumi = findViewById(R.id.calculation_view_yumi);
        view_shuidao = findViewById(R.id.calculation_view_shuidao);
        view_dadou = findViewById(R.id.calculation_view_dadou);
        et_area = findViewById(R.id.calculation_et_area);
        et_weater = findViewById(R.id.calculation_et_weater);
        et_lizl = findViewById(R.id.calculation_et_lizl);
        et_zazhi = findViewById(R.id.calculation_et_zazhi);
        et_zhuNumber = findViewById(R.id.calculation_et_zhuNumber);
        et_suiNumber = findViewById(R.id.calculation_et_suiNumber);
        et_ybzl = findViewById(R.id.calculation_et_ybzl);

        rel_area = findViewById(R.id.calculation_rel_area);
        rel_weater = findViewById(R.id.calculation_rel_weater);
        rel_lizl = findViewById(R.id.calculation_rel_lizl);
        rel_zazhi = findViewById(R.id.calculation_rel_zazhi);
        rel_zhuNumber = findViewById(R.id.calculation_rel_zhuNumber);
        rel_suiNumber = findViewById(R.id.calculation_rel_suiNumber);
        rel_ybzl = findViewById(R.id.calculation_rel_ybzl);

        view_area = findViewById(R.id.calculation_view_area);
        view_weater = findViewById(R.id.calculation_view_weater);
        view_lizl = findViewById(R.id.calculation_view_lizl);
        view_zazhi = findViewById(R.id.calculation_view_zazhi);
        view_zhuNumber = findViewById(R.id.calculation_view_zhuNumber);
        view_suiNumber = findViewById(R.id.calculation_view_suiNumber);
        view_ybzl = findViewById(R.id.calculation_view_ybzl);

        btn_calculation = findViewById(R.id.calculation_btn_calculation);
        tv_result = findViewById(R.id.calculation_tv_result);
        tv_yumi.setOnClickListener(this);
        tv_dadou.setOnClickListener(this);
        tv_shuidao.setOnClickListener(this);
        btn_calculation.setOnClickListener(this);
    }

    private void initData() {
        flag = 0;
        view_yumi.setBackgroundColor(Color.parseColor("#2485F1"));
        view_shuidao.setBackgroundColor(Color.parseColor("#00000000"));
        view_dadou.setBackgroundColor(Color.parseColor("#00000000"));
        rel_area.setVisibility(View.VISIBLE);
        rel_weater.setVisibility(View.VISIBLE);
        rel_zhuNumber.setVisibility(View.VISIBLE);
        rel_suiNumber.setVisibility(View.VISIBLE);
        rel_ybzl.setVisibility(View.VISIBLE);
        rel_lizl.setVisibility(View.GONE);
        rel_zazhi.setVisibility(View.GONE);
        view_area.setVisibility(View.VISIBLE);
        view_weater.setVisibility(View.VISIBLE);
        view_lizl.setVisibility(View.GONE);
        view_zazhi.setVisibility(View.GONE);
        view_zhuNumber.setVisibility(View.VISIBLE);
        view_suiNumber.setVisibility(View.VISIBLE);
        view_ybzl.setVisibility(View.VISIBLE);
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculation_tv_yumi:
                flag = 0;
                view_yumi.setBackgroundColor(Color.parseColor("#2485F1"));
                view_shuidao.setBackgroundColor(Color.parseColor("#00000000"));
                view_dadou.setBackgroundColor(Color.parseColor("#00000000"));
                rel_area.setVisibility(View.VISIBLE);
                rel_weater.setVisibility(View.VISIBLE);
                rel_zhuNumber.setVisibility(View.VISIBLE);
                rel_suiNumber.setVisibility(View.VISIBLE);
                rel_ybzl.setVisibility(View.VISIBLE);
                rel_lizl.setVisibility(View.GONE);
                rel_zazhi.setVisibility(View.GONE);

                view_area.setVisibility(View.VISIBLE);
                view_weater.setVisibility(View.VISIBLE);
                view_lizl.setVisibility(View.GONE);
                view_zazhi.setVisibility(View.GONE);
                view_zhuNumber.setVisibility(View.VISIBLE);
                view_suiNumber.setVisibility(View.VISIBLE);
                view_ybzl.setVisibility(View.VISIBLE);
                break;
            case R.id.calculation_tv_shuidao:
                flag = 1;
                view_yumi.setBackgroundColor(Color.parseColor("#00000000"));
                view_shuidao.setBackgroundColor(Color.parseColor("#2485F1"));
                view_dadou.setBackgroundColor(Color.parseColor("#00000000"));
                rel_area.setVisibility(View.VISIBLE);
                rel_weater.setVisibility(View.VISIBLE);
                rel_lizl.setVisibility(View.VISIBLE);
                rel_zazhi.setVisibility(View.VISIBLE);
                rel_zhuNumber.setVisibility(View.GONE);
                rel_suiNumber.setVisibility(View.GONE);
                rel_ybzl.setVisibility(View.GONE);

                view_area.setVisibility(View.VISIBLE);
                view_weater.setVisibility(View.VISIBLE);
                view_lizl.setVisibility(View.VISIBLE);
                view_zazhi.setVisibility(View.VISIBLE);
                view_zhuNumber.setVisibility(View.GONE);
                view_suiNumber.setVisibility(View.GONE);
                view_ybzl.setVisibility(View.GONE);
                break;
            case R.id.calculation_tv_dadou:
                flag = 2;
                view_yumi.setBackgroundColor(Color.parseColor("#00000000"));
                view_shuidao.setBackgroundColor(Color.parseColor("#00000000"));
                view_dadou.setBackgroundColor(Color.parseColor("#2485F1"));
                rel_area.setVisibility(View.VISIBLE);
                rel_weater.setVisibility(View.VISIBLE);
                rel_lizl.setVisibility(View.VISIBLE);
                rel_zazhi.setVisibility(View.VISIBLE);
                rel_zhuNumber.setVisibility(View.GONE);
                rel_suiNumber.setVisibility(View.GONE);
                rel_ybzl.setVisibility(View.GONE);

                view_area.setVisibility(View.VISIBLE);
                view_weater.setVisibility(View.VISIBLE);
                view_lizl.setVisibility(View.VISIBLE);
                view_zazhi.setVisibility(View.VISIBLE);
                view_zhuNumber.setVisibility(View.GONE);
                view_suiNumber.setVisibility(View.GONE);
                view_ybzl.setVisibility(View.GONE);
                break;
            case R.id.calculation_btn_calculation:
                switch (flag) {
                    case 0:
                        if (StrUtil.isBlankIfStr(et_area.getText().toString())) {
                            Toast.makeText(this, "请输入样本面积", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_weater.getText().toString())) {
                            Toast.makeText(this, "请输入含水量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_zhuNumber.getText().toString())) {
                            Toast.makeText(this, "请输入样本点株数", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_suiNumber.getText().toString())) {
                            Toast.makeText(this, "请输入取样穗数", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_ybzl.getText().toString())) {
                            Toast.makeText(this, "请输入样本重量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double result = Double.parseDouble(et_ybzl.getText().toString().trim()) * Double.parseDouble(et_zhuNumber.getText().toString().trim()) * ((1 - Double.parseDouble(et_weater.getText().toString().trim())) / (1 - 0.14)) * 667 / (Double.parseDouble(et_suiNumber.getText().toString().trim()) * Double.parseDouble(et_area.getText().toString().trim()));
                        tv_result.setText("结果：" + result + "kg/亩");
                        break;
                    case 1:
                        if (StrUtil.isBlankIfStr(et_area.getText().toString())) {
                            Toast.makeText(this, "请输入样本面积", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_lizl.getText().toString())) {
                            Toast.makeText(this, "请输入取样粒重", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_zazhi.getText().toString())) {
                            Toast.makeText(this, "请输入杂志含量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double result1 = ((1 - Double.parseDouble(et_weater.getText().toString())) / (1 - 0.145)) * Double.parseDouble(et_lizl.getText().toString()) * (1 - Double.parseDouble(et_zazhi.getText().toString())) * 667 / Double.parseDouble(et_area.getText().toString());
                        tv_result.setText("结果：" + result1 + "kg/亩");
                        break;
                    case 2:
                        if (StrUtil.isBlankIfStr(et_area.getText().toString())) {
                            Toast.makeText(this, "请输入样本面积", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_lizl.getText().toString())) {
                            Toast.makeText(this, "请输入取样粒重", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StrUtil.isBlankIfStr(et_zazhi.getText().toString())) {
                            Toast.makeText(this, "请输入杂志含量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double result2 = ((1 - Double.parseDouble(et_weater.getText().toString())) / (1 - 0.145)) * Double.parseDouble(et_lizl.getText().toString()) * (1 - Double.parseDouble(et_zazhi.getText().toString())) * 667 / Double.parseDouble(et_area.getText().toString());
                        tv_result.setText("结果：" + result2 + "kg/亩");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
