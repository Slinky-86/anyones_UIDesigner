package com.uidesigner.library.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uidesigner.library.repository.UIDesignerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIDesignerViewModelFactory @Inject constructor(
    private val repository: UIDesignerRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UIDesignerViewModel::class.java) -> {
                UIDesignerViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
