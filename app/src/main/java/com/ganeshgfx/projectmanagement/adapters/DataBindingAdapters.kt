package com.ganeshgfx.projectmanagement.adapters

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.models.Status
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel

@BindingAdapter("setMenuListener")
fun MaterialToolbar.setMenuListener(listener: Toolbar.OnMenuItemClickListener) {
    setOnMenuItemClickListener(listener)
}

@BindingAdapter("setDynamicStatusIcon")
fun View.setDynamicStatusIcon(status: Status) {
    var iconResource = 0
    var iconTintResource = 0
    var backgroundRes = 0
    when (status) {
        Status.PENDING -> {
            iconResource = R.drawable.outline_circle_24
            iconTintResource = R.color.control
            backgroundRes = R.color.controlBack
        }
        Status.IN_PROGRESS -> {
            iconResource = R.drawable.twotone_draw_24
            iconTintResource = R.color.taskIn
            backgroundRes = R.color.taskInBack
        }
        Status.DONE -> {
            iconResource = R.drawable.round_done_outline_24
            iconTintResource = R.color.taskDone
            backgroundRes = R.color.taskDoneBack
        }
    }
    (this as MaterialButton).setIconResource(iconResource)
    (this as MaterialButton).setIconTintResource(iconTintResource)
    (this as MaterialButton).setBackgroundColor(ContextCompat.getColor(context, backgroundRes))
    //(this as MaterialButton).setBackgroundResource(iconTintResource)
}

@BindingAdapter("setUrl")
fun ImageView.setUrl(url:String){
    Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.twotone_tag_faces_24)
        .into(this)
}