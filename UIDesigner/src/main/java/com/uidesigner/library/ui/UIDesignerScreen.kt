package com.uidesigner.library.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uidesigner.library.UIDesignerConfig
import com.uidesigner.library.UIDesignerTheme
import com.uidesigner.library.ui.components.*
import com.uidesigner.library.ui.theme.UIDesignerTheme as UIDesignerMaterialTheme
import com.uidesigner.library.ui.viewmodel.UIDesignerViewModel
import com.uidesigner.library.ui.viewmodel.UIDesignerViewModelFactory

@Composable
fun UIDesignerScreen(
    config: UIDesignerConfig = UIDesignerConfig(),
    theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
    viewModelFactory: UIDesignerViewModelFactory,
    onXMLGenerated: (String) -> Unit = {},
    onProjectSaved: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: UIDesignerViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<com.uidesigner.library.model.ComponentCategory?>(null) }
    var showComponentPalette by remember { mutableStateOf(true) }
    var showHierarchy by remember { mutableStateOf(true) }
    var showProperties by remember { mutableStateOf(true) }
    var canvasZoom by remember { mutableStateOf(1f) }
    
    LaunchedEffect(config) {
        viewModel.setConfig(config)
    }
    
    UIDesignerMaterialTheme(theme = theme) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Advanced Toolbar
            AdvancedToolbar(
                canUndo = uiState.components.isNotEmpty(), // Simplified for now
                canRedo = false, // Simplified for now
                zoom = canvasZoom,
                showComponentPalette = showComponentPalette,
                showHierarchy = showHierarchy,
                showProperties = showProperties,
                onUndo = viewModel::undo,
                onRedo = viewModel::redo,
                onZoomIn = { canvasZoom = (canvasZoom * 1.2f).coerceAtMost(3f) },
                onZoomOut = { canvasZoom = (canvasZoom / 1.2f).coerceAtLeast(0.25f) },
                onZoomReset = { canvasZoom = 1f },
                onToggleComponentPalette = { showComponentPalette = !showComponentPalette },
                onToggleHierarchy = { showHierarchy = !showHierarchy },
                onToggleProperties = { showProperties = !showProperties },
                onClear = viewModel::clearCanvas,
                onGenerateXML = {
                    val xml = viewModel.generateXML()
                    onXMLGenerated(xml)
                },
                onSaveProject = {
                    // Implement project saving
                    onProjectSaved("current_project")
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Main workspace
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Left panel - Component Palette
                if (showComponentPalette) {
                    Column {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search components...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            modifier = Modifier
                                .width(280.dp)
                                .padding(bottom = 8.dp)
                        )
                        
                        AdvancedComponentPalette(
                            onComponentSelected = { componentType ->
                                // Add component at center of visible canvas
                                viewModel.addComponent(componentType, 200f, 200f)
                            },
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                
                // Center - Design Canvas
                AdvancedDesignCanvas(
                    components = uiState.components,
                    selectedComponentId = uiState.selectedComponentId,
                    config = config,
                    zoom = canvasZoom,
                    onComponentSelected = viewModel::selectComponent,
                    onComponentMoved = viewModel::moveComponent,
                    onComponentResized = viewModel::resizeComponent,
                    onMultipleComponentsSelected = { componentIds ->
                        // Handle multiple selection
                        if (componentIds.isNotEmpty()) {
                            viewModel.selectComponent(componentIds.first())
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = if (showComponentPalette || showHierarchy || showProperties) 8.dp else 0.dp)
                )
                
                // Right panels
                Row {
                    // Component Hierarchy
                    if (showHierarchy) {
                        ComponentHierarchy(
                            components = uiState.components,
                            selectedComponentId = uiState.selectedComponentId,
                            onComponentSelected = viewModel::selectComponent,
                            onComponentVisibilityToggled = { componentId ->
                                viewModel.toggleComponentVisibility(componentId)
                            },
                            onComponentLockToggled = { componentId ->
                                viewModel.toggleComponentLock(componentId)
                            },
                            onComponentDeleted = viewModel::deleteComponent,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    // Properties Panel
                    if (showProperties) {
                        AdvancedPropertiesPanel(
                            selectedComponent = uiState.components.find { it.id == uiState.selectedComponentId },
                            onPropertyChanged = { propertyName, value ->
                                uiState.selectedComponentId?.let { componentId ->
                                    when (propertyName) {
                                        "x" -> value.toFloatOrNull()?.let { 
                                            val currentComponent = uiState.components.find { it.id == componentId }
                                            currentComponent?.let {
                                                viewModel.moveComponent(componentId, it - it.x, 0f)
                                            }
                                        }
                                        "y" -> value.toFloatOrNull()?.let { 
                                            val currentComponent = uiState.components.find { it.id == componentId }
                                            currentComponent?.let {
                                                viewModel.moveComponent(componentId, 0f, it - it.y)
                                            }
                                        }
                                        "width" -> value.toFloatOrNull()?.let { 
                                            val currentComponent = uiState.components.find { it.id == componentId }
                                            currentComponent?.let {
                                                viewModel.resizeComponent(componentId, it, it.height)
                                            }
                                        }
                                        "height" -> value.toFloatOrNull()?.let { 
                                            val currentComponent = uiState.components.find { it.id == componentId }
                                            currentComponent?.let {
                                                viewModel.resizeComponent(componentId, it.width, it)
                                            }
                                        }
                                        else -> viewModel.updateComponentProperty(componentId, propertyName, value)
                                    }
                                }
                            },
                            onConstraintChanged = { constraintSet ->
                                uiState.selectedComponentId?.let { componentId ->
                                    viewModel.updateComponentConstraints(componentId, constraintSet)
                                }
                            },
                            onStyleChanged = { updatedComponent ->
                                viewModel.updateComponent(updatedComponent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedToolbar(
    canUndo: Boolean,
    canRedo: Boolean,
    zoom: Float,
    showComponentPalette: Boolean,
    showHierarchy: Boolean,
    showProperties: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomReset: () -> Unit,
    onToggleComponentPalette: () -> Unit,
    onToggleHierarchy: () -> Unit,
    onToggleProperties: () -> Unit,
    onClear: () -> Unit,
    onGenerateXML: () -> Unit,
    onSaveProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section - Title and main actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "UI Designer Pro",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.primary
                )
                
                // Undo/Redo
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) MaterialTheme.colors.onSurface 
                                  else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    
                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) MaterialTheme.colors.onSurface 
                                  else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
                
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                )
                
                // Zoom controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onZoomOut) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                    }
                    
                    TextButton(onClick = onZoomReset) {
                        Text("${(zoom * 100).toInt()}%")
                    }
                    
                    IconButton(onClick = onZoomIn) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                    }
                }
            }
            
            // Center section - Panel toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onToggleComponentPalette,
                    colors = IconButtonDefaults.iconButtonColors(
                        backgroundColor = if (showComponentPalette) MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                         else androidx.compose.ui.graphics.Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Toggle Components",
                        tint = if (showComponentPalette) MaterialTheme.colors.primary 
                              else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                IconButton(
                    onClick = onToggleHierarchy,
                    colors = IconButtonDefaults.iconButtonColors(
                        backgroundColor = if (showHierarchy) MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                         else androidx.compose.ui.graphics.Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = "Toggle Hierarchy",
                        tint = if (showHierarchy) MaterialTheme.colors.primary 
                              else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                IconButton(
                    onClick = onToggleProperties,
                    colors = IconButtonDefaults.iconButtonColors(
                        backgroundColor = if (showProperties) MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                         else androidx.compose.ui.graphics.Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Toggle Properties",
                        tint = if (showProperties) MaterialTheme.colors.primary 
                              else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Right section - Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onClear,
                    colors = IconButtonDefaults.iconButtonColors(
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Canvas",
                        tint = MaterialTheme.colors.error
                    )
                }
                
                Button(
                    onClick = onSaveProject,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
                
                Button(
                    onClick = onGenerateXML,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Generate XML")
                }
            }
        }
    }
}
