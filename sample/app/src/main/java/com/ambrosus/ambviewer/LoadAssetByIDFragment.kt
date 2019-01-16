package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_asset_search.*
import kotlinx.android.synthetic.main.loading_indicator_small.*
import java.io.Serializable

class LoadAssetByIDFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().asset.observe(this, Observer {
            loadingIndicatorSmall.visibility = View.GONE
            if(it != null) {
                message.text = if(it.isSuccessful()) "${it.data}" else AMBSampleApp.errorHandler.getErrorMessage(it.error)
            }
        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_asset_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message.text = "Loading Asset ${ARG_ID.get(this)}"
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Loading")
    }

    private fun getViewModel(): AssetViewModel {
        return ViewModelProviders.of(
                this,
                AssetViewModelFactory(ARG_ID.get(this), AMBSampleApp.network)
        ).get(AssetViewModel::class.java)
    }

    companion object {

        fun createFor(assetID: String): LoadAssetByIDFragment {
            return ARG_ID.putTo(LoadAssetByIDFragment(), assetID)
        }

    }

}