package com.example.dicodingevent.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(
    private val onItemClick: (EventItem) -> Unit,
    private val onFavoriteClick: (EventItem) -> Unit
) : ListAdapter<EventItem, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventItem) {
            binding.apply {
                ivEventImage.load(event.getImageUrl()) {
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                }

                tvEventName.text = event.name

                val ownerText = event.ownerName
                if (!ownerText.isNullOrEmpty()) {
                    tvEventOwner.text = itemView.context.getString(R.string.owner_format, ownerText)
                    tvEventOwner.visibility = View.VISIBLE
                } else {
                    tvEventOwner.visibility = View.GONE
                }

                val formattedTime = formatDisplayTime(event.beginTime, itemView.context.getString(R.string.time_unknown))
                val isTimeValid = formattedTime != itemView.context.getString(R.string.time_unknown)

                if (isTimeValid) {
                    tvEventTime.text = formattedTime
                    tvEventTime.visibility = View.VISIBLE
                } else {
                    tvEventTime.visibility = View.GONE
                }


                tvEventStatus.text = when {
                    event.isFull() -> itemView.context.getString(R.string.status_full)
                    event.getRemainingQuota() != null -> "Sisa Kuota: ${event.getRemainingQuota()}"
                    else -> itemView.context.getString(R.string.quota_unknown)
                }

                val favIcon = if (event.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                ivFavoriteToggle.setImageDrawable(ContextCompat.getDrawable(ivFavoriteToggle.context, favIcon))

                root.setOnClickListener { onItemClick(event) }
                ivFavoriteToggle.setOnClickListener { onFavoriteClick(event) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun formatDisplayTime(time: String?, defaultText: String): String {
        if (time.isNullOrEmpty()) return defaultText
        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val displayFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("in-ID"))
            val date = apiFormat.parse(time)

            date?.let { displayFormat.format(it) } ?: defaultText
        } catch (e: Exception) {
            Log.e("EventAdapter", "Error parsing time: $time. Message: ${e.message}")
            defaultText
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean = oldItem == newItem
    }
}