package com.oortcloud.clouddisk.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.EventMessage;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.bean.Result;
import com.oortcloud.clouddisk.db.DBUtils;
import com.oortcloud.clouddisk.db.SharedPreferenceManager;
import com.oortcloud.clouddisk.http.bus.RxBusSubscriber;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.FilecrudUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2020/12/13 17:19
 * @version： v1.0
 * @function：
 */
public class HttpResult {

    /**
     * 创建文件夹
     *
     * @param
     * @return
     */
    public static void mkdir(String dir, String name) {
        HttpRequestCenter.mkdir(dir, name).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                }.getType());
                if (result.isOk()) {
                    ToastUtils.showContent("创建成功");
                    HttpResult.fileList(dir,  "", 1, 50, "");
                } else {
                    ToastUtils.showContent(result.getMsg());
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showContent(e.getMessage());
            }
        });
    }

    /**
     * 获取文件列表
     *
     * @param
     * @return
     */
    public static void fileList(String dir, String keyword, int pageNum, int pagesize, String sort) {
        String order =  SharedPreferenceManager.getInstance().getString("order" );

        HttpRequestCenter.fileList(dir, keyword, order, pageNum, pagesize, sort).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                Result<DirData<FileInfo>> result = new Gson().fromJson(s, new TypeToken<Result<DirData<FileInfo>>>() {
                }.getType());

                if (result.isOk()) {
                    DirData data = result.getData();
                    if (data != null) {
                        //空判断处理
                        if (data.getList() == null){

                            data.setList(new ArrayList());
                        }
                        DBUtils.getUpAndDown(data.getList() ,dir);
                        EventBus.getDefault().post(new EventMessage<FileInfo>(dir, data));
                    }

                } else {
                    if (result.getCode() == 4004) {
                        ToastUtils.showContent("无效的token");
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                if (e.toString().contains("java.net.SocketTimeoutException")) {
//                    ToastUtils.showContent("服务端连接异常");
                    ToastUtils.showContent(e.getMessage());
                }
            }
        });
    }

    /**
     * 文件/文件夹改名
     *
     * @param
     * @return
     */
    public static void newName(String dir, String name, String newName) {
        HttpRequestCenter.newName(dir, name, newName).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                }.getType());
                if (result.isOk()) {
                    ToastUtils.showContent("修改成功");
                    HttpResult.fileList(dir,  "", 1, 50, "");
                    FilecrudUtils.newName(dir, name, newName);

                } else {
                    ToastUtils.showContent("修改失败");
                }


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showContent(e.getMessage());
            }
        });
    }

    /**
     * 文件/文件夹移动
     *
     * @param
     * @return
     */

    public static void move(List<FileInfo> data, String newDir ,CallListener callListener) {
        if (data != null) {
            callCount = 0;
            for (FileInfo fileInfo : data) {
                callCount += 1;
                HttpRequestCenter.move(fileInfo.getName(), fileInfo.getDir(), newDir)
                        .subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                        }.getType());
                        if (result.isOk()) {
                            if (callCount == data.size()) {
                                ToastUtils.showContent("移动成功");
                                HttpResult.fileList(newDir,  "", 1, 50, "");
                                HttpResult.fileList(fileInfo.getDir(), "", 1, 50, "");
                                if (callListener != null){
                                    callListener.Listener(callCount);
                                }

                            }
                            FilecrudUtils.moveUpdate(fileInfo.getName(), fileInfo.getDir(), newDir);

                        } else {

                            ToastUtils.showContent(fileInfo.getName() + "移动失败");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showContent(e.getMessage());
                    }
                });
            }

        }

    }

    /**
     * 文件/文件夹复制
     *
     * @param
     * @return
     */
    public static void copy(List<FileInfo> data, String newDir ,CallListener callListener) {
        if (data != null) {
            callCount = 0 ;
            for (FileInfo fileInfo : data) {
                callCount += 1;
                HttpRequestCenter.copy(fileInfo.getName(), fileInfo.getDir(), newDir).subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                        }.getType());
                        if (result.isOk()) {
                            if (callCount == data.size()) {
                                ToastUtils.showContent("复制成功");
                                HttpResult.fileList(newDir,  "", 1, 50, "");
                                if (callListener != null){
                                    callListener.Listener(callCount);
                                }
                            }
                            FilecrudUtils.copyUpdate(fileInfo.getName(), fileInfo.getDir(), newDir);

                        } else {

                            ToastUtils.showContent(fileInfo.getName() + "复制失败");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showContent(e.getMessage());
                    }
                });
            }
        }

    }
    //记录调用次数
    static int  callCount = 0;
    /**
     * 文件/文件夹删除接口
     *
     * @param
     * @return
     */
    public static void delete(boolean confirm, List<FileInfo> data) {
        if (data != null) {
            callCount = 0;
            for (FileInfo fileInfo : data) {
                callCount++;
                HttpRequestCenter.delete(confirm, fileInfo.getDir(),
                        fileInfo.getName()).subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String s) {
                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                        }.getType());
                        if (result.isOk()) {
                            if (callCount == data.size()) {
                                ToastUtils.showContent("删除成功");
                                HttpResult.fileList(fileInfo.getDir(), "", 1, 50, "");

                            }

                            FilecrudUtils.delete(fileInfo.getDir(), fileInfo.getName());

                        } else {
                            ToastUtils.showContent("删除失败");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showContent(e.getMessage());
                    }
                });
            }


        }
    }

}
