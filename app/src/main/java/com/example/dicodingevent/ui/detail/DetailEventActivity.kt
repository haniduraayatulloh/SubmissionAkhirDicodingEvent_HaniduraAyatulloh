package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.ActivityDetailEventBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private lateinit var binding: ActivityDetailEventBinding
    private var currentEvent: EventItem? = null

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId == -1) {
            showError("ID Event tidak valid.")
            finish()
            return
        }

        viewModel.fetchDetailEvent(eventId)

        observeDetailEvent()
        observeFavoriteStatus()

        binding.btnRegister.setOnClickListener {
            currentEvent?.link?.let { openRegistrationLink(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val favoriteItem = menu.findItem(R.id.action_favorite)
        favoriteItem.isVisible = currentEvent != null

        val isFavorite = viewModel.isFavorite.value == true
        favoriteItem.icon = ContextCompat.getDrawable(
            this,
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_favorite -> {
                currentEvent?.let { viewModel.toggleFavorite(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeDetailEvent() {
        viewModel.detailEvent.observe(this) { result ->
            when (result) {
                is EventResult.Loading -> showLoading(true)
                is EventResult.Success -> {
                    showLoading(false)
                    currentEvent = result.data
                    showDetailEvent(result.data)
                }
                is EventResult.Error -> {
                    showLoading(false)
                    showError(result.exception.message ?: getString(R.string.error_event_not_found))
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewModel.isFavorite.observe(this) {
            invalidateOptionsMenu()
        }
    }

    private fun showDetailEvent(event: EventItem) {
        binding.apply {
            ivDetailImage.load(event.getImageUrl()) {
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
            }
            tvDetailName.text = event.name

            val owner = event.ownerName
            if (!owner.isNullOrEmpty()) {
                tvDetailOwner.text = getString(R.string.owner_format, owner)
                tvDetailOwner.visibility = View.VISIBLE
            } else {
                tvDetailOwner.visibility = View.GONE
            }

            val formattedTime = formatDisplayTime(event.beginTime)
            val isTimeValid = formattedTime != getString(R.string.time_unknown)

            if (isTimeValid) {
                tvDetailTime.text = formattedTime
                tvDetailTime.visibility = View.VISIBLE
            } else {
                tvDetailTime.visibility = View.GONE
            }

            val quotaText = when {
                event.isFull() -> getString(R.string.status_full)
                event.getRemainingQuota() != null -> "Sisa Kuota: ${event.getRemainingQuota()}"
                else -> getString(R.string.quota_unknown)
            }
            tvDetailQuota.text = quotaText

            tvDetailSummary.text = event.summary ?: "Tidak ada ringkasan."

            tvDetailDescription.text = if (event.description.isNullOrBlank()) {
                "Tidak ada deskripsi."
            } else {
                val cleanedText = event.description
                    .replace("<br>", "\n\n")
                    .replace("<br />", "\n\n")
                Html.fromHtml(cleanedText, Html.FROM_HTML_MODE_COMPACT).toString().trim()
            }

            btnRegister.visibility = if (event.link.isNullOrBlank()) View.GONE else View.VISIBLE
        }
    }

    private fun openRegistrationLink(link: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_invalid_link), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.detailContentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun formatDisplayTime(time: String?): String {
        val defaultText = getString(R.string.time_unknown)
        if (time.isNullOrEmpty()) return defaultText
        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm 'WIB'", Locale.forLanguageTag("in-ID"))
            val date = apiFormat.parse(time)

            date?.let { displayFormat.format(it) } ?: defaultText
        } catch (e: Exception) {
            Log.e("DetailEventActivity", "Error parsing time: $time. Message: ${e.message}")
            defaultText
        }
    }
}