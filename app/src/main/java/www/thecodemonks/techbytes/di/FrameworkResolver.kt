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

package www.thecodemonks.techbytes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import www.thecodemonks.techbytes.datastore.UIModeDataStore
import www.thecodemonks.techbytes.db.AppDatabase
import www.thecodemonks.techbytes.utils.NetworkManager
import javax.inject.Singleton

// this module resolve all the hard android framework dependent objects

@Module
@InstallIn(SingletonComponent::class)
object FrameworkResolver {

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

    @Singleton
    @Provides
    fun providesUIModelDataStore(@ApplicationContext context: Context): UIModeDataStore {
        return UIModeDataStore(context)
    }

    @Singleton
    @Provides
    fun providesNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManager(context)
    }
}
