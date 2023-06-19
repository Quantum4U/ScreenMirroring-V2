package engine.app.exitapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.pnd.adshandler.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import engine.app.adshandler.AHandler
import engine.app.analytics.EngineAnalyticsConstant.Companion.ExitPageType
import engine.app.server.v2.ExitAppListResponse
import engine.app.server.v2.Slave

class ExitAdsType2Activity : AppCompatActivity(){
    private var exitType: String? = null
    private var  dialog: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent != null) {
            exitType = intent.getStringExtra(ExitPageType)
        }

        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogNew)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.exit_layout_type2)
        dialog?.setCancelable(true)
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        if (!isFinishing) {
            try {
                dialog?.show()
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }
        }

        val native_ads: LinearLayout? = dialog?.findViewById(R.id.native_ads)
        val exit_btn_no: TextView? = dialog?.findViewById(R.id.exit_btn_no)



        when (exitType) {
            Slave.EXIT_TYPE2 -> {
                native_ads?.addView(AHandler.getInstance().getNativeLarge(this))
            }
            Slave.EXIT_TYPE3 -> {
                native_ads?.addView(AHandler.getInstance().getBannerRectangle(this))

            }

        }



        exit_btn_no?.setOnClickListener{
            dialog?.dismiss()
        }
        dialog?.setOnDismissListener {
            finish()
        }



    }

    fun appExitExit(view: View?) {
        dialog?.dismiss()
        finishAffinity()
    }

}