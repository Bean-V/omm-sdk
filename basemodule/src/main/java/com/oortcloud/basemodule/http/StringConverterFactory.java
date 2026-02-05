package com.oortcloud.basemodule.http;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhang-zhi/@date: 2020/1/15 16:41
 */
public class StringConverterFactory extends Converter.Factory {

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    @Override
    public Converter<?, String> stringConverter(@NonNull Type type, @NonNull Annotation[] annotation, @NonNull Retrofit retrofit) {
        return super.stringConverter(type, annotation, retrofit);

    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type, @NonNull Annotation[] annotation, @NonNull Retrofit retrofit) {
        return new ConfigurationServiceConverter();
    }

    static final class ConfigurationServiceConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }
}