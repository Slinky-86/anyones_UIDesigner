package com.uidesigner.library.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uidesigner.library.ui.components.*
import com.uidesigner.library.ui.viewmodel.UIDesignerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIDesignerScreen(
    viewModel: UIDesignerViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val config by viewModel.config.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Toolbar
        TopAppBar(
            title = { Text("UI Designer") },
            actions = {
                ToolbarActions(
                    onUndo = { viewModel.undo() },
                    onRedo = { viewModel.redo() },
                    onClear = { viewModel.clearCanvas() },
                    onGenerateXML = { 
                        // Handle XML generation
                        val xml = viewModel.generateXML()
                        // Show XML in dialog or export
                    },
                    onClose = onClose
                )
            }
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Component Palette
            ComponentPalette(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                onComponentSelected = { type, x, y ->
                    viewModel.addComponent(type, x, y)
                }
            )
            
            // Main Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                DesignCanvas(
                    components = uiState.components,
                    selectedComponentId = uiState.selectedComponentId,
                    gridSize = config.gridSize,
                    snapToGrid = config.snapToGrid,
                    onComponentSelected = { componentId ->
                        viewModel.selectComponent(componentId)
                    },
                    onComponentMoved = { componentId, deltaX, deltaY ->
                        viewModel.moveComponent(componentId, deltaX, deltaY)
                    },
                    onComponentResized = { componentId, width, height ->
                        viewModel.resizeComponent(componentId, width, height)
                    },
                    onComponentDeleted = { componentId ->
                        viewModel.deleteComponent(componentId)
                    }
                )
            }
            
            // Properties Panel
            PropertiesPanel(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                selectedComponent = uiState.components.find { it.id == uiState.selectedComponentId },
                onPropertyChanged = { propertyName, value ->
                    uiState.selectedComponentId?.let { componentId ->
                        viewModel.updateComponentProperty(componentId, propertyName, value)
                    }
                }
            )
        }
    }
}
