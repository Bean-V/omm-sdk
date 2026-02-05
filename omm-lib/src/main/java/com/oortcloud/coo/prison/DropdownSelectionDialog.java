package com.oortcloud.coo.prison;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DropdownSelectionDialog extends Dialog {

    private final Context context;
    private final String title;
    private final List<String> options;
    private final int selectedIndex;
    private OnSelectionListener listener;

    public DropdownSelectionDialog(@NonNull Context context, String title, List<String> options, int selectedIndex) {
        super(context, R.style.DropdownDialogStyle);
        this.context = context;
        this.title = title;
        this.options = new ArrayList<>(options);
        this.selectedIndex = selectedIndex;
        
        initView();
    }
    @SuppressLint("InflateParams")
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
       View view = inflater.inflate(R.layout.dialog_dropdown_selection, null);
        setContentView(view);

        // 设置对话框属性
        Objects.requireNonNull(getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        RecyclerView rvOptions = view.findViewById(R.id.rv_options);

        tvTitle.setText(title);
        
        ivClose.setOnClickListener(v -> dismiss());

        // 设置RecyclerView
        rvOptions.setLayoutManager(new LinearLayoutManager(context));
        OptionAdapter adapter = new OptionAdapter(options, selectedIndex, new OptionAdapter.OnOptionClickListener() {
            @Override
            public void onOptionClick(int position) {
                if (listener != null) {
                    listener.onSelection(position, options.get(position));
                }
                dismiss();
            }
        });
        rvOptions.setAdapter(adapter);
    }

    public void setOnSelectionListener(OnSelectionListener listener) {
        this.listener = listener;
    }

    public interface OnSelectionListener {
        void onSelection(int position, String selectedValue);
    }

    // 选项适配器
    private static class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {
        private final List<String> options;
        private final int selectedIndex;
        private final OnOptionClickListener listener;

        public OptionAdapter(List<String> options, int selectedIndex, OnOptionClickListener listener) {
            this.options = options;
            this.selectedIndex = selectedIndex;
            this.listener = listener;
        }

        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dropdown_option, parent, false);
            return new OptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
            String option = options.get(position);
            holder.tvOptionText.setText(option);
            holder.ivSelected.setVisibility(position == selectedIndex ? View.VISIBLE : View.GONE);
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public interface OnOptionClickListener {
            void onOptionClick(int position);
        }

        static class OptionViewHolder extends RecyclerView.ViewHolder {
            TextView tvOptionText;
            ImageView ivSelected;

            public OptionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOptionText = itemView.findViewById(R.id.tv_option_text);
                ivSelected = itemView.findViewById(R.id.iv_selected);
            }
        }
    }
}


