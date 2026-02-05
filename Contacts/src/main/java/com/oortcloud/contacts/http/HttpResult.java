package com.oortcloud.contacts.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptUserConfig;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.bean.omm.AddAttentionResult;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.contacts.bean.omm.User;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.SortComparator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @ProjectName:
 * @FileName: HttpResult.java
 * @Function: 处理返回数据
 * @Author: zzj / @CreateDate: 20/03/12 16:00
 * @Version: 1.0
 */
public class HttpResult {
    /**
     * 获取部门详细信息
     *
     * @param depCode
     */
    public static void getDeptInfo(String depCode) {
        HttpRequestCenter.getDeptInfo(depCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>() {
                }.getType());
                if (result.isOk()) {
                    EventBus.getDefault().post(new EventMessage(depCode, result.getData().getDeptInfo()));
                }
            }

        });
    }

    /**
     * 获取部门与用户树
     *
     * @param depCode
     * @param showUser 是否返回用户 1:是 0:否 默认0
     * @param dataType 类型
     */
    public static void getDeptAndUserTree(String depCode, int showUser, String dataType) {
        getDeptInfo(depCode);
        HttpRequestCenter.post(depCode, showUser).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                getDeptInfo(depCode);
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                if (result.isOk()) {
                    List<Sort> sortList = new ArrayList<>();
                    Data data = result.getData();
                    if (data != null) {
                        List<UserInfo> userInfoData = data.getUser();
                        //添加部门人员
                        Collections.sort(userInfoData, new SortComparator());
                        sortList.addAll(userInfoData);
                        //添加子部门
                        List<Department> departments = data.getDept();
                        Collections.sort(departments, new SortComparator());
                        sortList.addAll(departments);
                    }

                    EventBus.getDefault().post(new EventMessage(dataType, sortList));


            }
        }

    });
}

    /**
     * 获取部门或部门人员
     *
     * @param depCode
     * @param showUser 是否返回用户 1:是 0:否 默认0
     * @param dataType 类型
     */
    public static void getPersonnel(String depCode, int showUser, String dataType) {

        HttpRequestCenter.post(depCode, showUser).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {
                }.getType());

                if (result.isOk()) {
                    List<Sort> sortList = new ArrayList<>();
                    Data data = result.getData();
                    if (data != null) {
                        List<UserInfo> userInfoData = data.getUser();

                        if (userInfoData != null && userInfoData.size() > 0) {
                            Collections.sort(userInfoData, new SortComparator());
                            sortList.addAll(userInfoData);
                        }

//                           List<Department> departments =  data.getDept();
//
//                           if (departments!= null && departments.size() > 0) {
//                               Collections.sort(departments, new SortComparator());
//                               sortList.addAll(departments);
//                               EventBus.getDefault().post(new MessageEvent(sortList  , 0 ,dataType));
//
//                           }else {
                        EventBus.getDefault().post(new EventMessage(1, dataType, sortList));
//                           }

                    }

                }
            }

        });
    }

    /**
     * 获取设置的部门列表 （返回带有隐藏部门）
     *
     * @param pDeptCode 上级部门编码
     * @return
     */
    public static void getDeptList(String pDeptCode) {
        //获取当前部门详情
        HttpResult.getDeptInfo(pDeptCode);
        ///获取部门数据
        HttpRequestCenter.getDeptList(pDeptCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>() {
                }.getType());
                if (result.isOk()) {
                    EventBus.getDefault().post(new EventMessage(Constants.DEPT, result.getData().getList()));
                }
            }

        });
    }

    /**
     * 获取设置的 部门 和部门信息 用户列表(返回带有隐藏用户)
     *
     * @param deptCode 当前部门编码
     * @param isWork   用户离职状态 1:在职;2:离职 0:所有(默认)
     * @return
     */
    public static void getUserList(String deptCode, int isWork) {
        //获取当前部门详情
        HttpResult.getDeptInfo(deptCode);
        //获取用户数据
        HttpRequestCenter.getUserList(deptCode, isWork).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                List<Sort> dataList = new ArrayList<>();
                Result<Data<UserInfo>> UserResult = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {
                }.getType());
                if (UserResult.isOk()) {
                    dataList.addAll(UserResult.getData().getList());
                    ///获取部门数据
                    HttpRequestCenter.getDeptList(deptCode).subscribe(new RxBusSubscriber<String>() {
                        @Override
                        protected void onEvent(String s) {
                            Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>() {
                            }.getType());
                            if (result.isOk()) {
                                dataList.addAll(result.getData().getList());
                                EventBus.getDefault().post(new EventMessage(UserResult.getData().getList().size(), Constants.USER,  dataList));
                            }
                        }

                    });


                }
            }

        });
    }
    /**
     * 部门排序
     * @param deptCode 排序后的部门编码
     * @param pDeptCode 上级部门编码
     * @return
     */
    public static void sortDept(List<String> deptCode, String pDeptCode) {
        ///获取部门数据
        HttpRequestCenter.sortDept(deptCode, pDeptCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                if (result.isOk()){

                }
            }

        });
    }
    public static void sortUser(List<String>  uuid, String deptCode) {
        ///获取部门数据
        HttpRequestCenter.sortUser(uuid, deptCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                if (result.isOk()){

                }
            }

        });
    }
    //获取默认配置
    public static void getDefaultConfig() {
        HttpRequestCenter.getDefaultConfig().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg" ,"getDefaultConfig------>" + s);
                Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                if (result.isOk()){

                }
            }

        });
    }
    //获取部门配置
    public static void getDeptConfig(String deptCode) {
        HttpRequestCenter.getDeptConfig(deptCode).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg" ,"getDeptConfig------>" + s);
                Result<Data<DeptUserConfig>> result = new Gson().fromJson(s, new TypeToken<Result<Data<DeptUserConfig>>>() {}.getType());
                if (result.isOk()){
                    EventBus.getDefault().post(new EventMessage(result.getData().getList().get(0)));
                }
            }

        });
    }
    //获取人员配置
    public static void getUserConfig(String deptCode, String uuid) {
        HttpRequestCenter.getUserConfig(deptCode, uuid).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg" ,"getUserConfig------>" + s);
                Result<Data<DeptUserConfig>> result = new Gson().fromJson(s, new TypeToken<Result<Data<DeptUserConfig>>>() {}.getType());
                if (result.isOk()){
                    EventBus.getDefault().post(new EventMessage(result.getData().getList().get(0)));
                }
            }

        });
    }

    public static void getUserInfo(String uuid) {
        HttpRequestCenter.getUserInfo(uuid).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {

                Result<Data<UserInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {}.getType());
                if (result.isOk()){

                    EventBus.getDefault().post(new EventMessage(result.getData().getUserInfo()));
                }
            }

        });
    }
    /**
     * 获取IM好友
     *
     * @return
     */
    public static void getIMFriendList(String userId) {
        HttpRequestCenter.getIMFriendList().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<List<AttentionUser>> result = new Gson().fromJson(s, new TypeToken<Result<List<AttentionUser>>>() {
                }.getType());
                if (result.getResultCode() == 1) {
                    List<AttentionUser> mAttentionList = result.getData();
                    if (mAttentionList != null) {
                        for (AttentionUser attentionUser : mAttentionList) {
                            if (attentionUser.getToUserId().equals(userId)) {
                                EventBus.getDefault().post(new EventMessage(attentionUser));
                                break;
                            }
                        }
                    }else{
                        EventBus.getDefault().post(new EventMessage(result.getResultMsg()));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                EventBus.getDefault().post(new EventMessage(e.getMessage()));
            }
        });

    }
    /**
     * 添加IM好友
     *
     * @param toUserId IM用户id
     * @return
     */
    public static void addIMFriend(String toUserId) {
        HttpRequestCenter.addIMFriend(toUserId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<AddAttentionResult> result = new Gson().fromJson(s, new TypeToken<Result<AddAttentionResult>>() {
                }.getType());
                if (result.getResultCode() == 1) {
                    getIMFriendList(toUserId);
                }else {
                    EventBus.getDefault().post(new EventMessage(result.getResultMsg()));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                EventBus.getDefault().post(new EventMessage(e.getMessage()));
            }
        });

//        HttpRequestCenter.addIMFriend(toUserId).subscribe(new RxBusSubscriber<String>() {
//            @Override
//            protected void onEvent(String s) {
//                Result<AddAttentionResult> result = new Gson().fromJson(s, new TypeToken<Result<AddAttentionResult>>() {
//                }.getType());
//                if (result.getResultCode() == 1) {
//                    getIMFriendList(toUserId);
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//        });
    }

    /**
     * IMUser资料
     *
     * @return
     */
    public static void getIMUser(String userId) {
        HttpRequestCenter.getIMUser(userId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<User> result = new Gson().fromJson(s, new TypeToken<Result<User>>() {
                }.getType());

                if (result.getResultCode() == 1) {
                    EventBus.getDefault().post(new EventMessage(result.getData()));
                }else {
                    EventBus.getDefault().post(new EventMessage(result.getResultMsg()));
                }
            }
        });
    }

    /**
     * 获取IM好友
     *
     * @return
     */
    public static void getIMFriendList(String userId ,UserClickListener listener ) {
        HttpRequestCenter.getIMFriendList().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<List<AttentionUser>> result = new Gson().fromJson(s, new TypeToken<Result<List<AttentionUser>>>() {
                }.getType());
                if (result.getResultCode() == 1) {
                    List<AttentionUser> mAttentionList = result.getData();
                    if (mAttentionList != null) {
                        for (AttentionUser attentionUser : mAttentionList) {
                            if (attentionUser.getToUserId().equals(userId)) {
                                listener.onCallBack(attentionUser);
                                break;
                            }
                        }
                    } else {
                        listener.onCallBack(null);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onCallBack(null);
            }
        });

    }
    /**
     * 添加IM好友
     *
     * @param toUserId IM用户id
     */
    public static void addIMFriend(String toUserId , UserClickListener listener) {
        HttpRequestCenter.addIMFriend(toUserId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<AddAttentionResult> result = new Gson().
                        fromJson(s, new TypeToken<Result<AddAttentionResult>>() {
                        }.getType());
                if (result.getResultCode() == 1 || result.getResultCode() == 100512) {
                    getIMFriendList(toUserId , listener);
                }
                else {
                    listener.onCallBack(null);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onCallBack(null);
            }
        });
    }

    public interface UserClickListener {
        void onCallBack(AttentionUser attentionUser);
    }

}
