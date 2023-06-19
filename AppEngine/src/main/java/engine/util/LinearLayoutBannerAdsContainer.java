package engine.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import engine.app.listener.OnBannerAdsIdLoaded;

public  class LinearLayoutBannerAdsContainer extends LinearLayout {
    private boolean isadsLoaded=false;
    private OnBannerAdsIdLoaded onBannerAdsIdLoaded;
    private Context mContext;



    public LinearLayoutBannerAdsContainer(Context context, OnBannerAdsIdLoaded onBannerAdsIdLoaded) {
        super(context);
        this.mContext=context;
        this.onBannerAdsIdLoaded = onBannerAdsIdLoaded;
        isadsLoaded=false;
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

    }

    @Override
    public void onProvideVirtualStructure(ViewStructure structure) {
        super.onProvideVirtualStructure(structure);

    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

//        if(!changed && r>10 && b>10){
        if(l==0 && t==0 && r>10 && b>10){
            if(!isadsLoaded){
                isadsLoaded=true;
                onBannerAdsIdLoaded.loadandshowBannerAds();
            }

        }


    }


}
