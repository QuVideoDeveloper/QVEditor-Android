package com.quvideo.application.draft;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.db.DraftInfoDao;
import com.quvideo.application.editor.R;
import java.util.Collections;
import java.util.List;

public class DraftActivity extends AppCompatActivity {

  private RecyclerView mRecyclerView;

  private ImageView btnBack;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_draft);
    initView();
    initData();
  }

  private void initView() {
    btnBack = findViewById(R.id.btn_back);
    mRecyclerView = findViewById(R.id.draft_recycler);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.addItemDecoration(new DraftItemDecoration(this));
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
  }

  private void initData() {
    DraftAdapter draftAdapter = new DraftAdapter(this);
    mRecyclerView.setAdapter(draftAdapter);

    DraftInfoDao draftInfoDao = new DraftInfoDao();
    List<DraftModel> dataList = draftInfoDao.getAllItem();
    Collections.sort(dataList);
    draftAdapter.setData(dataList);
  }
}