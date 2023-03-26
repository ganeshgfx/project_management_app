package com.ganeshgfx.projectmanagement.adapters

class OnClickListener(val clickListener: (pos: Int) -> Unit) {
    fun onClick(pos: Int) = clickListener(pos)
}