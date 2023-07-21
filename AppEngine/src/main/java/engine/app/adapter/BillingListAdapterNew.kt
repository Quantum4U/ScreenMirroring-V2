package engine.app.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.pnd.adshandler.R
import com.squareup.picasso.Picasso
import engine.app.listener.RecyclerViewClickListener
import engine.app.server.v2.Billing
import engine.app.server.v2.Slave

/**
 * Created by Meenu Singh on 20/05/2021.
 */
class BillingListAdapterNew(
    private var context: Context,
    private var billingList: ArrayList<Billing>,
    private var recyclerViewClickListener: RecyclerViewClickListener
) : RecyclerView.Adapter<BillingListAdapterNew.CustomViewHolder>() {

    //by default it selected to yearly according to ui in quantum su....
    private var selectedBillingType: String = Slave.Billing_Yearly
    var selectedPos = 0

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.iv_pro)
        var price: TextView = itemView.findViewById(R.id.tv_price_pro)
        var title: TextView = itemView.findViewById(R.id.tv_pro_title)
        var subTitle: TextView = itemView.findViewById(R.id.tv_pro_subtitle)
        private var textViewDes: TextView = itemView.findViewById(R.id.tv_price_subs)
        var ivOffer: ImageView = itemView.findViewById(R.id.iv_offer_pro)
        //var ivOfferText: TextView = itemView.findViewById(R.id.iv_offer_text)

        var btn: Button = itemView.findViewById(R.id.btn_pro)
        var rl: RelativeLayout = itemView.findViewById(R.id.rl_parentPro)
        var cb: ImageView = itemView.findViewById(R.id.cb)

        fun updateData(billing: Billing) {
            Picasso.get().load(billing.feature_src).into(image)
            price.text = addHtml(billing.product_price)
            title.text = billing.product_offer_text
            if (billing.product_offer_sub_text != null && billing.product_offer_sub_text.contains("#")) {
                val str = billing.product_offer_sub_text.split("#").toTypedArray()
                val subTitleText = str[0]
                val s1 = str[1]
                subTitle.text = subTitleText
                textViewDes.visibility = View.VISIBLE
                textViewDes.text = s1
            } else {
                textViewDes.visibility = View.GONE
                subTitle.text = billing.product_offer_sub_text
            }


//            if (billing.button_sub_text != null
//                && billing.button_sub_text.isNotEmpty()
//                && !billing.button_sub_text.equals("")) {
//                textViewDes.visibility = View.VISIBLE
//                textViewDes.text = billing.button_sub_text
//            } else {
//                textViewDes.visibility = View.GONE
//            }

            if (billing.product_offer_status) {
                ivOffer.visibility = View.VISIBLE
                //ivOfferText.visibility = View.VISIBLE
                if (billing.product_offer_src != null && !billing.product_offer_src.equals(""))
                    Picasso.get().load(billing.product_offer_src).into(ivOffer)
            } else {
                ivOffer.visibility = View.INVISIBLE
                //ivOfferText.visibility = View.GONE
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

        //only according to new in-app ui
        holder.rl.setOnClickListener {
            println(
                "BillingListAdapter.onBindViewHolder ola kale kale uu" + " " + Slave.IS_PRO + " " + Slave.IS_YEARLY + "" +
                        Slave.IS_HALFYEARLY + " " + Slave.IS_QUARTERLY + " " + Slave.IS_MONTHLY
            )
            when (billingList[position].billing_type) {
                Slave.Billing_Free ->
                    if (Slave.hasPurchased(context)) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }
                Slave.Billing_Pro ->
                    if (Slave.IS_PRO) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }
                Slave.Billing_Weekly -> {
                    if (Slave.IS_PRO || Slave.IS_YEARLY || Slave.IS_HALFYEARLY || Slave.IS_QUARTERLY || Slave.IS_MONTHLY || Slave.IS_WEEKLY) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }

                    if (Slave.IS_PRO || Slave.IS_MONTHLY || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                        holder.rl.alpha = 0.5f
                    }
                }
                Slave.Billing_Monthly -> {
                    if (Slave.IS_PRO || Slave.IS_YEARLY || Slave.IS_HALFYEARLY || Slave.IS_QUARTERLY || Slave.IS_MONTHLY) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }

                    if (Slave.IS_PRO || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                        holder.rl.alpha = 0.5f
                    }
                }
                Slave.Billing_Quarterly -> {
                    if (Slave.IS_PRO || Slave.IS_YEARLY || Slave.IS_HALFYEARLY || Slave.IS_QUARTERLY) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }

                    if (Slave.IS_PRO || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                        holder.rl.alpha = 0.5f
                    }
                }
                Slave.Billing_HalfYear -> {
                    if (Slave.IS_PRO || Slave.IS_YEARLY || Slave.IS_HALFYEARLY) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }

                    if (Slave.IS_PRO || Slave.IS_YEARLY) {
                        holder.rl.alpha = 0.5f
                    }
                }
                Slave.Billing_Yearly -> {
                    if (Slave.IS_PRO || Slave.IS_YEARLY) {
                        showToast()
                    } else {
                        clickItem(it, holder, position)
                    }

                    if (Slave.IS_PRO) {
                        holder.rl.alpha = 0.5f
                    }
                }
            }


        }


        if (selectedBillingType == billingList[position].billing_type) {
            selectedPos = position
            holder.rl.setBackgroundResource(R.drawable.corner_color)
            setTextViewColor(
                ContextCompat.getColor(context, R.color.inapp_unselected_text_color),
                holder
            )
            holder.cb.isSelected = true
        } else {
            holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
            setTextViewColor(
                ContextCompat.getColor(context, R.color.inapp_unselected_text_color),
                holder
            )
            holder.cb.visibility = View.VISIBLE
            holder.cb.isSelected = false
        }

        changeButtonSelection(position, holder)

    }

    private fun changeButtonSelection(position: Int, holder: CustomViewHolder) {
        when (billingList[position].billing_type) {
            Slave.Billing_Free -> {
                if (!Slave.hasPurchased(context)) {
                    holder.btn.visibility = View.GONE
                }
            }
            Slave.Billing_Pro -> {
                if (Slave.IS_PRO) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    //selectedPos = position
                }
            }
            Slave.Billing_Weekly -> {
                if (Slave.IS_WEEKLY) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    // selectedPos = position
                }

                if (Slave.IS_PRO || Slave.IS_MONTHLY || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_Monthly -> {
                if (Slave.IS_MONTHLY) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    //selectedPos = position
                }

                if (Slave.IS_PRO || Slave.IS_QUARTERLY || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_Quarterly -> {
                if (Slave.IS_QUARTERLY) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    //selectedPos = position
                }
                if (Slave.IS_PRO || Slave.IS_HALFYEARLY || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }
            }
            Slave.Billing_HalfYear -> {
                if (Slave.IS_HALFYEARLY) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    //selectedPos = position
                }
                if (Slave.IS_PRO || Slave.IS_YEARLY) {
                    holder.rl.alpha = 0.5f
                }

            }
            Slave.Billing_Yearly -> {
                if (Slave.IS_YEARLY) {
                    holder.rl.setBackgroundResource(R.drawable.inapp_corner_color_unselect)
                    holder.btn.visibility = View.VISIBLE
                    holder.ivOffer.visibility = View.INVISIBLE
                    holder.cb.visibility = View.GONE
                    //selectedPos = position
                }
                if (Slave.IS_PRO) {
                    holder.rl.alpha = 0.5f
                }
            }

        }
    }

    private fun setTextViewColor(color: Int, holder: CustomViewHolder) {
//        holder.title.setTextColor(color)
//        holder.subTitle.setTextColor(color)
//        holder.price.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return billingList.size
    }

    fun getItem(position: Int): Billing {
        return billingList[position]
    }

    private fun showToast() {
        Toast.makeText(
            context,
            context.resources.getString(R.string.already_premium_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun clickItem(itemView: View, holder: CustomViewHolder, position: Int) {
        recyclerViewClickListener.onViewClicked(itemView, position)
        holder.rl.setBackgroundResource(R.drawable.corner_color)
        setTextViewColor(
            ContextCompat.getColor(context, R.color.inapp_unselected_text_color),
            holder
        )
        selectedBillingType = billingList[position].billing_type
        selectedPos = position
        notifyDataSetChanged()

    }

}