package com.example.lml.easyphoto.addressSelect;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lml.easyphoto.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wepon on 2017/12/4.
 * 自定义仿京东地址选择器
 */

public class AddressPickerView extends RelativeLayout {
    // recyclerView 选中Item 的颜色
    private int defaultSelectedColor = Color.parseColor("#2f79c4");
    // recyclerView 未选中Item 的颜色
    private int defaultUnSelectedColor = Color.parseColor("#262626");
    private Context mContext;
    private int defaultTabCount = 5; //tab 的数量
    private TabLayout mTabLayout; // tabLayout
    private RecyclerView mRvList; // 显示数据的RecyclerView
    private String defaultProvince = "省"; //显示在上面tab中的省份
    private String defaultCity = "市"; //显示在上面tab中的城市
    private String defaultDistrict = "县"; //显示在上面tab中的县
    private String defaultTown = "乡"; //显示在上面tab中的乡
    private String defaultVillage = "村"; //显示在上面tab中的村

    private List<AddressSelectBean> mRvData; // 用来在recyclerview显示的数据
    private AddressAdapter mAdapter;   // recyclerview 的 adapter

    private List<AddressSelectBean> mSelectProvice; //选中 省份 bean
    private List<AddressSelectBean> mSelectCity;//选中 城市  bean
    private List<AddressSelectBean> mSelectDistrict;//选中 县  bean
    private List<AddressSelectBean> mSelectTown;//选中 乡  bean
    private List<AddressSelectBean> mSelectVillage;//选中 村  bean
    private int mSelectProvicePosition = 0; //选中 省份 位置
    private int mSelectCityPosition = 0;//选中 城市  位置
    private int mSelectDistrictPosition = 0;//选中 区县  位置
    private int mSelectTownPosition = 0;//选中 乡  位置
    private int mSelectVillagePosition = 0;//选中 村  位置

    private OnAddressPickerSureListener mOnAddressPickerSureListener;
    //    private OnAddressPickerChangeListener mOnAddressPickerChangeListener;
    private OnAddressPickerItemListener mOnAddressPickerItemListener;

    public AddressPickerView(Context context) {
        super(context);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        mRvData = new ArrayList<>();
        // UI
        View rootView = inflate(mContext, R.layout.address_picker_view, this);
        // 确定
        // tablayout初始化
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tlTabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultProvince));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultCity));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultDistrict));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultTown));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultVillage));

        mTabLayout.addOnTabSelectedListener(tabSelectedListener);

        // recyclerview adapter的绑定
        mRvList = (RecyclerView) rootView.findViewById(R.id.rvList);
        mRvList.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new AddressAdapter();
        mRvList.setAdapter(mAdapter);
    }

    public void setData(int tabPosition, String tabName, List<AddressSelectBean> mRvDatas, int xzqyPosition) {
        int a = tabPosition;
        switch (tabPosition) {
            case 0:
                mSelectProvice = mRvDatas; //选中 省份 bean

                defaultProvince = "省";
                defaultCity = "市";
                defaultDistrict = "县";
                defaultTown = "乡";
                defaultVillage = "村";
                // 清空后面4个的数据
                mTabLayout.getTabAt(0).setText(defaultProvince);
                mTabLayout.getTabAt(1).setText(defaultCity);
                mTabLayout.getTabAt(2).setText(defaultDistrict);
                mTabLayout.getTabAt(3).setText(defaultTown);
                mTabLayout.getTabAt(4).setText(defaultVillage);
                // 跳到下一个选择
                mTabLayout.getTabAt(0).select();
//                mSelectProvicePosition = tabPosition;
                break;
            case 1:
                mSelectCity = mRvDatas;//选中 城市  bean
                defaultProvince = tabName;
                defaultCity = "市";
                defaultDistrict = "县";
                defaultTown = "乡";
                defaultVillage = "村";
                mTabLayout.getTabAt(0).setText(tabName);
                mTabLayout.getTabAt(1).setText(defaultCity);
                mTabLayout.getTabAt(2).setText(defaultDistrict);
                mTabLayout.getTabAt(3).setText(defaultTown);
                mTabLayout.getTabAt(4).setText(defaultVillage);
                // 跳到下一个选择
                mTabLayout.getTabAt(1).select();
                if (mSelectProvicePosition != xzqyPosition) {
                    mSelectCityPosition = 0;
                    mSelectDistrictPosition = 0;
                    mSelectTownPosition = 0;
                    mSelectVillagePosition = 0;
                }
                mSelectProvicePosition = xzqyPosition;
                break;
            case 2:
                mSelectDistrict = mRvDatas;//选中 县  bean
                defaultCity = tabName;
                defaultDistrict = "县";
                defaultTown = "乡";
                defaultVillage = "村";
                mTabLayout.getTabAt(0).setText(defaultProvince);
                mTabLayout.getTabAt(1).setText(tabName);
                mTabLayout.getTabAt(2).setText(defaultDistrict);
                mTabLayout.getTabAt(3).setText(defaultTown);
                mTabLayout.getTabAt(4).setText(defaultVillage);
                // 跳到下一个选择
                mTabLayout.getTabAt(2).select();

                if (mSelectCityPosition != xzqyPosition) {
                    mSelectDistrictPosition = 0;
                    mSelectTownPosition = 0;
                    mSelectVillagePosition = 0;
                }
                mSelectCityPosition = xzqyPosition;
                break;
            case 3:
                mSelectTown = mRvDatas;//选中 乡  bean
                defaultDistrict = tabName;
                defaultTown = "乡";
                defaultVillage = "村";
                mTabLayout.getTabAt(0).setText(defaultProvince);
                mTabLayout.getTabAt(1).setText(defaultCity);
                mTabLayout.getTabAt(2).setText(tabName);
                mTabLayout.getTabAt(3).setText(defaultTown);
                mTabLayout.getTabAt(4).setText(defaultVillage);
                // 跳到下一个选择
                mTabLayout.getTabAt(3).select();
                if (mSelectDistrictPosition != xzqyPosition) {
                    mSelectTownPosition = 0;
                    mSelectVillagePosition = 0;
                }
                mSelectDistrictPosition = xzqyPosition;
                break;
            case 4:
                mSelectVillage = mRvDatas;//选中 村  bean
                defaultTown = tabName;
                defaultVillage = "村";
                mTabLayout.getTabAt(0).setText(defaultProvince);
                mTabLayout.getTabAt(1).setText(defaultCity);
                mTabLayout.getTabAt(2).setText(defaultDistrict);
                mTabLayout.getTabAt(3).setText(tabName);
                mTabLayout.getTabAt(4).setText(defaultVillage);
                // 跳到下一个选择
                mTabLayout.getTabAt(4).select();
                if (mSelectTownPosition != xzqyPosition) {
                    mSelectVillagePosition = 0;
                }
                mSelectTownPosition = xzqyPosition;
                break;
        }
        mRvData.clear();
        mRvData.addAll(mRvDatas);
        mAdapter.notifyDataSetChanged();
    }

    public void lastOnClick(String tabName, int xzqyPosition) {
        defaultVillage = tabName;
        mTabLayout.getTabAt(0).setText(defaultProvince);
        mTabLayout.getTabAt(1).setText(defaultCity);
        mTabLayout.getTabAt(2).setText(defaultDistrict);
        mTabLayout.getTabAt(3).setText(defaultTown);
        mTabLayout.getTabAt(4).setText(tabName);
        // 跳到下一个选择
        mSelectVillagePosition = xzqyPosition;
        mAdapter.notifyDataSetChanged();
        mOnAddressPickerSureListener.onSureClick(
                mSelectProvice.get(mSelectProvicePosition).getAreaCode(),
                mSelectCity.get(mSelectCityPosition).getAreaCode(),
                mSelectDistrict.get(mSelectDistrictPosition).getAreaCode(),
                mSelectTown.get(mSelectTownPosition).getAreaCode(),
                mSelectVillage.get(mSelectVillagePosition).getAreaCode(),
                mSelectProvice.get(mSelectProvicePosition).getAreaName(),
                mSelectCity.get(mSelectCityPosition).getAreaName(),
                mSelectDistrict.get(mSelectDistrictPosition).getAreaName(),
                mSelectTown.get(mSelectTownPosition).getAreaName(),
                mSelectVillage.get(mSelectVillagePosition).getAreaName()
        );
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * TabLayout 切换事件
     */
    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mRvData.clear();
            switch (tab.getPosition()) {
                case 0:
                    mRvData.addAll(mSelectProvice);
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    if (mSelectProvicePosition != -1) {
                        mRvList.smoothScrollToPosition(mSelectProvicePosition);
                    }
                    break;
                case 1:
                    // 点到城市的时候要判断有没有选择省份
                    if (mSelectCity != null) {
                        mRvData.addAll(mSelectCity);
                        mAdapter.notifyDataSetChanged();
                        // 滚动到这个位置
                        mRvList.smoothScrollToPosition(mSelectCityPosition);
                    } else {
                        Toast.makeText(mContext, "请您先选择省", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectCityPosition);
                    break;
                case 2:
                    // 点到城市的时候要判断有没有选择省份
                    if (mSelectDistrict != null) {
                        mRvData.addAll(mSelectDistrict);
                        mAdapter.notifyDataSetChanged();
                        // 滚动到这个位置
                        mRvList.smoothScrollToPosition(mSelectDistrictPosition);
                    } else {
                        Toast.makeText(mContext, "请您先选择市", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectDistrictPosition);
                    break;
                case 3:
                    // 点到城市的时候要判断有没有选择省份
                    if (mSelectTown != null) {
                        mRvData.addAll(mSelectTown);
                        mAdapter.notifyDataSetChanged();
                        // 滚动到这个位置
                        mRvList.smoothScrollToPosition(mSelectTownPosition);
                    } else {
                        Toast.makeText(mContext, "请您先选择县", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectTownPosition);
                    break;
                case 4:
                    // 点到城市的时候要判断有没有选择省份
                    if (mSelectVillage != null) {
                        mRvData.addAll(mSelectVillage);
                        mAdapter.notifyDataSetChanged();
                        // 滚动到这个位置
                        mRvList.smoothScrollToPosition(mSelectVillagePosition);
                    } else {
                        Toast.makeText(mContext, "请您先选择乡", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    // 滚动到这个位置
                    mRvList.smoothScrollToPosition(mSelectVillagePosition);
                    break;
            }

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };


    /**
     * 下面显示数据的adapter
     */
    class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_address_text, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int tabSelectPosition = mTabLayout.getSelectedTabPosition();
            holder.mTitle.setText(mRvData.get(position).getAreaName());
            holder.mTitle.setTextColor(defaultUnSelectedColor);
            // 设置选中效果的颜色
            switch (tabSelectPosition) {
                case 0:
                    if (mRvData.get(position) != null &&
                            mSelectProvice != null &&
                            position == mSelectProvicePosition) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 1:
                    if (mRvData.get(position) != null &&
                            mSelectCity != null &&
                            position == mSelectCityPosition) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 2:
                    if (mRvData.get(position) != null &&
                            mSelectDistrict != null &&
                            position == mSelectDistrictPosition) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 3:
                    if (mRvData.get(position) != null &&
                            mSelectDistrict != null &&
                            position == mSelectTownPosition) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
                case 4:
                    if (mRvData.get(position) != null &&
                            mSelectDistrict != null &&
                            position == mSelectVillagePosition) {
                        holder.mTitle.setTextColor(defaultSelectedColor);
                    }
                    break;
            }
            // 设置点击之后的事件
            holder.mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddressPickerItemListener.onSureClick(tabSelectPosition, position);
                    // 点击 分类别
                    /*switch (tabSelectPosition) {
                        case 0:
                            mSelectProvice = mRvData.get(position);
                            // 清空后面两个的数据
                            mSelectCity = null;
                            mSelectDistrict = null;
                            mSelectCityPosition = 0;
                            mSelectDistrictPosition = 0;
                            mTabLayout.getTabAt(1).setText(defaultCity);
                            mTabLayout.getTabAt(2).setText(defaultDistrict);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(0).setText(mSelectProvice.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(1).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            mSelectProvicePosition = position;
                            break;
                        case 1:
                            mSelectCity = mRvData.get(position);
                            // 清空后面一个的数据
                            mSelectDistrict = null;
                            mSelectDistrictPosition = 0;
                            mTabLayout.getTabAt(2).setText(defaultDistrict);
                            // 设置这个对应的标题
                            mTabLayout.getTabAt(1).setText(mSelectCity.getN());
                            // 跳到下一个选择
                            mTabLayout.getTabAt(2).select();
                            // 灰掉确定按钮
                            mTvSure.setTextColor(defaultSureUnClickColor);
                            mSelectCityPosition = position;
                            break;
                        case 2:
                            mSelectDistrict = mRvData.get(position);
                            // 没了，选完了，这个时候可以点确定了
                            mTabLayout.getTabAt(2).setText(mSelectDistrict.getN());
                            notifyDataSetChanged();
                            // 确定按钮变亮
                            mTvSure.setTextColor(defaultSureCanClickColor);
                            mSelectDistrictPosition = position;
                            break;
                    }*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRvData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTitle;

            ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.itemTvTitle);
            }

        }
    }


    /**
     * 点确定回调这个接口
     */
    public interface OnAddressPickerSureListener {
        void onSureClick(String provincecode, String citycode, String zipcode,
                         String sidecode, String villcode,
                         String provinceName, String cityName, String townName,
                         String countryName, String villageName);
    }

    public void setOnAddressPickerSure(OnAddressPickerSureListener listener) {
        this.mOnAddressPickerSureListener = listener;
    }

    /**
     * 点Tab选项回调这个接口
     */
   /* public interface OnAddressPickerChangeListener {
        void onSureClick(int position);
    }*/

//    public void setOnAddressPickerChange(OnAddressPickerChangeListener listener) {
//        this.mOnAddressPickerChangeListener = listener;
//    }

    /**
     * 点行政区域Item回调这个接口
     */
    public interface OnAddressPickerItemListener {
        void onSureClick(int tabPosition, int itemPosition);
    }

    public void setOnAddressPickerItem(OnAddressPickerItemListener listener) {
        this.mOnAddressPickerItemListener = listener;
    }

}
