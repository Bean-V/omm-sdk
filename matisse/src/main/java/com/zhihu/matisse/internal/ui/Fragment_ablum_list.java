package com.zhihu.matisse.internal.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.CheckView;
import com.zhihu.matisse.listener.ItemOnCickListener;
import com.zhihu.matisse.ui.MatisseActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_ablum_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ablum_list extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArrayList<Album> getList() {
        return list;
    }

    public void setList(ArrayList<Album> list) {
        this.list = list;
        if(adp != null){
            adp.reloadData(list);
        }
    }

    private ArrayList<Album> list = new ArrayList<>();

    public RvAdapter getAdp() {
        return adp;
    }

    public void setAdp(RvAdapter adp) {
        this.adp = adp;
    }

    private RvAdapter adp;
    private RecyclerView rv;

    public ItemOnCickListener getItemOnClickListem() {
        return itemOnClickListem;
    }

    public void setItemOnClickListem(ItemOnCickListener itemOnClickListem) {
        this.itemOnClickListem = itemOnClickListem;
    }

    ItemOnCickListener itemOnClickListem;

    public Fragment_ablum_list() {
        // Required empty public constructor
    }

    public void setEdit(boolean edit) {
        this.edit = edit;

        if(adp != null){
            adp.setEdit(edit);
        }
    }

    private boolean edit = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ablum_list.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ablum_list newInstance(String param1, String param2) {
        Fragment_ablum_list fragment = new Fragment_ablum_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    static int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ablum_list, container, false);

        rv = view.findViewById(R.id.rv);
        //rv.setAdapter(mAlbumsAdapter);

        int space = dp2px(getContext(),10);
        rv.addItemDecoration(new SpacesItemDecoration(space));
        rv.setPadding(space, 0, 0, 0);
        rv.setLayoutManager(new GridLayoutManager(getContext(),3));
//        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
//        rv.addItemDecoration(new MediaGridInset(3, 5, false));


        adp = new RvAdapter(list);
        rv.setAdapter(adp);

        adp.setItemOnClickListem(new ItemOnCickListener() {
            @Override
            public void itemOnClikListener(int postion, Object obj) {
                if(itemOnClickListem != null){
                    itemOnClickListem.itemOnClikListener(postion,null);
                }
            }

            @Override
            public void itemOnLongClikListener(int postion, Object obj) {
                if(itemOnClickListem != null){
                    itemOnClickListem.itemOnLongClikListener(postion,null);
                }
            }
        });



        return view;
    }

    public class RvAdapter extends RecyclerView.Adapter {



        public ItemOnCickListener getItemOnClickListem() {
            return itemOnClickListem;
        }

        public void setItemOnClickListem(ItemOnCickListener itemOnClickListem) {
            this.itemOnClickListem = itemOnClickListem;
        }

        public boolean isEdit() {
            return edit;
        }

        public void setEdit(boolean edit) {
            this.edit = edit;
            notifyDataSetChanged();
        }

        private  boolean edit = false;
        ItemOnCickListener itemOnClickListem;
        ArrayList<Album> mlist = new ArrayList<Album>();
        ArrayList<Album> slist = new ArrayList<Album>();
        public RvAdapter(ArrayList<Album>list){
            mlist = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v =  LayoutInflater.from(getContext()).inflate(R.layout.item_fragment_album,parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Album album = mlist.get(position);
            VH vh = (VH) holder;
            vh.tv_name.setText(album.getDisplayName(getContext()) + "(" + String.valueOf(album.getCount()) + ")" );
//            holder.tv_count.setText(String.valueOf(album.getCount()));
//
//            // do not need to load animated Gif
            SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(), getContext().getResources().getDimensionPixelSize(R
                            .dimen.media_grid_size), null,
                    vh.ic_cover, album.getCoverUri());


            vh.check_view.setVisibility(View.GONE);
            if(edit){
                vh.check_view.setVisibility(View.VISIBLE);
                vh.check_view.setChecked(slist.contains(album));
            }

            vh.ic_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(!edit) {
                        ((MatisseActivity) getActivity()).onAlbumSelected(album);
                    }else{
                        if(itemOnClickListem != null){
                            itemOnClickListem.itemOnClikListener(position,null);
                        }
                    }
                }
            });
            vh.ic_cover.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(itemOnClickListem != null){
                        itemOnClickListem.itemOnLongClikListener(position,null);
                    }
                    return true;
                }
            });

        }


        @Override
        public int getItemCount() {
            return mlist.size();
        }


        public void reloadData(ArrayList <Album> list){
            mlist = list;
            notifyDataSetChanged();

        }

        public void reloadSelectData(ArrayList <Album> list){
            slist = list;
            notifyDataSetChanged();

        }
    }

    public class VH extends RecyclerView.ViewHolder {

        private  CheckView check_view;
        public TextView tv_name;
        public ImageView ic_cover;

        public VH(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_album_name);
            ic_cover = itemView.findViewById(R.id.ic_album_cover);
            check_view = itemView.findViewById(R.id.check_view);

            // do not need to load animated Gif

        }
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace = 0;
        public SpacesItemDecoration(int space) {
            mSpace = space;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if(parent.getLayoutManager() instanceof  GridLayoutManager){
                outRect.top = mSpace;
                outRect.right = mSpace;
            }
        }
    }
}