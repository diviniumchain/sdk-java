package com.ambrosus.ambviewer

import android.content.Context
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import com.ambrosus.ambrosussdk.model.AMBAsset
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.ambrosus.ambrosussdk.utils.Section
import kotlinx.android.synthetic.main.activity_asset.*


class AssetActivity : AppCompatActivity() {

    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var toolbarImage: ImageView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: AssetRecyclerAdapter? = null
    private var barcodeType: Int? = null

    private lateinit var asset: AMBAsset

    private var events: List<AMBEvent> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset)
        asset = intent.extras.getSerializable("asset") as AMBAsset
        events = asset.events ?: ArrayList()
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        toolbarImage = findViewById(R.id.toolbarImage)

        assetToolbar.setTitleTextColor(resources.getColor(R.color.colorTextPrimary))
        setSupportActionBar(assetToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        layoutManager = LinearLayoutManager(this)
        rvAssetList.layoutManager = layoutManager
        adapter = AssetRecyclerAdapter({
            IntentsUtil.runEventActivity(this,
                    it)
        })

        if (events.firstOrNull() != null) {
            val imageUrl = asset.imageUrl
            if (toolbarImage != null && imageUrl
                    != null) {
                GlideApp.with(this).load(imageUrl).placeholder(R.drawable.placeholder_logo).into(toolbarImage!!)
            }
        }
        collapsingToolbarLayout!!.title = asset.name ?: asset.id
        var dataset = asset.formattedSections?.toMutableList()
        val map: Map<String, Any> = hashMapOf("events" to events)
        dataset?.add(Section("events", map))
        adapter?.dataset = dataset
        rvAssetList.adapter = adapter
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

fun CharSequence.setClipboard(context: Context, label: String) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
        clipboard.text = this
    } else {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", this)
        clipboard.primaryClip = clip
    }
    Toast.makeText(context, "Copied $label!", Toast
            .LENGTH_LONG).show()
}