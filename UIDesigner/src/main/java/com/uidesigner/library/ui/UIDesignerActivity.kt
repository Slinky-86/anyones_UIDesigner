package com.uidesigner.library.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.uidesigner.library.UIDesignerConfig
import com.uidesigner.library.ui.screen.UIDesignerScreen
import com.uidesigner.library.ui.theme.UIDesignerTheme
import com.uidesigner.library.ui.viewmodel.UIDesignerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UIDesignerActivity : ComponentActivity() {
    
    private val viewModel: UIDesignerViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = intent.getParcelableExtra<UIDesignerConfig>("config") 
            ?: UIDesignerConfig()
        
        viewModel.setConfig(config)
        
        setContent {
            UIDesignerTheme(theme = config.theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UIDesignerScreen(
                        viewModel = viewModel,
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}
