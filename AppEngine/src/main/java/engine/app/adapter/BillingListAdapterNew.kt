package engine.app.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.pnd.adshandler.R
import com.squareup.picasso.Picasso
import engine.app.listener.RecyclerViewClickListener
import engine.app.server.v2.Billing
import engine.app.server.v2.Slave
import java.util.logging.Handler

/**
 * Created by Meenu Singh on 20/05/2021.
 */
class BillingListAdapterNew(
    private var context: Context,
    private var billingList: ArrayList<Billing>,
    private var recyclerViewClickListener: RecyclerViewClickListener
) : RecyclerView.Adapter<BillingListAdapterNew.CustomViewHolder>() {
    var selectedPos: Int = 0

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var price: TextView = itemView.findViewById(R.id.tv_price_pro)
        var title: TextView = itemView.findViewById(R.id.tv_pro_title)
        var textViewDes: TextView = itemView.findViewById(R.id.tv_price_subs)
        var puchasedIcon: ImageView = itemView.findViewById(R.id.iv_purchased_icon)
        var checkBox: CheckBox = itemView.findViewById(R.id.planPurchaseChk)
        var rl: RelativeLayout = itemView.findViewById(R.id.rl_parentPro)

        fun updateData(billing: Billing) {
            Picasso.get().load(billing.product_offer_src).into(puchasedIcon)
            price.text = addHtml(billing.product_price) + " " + billing.product_offer_sub_text

            if (billing.button_sub_text.isEmpty() || billing.button_sub_text.length <= 0 || billing.button_sub_text == "") {
                textViewDes.visibility = View.GONE
            } else {
                textViewDes.visibility = View.VISIBLE
                textViewDes.text = billing.button_sub_text
            }

            if (billing.product_offer_text.isEmpty() || billing.product_offer_text.length <= 0 || billing.product_offer_text == "") {
                title.visibility = View.GONE
            } else {
                title.visibility = View.VISIBLE
                title.text = billing.product_offer_text
            }
        }

        private fun addHtml(text: String): String {
            return Html.fromHtml(text).toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_purchase_item_new, null)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.updateData(billingList[position])
        holder.checkBox.isChecked = false

        if ((!Slave.hasPurchased(context) && position == selectedPos) || (Slave.hasPurchased(context) && position == selectedPos)) {
            holder.checkBox.isChecked = true
            holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
            android.os.Handler().postDelayed(Runnable {             recyclerViewClickListener.onViewClicked(position)
            },100)
        } else {
            holder.checkBox.isChecked = false
            holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
        }

        holder.rl.setOnClickListener {
            selectedPos = position
            recyclerViewClickListener.onViewClicked(it, position)
            notifyDataSetChanged()
        }

        when (billingList[position].billing_type) {
            Slave.Billing_Free -> {
                if (!Slave.hasPurchased(context)) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                }
            }
            Slave.Billing_Pro -> {
                if (Slave.IS_PRO) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                }
            }
            Slave.Billing_Weekly -> {
                if (Slave.IS_WEEKLY) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                } else if (Slave.IS_PRO || Slave.IS_MONTHLY || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_Monthly -> {
                if (Slave.IS_MONTHLY) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                } else if (Slave.IS_PRO || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_Quarterly -> {
                if (Slave.IS_QUARTERLY) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                } else if (Slave.IS_PRO || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_HalfYear -> {
                if (Slave.IS_HALFYEARLY) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                } else if (Slave.IS_PRO || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }

            }
            Slave.Billing_Yearly -> {
                if (Slave.IS_YEARLY) {
                    holder.rl.setBackgroundResource(R.drawable.purchased_plan_bg)
                    holder.checkBox.isChecked = true
                    selectedPos = position
                    recyclerViewClickListener.onViewClicked(position)
                } else if (Slave.IS_PRO) {
                    holder.rl.alpha = 0.5f
                }
            }
            else -> {
                holder.rl.setBackgroundResource(R.drawable.corner_color)
                holder.checkBox.isChecked = true
            }

        }
    }

    override fun getItemCount(): Int {
        return billingList.size
    }

    fun getItem(position: Int): Billing {
        return billingList[position]
    }
}