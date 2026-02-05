package com.oortcloud.coo.police;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.R;
import com.oortcloud.basemodule.utils.StatusBarUtil;

import java.util.HashMap;
import java.util.Objects;

public class CoordinationActivity extends BaseActivity implements View.OnClickListener {
    
    private ImageView btnBack;
    private ImageView btnSearch;
    private ImageView btnAdd;
    private TextView tabParticipated;
    private TextView tabMyIncidents;
    private TextView tabUnitIncidents;
    private ViewPager2 viewPager;
    private IncidentPagerAdapter pagerAdapter;
    private SearchPopupWindow searchPopupWindow;
    
    private int currentTab = 0; // 当前选中的标签页
    private IncidentListFragment[] fragments = new IncidentListFragment[3]; // 缓存Fragment引用
    private boolean isSearching = false; // 是否正在搜索状态

    @Override
    public void setStatusBarLight(boolean light) {
        StatusBarUtil.setStatusBarColor(this, R.color.white);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordination);
        Objects.requireNonNull( getSupportActionBar()).hide();
        
        initViews();
        setupViewPager();
        setupTabListeners();
        initSearchPopup();


        String params = getIntent().getStringExtra("params");
        if(params != null && !params.isEmpty()){
            Intent intent = new Intent(this, IncidentDetailActivity.class);
            intent.putExtra("receivingAlertNumber", params);
            startActivity(intent);


        }
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        tabParticipated = findViewById(R.id.tab_participated);
        tabMyIncidents = findViewById(R.id.tab_my_incidents);
        tabUnitIncidents = findViewById(R.id.tab_unit_incidents);
        viewPager = findViewById(R.id.viewpager_incidents);
        
        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }
    
    private void setupViewPager() {
        pagerAdapter = new IncidentPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // 监听ViewPager2的页面变化
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabSelection(position);
            }
        });
    }
    
    private void setupTabListeners() {
        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        tabParticipated.setOnClickListener(this);
        tabMyIncidents.setOnClickListener(this);
        tabUnitIncidents.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            handleBackPress();
        } else if (id == R.id.btn_search) {
            openSearchActivity();
        }else if (id == R.id.btn_add) {
            openAddActivity();
        }else if (id == R.id.tab_participated) {
            switchToTab(0);
        } else if (id == R.id.tab_my_incidents) {
            switchToTab(1);
        } else if (id == R.id.tab_unit_incidents) {
            switchToTab(2);
        }
    }
    
    private void switchToTab(int position) {
        if (position != currentTab) {
            viewPager.setCurrentItem(position, true);
            // 切换标签页时清除搜索状态
            if (isSearching) {
                isSearching = false;
            }
        }
    }
    
    /**
     * 处理返回按钮点击
     */
    private void handleBackPress() {
        if (isSearching) {
            // 如果正在搜索状态，先返回到全部数据
            clearSearchAndRefresh();
        } else {
            // 如果不在搜索状态，直接退出Activity
            finish();
        }
    }
    
    /**
     * 清除搜索并刷新数据
     */
    private void clearSearchAndRefresh() {
        isSearching = false;
        refreshData();
    }
    
    private void updateTabSelection(int position) {
        currentTab = position;
        
        // 重置所有标签页的样式
        resetTabStyles();
        
        // 设置当前选中标签页的样式
        switch (position) {
            case 0:
                setTabSelected(tabParticipated);
                break;
            case 1:
                setTabSelected(tabMyIncidents);
                break;
            case 2:
                setTabSelected(tabUnitIncidents);
                break;
        }
    }
    
    private void resetTabStyles() {
        setTabUnselected(tabParticipated);
        setTabUnselected(tabMyIncidents);
        setTabUnselected(tabUnitIncidents);
    }
    
    private void setTabSelected(TextView tab) {
        tab.setTextColor(getResources().getColor(R.color.blue_primary));

    }
    
    private void setTabUnselected(TextView tab) {
        tab.setTextColor(getResources().getColor(R.color.gray_secondary));
    }


    private void initSearchPopup() {
        searchPopupWindow = new SearchPopupWindow(this);
        searchPopupWindow.setOnSearchListener(new SearchPopupWindow.OnSearchListener() {
            @Override
            public void onSearch(SearchPopupWindow.SearchCriteria criteria) {
                handleSearchResult(criteria);
            }

            @Override
            public void onReset() {
                // 重置搜索条件，刷新数据
                refreshData();
            }
        });
    }

    private void openSearchActivity() {
        if (searchPopupWindow != null) {
            searchPopupWindow.show(btnSearch);
        }
    }

    private void openAddActivity() {
        Intent intent = new Intent(this, CreateTaskActivity.class);
        startActivity(intent);
    }
    
    
    private void handleSearchResult(SearchPopupWindow.SearchCriteria criteria) {
        // 这里可以根据搜索条件过滤数据
        // 例如：更新当前Fragment的数据
        // 或者切换到搜索结果页面
        
        // 简单的提示
        String searchInfo = "搜索条件：";
        if (!criteria.getIncidentNumber().isEmpty()) {
            searchInfo += "接警单编号=" + criteria.getIncidentNumber() + " ";
        }
        if (!criteria.getReporterName().isEmpty()) {
            searchInfo += "报警人姓名=" + criteria.getReporterName() + " ";
        }
        if (!criteria.getStatus().equals("全部")) {
            searchInfo += "状态=" + criteria.getStatus() + " ";
        }
        if (!criteria.getLevel().equals("全部")) {
            searchInfo += "级别=" + criteria.getLevel() + " ";
        }
        
        // 这里可以显示搜索结果或更新列表
        // 暂时用Toast显示搜索条件
//        android.widget.Toast.makeText(this, searchInfo, android.widget.Toast.LENGTH_SHORT).show();
        
        // 可以根据搜索条件刷新数据
        refreshDataWithCriteria(criteria);
    }
    
    private void refreshData() {
        // 刷新数据，显示所有数据
        if (pagerAdapter != null) {
            // 获取当前Fragment并清除搜索参数
            IncidentListFragment currentFragment = getCurrentFragment();
            if (currentFragment != null) {
                currentFragment.clearSearchParams();
                // 清除搜索状态
                isSearching = false;
                // 强制重新加载数据
                currentFragment.refreshData();
            }
        }
    }
    
    private void refreshDataWithCriteria(SearchPopupWindow.SearchCriteria criteria) {
        // 根据搜索条件刷新数据
        if (pagerAdapter != null) {
            // 将搜索条件转换为搜索参数
            HashMap<String, Object> searchParams = convertCriteriaToSearchParams(criteria);
            // 获取当前Fragment并设置搜索参数
            IncidentListFragment currentFragment = getCurrentFragment();
            if (currentFragment != null) {
                currentFragment.setSearchParams(searchParams);
                // 设置搜索状态
                isSearching = true;
            }
        }
    }
    
    /**
     * 获取当前显示的Fragment
     */
    private IncidentListFragment getCurrentFragment() {
        try {
            int currentItem = viewPager.getCurrentItem();
            // 通过pagerAdapter获取缓存的Fragment
            return pagerAdapter.getFragment(currentItem);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 将搜索条件转换为搜索参数
     */
    private HashMap<String, Object> convertCriteriaToSearchParams(SearchPopupWindow.SearchCriteria criteria) {
        HashMap<String, Object> searchParams = new HashMap<>();
        
        // 处理所有布局字段
        if (criteria.getIncidentNumber() != null && !criteria.getIncidentNumber().trim().isEmpty()) {
            searchParams.put("receivingAlertNumber", criteria.getIncidentNumber());
        }
        
        if (criteria.getReporterName() != null && !criteria.getReporterName().trim().isEmpty()) {
            searchParams.put("receivingOfficer", criteria.getReporterName());
        }
        
        if (criteria.getReporterPhone() != null && !criteria.getReporterPhone().trim().isEmpty()) {
            searchParams.put("receivingOfficerPhone", criteria.getReporterPhone());
        }
        
        if (criteria.getHandlingUnit() != null && !criteria.getHandlingUnit().trim().isEmpty()) {
            searchParams.put("responseUnit", criteria.getHandlingUnit());
        }
        
        if (criteria.getStatus() != null && !criteria.getStatus().equals("全部")) {
            searchParams.put("alertStatus", criteria.getStatus());
        }
        
        if (criteria.getLevel() != null && !criteria.getLevel().equals("全部")) {
            searchParams.put("alertLevel", criteria.getLevel());
        }
        
        // 时间范围搜索（可以根据需要添加时间参数）
        if (criteria.getStartTime() != null && !criteria.getStartTime().trim().isEmpty()) {
            searchParams.put("startTime", criteria.getStartTime());
        }
        
        if (criteria.getEndTime() != null && !criteria.getEndTime().trim().isEmpty()) {
            searchParams.put("endTime", criteria.getEndTime());
        }
        
        return searchParams;
    }
    
    @Override
    public void onBackPressed() {
        // 处理系统返回键
        handleBackPress();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            // 任务创建成功，刷新数据
            refreshData();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 每次Activity恢复时都重新加载数据
        refreshData();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        // 每次Activity重启时都重新加载数据
        refreshData();
    }
}
