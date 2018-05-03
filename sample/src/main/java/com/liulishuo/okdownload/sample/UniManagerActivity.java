/*
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.okdownload.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.UnifiedListenerManager;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.liulishuo.okdownload.sample.base.BaseSampleActivity;
import com.liulishuo.okdownload.sample.util.DemoUtil;
import com.liulishuo.okdownload.sample.util.queue.UniManagerAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UniManagerActivity extends BaseSampleActivity {
    private String[] mUrls = {
            "http://dldir1.qq.com/weixin/android/weixin6516android1120.apk",
            "https://cdn.llscdn.com/yy/files/tkzpx40x-lls-LLS-5.7-785-20171108-111118.apk",
            "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk",
            "https://dldir1.qq.com/qqfile/QQforMac/QQ_V6.2.0.dmg",
            "http://d1.music.126.net/dmusic/CloudMusic_official_4.3.2.468990.apk",
            "http://d1.music.126.net/dmusic/NeteaseMusic_1.5.9_622_officialsite.dmg",
            "http://dldir1.qq.com/weixin/Windows/WeChatSetup.exe",
            "https://dldir1.qq.com/foxmail/work_weixin/WXWork_2.4.5.213.dmg"
    };

    private UniManagerAdapter adapter;
    private ArrayList<DownloadTask> mDownloadTasks = new ArrayList<>();
    DownloadListener taskDownloadListener = new DownloadListener();
    UnifiedListenerManager mUnifiedListenerManager = new UnifiedListenerManager();

    @Override
    public int titleRes() {
        return R.string.task_manager_title;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        initQueueActivity(findViewById(R.id.actionView), (TextView) findViewById(R.id.actionTv),
                (AppCompatRadioButton) findViewById(R.id.serialRb),
                (AppCompatRadioButton) findViewById(R.id.parallelRb),
                (RecyclerView) findViewById(R.id.recyclerView),
                (CardView) findViewById(R.id.deleteActionView), findViewById(R.id.deleteActionTv));
    }
    private void initQueueActivity(final View actionView, final TextView actionTv,
                                   final AppCompatRadioButton serialRb,
                                   final AppCompatRadioButton parallelRb,
                                   RecyclerView recyclerView,
                                   final CardView deleteActionView, final View deleteActionTv) {
//        initController(actionView, actionTv, serialRb, parallelRb,
//                deleteActionView, deleteActionTv);
        initRecyclerView(recyclerView);
        initAction(actionView, actionTv, serialRb, parallelRb, deleteActionView, deleteActionTv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnifiedListenerManager.detachListener(taskDownloadListener);
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < mUrls.length; i++) {
            DownloadTask task = new DownloadTask.Builder(mUrls[i], DemoUtil.getParentFile(this)).build();
            mDownloadTasks.add(task);
            mUnifiedListenerManager.attachListener(task, taskDownloadListener);

        }
        adapter = new UniManagerAdapter(R.layout.item_queue);
        adapter.setNewData(mDownloadTasks);
        recyclerView.setAdapter(adapter);
    }

    private void initAction(final View actionView, final TextView actionTv,
                            final AppCompatRadioButton serialRb,
                            final AppCompatRadioButton parallelRb,
                            final CardView deleteActionView, final View deleteActionTv) {
        deleteActionView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                adapter.notifyDataSetChanged();
            }
        });

        actionTv.setText(R.string.start);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final boolean started = v.getTag() != null;

                if (started) {
                    stop();
                    
                } else {
                    v.setTag(new Object());
                    actionTv.setText(R.string.cancel);

                    // to start
                    start();
                    adapter.notifyDataSetChanged();

                    serialRb.setEnabled(false);
                    parallelRb.setEnabled(false);
                    deleteActionView.setEnabled(false);
                    deleteActionView.setTag(deleteActionView.getCardElevation());
                    deleteActionView.setCardElevation(0);
                    deleteActionTv.setEnabled(false);
                }
            }
        });
    }

    private void stop() {
        for (DownloadTask task :
                mDownloadTasks) {
            task.cancel();
        }
    }

    private void start(){
        for (DownloadTask task :
                mDownloadTasks) {
            mUnifiedListenerManager.enqueueTaskWithUnifiedListener(task, taskDownloadListener);
        }
    }
    private class DownloadListener extends DownloadListener1 {

        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            Log.i("taskStart",  "fileName " + task.getFilename()+ " start");
            List<DownloadTask> entityList = adapter.getData();
            for (int i = 0; i < entityList.size(); i++) {
                if (task.getUrl().equalsIgnoreCase(adapter.getData().get(i).getUrl())) {
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            Log.i("taskStart",  "fileName " + task.getFilename()+ " start");
            List<DownloadTask> entityList = adapter.getData();
            for (int i = 0; i < entityList.size(); i++) {
                if (task.getUrl().equalsIgnoreCase(adapter.getData().get(i).getUrl())) {
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            Log.i("taskStart",  "fileName " + task.getFilename()+ " start");
            List<DownloadTask> entityList = adapter.getData();
            for (int i = 0; i < entityList.size(); i++) {
                if (task.getUrl().equalsIgnoreCase(adapter.getData().get(i).getUrl())) {
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            Log.i("taskStart",  "fileName " + task.getFilename()+ " start");
            List<DownloadTask> entityList = adapter.getData();
            for (int i = 0; i < entityList.size(); i++) {
                if (task.getUrl().equalsIgnoreCase(adapter.getData().get(i).getUrl())) {
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @android.support.annotation.Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            Log.i("taskStart",  "fileName " + task.getFilename()+ " start");
            List<DownloadTask> entityList = adapter.getData();
            for (int i = 0; i < entityList.size(); i++) {
                if (task.getUrl().equalsIgnoreCase(adapter.getData().get(i).getUrl())) {
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };
}
