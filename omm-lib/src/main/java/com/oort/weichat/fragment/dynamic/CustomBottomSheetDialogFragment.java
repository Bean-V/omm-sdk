package com.oort.weichat.fragment.dynamic;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oort.weichat.R;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public CustomBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    private Fragment mFragment;


    private int mInitialHeight; // 用于保存传递的高度

    public CustomBottomSheetDialogFragment(Fragment fragment, int initialHeight) {
        this.mFragment = fragment;
        this.mInitialHeight = initialHeight;
    }

    public CustomBottomSheetDialogFragment(Fragment fragment) {

        mFragment = fragment;
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        if(mInitialHeight > 0) {

            dialog.setOnShowListener(dialogInterface -> {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                if (bottomSheet != null) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                    // 设置初始高度（从构造参数传入）
                    bottomSheet.getLayoutParams().height = mInitialHeight;
                    bottomSheet.requestLayout();

                    // 设置展开状态
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
        }

        return dialog;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // 获取 BottomSheetDialogFragment 的底层对话框
//        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
//        if (dialog != null) {
//            // 获取底部的布局
//            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                // 获取屏幕高度
//                int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//                // 设置最小高度（例如，屏幕高度的 30%）
//                int minHeight = (int) (screenHeight * 0.3);
//                // 设置最小高度
//                bottomSheet.setMinimumHeight(minHeight);
//
//                // 如果需要，设置最大高度（例如，屏幕高度的 70%）
//                int maxHeight = (int) (screenHeight * 0.7);
//                bottomSheet.getLayoutParams().height = Math.min(bottomSheet.getHeight(), maxHeight);
//                bottomSheet.requestLayout();
//            }
//        }
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dynamic_bottom_sheet, container, false);

        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(mFragment == null){
            return;
        }
        // 添加自定义Fragment
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ll_container, mFragment);
        transaction.commit();

    }
}

