/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package org.apache.cordova;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigXmlParser {
    private static final String TAG = "ConfigXmlParser"; // 常量优化，避免重复赋值

    private String launchUrl = "file:///android_asset/www/index.html";
    private CordovaPreferences prefs = new CordovaPreferences();
    private ArrayList<PluginEntry> pluginEntries = new ArrayList<PluginEntry>(20);

    public CordovaPreferences getPreferences() {
        return prefs;
    }

    public ArrayList<PluginEntry> getPluginEntries() {
        return pluginEntries;
    }

    public String getLaunchUrl() {
        return launchUrl;
    }

    public void parse(Context action) {
        // 1. 优先从当前 Activity 包名查找 config.xml
        int id = action.getResources().getIdentifier("config", "xml", action.getClass().getPackage().getName());
        if (id == 0) {
            // 2. 若未找到，从 AndroidManifest 声明的包名查找
            id = action.getResources().getIdentifier("config", "xml", action.getPackageName());
            if (id == 0) {
                Log.e(TAG, "res/xml/config.xml 文件缺失！无法解析插件配置");
                return;
            }
        }
        Log.d(TAG, "找到 config.xml 资源，资源ID: " + id);
        try {
            // 解析 config.xml 并打印结果
            parse(action.getResources().getXml(id));
            printParsedPlugins(); // 解析后打印插件列表，验证 Whitelist 是否存在
        } catch (Exception e) {
            Log.e(TAG, "解析 config.xml 失败", e);
        }
    }

    // 解析状态变量
    private boolean insideFeature = false;
    private String service = "";
    private String pluginClass = "";
    private String paramType = "";
    private boolean onload = false;

    public void parse(XmlPullParser xml) {
        int eventType = -1;
        try {
            eventType = xml.getEventType();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "获取 XML 初始事件类型失败", e);
            return;
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag(xml);
            } else if (eventType == XmlPullParser.END_TAG) {
                handleEndTag(xml);
            }
            // 读取下一个 XML 事件
            try {
                eventType = xml.next();
            } catch (XmlPullParserException e) {
                Log.e(TAG, "读取 XML 事件失败（PullParserException）", e);
                break;
            } catch (IOException e) {
                Log.e(TAG, "读取 XML 事件失败（IOException）", e);
                break;
            }
        }
    }

    /**
     * 处理 XML 开始标签（核心修复：通过索引获取属性，避免命名空间问题）
     */
    public void handleStartTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if (strNode == null) {
            Log.w(TAG, "遇到空的 XML 标签，跳过处理");
            return;
        }

        switch (strNode) {
            case "feature":
                // 开始解析插件配置：重置上一个插件的状态，避免数据污染
                resetFeatureState();
                insideFeature = true;
                // 通过索引获取 <feature> 标签的 "name" 属性（插件唯一标识）
                int featureNameIndex = getAttributeIndex(xml, "name");
                if (featureNameIndex != -1) {
                    service = xml.getAttributeValue(featureNameIndex);
                    Log.d(TAG, "=== 开始解析插件: " + service + " ===");
                } else {
                    Log.w(TAG, "发现无 'name' 属性的 <feature> 标签，跳过该插件解析");
                    insideFeature = false; // 标记为非解析中，避免后续param误处理
                }
                break;

            case "param":
                if (insideFeature) {
                    // 1. 先获取 "name" 和 "value" 属性的索引（核心必填属性）
                    int paramNameIndex = getAttributeIndex(xml, "name");
                    int paramValueIndex = getAttributeIndex(xml, "value");
                    if (paramNameIndex == -1) {
                        Log.w(TAG, "插件 <param> 标签缺少 'name' 属性（插件名：" + service + "），跳过");
                        break;
                    }
                    if (paramValueIndex == -1) {
                        Log.w(TAG, "插件 <param> 标签缺少 'value' 属性（插件名：" + service + "），跳过");
                        break;
                    }

                    // 2. 安全获取属性值，避免空指针
                    paramType = xml.getAttributeValue(paramNameIndex);
                    String paramValue = xml.getAttributeValue(paramValueIndex);
                    if (paramType == null || paramType.trim().isEmpty()) {
                        Log.w(TAG, "插件 <param> 标签的 'name' 属性值为空（插件名：" + service + "），跳过");
                        break;
                    }
                    if (paramValue == null) {
                        paramValue = ""; // 空值统一处理，避免后续equals报错
                    }

                    // 3. 打印详细解析日志，方便调试
                    Log.d(TAG, "解析插件参数（插件名：" + service + "）：name=" + paramType + ", value=" + paramValue);

                    // 4. 根据参数类型处理（与拆分后的 config.xml 完全匹配）
                    switch (paramType) {
                        case "service":
                            // 兼容旧版插件：用param的value覆盖feature的name作为插件标识
                            service = paramValue;
                            Log.d(TAG, "兼容旧版插件：更新 service 为 " + service);
                            break;
                        case "package":
                        case "android-package":
                            // 解析插件的Android类路径（核心参数）
                            pluginClass = paramValue;
                            Log.d(TAG, "插件 " + service + " 类路径确认：" + pluginClass);
                            break;
                        case "onload":
                            // 解析是否自动加载（与独立 <param name="onload" value="true"/> 匹配）
                            onload = "true".equalsIgnoreCase(paramValue); // 忽略大小写，增强兼容性
                            Log.d(TAG, "插件 " + service + " 自动加载配置：" + (onload ? "启用（启动时初始化）" : "禁用（按需初始化）"));
                            break;
                        default:
                            // 忽略其他未定义的param类型（如自定义参数），避免干扰核心逻辑
                            Log.d(TAG, "插件 " + service + " 存在未处理的参数类型：" + paramType + "，跳过");
                            break;
                    }
                }
                break;

            case "preference":
                // 解析偏好设置：增加空值容错
                int prefNameIndex = getAttributeIndex(xml, "name");
                int prefValueIndex = getAttributeIndex(xml, "value");
                if (prefNameIndex != -1 && prefValueIndex != -1) {
                    String name = xml.getAttributeValue(prefNameIndex);
                    String value = xml.getAttributeValue(prefValueIndex);
                    if (name != null && value != null) {
                        name = name.toLowerCase(Locale.ENGLISH);
                        prefs.set(name, value);
                        Log.d(TAG, "解析偏好设置: [" + name + "] = [" + value + "]");
                    } else {
                        Log.w(TAG, "偏好设置 <preference> 标签的 'name' 或 'value' 为空，跳过");
                    }
                } else {
                    Log.w(TAG, "偏好设置 <preference> 标签缺少 'name' 或 'value' 属性，跳过");
                }
                break;

            case "content":
                // 解析应用入口URL：增加空值判断
                int contentSrcIndex = getAttributeIndex(xml, "src");
                if (contentSrcIndex != -1) {
                    String src = xml.getAttributeValue(contentSrcIndex);
                    if (src != null && !src.trim().isEmpty()) {
                        setStartUrl(src);
                        Log.d(TAG, "应用入口 URL 解析完成：" + launchUrl);
                    } else {
                        Log.w(TAG, "<content> 标签的 'src' 属性为空，使用默认入口 URL: " + launchUrl);
                    }
                } else {
                    Log.w(TAG, "<content> 标签缺少 'src' 属性，使用默认入口 URL: " + launchUrl);
                }
                break;

            default:
                // 忽略其他 Cordova 非核心标签（如 <name>、<description> 等，由其他逻辑处理）
                Log.v(TAG, "忽略非核心标签：" + strNode + "（无需解析）");
                break;
        }
    }

    // 新增：重置插件解析状态的工具方法（避免跨插件数据污染）


    /**
     * 处理 XML 结束标签
     */
    public void handleEndTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if ("feature".equals(strNode) && insideFeature) {
            // 插件标签解析结束，添加到插件列表
            if (!service.isEmpty() && !pluginClass.isEmpty()) {
                PluginEntry entry = new PluginEntry(service, pluginClass, onload);
                pluginEntries.add(entry);
                Log.d(TAG, "插件解析完成，添加到列表: " + service + "（类路径: " + pluginClass + "）");
            } else {
                Log.w(TAG, "插件解析不完整，跳过添加（service: " + service + ", class: " + pluginClass + "）");
            }
            // 重置插件解析状态
            resetFeatureState();
        }
    }

    /**
     * 重置插件解析状态（避免下一个插件受影响）
     */
    private void resetFeatureState() {
        insideFeature = false;
        service = "";
        pluginClass = "";
        paramType = "";
        onload = false;
    }

    /**
     * 通过属性名获取 XML 标签的属性索引（核心修复：避免依赖命名空间方法）
     * @param xml XmlPullParser 实例
     * @param attrName 要查找的属性名
     * @return 属性索引（-1 表示未找到）
     */
    private int getAttributeIndex(XmlPullParser xml, String attrName) {
        if (xml == null || attrName == null) {
            return -1;
        }
        try {
            int attrCount = xml.getAttributeCount();
            for (int i = 0; i < attrCount; i++) {
                String currentAttrName = xml.getAttributeName(i);
                if (attrName.equals(currentAttrName)) {
                    return i;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取属性索引失败（attrName: " + attrName + "）", e);
        }
        return -1;
    }

    /**
     * 设置应用入口 URL（原有逻辑不变）
     */
    private void setStartUrl(String src) {
        Pattern schemeRegex = Pattern.compile("^[a-z-]+://");
        Matcher matcher = schemeRegex.matcher(src);
        if (matcher.find()) {
            launchUrl = src;
        } else {
            if (src.charAt(0) == '/') {
                src = src.substring(1);
            }
            launchUrl = "file:///android_asset/www/" + src;
        }
    }

    /**
     * 打印解析到的所有插件（用于验证 Whitelist 是否被正确加载）
     */
    private void printParsedPlugins() {
        Log.d(TAG, "==================== 解析到的插件列表 ====================");
        Log.d(TAG, "总插件数量: " + pluginEntries.size());
        for (int i = 0; i < pluginEntries.size(); i++) {
            PluginEntry entry = pluginEntries.get(i);
            Log.d(TAG, String.format("[%d] 插件名: %s | 类路径: %s | 自动加载: %b",
                    i + 1, entry.service, entry.pluginClass, entry.onload));
        }
        Log.d(TAG, "========================================================");
    }
}