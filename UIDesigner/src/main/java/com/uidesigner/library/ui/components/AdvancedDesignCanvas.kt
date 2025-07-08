package com.uidesigner.library.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.uidesigner.library.UIDesignerConfig
import com.uidesigner.library.model.UIComponent
import kotlin.math.abs

@Composable
fun AdvancedDesignCanvas(
    components: List<UIComponent>,
    selectedComponentId: String?,
    config: UIDesignerConfig,
    canvasSize: Size = Size(800f, 1200f),
    zoom: Float = 1f,
    onComponentSelected: (String?) -> Unit,
    onComponentMoved: (String, Float, Float) -> Unit,
    onComponentResized: (String, Float, Float) -> Unit,
    onMultipleComponentsSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var dragState by remember { mutableStateOf<DragState>(DragState.None) }
    var selectionBox by remember { mutableStateOf<SelectionBox?>(null) }
    var showGuides by remember { mutableStateOf(false) }
    var guides by remember { mutableStateOf<List<Guide>>(emptyList()) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val hitComponent = findComponentAtPosition(components, offset, density)
                        if (hitComponent != null) {
                            onComponentSelected(hitComponent.id)
                            dragState = DragState.MovingComponent(hitComponent.id, offset)
                        } else {
                            // Start selection box
                            selectionBox = SelectionBox(offset, offset)
                            dragState = DragState.SelectingMultiple
                        }
                    },
                    onDrag = { change ->
                        when (val currentDragState = dragState) {
                            is DragState.MovingComponent -> {
                                val component = components.find { it.id == currentDragState.componentId }
                                if (component != null && !component.isLocked) {
                                    val deltaX = change.position.x - currentDragState.startOffset.x
                                    val deltaY = change.position.y - currentDragState.startOffset.y
                                    
                                    // Show alignment guides
                                    guides = calculateAlignmentGuides(component, components, deltaX, deltaY)
                                    showGuides = true
                                    
                                    // Snap to guides if enabled
                                    val (snappedDeltaX, snappedDeltaY) = if (config.snapToGrid) {
                                        snapToGuides(deltaX, deltaY, guides)
                                    } else {
                                        deltaX to deltaY
                                    }
                                    
                                    onComponentMoved(currentDragState.componentId, snappedDeltaX, snappedDeltaY)
                                }
                            }
                            is DragState.SelectingMultiple -> {
                                selectionBox = selectionBox?.copy(end = change.position)
                            }
                            else -> {}
                        }
                    },
                    onDragEnd = {
                        when (dragState) {
                            is DragState.SelectingMultiple -> {
                                selectionBox?.let { box ->
                                    val selectedComponents = findComponentsInSelection(components, box, density)
                                    if (selectedComponents.isNotEmpty()) {
                                        onMultipleComponentsSelected(selectedComponents.map { it.id })
                                    }
                                }
                                selectionBox = null
                            }
                            else -> {}
                        }
                        
                        dragState = DragState.None
                        showGuides = false
                        guides = emptyList()
                    }
                )
            }
    ) {
        // Canvas background with grid
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw grid
            if (config.showGrid) {
                drawGrid(config.gridSize, MaterialTheme.colors.onBackground.copy(alpha = 0.1f))
            }
            
            // Draw alignment guides
            if (showGuides) {
                guides.forEach { guide ->
                    drawGuide(guide)
                }
            }
            
            // Draw selection box
            selectionBox?.let { box ->
                drawSelectionBox(box)
            }
        }
        
        // Render components
        components
            .sortedBy { it.zIndex }
            .forEach { component ->
                if (component.isVisible) {
                    AdvancedComponentRenderer(
                        component = component,
                        isSelected = component.id == selectedComponentId,
                        zoom = zoom,
                        onSelected = { onComponentSelected(component.id) },
                        onResized = { width, height ->
                            onComponentResized(component.id, width, height)
                        },
                        modifier = Modifier
                            .offset(
                                x = with(density) { (component.x * zoom).toDp() },
                                y = with(density) { (component.y * zoom).toDp() }
                            )
                            .size(
                                width = with(density) { (component.width * zoom).toDp() },
                                height = with(density) { (component.height * zoom).toDp() }
                            )
                            .zIndex(component.zIndex.toFloat())
                    )
                }
            }
        
        // Canvas tools overlay
        CanvasToolsOverlay(
            zoom = zoom,
            showGrid = config.showGrid,
            snapToGrid = config.snapToGrid,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun AdvancedComponentRenderer(
    component: UIComponent,
    isSelected: Boolean,
    zoom: Float,
    onSelected: () -> Unit,
    onResized: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isResizing by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape((4 * zoom).dp))
            .background(
                color = getComponentBackgroundColor(component),
                shape = RoundedCornerShape((component.background.cornerRadius * zoom).dp)
            )
            .border(
                width = if (isSelected) (2 * zoom).dp else (1 * zoom).dp,
                color = if (isSelected) MaterialTheme.colors.primary 
                       else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape((4 * zoom).dp)
            )
            .clickable { onSelected() }
            .drawBehind {
                // Draw component-specific decorations
                drawComponentDecorations(component, isSelected, zoom)
            }
    ) {
        // Component content
        ComponentContent(
            component = component,
            zoom = zoom,
            modifier = Modifier.fillMaxSize()
        )
        
        // Selection handles
        if (isSelected && !component.isLocked) {
            SelectionHandles(
                zoom = zoom,
                onResizeStart = { isResizing = true },
                onResize = onResized,
                onResizeEnd = { isResizing = false }
            )
        }
        
        // Component overlay info
        if (isSelected) {
            ComponentOverlayInfo(
                component = component,
                zoom = zoom,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun ComponentContent(
    component: UIComponent,
    zoom: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (component.type.name) {
            "BUTTON" -> {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(android.graphics.Color.parseColor(component.background.color))
                    )
                ) {
                    Text(
                        text = component.properties["android:text"] ?: "Button",
                        color = component.textStyle?.let { 
                            Color(android.graphics.Color.parseColor(it.textColor))
                        } ?: MaterialTheme.colors.onPrimary
                    )
                }
            }
            "TEXT_VIEW" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding((4 * zoom).dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = component.properties["android:text"] ?: "TextView",
                        style = MaterialTheme.typography.body2,
                        color = component.textStyle?.let { 
                            Color(android.graphics.Color.parseColor(it.textColor))
                        } ?: MaterialTheme.colors.onSurface
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
            "IMAGE_VIEW" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Image",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size((24 * zoom).dp)
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding((4 * zoom).dp),
                    contentAlignment = Alignment.Center
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

@Composable
private fun SelectionHandles(
    zoom: Float,
    onResizeStart: () -> Unit,
    onResize: (Float, Float) -> Unit,
    onResizeEnd: () -> Unit
) {
    val handleSize = (8 * zoom).dp
    
    // Corner handles
    listOf(
        Alignment.TopStart,
        Alignment.TopEnd,
        Alignment.BottomStart,
        Alignment.BottomEnd
    ).forEach { alignment ->
        Box(
            modifier = Modifier
                .align(alignment)
                .size(handleSize)
                .offset(
                    x = when (alignment) {
                        Alignment.TopStart, Alignment.BottomStart -> (-handleSize / 2)
                        else -> (handleSize / 2)
                    },
                    y = when (alignment) {
                        Alignment.TopStart, Alignment.TopEnd -> (-handleSize / 2)
                        else -> (handleSize / 2)
                    }
                )
                .background(
                    MaterialTheme.colors.primary,
                    RoundedCornerShape(50)
                )
                .border(
                    (1 * zoom).dp,
                    MaterialTheme.colors.background,
                    RoundedCornerShape(50)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { onResizeStart() },
                        onDrag = { change ->
                            // Calculate new size based on drag
                            val deltaX = change.position.x / zoom
                            val deltaY = change.position.y / zoom
                            onResize(deltaX, deltaY)
                        },
                        onDragEnd = { onResizeEnd() }
                    )
                }
        )
    }
}

@Composable
private fun ComponentOverlayInfo(
    component: UIComponent,
    zoom: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .offset(x = (-8).dp, y = (-32).dp),
        elevation = (4 * zoom).dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Text(
            text = "${component.type.displayName} (${component.width.toInt()}×${component.height.toInt()})",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CanvasToolsOverlay(
    zoom: Float,
    showGrid: Boolean,
    snapToGrid: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Zoom: ${(zoom * 100).toInt()}%",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (showGrid) Icons.Default.GridOn else Icons.Default.GridOff,
                    contentDescription = "Grid",
                    tint = if (showGrid) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = if (snapToGrid) Icons.Default.CenterFocusStrong else Icons.Default.CenterFocusWeak,
                    contentDescription = "Snap",
                    tint = if (snapToGrid) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Helper functions and data classes
private sealed class DragState {
    object None : DragState()
    data class MovingComponent(val componentId: String, val startOffset: Offset) : DragState()
    object SelectingMultiple : DragState()
}

private data class SelectionBox(
    val start: Offset,
    val end: Offset
)

private data class Guide(
    val start: Offset,
    val end: Offset,
    val type: GuideType
)

private enum class GuideType {
    VERTICAL, HORIZONTAL
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

private fun DrawScope.drawGuide(guide: Guide) {
    drawLine(
        color = Color.Red,
        start = guide.start,
        end = guide.end,
        strokeWidth = 2f
    )
}

private fun DrawScope.drawSelectionBox(box: SelectionBox) {
    val topLeft = Offset(
        minOf(box.start.x, box.end.x),
        minOf(box.start.y, box.end.y)
    )
    val size = Size(
        abs(box.end.x - box.start.x),
        abs(box.end.y - box.start.y)
    )
    
    drawRect(
        color = Color.Blue.copy(alpha = 0.2f),
        topLeft = topLeft,
        size = size
    )
    
    drawRect(
        color = Color.Blue,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawComponentDecorations(
    component: UIComponent,
    isSelected: Boolean,
    zoom: Float
) {
    // Draw component-specific decorations like shadows, borders, etc.
    if (component.elevation > 0) {
        // Draw shadow effect
        drawRect(
            color = Color.Black.copy(alpha = 0.1f),
            topLeft = Offset(component.elevation * zoom, component.elevation * zoom),
            size = Size(size.width, size.height)
        )
    }
}

private fun getComponentBackgroundColor(component: UIComponent): Color {
    return try {
        Color(android.graphics.Color.parseColor(component.background.color))
    } catch (e: Exception) {
        when (component.type.name) {
            "BUTTON" -> Color(0xFF2196F3)
            "TEXT_VIEW" -> Color.Transparent
            "EDIT_TEXT" -> Color.White
            else -> Color(0xFFF5F5F5)
        }
    }
}

private fun findComponentAtPosition(
    components: List<UIComponent>,
    position: Offset,
    density: androidx.compose.ui.unit.Density
): UIComponent? {
    return components
        .sortedByDescending { it.zIndex }
        .find { component ->
            val left = component.x
            val top = component.y
            val right = left + component.width
            val bottom = top + component.height
            
            position.x >= left && position.x <= right &&
            position.y >= top && position.y <= bottom
        }
}

private fun findComponentsInSelection(
    components: List<UIComponent>,
    selectionBox: SelectionBox,
    density: androidx.compose.ui.unit.Density
): List<UIComponent> {
    val left = minOf(selectionBox.start.x, selectionBox.end.x)
    val top = minOf(selectionBox.start.y, selectionBox.end.y)
    val right = maxOf(selectionBox.start.x, selectionBox.end.x)
    val bottom = maxOf(selectionBox.start.y, selectionBox.end.y)
    
    return components.filter { component ->
        val compLeft = component.x
        val compTop = component.y
        val compRight = compLeft + component.width
        val compBottom = compTop + component.height
        
        // Check if component intersects with selection box
        !(compRight < left || compLeft > right || compBottom < top || compTop > bottom)
    }
}

private fun calculateAlignmentGuides(
    movingComponent: UIComponent,
    allComponents: List<UIComponent>,
    deltaX: Float,
    deltaY: Float
): List<Guide> {
    val guides = mutableListOf<Guide>()
    val newX = movingComponent.x + deltaX
    val newY = movingComponent.y + deltaY
    val newCenterX = newX + movingComponent.width / 2
    val newCenterY = newY + movingComponent.height / 2
    
    allComponents.forEach { component ->
        if (component.id != movingComponent.id) {
            val compCenterX = component.x + component.width / 2
            val compCenterY = component.y + component.height / 2
            
            // Vertical alignment guides
            if (abs(newCenterX - compCenterX) < 5f) {
                guides.add(
                    Guide(
                        start = Offset(compCenterX, 0f),
                        end = Offset(compCenterX, 1200f),
                        type = GuideType.VERTICAL
                    )
                )
            }
            
            // Horizontal alignment guides
            if (abs(newCenterY - compCenterY) < 5f) {
                guides.add(
                    Guide(
                        start = Offset(0f, compCenterY),
                        end = Offset(800f, compCenterY),
                        type = GuideType.HORIZONTAL
                    )
                )
            }
        }
    }
    
    return guides
}

private fun snapToGuides(deltaX: Float, deltaY: Float, guides: List<Guide>): Pair<Float, Float> {
    // Implement snapping logic based on guides
    return deltaX to deltaY
}
