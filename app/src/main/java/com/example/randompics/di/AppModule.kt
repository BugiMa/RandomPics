package com.example.randompics.di

import com.example.randompics.RandomPicsApplication
import com.example.randompics.api.LoremPicsumApi
import com.example.randompics.repo.PicturesRepository
import com.example.randompics.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Singleton
    @Provides
    fun provideLoremPicsumApi(): LoremPicsumApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(LoremPicsumApi::class.java)
    }

}

