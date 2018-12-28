package com.ambrosus.ambviewer

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambrosussdk.utils.Section
import java.text.SimpleDateFormat
import java.util.*


class AssetRecyclerAdapter(val eventClickListener: (event: AMBEvent) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>
        () {
    companion object {
        private val TAG: String = "AssetRecyclerAdapter"
        private const val DEFAULT_TYPE: Int = 1
        private const val EVENT_TYPE: Int = 2

    }

    var dataset: List<Section>? = null

    private var context: Context? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = viewGroup.context;
        var v: View? = null
        when (viewType) {
            DEFAULT_TYPE -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.asset_row_item, viewGroup, false)
            }
            EVENT_TYPE -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.asset_row_event, viewGroup, false)
                return EventViewHolder(v)
            }
            else -> {
                v = LayoutInflater.from(context)
                        .inflate(R.layout.asset_row_item, viewGroup, false)
            }
        }
        return DefaultViewHolder(v)
    }


    override fun getItemViewType(position: Int): Int {
        if (dataset?.get(position)?.sectionName == "events") {
            return EVENT_TYPE
        }
        return DEFAULT_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        when (viewHolder.itemViewType) {
            DEFAULT_TYPE -> {
                val defaultViewHolder = viewHolder as DefaultViewHolder
                defaultViewHolder.itemTitle.text = dataset?.get(i)?.sectionName
                var tempTitleTv: TextView
                var tempSubtitleTv: TextView
                for ((key, value) in dataset?.get(i)?.items.orEmpty()) {
                    tempTitleTv = TextView(context)
                    val sb = StringBuilder(key)
                    sb.setCharAt(0, Character.toUpperCase(sb.get(0)))
                    tempTitleTv.text = sb.toString()
                    tempTitleTv.setTypeface(null, Typeface.BOLD);
                    context?.resources?.getColor(R.color.colorTextPrimary)?.let {
                        tempTitleTv.setTextColor(it)
                    }
                    defaultViewHolder.cardViewLayout.addView(tempTitleTv)

                    tempSubtitleTv = TextView(context)
                    if (key.toLowerCase() == "timestamp") {
                        tempSubtitleTv.text = (value as Long).toString()
                    } else {
                        tempSubtitleTv.text = value.toString()
                    }
                    context?.resources?.getColor(R.color.colorTextSecondary)?.let {
                        tempSubtitleTv.setTextColor(it)
                    }
                    defaultViewHolder.cardViewLayout.addView(tempSubtitleTv)
                    tempSubtitleTv.setOnClickListener { context?.let { it1 -> (it as TextView).text.setClipboard(it1, key) } }
                }
            }
            EVENT_TYPE -> {
                val eventViewHolder = viewHolder as EventViewHolder
                eventViewHolder.itemTitle.text = dataset?.get(i)?.sectionName

                var tempCard: View
                var eventTitle: TextView
                var eventSubtitle: TextView
                var eventDate: TextView
                var eventVisibility: TextView
                var line1: View
                var line2: View
                var event: AMBEvent
                val eventList = (dataset?.get(i)?.items?.get("events") as
                        List<AMBEvent>)
                for (j in eventList.indices) {
                    tempCard = LayoutInflater.from(context).inflate(R.layout.single_event_view, null)
                    eventTitle = tempCard.findViewById<TextView>(R.id.eventTitle)
                    eventSubtitle = tempCard.findViewById<TextView>(R.id.eventSubTitle)
                    eventDate = tempCard.findViewById<TextView>(R.id.eventDate)
                    eventVisibility = tempCard.findViewById<TextView>(R.id.eventVisibility)
                    line1 = tempCard.findViewById<View>(R.id.line1)
                    line2 = tempCard.findViewById<View>(R.id.line2)
                    event = eventList[j]
                    eventTitle.setText(event.type ?: event.id)
                    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
                    eventDate.setText(simpleDateFormat.format(Date(event.timestamp.toLong() * 1000)))
                    eventVisibility.setText(generatePrivacy())
                    eventSubtitle.setText(event.locationName ?: generateLocation())
                    line1.bringToFront()
                    line2.bringToFront()
                    line1.visibility = View.GONE
                    line2.visibility = View.GONE
                    if (j == 0) {
                        line1.visibility = View.INVISIBLE
                        line2.visibility = View.VISIBLE
                    } else if (j == 4) {
                        line1.visibility = View.VISIBLE
                        line2.visibility = View.INVISIBLE
                    } else if (eventList.size == 1) {
                        line1.visibility = View.INVISIBLE
                        line2.visibility = View.INVISIBLE
                    } else {
                        line1.visibility = View.VISIBLE
                        line2.visibility = View.VISIBLE
                    }
                    tempCard.tag = event
                    tempCard.setOnClickListener {
                        eventClickListener(it.tag as AMBEvent)
                    }
                    eventViewHolder.eventsContainer.addView(tempCard)
                }

            }
            else -> {
            }
        }

    }

    private fun generateLocation(): String {
        val random = (0..3).shuffled().last()

        return when (random) {
            0 -> "Wisconsin, United States"
            1 -> "New York, NY"
            else -> ""
        }
    }

    private fun generatePrivacy(): String {
        val random = (0..2).shuffled().last()

        return when (random) {
            0 -> "Public"
            else -> "Private"
        }
    }

    inner class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var cardViewLayout: LinearLayout

        init {
            itemTitle = itemView.findViewById(R.id.title)
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout)
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var eventsContainer: LinearLayout

        init {
            itemTitle = itemView.findViewById(R.id.title)
            eventsContainer = itemView.findViewById(R.id.eventsContainer)
        }
    }

    override fun getItemCount(): Int {
        return dataset?.size!!
    }

    fun getLineSeparator(): CharSequence {
        return System.getProperty("line.separator")
    }
}