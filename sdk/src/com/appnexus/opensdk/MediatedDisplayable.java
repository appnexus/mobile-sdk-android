/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import android.view.View;

import com.appnexus.opensdk.utils.ViewUtil;

class MediatedDisplayable implements Displayable {
    private View view;
    private MediatedAdViewController mAVC;

    MediatedDisplayable(MediatedAdViewController mAVC) {
        this.mAVC = mAVC;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public boolean failed() {
        return mAVC.hasFailed;
    }

    @Override
    public void destroy() {
        mAVC.finishController();
        ViewUtil.removeChildFromParent(view);
    }

    @Override
    public int getCreativeWidth() {
        if (view != null) {
            return view.getWidth();
        }
        // 'view' is null for interstitials, return -1
        return -1;
    }

    @Override
    public int getCreativeHeight() {
        if (view != null) {
            return view.getHeight();
        }
        // 'view' is null for interstitials, return -1
        return -1;
    }

    @Override
    public void onPause() {
        mAVC.onPause();
    }

    @Override
    public void onResume() {
        mAVC.onResume();
    }

    @Override
    public void onDestroy() {
        mAVC.onDestroy();
        this.destroy();
    }

    void setView(View view) {
        this.view = view;
    }

    MediatedAdViewController getMAVC() {
        return mAVC;
    }

    void setMAVC(MediatedAdViewController mAVC) {
        this.mAVC = mAVC;
    }


}
