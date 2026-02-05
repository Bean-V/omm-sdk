package com.oortcloud.contacts.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptUserConfig;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.ActivityUserShowSettingBinding;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.DeptAndUserSetUtils;
import com.oortcloud.contacts.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @filename:
 * @author: zzj/@date: 2022/4/9 09:48
 * @version： v1.0
 * @function： 管理员设置用户信息是否可见详情
 */
public class SettingUserInfoShowActivity extends BaseActivity{
    private ImageView mBack;
    private TextView mTitle;
    private TextView mRight;
    private TextView mDeptName;
    private CheckBox mPortraitCb;
    private ImageView mPortrait;
    private CheckBox mNameCb;
    private TextView mName;
    private CheckBox mIDCardCb;
    private TextView mIDCard;
    private CheckBox mSexCb;
    private TextView mSex;
    private CheckBox mLoginAccountNumberCb;
    private TextView mLoginAccountNumber;
    private CheckBox mPhoneNumberCb;
    private TextView mPhoneNumber;
    private CheckBox mUserCodeCb;
    private TextView mUserCode;
    private CheckBox mUserStatusCb;
    private TextView mUserStatus;
    private CheckBox mDeptCodeCb;
    private TextView mDeptCode;
    private CheckBox mAccountNumberTypeCb;
    private TextView mAccountNumberType;
    private CheckBox mUserTypeCb;
    private TextView mUserType;
    private CheckBox mUserEmailCb;
    private TextView mUserEmail;
    private CheckBox mHouseNumberCb;
    private TextView mHouseNumber;
    private CheckBox mOphCb;
    private TextView mOph;
    private CheckBox mPPhoneCb;
    private TextView mPPhone;
    private CheckBox mQuartersCb;
    private TextView mQuarters;
    private CheckBox mJobCb;
    private TextView mJob;
    private CheckBox mRemarksCb;
    private TextView mRemarks;
 // 替换为实际生成的 Binding 类（如 ActivityUserSettingBinding）
    private UserInfo mUserInfo;
    private DeptUserConfig mDeptUserConfig;
    private com.oortcloud.contacts.databinding.ActivityUserShowSettingBinding binding;

    @Override
    protected View getRootView() {
        binding = ActivityUserShowSettingBinding.inflate(getLayoutInflater());
        // 通过 ViewBinding 赋值（ID 需与布局文件对应，注意驼峰命名规则）
        mBack = binding.backImg;             // @+id/back_img → backImg
        mTitle = binding.tvTitle;            // @+id/tv_title → tvTitle
        mRight = binding.ivTitleRight;       // @+id/iv_title_right → ivTitleRight
        mDeptName = binding.deptName;        // @+id/dept_name → deptName

        // CheckBox 与 TextView 赋值
        mPortraitCb = binding.portraitCb;    // @+id/portrait_cb → portraitCb
        mPortrait = binding.portrait;        // @+id/portrait → portrait
        mNameCb = binding.nameCb;            // @+id/name_cb → nameCb
        mName = binding.name;                // @+id/name → name
        mIDCardCb = binding.idCardCb;        // @+id/id_card_cb → idCardCb（注意下划线转驼峰：id_card → idCard）
        mIDCard = binding.idCard;            // @+id/id_card → idCard
        mSexCb = binding.sexCb;              // @+id/sex_cb → sexCb
        mSex = binding.sex;                  // @+id/sex → sex

        mLoginAccountNumberCb = binding.loginAccountNumberCb; // @+id/login_account_number_cb → loginAccountNumberCb
        mLoginAccountNumber = binding.loginAccountNumber;     // @+id/login_account_number → loginAccountNumber
        mPhoneNumberCb = binding.phoneNumberCb;                // @+id/phone_number_cb → phoneNumberCb
        mPhoneNumber = binding.phoneNumber;                    // @+id/phone_number → phoneNumber
        mUserCodeCb = binding.userCodeCb;                      // @+id/user_code_cb → userCodeCb
        mUserCode = binding.userCode;                          // @+id/user_code → userCode

        mUserStatusCb = binding.userStatusCb;                  // @+id/user_status_cb → userStatusCb
        mUserStatus = binding.userStatus;                      // @+id/user_status → userStatus
        mDeptCodeCb = binding.deptCodeCb;                      // @+id/dept_code_cb → deptCodeCb
        mDeptCode = binding.deptCode;                          // @+id/dept_code → deptCode
        mAccountNumberTypeCb = binding.accountNumberTypeCb;    // @+id/account_number_type_cb → accountNumberTypeCb
        mAccountNumberType = binding.accountNumberType;        // @+id/account_number_type → accountNumberType

        mUserTypeCb = binding.userTypeCb;                      // @+id/user_type_cb → userTypeCb
        mUserType = binding.userType;                          // @+id/user_type → userType
        mUserEmailCb = binding.userEmailCb;                    // @+id/user_email_cb → userEmailCb
        mUserEmail = binding.userEmail;                        // @+id/user_email → userEmail
        mHouseNumberCb = binding.houseNumberCb;                // @+id/house_number_cb → houseNumberCb
        mHouseNumber = binding.houseNumber;                    // @+id/house_number → houseNumber

        mOphCb = binding.ophCb;                                // @+id/oph_cb → ophCb（注意 ID 是否正确，避免拼写错误）
        mOph = binding.oph;                                    // @+id/oph → oph
        mPPhoneCb = binding.privatePhoneCb;                    // @+id/private_phone_cb → privatePhoneCb（假设原 ID 为 private_phone_cb）
        mPPhone = binding.privatePhone;                        // @+id/private_phone → privatePhone
        mQuartersCb = binding.quartersCb;                      // @+id/quarters_cb → quartersCb
        mQuarters = binding.quarters;                          // @+id/quarters → quarters

        mJobCb = binding.jobCb;                                // @+id/job_cb → jobCb
        mJob = binding.job;                                    // @+id/job → job
        mRemarksCb = binding.remarksCb;                        // @+id/remarks_cb → remarksCb
        mRemarks = binding.remarks;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_show_setting;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mLoadingDialog.dismiss();

    }
    @Override
    protected void initData() {
        if (DeptAndUserSetUtils.getDeptSize() > 0 ){
            mTitle.setText(R.string.dept_setting);
            if (DeptAndUserSetUtils.getDeptSize() == 1){
                //单部门
                Department department =  DeptAndUserSetUtils.getDeptList().get(0);
                mDeptName.setText(department.getOort_dname());
                HttpResult.getDeptConfig(department.getOort_dcode());
            }else {
                mDeptName.setText(R.string.many_dept);
                HttpResult.getDeptConfig(DeptAndUserSetUtils.getDeptCode());

            }
            HttpResult.getUserInfo("");
        }else if (DeptAndUserSetUtils.getUserSize() > 0){
            mTitle.setText(R.string.personal_setting);
            if (DeptAndUserSetUtils.getUserSize() == 1){
                //单人
                UserInfo userInfo = DeptAndUserSetUtils.getUserList().get(0);
                HttpResult.getUserInfo(userInfo.getOort_uuid());
                HttpResult.getUserConfig(DeptAndUserSetUtils.getDeptCode(), userInfo.getOort_uuid());
            }else {
                mDeptName.setText(R.string.many_user);
                HttpResult.getUserInfo("");
                HttpResult.getDeptConfig(DeptAndUserSetUtils.getDeptCode());
            }
        }

    }

    @Override
    protected void initView() {
        if (mUserInfo != null){
            mName.setText(mUserInfo.getOort_name());

            String idCard = mUserInfo.getOort_idcard();
            if (TextUtils.isEmpty(idCard)) {
                mIDCard.setText(R.string.unknown);
            } else {
                mIDCard.setText(idCard);
            }
            //0未知，1男，2女，3其它
            int sexType = mUserInfo.getOort_sex();
            if (sexType == 0) {
                mSex.setText(R.string.unknown);
            } else if (sexType == 1) {
                mSex.setText(R.string.man);
            } else if (sexType == 2) {
                mSex.setText(R.string.woman);
            } else if (sexType == 3) {
                mSex.setText(R.string.other);
            }
            String loginId = mUserInfo.getOort_loginid();
            if (TextUtils.isEmpty(loginId)) {
                mLoginAccountNumber.setText(R.string.unknown);
            } else {
                mLoginAccountNumber.setText(loginId);
            }
            String phoneNumber = mUserInfo.getOort_phone();
            if (TextUtils.isEmpty(phoneNumber)) {
                mPhoneNumber.setText(R.string.unknown);
            } else {
                mPhoneNumber.setText(phoneNumber);
            }
            String userCode = mUserInfo.getOort_code();
            if (TextUtils.isEmpty(userCode)) {
                mUserCode.setText(R.string.unknown);
            } else {
                mUserCode.setText(userCode);
            }
            //0禁用,1正常...9删除
            int userStatus = mUserInfo.getOort_status();
            if (userStatus == 0) {
                mUserStatus.setText(R.string.forbidden);
            } else if (userStatus == 1) {
                mUserStatus.setText(R.string.normal);
            } else if (userStatus == 9) {
                mUserStatus.setText(R.string.delete);
            } else {
                mUserStatus.setText(R.string.unknown);
            }
            String deptCode = mUserInfo.getOort_depcode();
            if (TextUtils.isEmpty(deptCode)) {
                mDeptCode.setText(R.string.unknown);
            } else {
                mDeptCode.setText(deptCode);
            }
            //1正式账号,2其它账号...9测试账号
            int userType = mUserInfo.getOort_usertype();
            if (userType == 1) {
                mUserType.setText(R.string.formal_account_number);
            } else if (userType == 2) {
                mUserType.setText(R.string.other_account_number);
            } else if (userType == 9) {
                mUserType.setText(R.string.test_account_number);
            } else {
                mUserType.setText(R.string.unknown);
            }

            String email = mUserInfo.getOort_email();
            if (TextUtils.isEmpty(email)) {
                mUserEmail.setText(R.string.unknown);
            } else {
                mUserEmail.setText(email);
            }
            String officeNumber = mUserInfo.getOort_office();
            if (TextUtils.isEmpty(officeNumber)) {
                mHouseNumber.setText(R.string.unknown);
            } else {
                mHouseNumber.setText(officeNumber);
            }
            String oph = mUserInfo.getOort_tel();
            if (TextUtils.isEmpty(oph)) {
                mOph.setText(R.string.unknown);
            } else {
                mOph.setText(oph);
            }
            String pPhone = mUserInfo.getOort_pphone();
            if (TextUtils.isEmpty(oph)) {
                mPPhone.setText(R.string.unknown);
            } else {
                mPPhone.setText(pPhone);
            }
            String quarters = mUserInfo.getOort_postname();
            if (TextUtils.isEmpty(quarters)) {
                mQuarters.setText(R.string.unknown);
            } else {
                mQuarters.setText(quarters);
            }
            String job = mUserInfo.getOort_jobname();
            if (TextUtils.isEmpty(job)) {
                mJob.setText(R.string.unknown);
            } else {
                mJob.setText(job);
            }
            mRemarks.setText(R.string.not);
        }
    }
    protected void initShow() {
        if (mDeptUserConfig != null){

            mPortraitCb.setChecked(isCheck(mDeptUserConfig.getOort_photo()));
            mNameCb.setChecked(isCheck(mDeptUserConfig.getOort_name()));
            mIDCardCb.setChecked(isCheck(mDeptUserConfig.getOort_idcard()));
            mSexCb.setChecked(isCheck(mDeptUserConfig.getOort_sex()));
            mLoginAccountNumberCb.setChecked(isCheck(mDeptUserConfig.getOort_loginid()));
            mPhoneNumberCb.setChecked(isCheck(mDeptUserConfig.getOort_phone()));
            mUserCodeCb.setChecked(isCheck(mDeptUserConfig.getOort_code()));
            //用户状态
//            mUserStatusCb.setChecked(Boolean.getBoolean(String.valueOf(mDeptUserConfig.)));
            //部门编码
//            mDeptCodeCb.setChecked(Boolean.getBoolean(String.valueOf(mDeptUserConfig.getOort_depcode())));
            //账号类型
//            mAccountNumberTypeCb.setChecked(Boolean.getBoolean(String.valueOf(mDeptUserConfig())));
            //用户类型
//            mUserTypeCb.setChecked(Boolean.getBoolean(String.valueOf(mDeptUserConfig.g)));
            mUserEmailCb.setChecked(isCheck(mDeptUserConfig.getOort_email()));
            mHouseNumberCb.setChecked(isCheck(mDeptUserConfig.getOort_office()));
            mOphCb.setChecked(isCheck(mDeptUserConfig.getOort_tel()));
            mPPhoneCb.setChecked(isCheck(mDeptUserConfig.getOort_pphone()));
            mQuartersCb.setChecked(isCheck(mDeptUserConfig.getOort_postname()));
            mRemarksCb.setChecked(isCheck(mDeptUserConfig.getOort_remark()));
        }
    }
    private boolean isCheck(int status){
        return status == 1 ? true: false;
    }
    @Override
    protected void initEvent() {
        mBack.setOnClickListener(v -> {
            finish();
        });
        mRight.setOnClickListener(v -> {
            if (DeptAndUserSetUtils.getDeptSize() > 0){
                HttpRequestCenter.storageDeptConfig( mDeptUserConfig.clone(), DeptAndUserSetUtils.getDeptGather()).subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Log.v("msg" ,"storageDeptConfig------>" +s);
                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                        if (result.isOk()){
                            ToastUtils.showBottom(R.string.save_successfully);
                        }
                    }

                });
            }
            if (DeptAndUserSetUtils.getUserSize() > 0){
                HttpRequestCenter.storageUserConfig(mDeptUserConfig, DeptAndUserSetUtils.getDeptCode(),  DeptAndUserSetUtils.getUserGather()).subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                        if (result.isOk()){
                            ToastUtils.showBottom(R.string.save_successfully);
                        }
                    }

                });
            }
        });
        mPortraitCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_photo(1);
            }else {
                mDeptUserConfig.setOort_photo(0);
            }
        });
        mNameCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_name(1);
            }else {
                mDeptUserConfig.setOort_name(0);
            }
        });
        mIDCardCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_idcard(1);
            }else {
                mDeptUserConfig.setOort_idcard(0);
            }
        });
        mSexCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_sex(1);
            }else {
                mDeptUserConfig.setOort_sex(0);
            }
        });
        mLoginAccountNumberCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_loginid(1);
            }else {
                mDeptUserConfig.setOort_loginid(0);
            }
        });
        mPhoneNumberCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_phone(1);
            }else {
                mDeptUserConfig.setOort_phone(0);
            }
        });
        mUserCodeCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_code(1);
            }else {
                mDeptUserConfig.setOort_code(0);
            }
        });
        mUserStatusCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
//                mDeptUserConfig.setOort_code(1);
            }else {
//                mDeptUserConfig.setOort_code(0);
            }
        });
        mDeptCodeCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
//                mDeptUserConfig.setOort_depcode(1);
            }else {
//                mDeptUserConfig.setOort_code(0);
            }
        });
        mAccountNumberTypeCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
//                mDeptUserConfig.setOort_depcode(1);
            }else {
//                mDeptUserConfig.setOort_code(0);
            }
        });
        mUserTypeCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
//                mDeptUserConfig.setOort_depcode(1);
            }else {
//                mDeptUserConfig.setOort_code(0);
            }
        });
        mUserEmailCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_email(1);
            }else {
                mDeptUserConfig.setOort_email(0);
            }
        });
        mHouseNumberCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_office(1);
            }else {
                mDeptUserConfig.setOort_office(0);
            }
        });
        mOphCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_tel(1);
            }else {
                mDeptUserConfig.setOort_tel(0);
            }
        });
        mPPhoneCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_pphone(1);
            }else {
                mDeptUserConfig.setOort_pphone(0);
            }
        });
        mQuartersCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_postname(1);
            }else {
                mDeptUserConfig.setOort_postname(0);
            }
        });
        mJobCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_jobname(1);
            }else {
                mDeptUserConfig.setOort_jobname(0);
            }
        });
        mRemarksCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mDeptUserConfig.setOort_remark(1);
            }else {
                mDeptUserConfig.setOort_remark(0);
            }
        });

    }

    @Override
    public void finish() {
        DeptAndUserSetUtils.clear();
        super.finish();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(EventMessage event) {
        if (event.getT() instanceof UserInfo){
            mUserInfo = (UserInfo) event.getT();
            initView();
        }
        else if (event.getT() instanceof DeptUserConfig){
            mDeptUserConfig = (DeptUserConfig) event.getT();
            initShow();
        }
    }
}
