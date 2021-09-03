package com.example.randompics.di

import com.example.randompics.api.LoremPicsumApi
import com.example.randompics.repo.IPicturesRepository
import com.example.randompics.repo.PicturesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideRepository(
        api: LoremPicsumApi
    ) = PicturesRepository(api) as IPicturesRepository
}