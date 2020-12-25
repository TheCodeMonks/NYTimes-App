/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2020 Spikey Sanju
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package www.thecodemonks.techbytes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Network Manager, extends capabilities of  ConnectivityManager#NetworkCallback()
 * by providing a observable callback on network status
 *
 * Author : [https://github.com/ch8n]
 * website : [https://chetangupta.net]
 * Creation Date : 4-08-2020
 */
class NetworkManager(context: Context) : ConnectivityManager.NetworkCallback() {

    private val _connectionStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val observeConnectionStatus: LiveData<Boolean> = _connectionStatusLiveData

    private val appContext: Context = context.applicationContext

    init {
        val connectivityManager = appContext.getSystemService<ConnectivityManager>()

        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallbackCompact(this)

            val connectionStatus = connectivityManager.allNetworks.any { network ->
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                return@any networkCapabilities?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) == true
            }

            _connectionStatusLiveData.value = connectionStatus
        }
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        _connectionStatusLiveData.postValue(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        _connectionStatusLiveData.postValue(false)
    }

    private fun ConnectivityManager.registerNetworkCallbackCompact(networkManager: NetworkManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerDefaultNetworkCallback(networkManager)
        } else {
            val builder = NetworkRequest.Builder()
            registerNetworkCallback(builder.build(), networkManager)
        }
    }
}
