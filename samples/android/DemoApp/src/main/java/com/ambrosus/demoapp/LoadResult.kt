/*
 * Copyright: Ambrosus Inc.
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

package com.ambrosus.demoapp

import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.ambrosus.sdk.utils.Strings
import java.lang.IllegalStateException
import java.lang.NullPointerException

class LoadResult<T:Any> {

    private val result: Any

    constructor(error: Throwable) {
        result = error
    }

    constructor(data: T) {
        result = data
    }

    val data get() = if (result !is Throwable) (result as T) else throw IllegalStateException("wasn't able to get load result")
    val error get() = if (result is Throwable) result else throw IllegalStateException("has a successful result")

    fun isSuccessful() = result !is Throwable

    override fun toString(): String {
        return "${Strings.defaultToString(this)} : $result}"
    }

    companion object {

        fun <T:Any> passResult(liveData: MutableLiveData<LoadResult<T>>, result: T) {
            liveData.value = LoadResult(result)
        }

        fun <T:Any> passError(liveData: MutableLiveData<LoadResult<T>>, t: Throwable) {
            liveData.value = LoadResult(t)
        }

    }
}