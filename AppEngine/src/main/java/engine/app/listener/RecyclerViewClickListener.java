package engine.app.listener;

import android.view.View;

public interface RecyclerViewClickListener {
    void onViewClicked(View mView, int position);
    void onListItemClicked(View mView, String reDirectUrl);
}
