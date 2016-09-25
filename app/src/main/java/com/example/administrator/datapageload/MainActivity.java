package com.example.administrator.datapageload;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private View footer;

    private List<String> data = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private boolean loadFinishFlag;
    private int startIndex;
    private int endIndex;
    private final int pageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) this.findViewById(R.id.listview);
        footer = getLayoutInflater().inflate(R.layout.footer, null);


        loadFinishFlag = true;
        startIndex = 0;
        endIndex = pageSize;
        data.addAll(getDataService(startIndex, endIndex));
        adapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.simple_list_text, data);
        listView.setAdapter(adapter);
        listView.addFooterView(footer);
        listView.setOnScrollListener(new ScrollListener());
        listView.removeFooterView(footer);
    }

    /**
     * 模拟加载数据
     *
     * @param from
     * @param to
     * @return
     */
    public List<String> getDataService(int from, int to) {
        List<String> resList = new ArrayList<>();
        for (int i = from; i < to; i++) {
            resList.add("测试数据" + i);
        }
        return resList;
    }

    public final class ScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(TAG, "---->" + scrollState);
            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    break;
                case SCROLL_STATE_TOUCH_SCROLL:
                    break;
                case SCROLL_STATE_FLING:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //获取屏幕最后Item的ID
            int lastVisibleItem = listView.getLastVisiblePosition();
            if (lastVisibleItem + 1 == totalItemCount) {
                if (loadFinishFlag) {
                    //标志位，防止多次加载
                    loadFinishFlag = false;
                    listView.addFooterView(footer);
                    //开线程加载数据
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startIndex += pageSize;
                            endIndex += pageSize;
                            Message message = handler.obtainMessage(0x123, getDataService(startIndex, endIndex));
                            message.sendToTarget();
                        }
                    }.start();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                data.addAll((List<String>) msg.obj);
                adapter.notifyDataSetChanged();
                listView.removeFooterView(footer);
                loadFinishFlag = true;
            }
        }
    };
}
