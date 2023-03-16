package com.ganeshgfx.projectmanagement.adapters

import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

@BindingAdapter("setMenuListener")
fun MaterialToolbar.setMenuListener(listener: Toolbar.OnMenuItemClickListener){
    setOnMenuItemClickListener(listener)
}