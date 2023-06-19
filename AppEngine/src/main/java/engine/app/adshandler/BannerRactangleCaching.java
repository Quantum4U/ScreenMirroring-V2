package engine.app.adshandler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
public class BannerRactangleCaching extends LinearLayout {

    public BannerRactangleCaching(Context context) {
        super(context);
        this.initComponent(context);
    }

    public BannerRactangleCaching(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initComponent(context);
    }



    private void initComponent(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.apnxt_large_banner, null, false);
//        this.addView(v);

    }
}
