package ru.profitsw2000.core.view

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.profitsw2000.core.R
import ru.profitsw2000.core.utils.constants.TAG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GraphMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            // Format your data here
            tvContent.text = "${formatDate(e.x)}\n${e.y}°C"
        }
        super.refreshContent(e, highlight)
    }

    private fun formatDate(xValue: Float): String {
        // Convert float timestamp to date string
        val date = Date(xValue.toLong())
        return SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault()).format(date)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }

    // Override to use touch position instead of entry position
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        // Use stored touch position
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        val translateX = posX + offset.x
        val translateY = chartView.height*0.02f

        canvas.save()
        canvas.translate(translateX, translateY)
        draw(canvas)
        canvas.restore()
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val offset = getOffset()
        val chart = chartView
        val markerWidth = width

        // Adjust position if marker would go off screen
        if (chart != null) {
            val chartWidth = chart.width

            if (posX + offset.x < 0) {
                offset.x = -posX                                            // If marker would go off left edge
            } else if (posX + markerWidth + offset.x > chartWidth) {
                offset.x = chartWidth - posX - markerWidth                  // If marker would go off right edge
            }
        }

        return offset
    }

}