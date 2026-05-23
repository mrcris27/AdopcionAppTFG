package com.example.adopciontfg.app.di

import android.app.Application
import android.content.Context
import com.example.adopciontfg.data.repository.AnimalRepository
import com.example.adopciontfg.data.repository.ShelterRepository
import com.example.adopciontfg.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideShelterRepository(
        @ApplicationContext context: Context
    ): ShelterRepository = ShelterRepository(context.applicationContext as Application)

    @Provides
    @Singleton
    fun provideAnimalRepository(
        @ApplicationContext context: Context
    ): AnimalRepository = AnimalRepository(context.applicationContext as Application)

    @Provides
    @Singleton
    fun provideUserRepository(
        @ApplicationContext context: Context
    ): UserRepository = UserRepository(context.applicationContext as Application)
}
