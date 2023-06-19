package com.example.projectorcasting.casting.activities

import android.util.Log
import android.view.Menu
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity

/** This activity will display a full screen player UI for cast */

class ExpandedControlsActivity : ExpandedControllerActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.expanded_controller, menu)
        menu.let { CastButtonFactory.setUpMediaRouteButton(this, it, R.id.media_route_menu_item) }

        Log.d("MainActivity", "playVideo A13 : expanded controls")
        return true
    }
}