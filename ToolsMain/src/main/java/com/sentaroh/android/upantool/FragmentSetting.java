package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

//import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.upantool.languagelib.LanguageType;
import com.sentaroh.android.upantool.languagelib.MultiLanguageUtil;
import com.sentaroh.android.upantool.sysTask.TastTool;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSetting extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int savedLanguageType = 0;
    private int selectedLanguage = 0;
    private boolean isSys;

    public FragmentSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSetting newInstance(String param1, String param2) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        TextView curlangeTv = v.findViewById(R.id.tv_current_lang);


//        String lang = Store.getLanguageLocal(getContext());
//
//        if(lang.contains("zh")) {
//            curlangeTv.setText("中文简体");
//        } if(lang.equals("")){
//            curlangeTv.setText("跟随系统");
//        }else if(lang.contains("en")){
//            curlangeTv.setText("English");
//        }else if(lang.contains("ja")) {
//            curlangeTv.setText("日本語");
//
//        }


        TextView versionTv = v.findViewById(R.id.tv_current_version);

        versionTv.setText(Activity_aboutUs.getVersionName(getContext()));


        savedLanguageType = MultiLanguageUtil.getInstance().getLanguageType(getContext());
        if (savedLanguageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            curlangeTv.setText("跟随系统");
        } else if (savedLanguageType == LanguageType.LANGUAGE_Japan) {
            curlangeTv.setText("日本語");
        } else if (savedLanguageType == LanguageType.LANGUAGE_EN) {
            curlangeTv.setText("English");
        } else if (savedLanguageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            curlangeTv.setText("中文简体");
        } else {
            curlangeTv.setText("中文简体");
        }


        v.findViewById(R.id.ll_language).setOnClickListener(new View.OnClickListener() {

            private PopupWindow mPop;
            private String language;

            @Override
            public void onClick(View view) {

                language = null;


                View popView = getLayoutInflater().inflate(R.layout.popview_layout, null);
                popView.findViewById(R.id.btn_sys).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        language = LanguageUtils.getCurrentLanguage();

                        selectedLanguage = LanguageType.LANGUAGE_FOLLOW_SYSTEM;
                        resetLanguage();
                        mPop.dismiss();

                    }
                });
                popView.findViewById(R.id.btn_ch).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        language = "zh_CN";

                        selectedLanguage = LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
                        resetLanguage();
                        mPop.dismiss();


                    }
                });
                popView.findViewById(R.id.btn_en).setOnClickListener(new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View v) {
                                                                             language = "en";

                                                                             selectedLanguage = LanguageType.LANGUAGE_EN;
                                                                             resetLanguage();
                                                                             mPop.dismiss();

                                                                         }
                                                                     }


                );


                popView.findViewById(R.id.btn_js).setOnClickListener(new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View v) {
                                                                             language = "ja";
                                                                             selectedLanguage = LanguageType.LANGUAGE_Japan;
                                                                             resetLanguage();
                                                                             mPop.dismiss();

                                                                         }
                                                                     }


                );


                mPop = new PopupWindow(popView, ViewTool.dp2px(getContext(), 110), ViewTool.dp2px(getContext(), 164));
                mPop.setOutsideTouchable(false);
                mPop.setFocusable(true);
                mPop.showAsDropDown(curlangeTv);
            }

            public void resetLanguage() {

//                Store.setLanguageLocal(getContext(), language);
//                Intent intent = new Intent("com.example.action");
//                intent.putExtra("msg", "EVENT_REFRESH_LANGUAGE");
//
//                getContext().sendBroadcast(intent);


                ProgressDialog pd = new ProgressDialog(getContext());
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(UsbHelper.getInstance().canCopyToU()){
                            UsbHelper.getInstance().toStopTrans(true);

                            while (!UsbHelper.getInstance().sysSuccess) {

                            }
                        }
                        MultiLanguageUtil.getInstance().updateLanguage(getActivity(),selectedLanguage);
                        TastTool.getInstance().stopSysToChangeFolderName();
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                pd.dismiss();

                                Intent intent = new Intent(getActivity(), ActivityMain_.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                    }
                }).start();




            }
        });
        v.findViewById(R.id.ll_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(),Activity_aboutUs.class);
                startActivity(in);
            }
        });

        Switch sh = v.findViewById(R.id.sh_sys);

        ImageView iv_sh_bg = v.findViewById(R.id.iv_sh_bg);


        TextView tv_see_detail = v.findViewById(R.id.tv_see_detail);

        isSys = TastTool.getInstance().isSysTask();


        if(isSys){
            sh.setChecked(true);

            iv_sh_bg.setImageResource(R.mipmap.ic_on);

            tv_see_detail.setVisibility(View.VISIBLE);
        }

        v.findViewById(R.id.tv_sh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSys = TastTool.getInstance().isSysTask();
                ViewTool.confirm_to_action(getContext(), null, isSys ? getString(R.string.comfirm_close) : getString(R.string.comfirm_open_tip), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(isSys){
                            TastTool.getInstance().closeSysTask();
                            sh.setChecked(false);
                            iv_sh_bg.setImageResource(R.mipmap.ic_off);

                            tv_see_detail.setVisibility(View.GONE);

                        }else{
                             {


                                XXPermissions.with(getContext())
                                        // 申请单个权限
                                        .permission(Permission.READ_CONTACTS)
                                        .permission(Permission.WRITE_CONTACTS)
                                        // 设置权限请求拦截器（局部设置）
                                        //.interceptor(new PermissionInterceptor())
                                        // 设置不触发错误检测机制（局部设置）
                                        .unchecked()
                                        .request(new OnPermissionCallback() {

                                            @Override
                                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                                if (!allGranted) {
                                                    //toast("获取部分权限成功，但部分权限未正常授予");
                                                    return;
                                                }
                                                sh.setChecked(true);
                                                iv_sh_bg.setImageResource(R.mipmap.ic_on);

                                                tv_see_detail.setVisibility(View.VISIBLE);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        TastTool.getInstance().openSysTask();


                                                    }
                                                }).start();

                                                return;
                                                //toast("获取录音和日历权限成功");
                                            }

                                            @Override
                                            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                                if (doNotAskAgain) {
                                                    //toast("被永久拒绝授权，请手动授予录音和日历权限");
                                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                                    XXPermissions.startPermissionActivity(getContext(), permissions);
                                                } else {

                                                }
                                            }
                                        });
                            }


                        }




                    }


                });
            }
        });

        v.findViewById(R.id.ll_syn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSys = TastTool.getInstance().isSysTask();
                if(!isSys){
                    return;
                }
                Activity_sys_statu.goSys(getContext());
            }
        });

        v.findViewById(R.id.ll_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityWeb.class);
                startActivity(intent);
            }
        });
        return v;
    }
}