package br.com.wolk.softdesign_teste.model

import android.app.Activity
import android.content.Context
import br.com.wolk.softdesign_teste.model.network.EventsApi
import br.com.wolk.softdesign_teste.view.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventsModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor {
                val requestBuilder = it.request().newBuilder()
                requestBuilder.header("Content-type", "application/json")
                requestBuilder.header("Accept", "application/json")
                it.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideEventsApi(client: OkHttpClient): EventsApi {
        val BASE_URL = "https://5f5a8f24d44d640016169133.mockapi.io"

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDataSave(@ApplicationContext context: Context): DataSave {
        return DataSave(context)
    }
}