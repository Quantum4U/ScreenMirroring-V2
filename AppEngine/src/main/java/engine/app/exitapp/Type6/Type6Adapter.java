package engine.app.exitapp.Type6;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.pnd.adshandler.R;
import engine.app.listener.RecyclerViewClickListener;
import engine.app.server.v2.ExitAppListResponse;
import engine.app.server.v2.Slave;

public class Type6Adapter extends RecyclerView.Adapter<Type6Adapter.ViewHolder> {

    private List<ExitAppListResponse> data;
    private RecyclerViewClickListener recyclerViewClickListener ;

    public Type6Adapter(List<ExitAppListResponse> data, RecyclerViewClickListener recyclerViewClickListener) {
        this.data = data;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.exit_type_6_adapter_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("Type6Adapter", "Hello onBindViewHolder gjshj" + " " + data.get(position).app_list_button_text);
        ExitAppListResponse exitAppListResponse = data.get(position);
        if(exitAppListResponse.app_list_src!=null&& !exitAppListResponse.app_list_src.isEmpty()) {
            onSetPicasso(exitAppListResponse.app_list_src, holder.image, holder.lottieAnimationView);
        }else {
            holder.lottieAnimationView.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private LottieAnimationView lottieAnimationView;



        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            lottieAnimationView = itemView.findViewById(R.id.more_app_animation);
        }
    }

    private void onSetPicasso(String src, ImageView view, LottieAnimationView lottieAnimationView) {
        Picasso.get()
                .load(src)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Picasso.get().load(src).into(view);
                    }

                    @Override
                    public void onError(Exception e) {
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        view.setVisibility(View.GONE);
//                        Picasso.get()
//                                .load(src)
//                                .error(placeHolder)
//                                .into(view);

                    }
                });


    }

}
