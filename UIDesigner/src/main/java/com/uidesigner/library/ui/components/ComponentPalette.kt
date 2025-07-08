package com.uidesigner.library.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.ComponentType

@Composable
fun ComponentPalette(
    onComponentSelected: (ComponentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(200.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "Components",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(ComponentType.values()) { componentType ->
                    ComponentPaletteItem(
                        componentType = componentType,
                        onClick = { onComponentSelected(componentType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ComponentPaletteItem(
    componentType: ComponentType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colors.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getComponentIcon(componentType),
            contentDescription = componentType.displayName,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = componentType.displayName,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

private fun getComponentIcon(componentType: ComponentType): ImageVector {
    return when (componentType) {
        ComponentType.TEXT_VIEW -> Icons.Default.TextFields
        ComponentType.BUTTON -> Icons.Default.SmartButton
        ComponentType.EDIT_TEXT -> Icons.Default.Edit
        ComponentType.IMAGE_VIEW -> Icons.Default.Image
        ComponentType.LINEAR_LAYOUT -> Icons.Default.ViewColumn
        ComponentType.RELATIVE_LAYOUT -> Icons.Default.ViewCompact
        ComponentType.CONSTRAINT_LAYOUT -> Icons.Default.GridView
        ComponentType.RECYCLER_VIEW -> Icons.Default.List
        ComponentType.CARD_VIEW -> Icons.Default.CreditCard
        ComponentType.SCROLL_VIEW -> Icons.Default.SwipeVertical
        ComponentType.HORIZONTAL_SCROLL_VIEW -> Icons.Default.SwipeLeft
        ComponentType.FRAME_LAYOUT -> Icons.Default.CropFree
        ComponentType.TABLE_LAYOUT -> Icons.Default.TableChart
        ComponentType.GRID_LAYOUT -> Icons.Default.GridOn
        ComponentType.TAB_LAYOUT -> Icons.Default.Tab
        ComponentType.VIEW_PAGER -> Icons.Default.ViewCarousel
    }
}
