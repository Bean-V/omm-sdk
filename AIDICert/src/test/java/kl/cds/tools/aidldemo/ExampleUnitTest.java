package kl.cds.tools.aidldemo;

import android.util.Log;

import org.junit.Test;

import java.io.IOException;

import kl.cds.utils.SvsHelper;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    public String test()  throws Exception {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("text1", "text1")
                .addFormDataPart("text2","text2")
                .addFormDataPart("text3", "text3")
                .build();
        Request request = new Request.Builder()
                .url("http://weitong.aiwuye.net/mobile/report_repaire")
                .post(requestBody)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()){
                throw new Exception();
            }else{
                Log.d("sd",response.body().toString());
                return response.body().toString();
            }

        } catch (IOException e) {
            Log.d("sd","upload IOException ",e);
        }
        return null;
    }
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        test();
    }
}