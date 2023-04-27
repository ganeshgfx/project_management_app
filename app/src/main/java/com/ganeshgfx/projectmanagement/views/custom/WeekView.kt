package com.ganeshgfx.projectmanagement.views.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.ganeshgfx.projectmanagement.Utils.getLastDay
import com.ganeshgfx.projectmanagement.Utils.makeShape
import com.ganeshgfx.projectmanagement.models.Day
import com.google.android.material.card.MaterialCardView
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.shape.ShapeAppearanceModel

class WeekView(
    private val context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    class CardShape(private val displayMetrics: DisplayMetrics) {

        private val SMALL = 0f
        private val LARGE = 25f

//        val str = randomString(10)

        @ColorInt
        val activeColor = Color.argb(100, 225, 217, 102)
        val taskStart: ShapeAppearanceModel = makeShape(
            cornerValue(SMALL),
            cornerValue(LARGE),
            cornerValue(SMALL),
            cornerValue(LARGE),
        )
        val taskMiddle: ShapeAppearanceModel = makeShape(
            cornerValue(SMALL),
            cornerValue(SMALL),
            cornerValue(SMALL),
            cornerValue(SMALL),
        )
        val taskEnd: ShapeAppearanceModel = makeShape(
            cornerValue(LARGE),
            cornerValue(SMALL),
            cornerValue(LARGE),
            cornerValue(SMALL),
        )

        private fun cornerValue(radius: Float) =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                radius,
                displayMetrics
            )

        companion object {
            private var instance: CardShape? = null

            fun getInstance(displayMetrics: DisplayMetrics): CardShape {
                if (instance == null) {
                    instance = CardShape(displayMetrics)
                }
                return instance as CardShape
            }

            fun destroy() {
                instance = null
            }
        }
    }

    val shape = CardShape.getInstance(context.resources.displayMetrics)

    fun setWeek(week: List<Day>) {
        removeAllViews()
        orientation = HORIZONTAL

        var newMonth = false
        val lastDay = getLastDay(week[0].year, week[0].month)
        val days = week.map { it.day }
        var lastWeek = days.contains(lastDay)
        for (day in week) {
            if (day.day == 1) {
                newMonth = true
                lastWeek = false
            }
            makeView(day, newMonth, lastWeek, lastDay)
        }
    }

    private fun makeView(day: Day, showMonthLine: Boolean, lastWeek: Boolean, lastDay: Int) {

        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (showMonthLine || lastWeek) {

            val divider = MaterialDivider(context)
            val width = 2.dp
            var dividerParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                width
            )
            if (showMonthLine) {
                dividerParams.gravity = Gravity.TOP
            }
            if (lastWeek) {
                dividerParams.gravity = Gravity.BOTTOM
            }
            if (day.day == lastDay) {
                dividerParams = FrameLayout.LayoutParams(
                    width,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                dividerParams.gravity = Gravity.END

                val dividerEndParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    width
                )
                val dividerLast = MaterialDivider(context)
                dividerEndParams.gravity = Gravity.BOTTOM
                dividerLast.layoutParams = dividerEndParams
                frameLayout.addView(dividerLast)
            }
            divider.layoutParams = dividerParams
            divider.setBackgroundColor(Color.GRAY)
            frameLayout.addView(divider)
        }

        val cardView = MaterialCardView(context)
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1F
        )
        layoutParams.gravity = Gravity.CENTER
        // layoutParams.topMargin = 5.dp
        cardView.layoutParams = layoutParams
        cardView.radius = 0F
        cardView.strokeColor = Color.argb(0, 0, 0, 0)
        cardView.setCardBackgroundColor(shape.activeColor)
        val range = 5..10
        day?.let {
            when (it.day) {
                range.first -> {
                    cardView.shapeAppearanceModel = shape.taskStart
                }
                range.last -> {
                    cardView.shapeAppearanceModel = shape.taskEnd
                }
                in range -> {
                    cardView.shapeAppearanceModel = shape.taskMiddle
                }
                else -> cardView.setCardBackgroundColor(Color.TRANSPARENT)
            }
        }

        cardView.isClickable = true
        cardView.setOnClickListener {
            onClickListener?.let { it -> it(day) }
        }

        val textView = TextView(context)
        val textViewLayoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textViewLayoutParams.gravity = Gravity.CENTER
        textView.layoutParams = textViewLayoutParams
        textView.text = day?.text ?: ""
        textView.textAlignment = TEXT_ALIGNMENT_CENTER
//        cardView.addView(textView)
        //ltrb
        textView.setPadding(0.dp, 25.dp, 0.dp, 25.dp)

        frameLayout.addView(textView)

        cardView.addView(frameLayout)
        addView(cardView)

    }

    private var onClickListener: ((Day?) -> Unit)? = null
    fun setOnClickListener(listener: (Day?) -> Unit) {
        onClickListener = listener
    }

    val Int.dp get() = (this * resources.displayMetrics.density + 0.5f).toInt()

}