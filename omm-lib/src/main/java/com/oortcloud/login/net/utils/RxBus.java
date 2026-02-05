package com.oortcloud.login.net.utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 *
 */
public class RxBus {

    private final Subject<Object> mBus;

    private RxBus() {
        // toSerialized method made bus thread safe
        mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    /**
     * 将数据添加到订阅
     * 这个地方是再添加订阅的地方。最好创建一个新的类用于数据的传递
     */
    public void post(@NonNull Object obj) {
        if (hasObservers()) {
            mBus.onNext(obj);
        }
    }

    /**
     * 这个是传递集合如果有需要的话你也可以进行更改
     */
    public void post(@NonNull List<Object> obj) {
        if (hasObservers()) {//判断当前是否已经添加订阅
            mBus.onNext(obj);
        }
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    /**
     * 注册，传递tClass的时候最好创建一个封装的类。这对数据的传递作用
     * 新更改仅仅抛出生成类和解析
     */
    public <T> Disposable register(Class<T> tClass, Consumer<T> consumer) {
        return toObservable(tClass).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    public <T> Disposable register(Class<T> tClass, Consumer<T> consumer,Consumer<Throwable> throwable) {
        return toObservable(tClass).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer,throwable);
    }

    public <T> void register(Class<T> tClass, Observer<T> observer) {
        toObservable(tClass).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public <T> void registerIO(Class<T> tClass, Observer<T> observer) {
        toObservable(tClass).subscribeOn(Schedulers.io()).subscribe(observer);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }

    public static class BusObserver<T> implements Observer<T>{

        private Disposable mDisposable;

        @Override
        public void onSubscribe(Disposable d) {
            mDisposable = d;
        }

        @Override
        public void onNext(T t) {
        }

        @Override
        public void onError(Throwable e) {
            mDisposable.dispose();
        }

        @Override
        public void onComplete() {
            mDisposable.dispose();
        }
    }

}
