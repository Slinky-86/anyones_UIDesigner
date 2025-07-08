package com.uidesigner.library.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.UIComponent
import com.uidesigner.library.UIDesignerConfig

@Composable
fun DesignCanvas(
    components: List<UIComponent>,
    selectedComponentId: String?,
    config: UIDesignerConfig,
    onComponentSelected: (String?) -> Unit,
    onComponentMoved: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable { onComponentSelected(null) }
    ) {
        // Grid background
        if (config.showGrid) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawGrid(config.gridSize, MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
            }
        }
        
        // Render components
        components.forEach { component ->
            ComponentRenderer(
                component = component,
                isSelected = component.id == selectedComponentId,
                onSelected = { onComponentSelected(component.id) },
                onMoved = { deltaX, deltaY ->
                    onComponentMoved(component.id, deltaX, deltaY)
                },
                modifier = Modifier
                    .offset(
                        x = with(density) { component.x.toDp() },
                        y = with(density) { component.y.toDp() }
                    )
                    .size(
                        width = with(density) { component.width.toDp() },
                        height = with(density) { component.height.toDp() }
                    )
            )
        }
    }
}

@Composable
private fun ComponentRenderer(
    component: UIComponent,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onMoved: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                when (component.type.name) {
                    "BUTTON" -> MaterialTheme.colors.primary.copy(alpha = 0.8f)
                    "TEXT_VIEW" -> MaterialTheme.colors.surface
                    "EDIT_TEXT" -> MaterialTheme.colors.surface
                    else -> MaterialTheme.colors.surface.copy(alpha = 0.7f)
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onSelected() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragOffset = offset
                    },
                    onDrag = { change ->
                        onMoved(change.position.x - dragOffset.x, change.position.y - dragOffset.y)
                        dragOffset = change.position
                    }
                )
            }
    ) {
        // Component content based on type
        when (component.type.name) {
            "BUTTON" -> {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        text = component.properties["android:text"] ?: "Button",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
            "TEXT_VIEW" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Text(
                        text = component.properties["android:text"] ?: "TextView",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            "EDIT_TEXT" -> {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = {
                        Text(component.properties["android:hint"] ?: "Enter text")
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary
                    )
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Text(
                        text = component.type.displayName,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawGrid(gridSize: Int, color: Color) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    
    // Draw vertical lines
    var x = 0f
    while (x <= canvasWidth) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, canvasHeight),
            strokeWidth = 1f
        )
        x += gridSize
    }
    
    // Draw horizontal lines
    var y = 0f
    while (y <= canvasHeight) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 1f
        )
        y += gridSize
    }
}
