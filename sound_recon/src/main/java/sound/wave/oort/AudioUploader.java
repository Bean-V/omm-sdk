package sound.wave.oort;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.OperLogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class AudioUploader {
    private   String UPLOAD_URL = "http://oort.oortcloudsmart.com:21310/oort/oortcloud-ai/workflows/v1/uploadFile";
    private   String RECOGNIZE_URL = "http://oort.oortcloudsmart.com:21310/oort/oortcloud-ai/workflows/v1/audioToText";
    private   String ACCESS_TOKEN = "123456";

    private final OkHttpClient client;

    public AudioUploader() {


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                // 使用Log.e输出日志，可以根据需要改为其他日志级别
                OperLogUtil.msg("lclog:" + message);
            }
        });

        // 设置日志级别
        // BASIC: 仅记录请求方法、URL、响应状态码和执行时间
        // HEADERS: 记录基本信息和请求/响应头
        // BODY: 记录完整的请求和响应体（包括请求参数和响应数据）
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new SensitiveDataInterceptor())
                .build();
        UPLOAD_URL = Constant.BASE_3CLASSURL + "oort/oortcloud-ai/workflows/v1/uploadFile";
        RECOGNIZE_URL = Constant.BASE_3CLASSURL + "oort/oortcloud-ai/workflows/v1/audioToText";
    }
    // 自定义拦截器，用于处理敏感信息
    private static class SensitiveDataInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            // 可以在这里对请求进行处理，例如替换敏感信息
            Request modifiedRequest = originalRequest;

            // 执行请求并获取响应
            Response response = chain.proceed(modifiedRequest);

            // 可以在这里对响应进行处理
            return response;
        }
    }

    // 上传文件并返回fileId
    public String uploadFile(String filePath) throws IOException {
        File file = new File(filePath);

        // 构建Multipart请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("accessToken", ACCESS_TOKEN)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.get("audio/wav")))
                .addFormDataPart("ContentType", "audio/wav")
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        // 执行请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("上传失败: " + response);

            // 使用FastJSON解析响应
            String jsonData = response.body().string();
            return parseFileIdFromJson(jsonData);
        }
    }

    // 使用FastJSON解析JSON响应获取fileId
    private String parseFileIdFromJson(String jsonData) {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonData);
            if (jsonObject.getIntValue("code") == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                return data.getString("id");
            } else {
                System.err.println("上传失败: " + jsonObject.getString("msg"));
                return null;
            }
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
            return null;
        }
    }

    // 使用fileId调用识别API
    public String recognizeAudio(String fileId) throws IOException {
        // 构建请求体
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("fileId", fileId);
            requestBody.put("accessToken", "123");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // 构建请求
        Request request = new Request.Builder()
                .url(RECOGNIZE_URL)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .post(RequestBody.create(requestBody.toString(), mediaType))
                .build();

        // 执行请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("识别失败: " + response);


            RecognizeResult result = JSON.parseObject(response.body().string(), RecognizeResult.class);
            // 返回识别结果
            return result.getData().getData().getOutputs().getText();
        }
    }

//    public static void main(String[] args) {
//        AudioUploader uploader = new AudioUploader();
//        String filePath = "path/to/your/audio.wav"; // 替换为实际文件路径
//
//        try {
//            // 上传文件
//            String fileId = uploader.uploadFile(filePath);
//            System.out.println("文件上传成功，fileId: " + fileId);
//
//            if (fileId != null) {
//                // 调用识别API
//                String result = uploader.recognizeAudio(fileId);
//                System.out.println("识别结果: " + result);
//            }
//        } catch (IOException e) {
//            System.err.println("操作失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}




