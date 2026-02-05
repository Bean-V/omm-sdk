package com.oortcloud.contacts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.databinding.FragmentManagerSetDeptBinding;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.utils.DeptAndUserSetUtils;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.utils.JsonHelper;
import com.oortcloud.contacts.view.recycleview.WrapperRecycleView;
import com.oortcloud.contacts.view.recycleview.adapter.CommonRecycleAdapter;
import com.oortcloud.contacts.view.recycleview.adapter.viewholder.CommonViewHolder;
import com.oortcloud.contacts.view.recycleview.helper.SlitherItemTouchHelperCallback;
import com.oortcloud.contacts.view.recycleview.helper.SlitherItemTouchListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2022/4/11 11:48
 * @version： v1.0
 * @function： 通讯录-管理员-设置-部门(默认设置)-用户信息是否显示
 */
public class SettingDeptFragment extends BaseFragment {



    private CheckBox mCheckBox;
    private RecyclerView mDeptRv;
    private WrapperRecycleView mDeptUserRv;
    private FrameLayout mEmptyFl;
    private RelativeLayout mDragTipsRl;
    private ImageView mDragTipsCloseImg;
 // 替换为实际生成的 Binding 类名（如 ActivityMainBinding）

    private String mFragmentType;
    private HigherDepartmentAdapter mDeptAdapter;
    //上级部门
    private List<Department> mDeptList;
    //子级部门
    private List<Department> mChildDeptList;
    private Adapter mAdapter;
    //当前部门编码-默认99
    private String mCurrentDeptCode;
    //当前子部门总数
    private int mCurrentChildDeptCount;
    //是否需要提交排序
    private boolean IS_SORT_FLAG;
    //是否需要设置
    private boolean IS_SET_FLAG;


    private SlitherItemTouchHelperCallback mSlitherCallback;
    private com.oortcloud.contacts.databinding.FragmentManagerSetDeptBinding binding;

    @Override
    protected View getRootView() {

        binding = FragmentManagerSetDeptBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值（ID 需与布局文件对应）
        mCheckBox = binding.checkbox;        // 对应 android:id="@+id/checkbox"
        mDeptRv = binding.deptRv;            // 对应 android:id="@+id/dept_rv"
        mDeptUserRv = binding.deptUserRv;    // 对应 android:id="@+id/dept_user_rv"
        mEmptyFl = binding.emptyFl;          // 对应 android:id="@+id/empty_fl"
        mDragTipsRl = binding.dragTipsLl;     // 对应 android:id="@+id/drag_tips_ll"（下划线转驼峰）
        mDragTipsCloseImg = binding.dragTipsCloseImg;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_manager_set_dept;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null) {
            mFragmentType = bundle.getString(Constants.TYPE);
        }

    }

    @Override
    protected void initView() {
        mDeptRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mDeptAdapter = new HigherDepartmentAdapter(mDeptList);
        mDeptRv.setAdapter(mDeptAdapter);

        mDeptUserRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Adapter(mContext, null, R.layout.item_depanduser_layout);
        mDeptUserRv.setAdapter(mAdapter);

        //设置滑动事件
        mSlitherCallback = new SlitherItemTouchHelperCallback(mContext, mAdapter);
        mSlitherCallback.setItemSlitherListener(new SlitherItemTouchListener() {
            /**
             * 排序
             */
            @Override
            public void move(int fromPosition, int toPosition) {
                //改变数据源
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mChildDeptList, i, i + 1);// 改变实际的数据集
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mChildDeptList, i, i - 1);// 改变实际的数据集
                    }
                }
                IS_SORT_FLAG = true;
            }

            /**
             * 排序完成
             */
            @Override
            public void stop() {
                //排序请求
                if (IS_SORT_FLAG) {
                    HttpResult.sortDept(JsonHelper.toDeptCodeArray(mChildDeptList), mCurrentDeptCode);
                    IS_SORT_FLAG = false;
                }

            }

        });

    }

    @Override
    protected void initData() {
        //默认顶级部门
        upData("99");
        notifyChanged(false);
    }

    @Override
    protected void initEvent() {
        mDragTipsRl.setVisibility(View.VISIBLE);
        mDragTipsCloseImg.setOnClickListener(v -> {
            mDragTipsRl.setVisibility(View.GONE);
        });
        mDeptAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                upData(((Department) adapter.getItem(position)).getOort_dcode());
            }
        });

        mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

            int size = DeptAndUserSetUtils.getDeptSize(mCurrentDeptCode);
            if (size == 0) {
                mCheckBox.setChecked(true);
                DeptAndUserSetUtils.addAll(mCurrentDeptCode, mChildDeptList);
            } else {
                DeptAndUserSetUtils.clearDept(mCurrentDeptCode);
                mCheckBox.setChecked(false);
            }
            mCheckBox.setButtonDrawable(R.drawable.user_setting_check_box);
            mAdapter.notifyDataSetChanged();
        });
    }

    private void upData(String deptCode) {
        mCurrentDeptCode = deptCode;
        HttpResult.getDeptList(mCurrentDeptCode);
    }
    @Override
    public void notifyChanged(boolean changed) {
        this.IS_SET_FLAG = changed;
        if (IS_SET_FLAG) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        mDeptUserRv.setItemDragSlither(mSlitherCallback, IS_SET_FLAG);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(EventMessage event) {
        if (event.getDataType().equals(mFragmentType)) {
            mChildDeptList = event.getList();

            if (mChildDeptList != null) {
                mCurrentChildDeptCount = mChildDeptList.size();
                if (mChildDeptList.size() > 0) {
                    mAdapter.refreshData(mChildDeptList);
                    mDeptUserRv.smoothScrollToPosition(0);
                    mEmptyFl.setVisibility(View.GONE);
                } else {
                    mEmptyFl.setVisibility(View.VISIBLE);
                }
            }

        } else if (mCurrentDeptCode.equals(event.getDataType())) {
            Department department = event.getDepartment();
            mDeptList = DeptUtils.splitDepartment(department.getOort_dpath(), department.getOort_dcodepath());
            mDeptAdapter.setNewData(mDeptList);
            mDeptAdapter.notifyDataSetChanged();
            mDeptRv.smoothScrollToPosition(mDeptList.size() - 1);
        }
    }

    public static SettingDeptFragment instantiate(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TYPE, type);
        SettingDeptFragment fragment = new SettingDeptFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    private class Adapter extends CommonRecycleAdapter<Department> {

        public Adapter(Context context, List<Department> sorts, int layoutId) {
            super(context, sorts, layoutId);
        }

        @Override
        protected void convert(CommonViewHolder holder, Department itemData, int position) {

            holder.setText(R.id.name, itemData.getOort_dname())
                    .setImageResource(R.id.dept_portrait_img, R.mipmap.icon_dept)
                    .setImageResource(R.id.right_img, R.mipmap.icon_sort);

            holder.getView(R.id.dept_portrait_img).setVisibility(View.VISIBLE);
            holder.getView(R.id.right_img).setVisibility(View.VISIBLE);

            CheckBox checkBox = holder.getView(R.id.checkbox);
            if (IS_SET_FLAG) {
                holder.itemView.setOnClickListener(null);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnCheckedChangeListener(null);
                if (DeptAndUserSetUtils.contains(mCurrentDeptCode, itemData)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                    if (isChecked) {
                        DeptAndUserSetUtils.add(mCurrentDeptCode, itemData);
                    } else {
                        DeptAndUserSetUtils.remove(mCurrentDeptCode, itemData);
                    }
                    int size = DeptAndUserSetUtils.getDeptSize(mCurrentDeptCode);

                    if (size > 0 && size < mCurrentChildDeptCount) {
                        mCheckBox.setButtonDrawable(R.mipmap.square_select_delete);
                    } else {
                        if (size == 0) {
                            mCheckBox.setButtonDrawable(R.mipmap.square_select_default);
                        } else if (size == mCurrentChildDeptCount) {
                            mCheckBox.setButtonDrawable(R.mipmap.square_select_focus);
                        }

                    }
                });
            } else {
                checkBox.setVisibility(View.GONE);
                mCheckBox.setButtonDrawable(R.drawable.user_setting_check_box);
                holder.itemView.setOnClickListener(v -> {
                    upData(itemData.getOort_dcode());
                });
            }
        }

    }
}
