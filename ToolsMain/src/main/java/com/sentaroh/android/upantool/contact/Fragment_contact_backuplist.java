package com.sentaroh.android.upantool.contact;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sentaroh.android.upantool.R;
import com.sentaroh.android.upantool.Fragment_ft;
import com.sentaroh.android.upantool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_contact_backuplist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_contact_backuplist extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Adapter ad;
    private Button btn;
    private RecyclerView rv;

    public BottomClickListener getBottomClickListener() {
        return bottomClickListener;
    }

    public void setBottomClickListener(BottomClickListener bottomClickListener) {
        this.bottomClickListener = bottomClickListener;
    }

    private BottomClickListener bottomClickListener;

    public interface BottomClickListener{
        void click();
    }



    public Fragment_contact_backuplist() {
        // Required empty public constructor
    }

    public List getMlist() {
        return mlist;
    }

    public void setMlist(List mlist) {
        this.mlist = mlist;
        if(ad != null){
            ad.refresh(mlist);
        }
    }

    private  List mlist = new ArrayList();


    public String getBtnTitle() {
        return btnTitle;
    }

    public void setBtnTitle(String btnTitle) {
        this.btnTitle = btnTitle;

        if(btn != null){
            btn.setText(btnTitle);
        }



    }

    private String btnTitle = "";





    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_contact_backuplist.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_contact_backuplist newInstance(String param1, String param2) {
        Fragment_contact_backuplist fragment = new Fragment_contact_backuplist();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View   v = inflater.inflate(R.layout.fragment_contact_backuplist, container, false);
        rv = v.findViewById(R.id.rv_contact);
       // rv.setBackgroundColor(Color.parseColor("#ff2222"));

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ad = new Adapter(mlist);
        rv.setAdapter(ad);


        btn = (Button) v.findViewById(R.id.btn_bottom);

        btn.setText(btnTitle);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomClickListener != null){
                    bottomClickListener.click();
                }
            }
        });




        return v;
    }


    public class Adapter extends RecyclerView.Adapter{


        private List<MyContact> mItems;
        public Adapter(List<MyContact> items) {
            //super();
            //super();
            mItems = items;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_backup_layout,parent, false);//item_fragment_filelist

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {

            ViewHolder holder = (ViewHolder) mholder;
            MyContact fileBean = mItems.get(position);

            holder.tv_name.setText(mItems.get(position).getName().toString());
            holder.tv_phone.setText(mItems.get(position).getPhone().toString());

        }

        @Override
        public int getItemCount() {
            int a = 5;
            int b =6;
            return mItems.size();
        }




        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_name;
            public TextView tv_phone;

            public ViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);


                tv_phone = v.findViewById(R.id.tv_phone);

            }
        }

        public void refresh(List list) {
            mItems = list;
            notifyDataSetChanged();
        }

//        public void refreshUserCheckStatu(List users) {
//            sList = users;
//            notifyDataSetChanged();
//        }

//        public void refreshEdit(boolean edit) {
//
//            if (ischeck != edit) {
//                ischeck = edit;
//                notifyDataSetChanged();
//            }
//        }

    }
}