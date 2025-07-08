package com.uidesigner.library.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.UIComponent

@Composable
fun PropertyPanel(
    selectedComponent: UIComponent?,
    onPropertyChanged: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Properties",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (selectedComponent != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Basic properties
                    item {
                        PropertyField(
                            label = "ID",
                            value = selectedComponent.id,
                            onValueChange = { /* ID is read-only */ },
                            readOnly = true
                        )
                    }
                    
                    item {
                        PropertyField(
                            label = "X Position",
                            value = selectedComponent.x.toString(),
                            onValueChange = { onPropertyChanged("x", it) }
                        )
                    }
                    
                    item {
                        PropertyField(
                            label = "Y Position",
                            value = selectedComponent.y.toString(),
                            onValueChange = { onPropertyChanged("y", it) }
                        )
                    }
                    
                    item {
                        PropertyField(
                            label = "Width",
                            value = selectedComponent.width.toString(),
                            onValueChange = { onPropertyChanged("width", it) }
                        )
                    }
                    
                    item {
                        PropertyField(
                            label = "Height",
                            value = selectedComponent.height.toString(),
                            onValueChange = { onPropertyChanged("height", it) }
                        )
                    }
                    
                    // Component-specific properties
                    items(selectedComponent.properties.toList()) { (key, value) ->
                        PropertyField(
                            label = key,
                            value = value,
                            onValueChange = { onPropertyChanged(key, it) }
                        )
                    }
                }
            } else {
                Text(
                    text = "No component selected",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PropertyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false
) {
    var textValue by remember(value) { mutableStateOf(value) }
    
    OutlinedTextField(
        value = textValue,
        onValueChange = { 
            textValue = it
            if (!readOnly) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        readOnly = readOnly,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
