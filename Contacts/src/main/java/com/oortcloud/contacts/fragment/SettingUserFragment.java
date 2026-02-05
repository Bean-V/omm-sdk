package com.oortcloud.contacts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.FragmentManagerSetUserBinding;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.utils.DeptAndUserSetUtils;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.utils.ImageLoader;
import com.oortcloud.contacts.utils.JsonHelper;
import com.oortcloud.contacts.view.recycleview.WrapperRecycleView;
import com.oortcloud.contacts.view.recycleview.adapter.CommonRecycleAdapter;
import com.oortcloud.contacts.view.recycleview.adapter.viewholder.CommonViewHolder;
import com.oortcloud.contacts.view.recycleview.helper.SlitherItemTouchHelperCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2022/4/11 11:48
 * @version： v1.0
 * @function： 通讯录-管理员-设置-个人-用户信息是否显示
 */
public class SettingUserFragment extends BaseFragment {
    private CheckBox mCheckBox;
    private RecyclerView mSuperDeptRv;
    private WrapperRecycleView mDeptUserRv;
    private FrameLayout mEmptyFl;
// 替换为实际生成的 Binding 类（如 ActivityDeptBinding）

    private String mFragmentType;
    private HigherDepartmentAdapter mDeptAdapter;
    //上级部门
    private List<Department> mDeptList;
    //子级部门和人员
    private List<Sort> mChildDeptAndUserList;

    private Adapter mAdapter;
    //当前部门编码
    private String mCurrentDeptCode;
    //用户人数
    private int mCurrentDeptUserCount;
    //是否需要设置
    private boolean IS_SET_FLAG;
    private ItemTouchHelper.Callback mCallback;
    private List<UserInfo> mChildUserList;
    private com.oortcloud.contacts.databinding.FragmentManagerSetUserBinding binding;

    @Override
    protected View getRootView() {
        binding = FragmentManagerSetUserBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值（ID 需与布局文件对应）
        mCheckBox = binding.checkbox;        // 对应 android:id="@+id/checkbox"
        mSuperDeptRv = binding.superDeptRv;  // 对应 android:id="@+id/super_dept_rv"
        mDeptUserRv = binding.userRv;        // 对应 android:id="@+id/user_rv"
        mEmptyFl = binding.emptyFl;
        return binding.getRoot();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_manager_set_user;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null) {
            mFragmentType = bundle.getString(Constants.TYPE);
        }
        mCallback = new SlitherItemTouchHelperCallback(mContext, mAdapter) {
            //判断是否需要提交排序
            private boolean IS_SORT_FLAG;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                // 拖动
                int dragFlags = 0;
                if (mChildDeptAndUserList.get(position) instanceof UserInfo) {
                    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        // GridView 样式四个方向都可以
                        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.LEFT |
                                ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
                    } else {
                        // ListView 样式不支持左右，只支持上下
                        dragFlags = ItemTouchHelper.UP |
                                ItemTouchHelper.DOWN;
                    }
                }
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // 获取原来的位置
                int fromPosition = viewHolder.getAdapterPosition();
                // 得到目标的位置
                int toPosition = target.getAdapterPosition();
                //目标位置超过用户人数，改到用户最后
                if (toPosition >= mCurrentDeptUserCount){
                    toPosition = mCurrentDeptUserCount - 1;
                }
                //改变数据源
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mChildDeptAndUserList, i, i + 1);// 改变实际的数据集
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mChildDeptAndUserList, i, i - 1);// 改变实际的数据集
                    }
                }
                //更新item
                mAdapter.notifyItemMoved(fromPosition, toPosition);

                IS_SORT_FLAG = true;
                return false;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //排序请求
                if (IS_SORT_FLAG) {
                    HttpResult.sortUser(JsonHelper.toUUIDArray(mChildDeptAndUserList), mCurrentDeptCode);
                    IS_SORT_FLAG = false;
                }
            }
        };
    }

    @Override
    protected void initView() {
        mSuperDeptRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mDeptAdapter = new HigherDepartmentAdapter(mDeptList);
        mSuperDeptRv.setAdapter(mDeptAdapter);

        mDeptUserRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Adapter(mContext, null, R.layout.item_depanduser_layout);
        mDeptUserRv.setAdapter(mAdapter);

        mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

            int size = DeptAndUserSetUtils.getUserSize(mCurrentDeptCode);
            if (size == 0) {
                mCheckBox.setChecked(true);
                DeptAndUserSetUtils.addUserAll(mCurrentDeptCode,mChildUserList);
            }
            else {
                DeptAndUserSetUtils.clearUser(mCurrentDeptCode);
                mCheckBox.setChecked(false);
            }
            mCheckBox.setButtonDrawable(R.drawable.user_setting_check_box);
            mAdapter.notifyDataSetChanged();
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
        mDeptAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                upData(((Department) adapter.getItem(position)).getOort_dcode());
            }
        });
    }

    private void upData(String deptCode) {
        mCurrentDeptCode = deptCode;
        HttpResult.getUserList(mCurrentDeptCode, 0);
    }

    @Override
    public void notifyChanged(boolean changed) {
        this.IS_SET_FLAG = changed;
        if (IS_SET_FLAG) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        //设置滑动事件
        mDeptUserRv.setItemDragSlither(mCallback, IS_SET_FLAG);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(EventMessage event) {
        if (event.getDataType().equals(mFragmentType)) {
            mChildDeptAndUserList = event.getList();
            if (mChildDeptAndUserList != null && mChildDeptAndUserList.size() > 0) {
                mCurrentDeptUserCount = event.getType();
                mAdapter.refreshData(mChildDeptAndUserList);
                mDeptUserRv.smoothScrollToPosition(0);
                mEmptyFl.setVisibility(View.GONE);
                mChildUserList = JsonHelper.toUserArray(mChildDeptAndUserList);
            } else {
                mEmptyFl.setVisibility(View.VISIBLE);

            }
        } else if (mCurrentDeptCode.equals(event.getDataType())) {

            Department department = event.getDepartment();
            mDeptList = DeptUtils.splitDepartment(department.getOort_dpath(), department.getOort_dcodepath());
            mDeptAdapter.setNewData(mDeptList);
            mDeptAdapter.notifyDataSetChanged();
            mSuperDeptRv.smoothScrollToPosition(mDeptList.size() - 1);
        }

    }

    public static SettingUserFragment instantiate(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TYPE, type);
        SettingUserFragment fragment = new SettingUserFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private class Adapter extends CommonRecycleAdapter<Sort> {
        private boolean is_checkBox;
        public Adapter(Context context, List<Sort> sorts, int layoutId) {
            super(context, sorts, layoutId);
        }

        @Override
        protected void convert(CommonViewHolder holder, Sort itemData, int position) {
            CheckBox checkBox = holder.getView(R.id.checkbox);
            ImageView mDeptPortraitImg = holder.getView((R.id.dept_portrait_img));
            ImageView mUserPortraitImg = holder.getView((R.id.user_portrait_img));
            ImageView mRightImg = holder.getView((R.id.right_img));
            TextView name = holder.getView((R.id.name));
            TextView jobTv = holder.getView(R.id.job_tv);
            if (itemData instanceof UserInfo) {
                UserInfo userInfo = (UserInfo) itemData;
                ImageLoader.loaderImage(mUserPortraitImg, userInfo);

                name.setText(userInfo.getOort_name());

                if (TextUtils.isEmpty(userInfo.getOort_jobname())) {
                    jobTv.setVisibility(View.GONE);
                } else {
                    jobTv.setText(userInfo.getOort_jobname());
                    jobTv.setVisibility(View.VISIBLE);
                }
                mDeptPortraitImg.setVisibility(View.GONE);
                mUserPortraitImg.setVisibility(View.VISIBLE);

                if (IS_SET_FLAG) {
                    checkBox.setButtonDrawable(R.drawable.user_setting_check_box);
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setOnCheckedChangeListener(null);
                    if (DeptAndUserSetUtils.contains(mCurrentDeptCode, userInfo)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }

                    checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

                        if (isChecked) {
                                DeptAndUserSetUtils.add(mCurrentDeptCode, userInfo);
                        } else {
                                DeptAndUserSetUtils.remove(mCurrentDeptCode, userInfo);

                        }
                        int size = DeptAndUserSetUtils.getUserSize(mCurrentDeptCode);
                        if (size == 0) {
                            mCheckBox.setButtonDrawable(R.mipmap.square_select_default);
                        } else if (size == mCurrentDeptUserCount) {
                            mCheckBox.setButtonDrawable(R.mipmap.square_select_focus);
                        }else {
                            mCheckBox.setButtonDrawable(R.mipmap.square_select_delete);
                        }
                    });
                } else {
                    checkBox.setVisibility(View.GONE);
                    mCheckBox.setButtonDrawable(R.drawable.user_setting_check_box);
                }
                holder.itemView.setOnClickListener(null);

            } else if (itemData instanceof Department) {
                Department department = (Department) itemData;

                mDeptPortraitImg.setImageResource(R.mipmap.icon_dept);

                name.setText(department.getOort_dname());

                mDeptPortraitImg.setVisibility(View.VISIBLE);
                mUserPortraitImg.setVisibility(View.GONE);
                jobTv.setVisibility(View.GONE);

                if (IS_SET_FLAG) {
                    checkBox.setButtonDrawable(R.mipmap.square_no_select);
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setOnCheckedChangeListener(null);
                    holder.itemView.setOnClickListener(null);
                } else {
                    checkBox.setVisibility(View.GONE);
                    mCheckBox.setButtonDrawable(R.drawable.user_setting_check_box);
                    holder.itemView.setOnClickListener(v -> {
                        upData(department.getOort_dcode());
                    });
                }

            }
            mRightImg.setImageResource(R.mipmap.icon_sort);
            mRightImg.setVisibility(View.VISIBLE);

        }
    }


}
