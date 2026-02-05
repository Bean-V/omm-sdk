package com.oort.weichat.ui.lccontact;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.AttentionUser;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.data.PageListResult;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PersonPickActivity extends BaseActivity {

    private RecyclerView select_bottom_rv;
    private Adapter_contacts_seleted adc;
    private TextView countDesLab;
    private ImageButton mDelBtn;
    private OrgFragment orgFragment;

    public static PickFinish pickFinish;
    public static PickFinish_v2 pickFinish_v2;
    private EditText editText;
    private Fragment_contacts_list cl;
    ArrayList searchRes = new ArrayList();
    ArrayList mSelectUsers = new ArrayList();
    private CYFragment cYFragment;
    private LabFragment labFragment;
    private LatestFragment latestFragment;
    private Fragment_contacts_list attentionFragment;
    private ArrayList<UserInfo> attentionDataList;
    private EditText searchEditText;

    public interface PickFinish {
        void finish(List imids,String names);
    }


    public interface PickFinish_v2 {
        void finish(List imids,List userIds,List names,List headerUrls,List extrs);
    }

    public class PickType {
        private String mTitle;

        public String getmTitle() {
            return mTitle;
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public int getmIcon() {
            return mIcon;
        }

        public void setmIcon(int mIcon) {
            this.mIcon = mIcon;
        }

        public int getsIcon() {
            return sIcon;
        }

        public void setsIcon(int sIcon) {
            this.sIcon = sIcon;
        }

        private int mIcon;
        private int sIcon;

        public PickType (String title,int icon,int s_icon){
            mTitle = title;
            mIcon = icon;
            sIcon = s_icon;
        }
    }

    public void setmExists(List mExists) {
        this.mExists = mExists;
    }

    private List mExists = new ArrayList();

    private List mSelects = new ArrayList();
    private List mWillDelSelects = new ArrayList();

    private List<String> AllSelectTypes = new ArrayList<String>();

    private List<String> canSelectTypes = new ArrayList<String>();//1最近2组织3常用4标签

    private DoneClickListener mDoneClickListener;
    public interface DoneClickListener {
        void doneClick(List datas,BaseActivity act);
    }
    public void  setOnItemClickListener(DoneClickListener listener){
        this.mDoneClickListener = listener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_pick);

        initActionBar();


        String users = getIntent().getStringExtra("selectUser");

        if(users != null){

            ArrayList list = (ArrayList) JSON.parseArray(users,UserInfo.class);
          // mSelectUsers.addAll(list);
           mSelects.addAll(list);
        }
        latestFragment = new LatestFragment();
        latestFragment.setOnItemClickListener(new LatestFragment.ItemClickListener() {

            @Override
            public void updateDatas(List datas) {
                mSelects = datas;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");
            }
        });


        attentionFragment = new Fragment_contacts_list();

        attentionFragment.setOnItemClickListener(new Fragment_contacts_list.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Sort s = (Sort) attentionDataList.get(position);
                UserInfo user = (UserInfo) s;
                UserInfo u = null;
                for(int i = 0;i< mSelects.size();i++){
                    UserInfo info = (UserInfo) mSelects.get(i);
                    if(info.getOort_uuid().equals(user.getOort_uuid())) {
                        u = info;
                        break;
                    }
                }
                if(u == null) {
                    mSelects.add(user);
                }else{
                    mSelects.remove(u);
                }
                attentionFragment.setSlist(mSelects);

//                mSelects = mSelectUsers;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");

                syscStatus();

            }

        });

        // cl.setMlist(mSortList);
        //attentionFragment.setSlist(mSelects);

        orgFragment = new OrgFragment();
        orgFragment.setOnItemClickListener(new OrgFragment.ItemClickListener() {
            @Override
            public void updateDatas(List datas) {
                mSelects = datas;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");

            }
        });
        cYFragment = new CYFragment();
        cYFragment.setOnItemClickListener(new CYFragment.ItemClickListener() {
            @Override
            public void updateDatas(List datas) {
                mSelects = datas;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");
            }
        });
        labFragment = new LabFragment();
        labFragment.setOnItemClickListener(new LabFragment.ItemClickListener() {
            @Override
            public void updateDatas(List datas) {
                mSelects = datas;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");
            }
        });

        cl = new Fragment_contacts_list();

        cl.setOnItemClickListener(new Fragment_contacts_list.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Sort s = (Sort) searchRes.get(position);
                UserInfo user = (UserInfo) s;
                UserInfo u = null;
                for(int i = 0;i< mSelects.size();i++){
                    UserInfo info = (UserInfo) mSelects.get(i);
                    if(info.getOort_uuid().equals(user.getOort_uuid())) {
                        u = info;
                        break;
                    }
                }
                if(u == null) {
                    mSelects.add(user);
                }else{
                    mSelects.remove(u);
                }
                cl.setSlist(mSelects);

                //mSelects = mSelectUsers;
                adc.refreshData(mSelects);

                countDesLab.setText(getString(R.string.select_person) + "(" + mSelects.size() + getString(R.string.person_str) +")");

                findViewById(R.id.ll_search_container).setVisibility(View.GONE);
//                InputMethodManager imm = (InputMethodManager) getSystemService(PersonPickActivity.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

//                editText.setFocusableInTouchMode(false);

                syscStatus();

            }

        });

        ArrayList flist = new ArrayList(0);
        flist.add(attentionFragment);
        flist.add(latestFragment);
        flist.add(orgFragment);
        flist.add(cYFragment);
        flist.add(labFragment);
        flist.add(cl);





        canSelectTypes = getTypeData();
        ListView labList = findViewById(R.id.list_Lab);
        PersonTypeAdapter labd = new PersonTypeAdapter(this,canSelectTypes);

        labList.setAdapter(labd);

        labList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    labd.selectIndex(i);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                   // String[] datas = (String[]) p.get(i);
                   // psd.refresh(Arrays.asList(datas));
                Fragment fragment = (Fragment) flist.get(i);
                if(i == 0){
                   attentionFragment.setSlist(mSelects);

                }else {

                    LCBaseFragment fragmentb = (LCBaseFragment) flist.get(i);

                    fragmentb.setmSelectUsers(mSelects);
                }

                if(getSupportFragmentManager().getFragments().size() == 0){

                    transaction.add(R.id.fragment_container, (Fragment) flist.get(i), String.valueOf(i));
                }else{
                    transaction.replace(R.id.fragment_container, (Fragment) flist.get(i));
                }


                transaction.commitNow();

            }
        });
        select_bottom_rv = findViewById(R.id.list_selected_ps);


        adc = new Adapter_contacts_seleted(this,mSelects);
        select_bottom_rv.setAdapter(adc);

        adc.setOnItemClickListener(new Adapter_contacts_seleted.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UserInfo user = (UserInfo) mSelects.get(position);
                UserInfo u = null;
                for(int i = 0;i< mWillDelSelects.size();i++){
                    UserInfo info = (UserInfo) mWillDelSelects.get(i);
                    if(info.getOort_uuid().equals(user.getOort_uuid())) {
                        u = info;
                        break;
                    }
                }
                if(u == null) {
                    mWillDelSelects.add(user);
                }else{
                    mWillDelSelects.remove(u);
                }
                adc.refreshSelectStatu(mWillDelSelects);

                mDelBtn.setImageResource(mWillDelSelects.size() > 0 ?
                 R.mipmap.icon_user_del_selected : R.mipmap.icon_user_del);


            }
        });

        countDesLab = findViewById(R.id.tv_count);

        mDelBtn = findViewById(R.id.btn_del_selectuser);
        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWillDelSelects.size() > 0){

                    com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(PersonPickActivity.this, getString(R.string.del_select_person_tip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           for (int j = 0;j<mWillDelSelects.size();j++) {
                               mSelects.remove(mWillDelSelects.get(j));
                           }
                           mWillDelSelects.clear();
                            adc.refreshSelectStatu(mWillDelSelects);

                            mDelBtn.setImageResource(mWillDelSelects.size() > 0 ?
                                    R.mipmap.icon_user_del_selected : R.mipmap.icon_user_del);

                            syscStatus();


                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                }
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // String[] datas = (String[]) p.get(i);
        // psd.refresh(Arrays.asList(datas));

        Fragment_contacts_list fragment = (Fragment_contacts_list) flist.get(0);

        fragment.setSlist(mSelects);


        transaction.add(R.id.fragment_container, (Fragment) flist.get(0), String.valueOf(0));

        transaction.commitNow();
        labd.selectIndex(0);
//        searchEditText = findViewById(R.id.edit_search);
//
//        searchEditText.setOnTouchListener((v, event) -> {
//            searchEditText.requestFocus();
//            return false;
//        });


        editText = findViewById(R.id.edit_search);
//在该Editview获得焦点的时候将“回车”键改为“搜索”
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
//不然回车【搜索】会换行
        editText.setSingleLine(true);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    findViewById(R.id.ll_search_container).setVisibility(View.GONE);
                    return;
                }
                if(s.length() < 2){
                    return;
                }
                if (s.charAt(s.length() - 1) == '\n') {
//                    findViewById(R.id.fragment_search_container).setVisibility(View.VISIBLE);
                    searchUsers(editText.getText().toString());



                    labd.selectIndex(100);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Fragment_contacts_list fragmentb = (Fragment_contacts_list) flist.get(5);
                    fragmentb.setSlist(mSelects);

                    if(getSupportFragmentManager().getFragments().size() == 0){

                        transaction.add(R.id.fragment_container, (Fragment) flist.get(5), String.valueOf(5));
                    }else{
                        transaction.replace(R.id.fragment_container, (Fragment) flist.get(5));
                    }
                    transaction.commitNow();
                   // Log.d("TEST RESPONSE", "Enter was pressed");
                }
            }
        });




        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                int x = i;
                if ((i == EditorInfo.IME_ACTION_UNSPECIFIED || i == EditorInfo.IME_ACTION_SEARCH) ) {
                    //点击搜索要做的操作

                    InputMethodManager imm = (InputMethodManager) getSystemService(PersonPickActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    if(StringUtil.isBlank(textView.getText().toString())){
                        return false;
                    }
                    //findViewById(R.id.ll_search_container).setVisibility(View.VISIBLE);
                    searchUsers(textView.getText().toString());
                    editText.clearFocus();
                    {
//                        findViewById(R.id.fragment_search_container).setVisibility(View.VISIBLE);
                        searchUsers(editText.getText().toString());



                        labd.selectIndex(100);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        Fragment_contacts_list fragmentb = (Fragment_contacts_list) flist.get(5);
                        fragmentb.setSlist(mSelects);

                        if(getSupportFragmentManager().getFragments().size() == 0){

                            transaction.add(R.id.fragment_container, (Fragment) flist.get(5), String.valueOf(5));
                        }else{
                            transaction.replace(R.id.fragment_container, (Fragment) flist.get(5));
                        }
                        transaction.commitNow();
                    }
                    return true;
                }
                return false;
            }


        });




       // cl.setMlist(mSortList);
        cl.setSlist(mSelects);

//        transaction.replace(R.id.fragment_search_container, cl, String.valueOf(33));
//
//        transaction.commitNow();


        getAttionlist();


        Intent in = getIntent();
        List<String> addtions = in.getStringArrayListExtra("addtions");
        if(addtions != null){

            for(String ids : addtions){

                UserInfo info = new UserInfo();
                info.setImuserid(ids);

                mSelects.add(info);
                HttpRequestCenter.getUserInfoByIMUserId(ids).subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {

                        Result<UserInfo> result = new Gson().fromJson(s, new TypeToken<Result<UserInfo>>() {
                        }.getType());
                        if (result.isOk()) {
                            UserInfo userInfo = result.getData();
                            if (userInfo != null) {
                                for (int i = 0; i < mSelects.size(); i++) {
                                    UserInfo userInfo01 = (UserInfo) mSelects.get(i);
                                    if (userInfo.getImuserid().equals(userInfo01.getImuserid())) {
                                        mSelects.set(i,userInfo);
                                        //mSelects = mSelectUsers;
                                        syscStatus();
                                    }
                                }
                            }
                        }
                    }

                });
            }
        }

    }

    private List getTypeData(){
        List list = new ArrayList();
        list.add(new PickType(getString(R.string.recent_contact),R.mipmap.icon_pp_latest_type,R.mipmap.icon_pp_latest_type_s));
        list.add(new PickType(getString(R.string.recent_use),R.mipmap.icon_pp_latest_type,R.mipmap.icon_pp_latest_type_s));
        list.add(new PickType(getString(R.string.org_struct),R.mipmap.icon_pp_org_type,R.mipmap.icon_pp_org_type_s));
        list.add(new PickType(getString(R.string.my_favorites),R.mipmap.icon_pp_cy_type,R.mipmap.icon_pp_cy_type_s));
        list.add(new PickType(getString(R.string.tags),R.mipmap.icon_pp_lab_type,R.mipmap.icon_pp_lab_type_s));
        return list;

    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.select_person));

        TextView rtvTitle = (TextView) findViewById(R.id.tv_title_right);
        rtvTitle.setText(getString(R.string.done));
        rtvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // mDoneClickListener.doneClick(mSelects,PersonPickActivity.this);
//                lab.setEdit(rtvTitle.getText().equals("编辑") ? true : false);
//                rtvTitle.setText(rtvTitle.getText().equals("编辑") ? "完成" :"编辑");
                List ids = new ArrayList();
                ArrayList<String> imids = new ArrayList();
                List userIds = new ArrayList();
                List headers = new ArrayList();
                List nameArr = new ArrayList();
                ArrayList<String> imUserIds = new ArrayList<>();
                String names = "";
                for (int i =0 ;i<mSelects.size();i++){

                    UserInfo info = (UserInfo) mSelects.get(i);
                    ids.add(info.getOort_uuid());
                    imids.add(info.getImuserid());

                    names = names.length() > 0 ? (names + "、" + info.getOort_name()) : info.getOort_name();
                    userIds.add(info.getOort_uuid());
                    nameArr.add(info.getOort_name());
                    headers.add(info.getOort_photo());

                }


                if(pickFinish != null){
                    pickFinish.finish(imids,names);
                }

                if(pickFinish_v2 != null){

                    pickFinish_v2.finish(imids,userIds,nameArr,headers,new ArrayList());
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();


                bundle.putStringArrayList("imUserIds", imids);
                bundle.putString("names", names);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    void syscStatus(){
        if(orgFragment != null) {
            orgFragment.setmSelectUsers(mSelects);
        }
        if(cYFragment != null) {
            cYFragment.setmSelectUsers(mSelects);
        }
        if(labFragment != null) {
            labFragment.setmSelectUsers(mSelects);
        }
        if(latestFragment != null) {
            latestFragment.setmSelectUsers(mSelects);
        }
        if(attentionFragment != null) {
            attentionFragment.setSlist(mSelects);
        }
    }

    private void searchUsers(String key) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", coreManager.getSelfStatus().accessToken);
        params.put("keyword", key);
        //params.put("oort_depcode", "");
        params.put("page", "1");
        params.put("pagesize", "100");

        HttpUtils.post().url(Constant.BASE_URL + Constant.USER_LIST)
                .params(params)
                .build()
                //.execute(new BaseCallback<PageListResult>(PageListResult.class) {
                .execute(new BaseCallback<String>(String.class) {

                    @Override
                    public void onResponse(ObjectResult<String> o) {



                        if (o.getCode() == 200 && o.getData() != null) {

                            PageListResult res = (new Gson()).fromJson(o.getData(),new TypeToken<PageListResult<UserInfo>>(){}.getType());
                            DialogHelper.dismissProgressDialog();
                            ArrayList labs = (ArrayList) res.getList();
                            searchRes.clear();
                            searchRes.addAll(labs);
                            cl.setMlist(searchRes);

                        }

                    }


                    @Override
                    public void onError(Call call, Exception e) {

                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(PersonPickActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getAttionlist() {
        // 这鬼库马上停止刷新会停不了，只能post一下，
        // 使用这个对话框阻止其他操作，以免主线程读写数据库被阻塞anr,
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_ATTENTION_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<AttentionUser>(AttentionUser.class) {
                    @Override
                    public void onResponse(ArrayResult<AttentionUser> result) {
                        if (result.getResultCode() == 1) {


                            attentionDataList = new ArrayList();
                            ArrayList<AttentionUser> datas = (ArrayList<AttentionUser>) result.getData();
                           for(AttentionUser user : datas){

                               if(user.getToUserType() == 0) {
                                   UserInfo info = new UserInfo();
                                   info.setImuserid(user.getToUserId());
                                   info.setOort_name(user.getToNickName());

                                   attentionDataList.add(info);
                                   HttpRequestCenter.getUserInfoByIMUserId(user.getToUserId()).subscribe(new RxBusSubscriber<String>() {
                                       @Override
                                       protected void onEvent(String s) {

                                           Result<UserInfo> result = new Gson().fromJson(s, new TypeToken<Result<UserInfo>>() {
                                           }.getType());
                                           if (result.isOk()) {
                                               UserInfo userInfo = result.getData();
                                               if (userInfo != null) {
                                                   for (int i = 0; i < attentionDataList.size(); i++) {
                                                       UserInfo userInfo01 = attentionDataList.get(i);
                                                       if (userInfo.getImuserid().equals(userInfo01.getImuserid())) {
                                                           attentionDataList.set(i,userInfo);
                                                           attentionFragment.setMlist(attentionDataList);
                                                       }
                                                   }
                                               }
                                           }
                                       }

                                   });
                               }
                            }


                           attentionFragment.setMlist(attentionDataList);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(PersonPickActivity.this);
                    }
                });
    }

}