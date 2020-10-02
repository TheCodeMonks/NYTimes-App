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

package www.thecodemonks.techbytes.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_base.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.ui.viewmodel.NewsViewModelProviderFactory
import www.thecodemonks.techbytes.worker.MyWorker
import java.util.concurrent.TimeUnit


class BaseActivity : AppCompatActivity() {

    lateinit var viewModel: ArticleViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)
        // setup VMProviderFactory
        val repo = Repo(
            ArticleDatabase(this)
        )

        // Passing application to ViewModel for DataStore
        val viewModelProviderFactory = NewsViewModelProviderFactory(this.application, repo)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(ArticleViewModel::class.java)

        // setup workManager
        initWorker()

        // init nav controller with back action button
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    private fun initWorker() {
        // worker constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // periodic worker for every 15 minutes
        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        // enqueue work
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(JOB_TAG, KEEP, notificationWorkRequest)

    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val JOB_TAG = "articlesWorkerTag"
    }
}