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

package com.liulishuo.okdownload.sample.util.queue;

import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.sample.R;

/**
 * Created by dayuan on 2018/2/1.
 */

public class UniManagerAdapter extends BaseQuickAdapter<DownloadTask, BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     */

    public UniManagerAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    protected void convert(BaseViewHolder helper, DownloadTask task) {
        ProgressBar pb = helper.itemView.findViewById(R.id.progressBar);

        BreakpointInfo breakpointInfo = StatusUtil.getCurrentInfo(task);
        if (breakpointInfo != null){
            pb.setMax((int) breakpointInfo.getTotalLength());
            pb.setProgress((int) breakpointInfo.getTotalOffset());
        }


        helper.setText(R.id.nameTv, task.getFilename());
        helper.setText(R.id.statusTv, StatusUtil.getStatus(task).toString());
    }
}
