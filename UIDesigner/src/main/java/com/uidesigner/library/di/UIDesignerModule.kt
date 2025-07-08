package com.uidesigner.library.di

import com.uidesigner.library.repository.UIDesignerRepository
import com.uidesigner.library.ui.viewmodel.UIDesignerViewModelFactory
import com.uidesigner.library.xml.XMLGenerator
import com.uidesigner.library.xml.XMLParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UIDesignerModule {
    
    @Provides
    @Singleton
    fun provideXMLGenerator(): XMLGenerator {
        return XMLGenerator()
    }
    
    @Provides
    @Singleton
    fun provideXMLParser(): XMLParser {
        return XMLParser()
    }
    
    @Provides
    @Singleton
    fun provideUIDesignerRepository(
        xmlGenerator: XMLGenerator
    ): UIDesignerRepository {
        return UIDesignerRepository(xmlGenerator)
    }
    
    @Provides
    @Singleton
    fun provideUIDesignerViewModelFactory(
        repository: UIDesignerRepository
    ): UIDesignerViewModelFactory {
        return UIDesignerViewModelFactory(repository)
    }
}
