package com.uidesigner.library.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.*

@Composable
fun AdvancedPropertiesPanel(
    selectedComponent: UIComponent?,
    onPropertyChanged: (String, String) -> Unit,
    onConstraintChanged: (ConstraintSet) -> Unit,
    onStyleChanged: (UIComponent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(PropertyTab.BASIC) }
    var expandedSections by remember { mutableStateOf(setOf<PropertySection>()) }
    
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with tabs
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Properties",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Property tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    backgroundColor = MaterialTheme.colors.surface,
                    contentColor = MaterialTheme.colors.primary,
                    edgePadding = 0.dp
                ) {
                    PropertyTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tab.displayName,
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        )
                    }
                }
            }
            
            Divider()
            
            // Content
            if (selectedComponent != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (selectedTab) {
                        PropertyTab.BASIC -> {
                            item {
                                BasicPropertiesSection(
                                    component = selectedComponent,
                                    onPropertyChanged = onPropertyChanged,
                                    isExpanded = expandedSections.contains(PropertySection.BASIC),
                                    onToggleExpanded = { toggleSection(PropertySection.BASIC, expandedSections) { expandedSections = it } }
                                )
                            }
                            
                            item {
                                LayoutPropertiesSection(
                                    component = selectedComponent,
                                    onPropertyChanged = onPropertyChanged,
                                    isExpanded = expandedSections.contains(PropertySection.LAYOUT),
                                    onToggleExpanded = { toggleSection(PropertySection.LAYOUT, expandedSections) { expandedSections = it } }
                                )
                            }
                        }
                        
                        PropertyTab.LAYOUT -> {
                            item {
                                ConstraintPropertiesSection(
                                    component = selectedComponent,
                                    onConstraintChanged = onConstraintChanged,
                                    isExpanded = expandedSections.contains(PropertySection.CONSTRAINTS),
                                    onToggleExpanded = { toggleSection(PropertySection.CONSTRAINTS, expandedSections) { expandedSections = it } }
                                )
                            }
                            
                            item {
                                MarginsAndPaddingSection(
                                    component = selectedComponent,
                                    onStyleChanged = onStyleChanged,
                                    isExpanded = expandedSections.contains(PropertySection.SPACING),
                                    onToggleExpanded = { toggleSection(PropertySection.SPACING, expandedSections) { expandedSections = it } }
                                )
                            }
                        }
                        
                        PropertyTab.APPEARANCE -> {
                            item {
                                BackgroundStyleSection(
                                    component = selectedComponent,
                                    onStyleChanged = onStyleChanged,
                                    isExpanded = expandedSections.contains(PropertySection.BACKGROUND),
                                    onToggleExpanded = { toggleSection(PropertySection.BACKGROUND, expandedSections) { expandedSections = it } }
                                )
                            }
                            
                            if (selectedComponent.textStyle != null) {
                                item {
                                    TextStyleSection(
                                        component = selectedComponent,
                                        onStyleChanged = onStyleChanged,
                                        isExpanded = expandedSections.contains(PropertySection.TEXT),
                                        onToggleExpanded = { toggleSection(PropertySection.TEXT, expandedSections) { expandedSections = it } }
                                    )
                                }
                            }
                        }
                        
                        PropertyTab.ADVANCED -> {
                            item {
                                TransformPropertiesSection(
                                    component = selectedComponent,
                                    onStyleChanged = onStyleChanged,
                                    isExpanded = expandedSections.contains(PropertySection.TRANSFORM),
                                    onToggleExpanded = { toggleSection(PropertySection.TRANSFORM, expandedSections) { expandedSections = it } }
                                )
                            }
                            
                            item {
                                AnimationPropertiesSection(
                                    component = selectedComponent,
                                    onStyleChanged = onStyleChanged,
                                    isExpanded = expandedSections.contains(PropertySection.ANIMATION),
                                    onToggleExpanded = { toggleSection(PropertySection.ANIMATION, expandedSections) { expandedSections = it } }
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Select a component\nto edit properties",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertySectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onToggle() }
            .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colors.primary
        )
        
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun BasicPropertiesSection(
    component: UIComponent,
    onPropertyChanged: (String, String) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Basic Properties",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyTextField(
                    label = "ID",
                    value = component.id,
                    onValueChange = { onPropertyChanged("id", it) },
                    readOnly = true
                )
                
                PropertyTextField(
                    label = "Width",
                    value = component.width.toString(),
                    onValueChange = { onPropertyChanged("width", it) },
                    keyboardType = KeyboardType.Number
                )
                
                PropertyTextField(
                    label = "Height",
                    value = component.height.toString(),
                    onValueChange = { onPropertyChanged("height", it) },
                    keyboardType = KeyboardType.Number
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyTextField(
                        label = "X",
                        value = component.x.toString(),
                        onValueChange = { onPropertyChanged("x", it) },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    
                    PropertyTextField(
                        label = "Y",
                        value = component.y.toString(),
                        onValueChange = { onPropertyChanged("y", it) },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LayoutPropertiesSection(
    component: UIComponent,
    onPropertyChanged: (String, String) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Layout Properties",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Component-specific properties
                component.properties.forEach { (key, value) ->
                    PropertyTextField(
                        label = key,
                        value = value,
                        onValueChange = { onPropertyChanged(key, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConstraintPropertiesSection(
    component: UIComponent,
    onConstraintChanged: (ConstraintSet) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Constraints",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Constraint properties will be implemented here",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun MarginsAndPaddingSection(
    component: UIComponent,
    onStyleChanged: (UIComponent) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Margins & Padding",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Margins
                Text(
                    text = "Margins",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyTextField(
                        label = "Start",
                        value = component.margins.start.toString(),
                        onValueChange = { 
                            val newMargins = component.margins.copy(start = it.toFloatOrNull() ?: 0f)
                            onStyleChanged(component.copy(margins = newMargins))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    
                    PropertyTextField(
                        label = "Top",
                        value = component.margins.top.toString(),
                        onValueChange = { 
                            val newMargins = component.margins.copy(top = it.toFloatOrNull() ?: 0f)
                            onStyleChanged(component.copy(margins = newMargins))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyTextField(
                        label = "End",
                        value = component.margins.end.toString(),
                        onValueChange = { 
                            val newMargins = component.margins.copy(end = it.toFloatOrNull() ?: 0f)
                            onStyleChanged(component.copy(margins = newMargins))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    
                    PropertyTextField(
                        label = "Bottom",
                        value = component.margins.bottom.toString(),
                        onValueChange = { 
                            val newMargins = component.margins.copy(bottom = it.toFloatOrNull() ?: 0f)
                            onStyleChanged(component.copy(margins = newMargins))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundStyleSection(
    component: UIComponent,
    onStyleChanged: (UIComponent) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Background & Style",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyTextField(
                    label = "Background Color",
                    value = component.background.color,
                    onValueChange = { 
                        val newBackground = component.background.copy(color = it)
                        onStyleChanged(component.copy(background = newBackground))
                    }
                )
                
                PropertyTextField(
                    label = "Corner Radius",
                    value = component.background.cornerRadius.toString(),
                    onValueChange = { 
                        val newBackground = component.background.copy(cornerRadius = it.toFloatOrNull() ?: 0f)
                        onStyleChanged(component.copy(background = newBackground))
                    },
                    keyboardType = KeyboardType.Number
                )
                
                PropertyTextField(
                    label = "Elevation",
                    value = component.elevation.toString(),
                    onValueChange = { 
                        onStyleChanged(component.copy(elevation = it.toFloatOrNull() ?: 0f))
                    },
                    keyboardType = KeyboardType.Number
                )
            }
        }
    }
}

@Composable
private fun TextStyleSection(
    component: UIComponent,
    onStyleChanged: (UIComponent) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Text Style",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                component.textStyle?.let { textStyle ->
                    PropertyTextField(
                        label = "Text Size",
                        value = textStyle.textSize.toString(),
                        onValueChange = { 
                            val newTextStyle = textStyle.copy(textSize = it.toFloatOrNull() ?: 14f)
                            onStyleChanged(component.copy(textStyle = newTextStyle))
                        },
                        keyboardType = KeyboardType.Number
                    )
                    
                    PropertyTextField(
                        label = "Text Color",
                        value = textStyle.textColor,
                        onValueChange = { 
                            val newTextStyle = textStyle.copy(textColor = it)
                            onStyleChanged(component.copy(textStyle = newTextStyle))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransformPropertiesSection(
    component: UIComponent,
    onStyleChanged: (UIComponent) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Transform",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyTextField(
                    label = "Rotation",
                    value = component.rotation.toString(),
                    onValueChange = { 
                        onStyleChanged(component.copy(rotation = it.toFloatOrNull() ?: 0f))
                    },
                    keyboardType = KeyboardType.Number
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyTextField(
                        label = "Scale X",
                        value = component.scaleX.toString(),
                        onValueChange = { 
                            onStyleChanged(component.copy(scaleX = it.toFloatOrNull() ?: 1f))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    
                    PropertyTextField(
                        label = "Scale Y",
                        value = component.scaleY.toString(),
                        onValueChange = { 
                            onStyleChanged(component.copy(scaleY = it.toFloatOrNull() ?: 1f))
                        },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                PropertyTextField(
                    label = "Alpha",
                    value = component.alpha.toString(),
                    onValueChange = { 
                        onStyleChanged(component.copy(alpha = it.toFloatOrNull()?.coerceIn(0f, 1f) ?: 1f))
                    },
                    keyboardType = KeyboardType.Number
                )
            }
        }
    }
}

@Composable
private fun AnimationPropertiesSection(
    component: UIComponent,
    onStyleChanged: (UIComponent) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column {
        PropertySectionHeader(
            title = "Animations",
            isExpanded = isExpanded,
            onToggle = onToggleExpanded
        )
        
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Animation properties will be implemented here",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PropertyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

private fun toggleSection(
    section: PropertySection,
    currentExpanded: Set<PropertySection>,
    onUpdate: (Set<PropertySection>) -> Unit
) {
    onUpdate(
        if (currentExpanded.contains(section)) {
            currentExpanded - section
        } else {
            currentExpanded + section
        }
    )
}

private enum class PropertyTab(val displayName: String) {
    BASIC("Basic"),
    LAYOUT("Layout"),
    APPEARANCE("Style"),
    ADVANCED("Advanced")
}

private enum class PropertySection {
    BASIC, LAYOUT, CONSTRAINTS, SPACING, BACKGROUND, TEXT, TRANSFORM, ANIMATION
}
