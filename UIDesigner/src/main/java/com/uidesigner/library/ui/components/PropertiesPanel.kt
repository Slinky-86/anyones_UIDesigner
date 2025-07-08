package com.uidesigner.library.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.UIComponent

@Composable
fun PropertiesPanel(
    selectedComponent: UIComponent?,
    onPropertyChanged: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Properties",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (selectedComponent != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Basic properties
                    item {
                        PropertyGroup(title = "Layout") {
                            PropertyField(
                                label = "X Position",
                                value = selectedComponent.x.toString(),
                                onValueChange = { onPropertyChanged("x", it) }
                            )
                            PropertyField(
                                label = "Y Position",
                                value = selectedComponent.y.toString(),
                                onValueChange = { onPropertyChanged("y", it) }
                            )
                            PropertyField(
                                label = "Width",
                                value = selectedComponent.width.toString(),
                                onValueChange = { onPropertyChanged("width", it) }
                            )
                            PropertyField(
                                label = "Height",
                                value = selectedComponent.height.toString(),
                                onValueChange = { onPropertyChanged("height", it) }
                            )
                        }
                    }
                    
                    // Component-specific properties
                    item {
                        PropertyGroup(title = "Attributes") {
                            selectedComponent.properties.forEach { (key, value) ->
                                PropertyField(
                                    label = key,
                                    value = value,
                                    onValueChange = { onPropertyChanged(key, it) }
                                )
                            }
                        }
                    }
                    
                    // Common Android properties
                    item {
                        PropertyGroup(title = "Common") {
                            PropertyField(
                                label = "android:id",
                                value = selectedComponent.properties["android:id"] ?: "",
                                onValueChange = { onPropertyChanged("android:id", it) }
                            )
                            PropertyField(
                                label = "android:layout_margin",
                                value = selectedComponent.properties["android:layout_margin"] ?: "",
                                onValueChange = { onPropertyChanged("android:layout_margin", it) }
                            )
                            PropertyField(
                                label = "android:padding",
                                value = selectedComponent.properties["android:padding"] ?: "",
                                onValueChange = { onPropertyChanged("android:padding", it) }
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Select a component to edit its properties",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PropertyGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun PropertyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var textValue by remember(value) { mutableStateOf(value) }
    
    OutlinedTextField(
        value = textValue,
        onValueChange = { 
            textValue = it
            onValueChange(it)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
