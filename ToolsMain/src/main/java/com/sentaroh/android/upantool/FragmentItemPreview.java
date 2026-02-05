package com.sentaroh.android.upantool;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sentaroh.android.upantool.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.PreviewItemFragment;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.io.File;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

//import it.sephiroth.android.library.imagezoom.ImageViewTouch;
//import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentItemPreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentItemPreview extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private OnFragmentInteractionListener mListener;

    public static FragmentItemPreview newInstance(File item) {
        FragmentItemPreview fragment = new FragmentItemPreview();
        Bundle bundle = new Bundle();
       // bundle.putParcelable(ARGS_ITEM, item);
        bundle.putString(ARGS_ITEM,item.getAbsolutePath());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String path = getArguments().getString(ARGS_ITEM);
        File item = new File(path);
        if (item == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.video_play_button);
        if (FileTool.isVideo(item.getName())) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(item.uri, "video/*");
                    try {
                        startActivity(FileTool.openFile(item.getAbsolutePath(),getContext()));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        Glide.with(getContext()).load(item).into(image);
        image.setOnTouchListener(new View.OnTouchListener() {
            float mPosX = 0;
            float mPosY = 0;
            float mCurPosX = 0;
            float mCurPosY = 0;
            boolean oneTouch = false;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MotionEvent event = motionEvent;



                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                             mPosX = event.getX();

                             mPosY = event.getY();
                             oneTouch = true;

                            break;
                    case MotionEvent.ACTION_MOVE:

                             mCurPosX = event.getX();

                             mCurPosY = event.getY();

                        if(motionEvent.getPointerCount() > 1){
                            oneTouch = false;
                        }
                            break;
                    case MotionEvent.ACTION_UP:
                        if(mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 25)) {

                        } else if(mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {

                         //向上滑动 collapse();
                            AppCompatActivity act = (AppCompatActivity) getActivity();

                            if(oneTouch) {
                                act.finish();
                            }





                         }
                         break;
                }
                return false;
            }
        });

//        Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), getActivity());
//        if (item.isGif()) {
//            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, image,
//                    item.getContentUri());
//        } else {
//            SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, image,
//                    item.getContentUri());
//        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}