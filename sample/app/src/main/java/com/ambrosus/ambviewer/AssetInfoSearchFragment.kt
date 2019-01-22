/*
 * Copyright: Ambrosus Technologies GmbH
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_status.*
import java.io.Serializable

class AssetInfoSearchFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().assetsList.observe(this, Observer {
            if(it != null) {
                if(it.isSuccessful()) {
                    getListener()?.onSearchResult(it.data, getSearchCriteria())
                } else {
                    loadingContainer.visibility = View.GONE
                    resultContainer.visibility = View.VISIBLE
                    statusMessage.text = AMBSampleApp.errorHandler.getErrorMessage(it.error)
                }
            }
        });
    }


    private fun getViewModel(): AssetInfoSearchViewModel {
        return ViewModelProviders.of(
                this,
                AMBAssetInfoSearchViewModelFactory(getSearchCriteria(), AMBSampleApp.network)
        ).get(AssetInfoSearchViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingMessage.text = "Searching for AMBAssetInfo ${getSearchCriteria()}"
        resultContainer.setOnClickListener {
            getListener()?.onCancel()
        }
    }

    private fun getSearchCriteria() = ARG_SEARCH_CRITERIA.get(this)

    private fun getListener() = parentFragment as? SearchResultListener

    interface SearchResultListener {

        fun onSearchResult(result: List<AMBAssetInfo>, searchCriteria: Any)
        fun onCancel()

    }

    companion object {

        private val ARG_SEARCH_CRITERIA = BundleArgument<Serializable>("ARG_SEARCH_CRITERIA", Serializable::class.java)

        fun createFor(assetID: String): AssetInfoSearchFragment {
            return createForCriteria(assetID)
        }

        fun createFor(identifier: Identifier): AssetInfoSearchFragment {
            return createForCriteria(identifier)
        }

        
        fun getArguments(assetID: String): Bundle {
            return getArgumentsByCriteria(assetID)
        }

        fun getArguments(identifier: Identifier): Bundle {
            return getArgumentsByCriteria(identifier)
        }

        private fun getArgumentsByCriteria(searchCriteria: Serializable): Bundle {
            return ARG_SEARCH_CRITERIA.put(Bundle(), searchCriteria)
        }

        private fun createForCriteria(searchCriteria: Serializable): AssetInfoSearchFragment {
            val result = AssetInfoSearchFragment()
            result.arguments = getArgumentsByCriteria(searchCriteria)
            return result;
        }


    }

}