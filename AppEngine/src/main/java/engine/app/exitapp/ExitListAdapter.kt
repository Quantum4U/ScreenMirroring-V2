package engine.app.exitapp

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import app.pnd.adshandler.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import engine.app.listener.RecyclerViewClickListener
import engine.app.server.v2.ExitAppListResponse
import engine.app.server.v2.Slave
import java.util.*
import com.squareup.picasso.MemoryPolicy

import com.squareup.picasso.NetworkPolicy
import java.lang.Exception


/**
 * Created by quantum4u  on 13/07/2022.
 */
class ExitListAdapter(
    private var context: Context,
    private var exitAppList: ArrayList<ExitAppListResponse>,
    private var recyclerViewClickListener: RecyclerViewClickListener
) : RecyclerView.Adapter<ExitListAdapter.CustomViewHolder>() {


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.iv_pro)
        private var title: TextView = itemView.findViewById(R.id.tv_pro_title)
        private var subTitle: TextView = itemView.findViewById(R.id.tv_pro_subtitle)

        var btn: Button = itemView.findViewById(R.id.btn_pro)
        var rl: RelativeLayout = itemView.findViewById(R.id.rl_parentPro)

        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar1)

        fun updateData(exitAppList: ExitAppListResponse) {
          //  Picasso.get().load(exitAppList.app_list_icon_src).into(image)
            println("NewEngine showFullAdsOnLaunch type 5 "+exitAppList +"  "+exitAppList.app_list_icon_src)

            if(exitAppList.app_list_icon_src!=null && !exitAppList.app_list_icon_src.isEmpty()) {
                onSetPicasso(
                    exitAppList.app_list_icon_src,
                    image,
                    R.drawable.ic_exit_app_list_default
                )
            }else{
                loadPlaceHolder( image, R.drawable.ic_exit_app_list_default)
            }
            title.text = exitAppList.app_list_title
            subTitle.text = exitAppList.app_list_subtitle
            btn.visibility = View.VISIBLE
            btn.text = exitAppList.app_list_button_text
            btn.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(exitAppList.app_list_button_bg))
            btn.setTextColor(
                ColorStateList.valueOf(Color.parseColor(exitAppList.app_list_button_text_color)))
            ratingBar.rating= (exitAppList.app_list_rate_count).toFloat()

        }

        private fun onSetPicasso(src:String, view: ImageView, placeHolder:Int){
             Picasso.get()
                    .load(src)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(view, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception?) {
                            println("NewEngine showFullAdsOnLaunch type 5 fail " + placeHolder + "  " + src)
                            loadPlaceHolder(view,placeHolder)
                        }
                    })
        }

        private fun loadPlaceHolder(view: ImageView ,placeHolder:Int){
            Picasso.get()
                .load(placeHolder)
                .error(placeHolder)
                .into(view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exit_list_item, null)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val exitData =  exitAppList[position]
        holder.updateData(exitData)
        holder.rl.setOnClickListener {
            recyclerViewClickListener.onListItemClicked(it,exitData.app_list_redirect)
        }


    }

    override fun getItemCount(): Int {
        return exitAppList.size
    }
}