package com.jun.framelibrary.skin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.VectorEnabledTintResources;
import androidx.core.view.ViewCompat;

import com.jun.baselibrary.base.activity.BaseActivity;
import com.jun.framelibrary.skin.attr.SkinAttr;
import com.jun.framelibrary.skin.attr.SkinView;
import com.jun.framelibrary.skin.callback.ISkinChangeListener;
import com.jun.framelibrary.skin.support.SkinAppCompatViewInflater;
import com.jun.framelibrary.skin.support.SkinSupport;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/27 21:12
 * Version 1.0
 * Description：插件换肤 Activity拦截Layout创建 皮肤管理
 */
@RestrictTo(LIBRARY)
public abstract class BaseSkinActivity extends BaseActivity implements LayoutInflater.Factory, ISkinChangeListener {
    private static final String TAG = "BaseSkinActivity";

    private SkinAppCompatViewInflater mAppCompatViewInflater;

    private final boolean IS_PRE_LOLLIPOP = Build.VERSION.SDK_INT < 21;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        LayoutInflaterCompat.setFactory2(layoutInflater, this);
//        super.onCreate(savedInstanceState);
//
//        //自定义View初始化换肤
//        changeSkin( SkinManager.getInstance().getSkinResource());
//    }
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }
    @Override
    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
       // 拦截到View的创建  获取View之后要去解析属性
        // 1. 创建View
        View view = createView(parent, name, context, attrs);

        //2、解析属性 src textColor  background  自定义属性
        if (view != null){
            //2.1解析属性
            List<SkinAttr> skinAttrs = SkinSupport.getSkinAttrs(context, attrs);
            SkinView skinView = new SkinView(view, skinAttrs);

            //3、统一管理
            managerSkinView(skinView);

            //4、检测是否换肤
            SkinManager.getInstance().checkChangeSKin(skinView);
        }
        return view;
    }

    /**
     * 统一管理SkinView
     * @param skinView
     */
    protected  void managerSkinView(SkinView skinView){
        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
        if(skinViews == null){
            skinViews = new ArrayList<>();
            SkinManager.getInstance().register(this, skinViews);
        }

        skinViews.add(skinView);
    }

    @SuppressLint("RestrictedApi")
    public View createView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {

        if (mAppCompatViewInflater == null) {
            //与主题相关的 viewInflaterClass
//            TypedArray a = this.obtainStyledAttributes(R.styleable.AppCompatTheme);
////            String viewInflaterClassName =
////                    a.getString(R.styleable.AppCompatTheme_viewInflaterClass);
//            String viewInflaterClassName = "com.jun.framelibrary.skin.support.theme.MaterialComponentsViewInflater";
//            if ((viewInflaterClassName == null)
//                    || AppCompatViewInflater.class.getName().equals(viewInflaterClassName)) {
//                // Either default class name or set explicitly to null. In both cases
//                // create the base inflater (no reflection)
//                mAppCompatViewInflater = new SkinAppCompatViewInflater();
//            } else {
//                try {
//                    Class<?> viewInflaterClass = Class.forName(viewInflaterClassName);
//                    mAppCompatViewInflater =
//                            (SkinAppCompatViewInflater) viewInflaterClass.getDeclaredConstructor()
//                                    .newInstance();
//                } catch (Throwable t) {
//                    Log.i(TAG, "Failed to instantiate custom view inflater "
//                            + viewInflaterClassName + ". Falling back to default.", t);
//                    mAppCompatViewInflater = new SkinAppCompatViewInflater();
//                }
//            }
            mAppCompatViewInflater = new SkinAppCompatViewInflater();
        }

        boolean inheritContext = false;
        if (IS_PRE_LOLLIPOP) {
            inheritContext = (attrs instanceof XmlPullParser)
                    // If we have a XmlPullParser, we can detect where we are in the layout
                    ? ((XmlPullParser) attrs).getDepth() > 1
                    // Otherwise we have to use the old heuristic
                    : shouldInheritContext((ViewParent) parent);
        }

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                IS_PRE_LOLLIPOP, /* Only read android:theme pre-L (L+ handles this anyway) */
                true, /* Read read app:theme as a fallback at all times for legacy reasons */
                VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
        );
    }

    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        final View windowDecor = getWindow().getDecorView();
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (parent == windowDecor || !(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }

    @Override
    public void changeSkin(SkinResource resources) {

    }

    @Override
    protected void onDestroy() {
        //移除，防止内存泄露
        SkinManager.getInstance().unRegister(this);
        super.onDestroy();
    }
}
