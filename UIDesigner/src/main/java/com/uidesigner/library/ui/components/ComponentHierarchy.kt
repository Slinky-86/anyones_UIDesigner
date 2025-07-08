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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.UIComponent

@Composable
fun ComponentHierarchy(
    components: List<UIComponent>,
    selectedComponentId: String?,
    onComponentSelected: (String?) -> Unit,
    onComponentVisibilityToggled: (String) -> Unit,
    onComponentLockToggled: (String) -> Unit,
    onComponentDeleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedComponents by remember { mutableStateOf(setOf<String>()) }
    
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Component Tree",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.primary
                )
                
                Row {
                    IconButton(
                        onClick = { 
                            expandedComponents = if (expandedComponents.size == components.size) {
                                emptySet()
                            } else {
                                components.map { it.id }.toSet()
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (expandedComponents.size == components.size) 
                                Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = "Toggle All",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Component count and stats
            Text(
                text = "${components.size} components",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Hierarchy Tree
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val rootComponents = buildHierarchy(components)
                items(rootComponents) { hierarchyItem ->
                    HierarchyItem(
                        item = hierarchyItem,
                        level = 0,
                        selectedComponentId = selectedComponentId,
                        expandedComponents = expandedComponents,
                        onComponentSelected = onComponentSelected,
                        onToggleExpanded = { componentId ->
                            expandedComponents = if (expandedComponents.contains(componentId)) {
                                expandedComponents - componentId
                            } else {
                                expandedComponents + componentId
                            }
                        },
                        onVisibilityToggled = onComponentVisibilityToggled,
                        onLockToggled = onComponentLockToggled,
                        onDeleted = onComponentDeleted
                    )
                }
            }
        }
    }
}

@Composable
private fun HierarchyItem(
    item: HierarchyItem,
    level: Int,
    selectedComponentId: String?,
    expandedComponents: Set<String>,
    onComponentSelected: (String?) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onVisibilityToggled: (String) -> Unit,
    onLockToggled: (String) -> Unit,
    onDeleted: (String) -> Unit
) {
    val isSelected = item.component.id == selectedComponentId
    val isExpanded = expandedComponents.contains(item.component.id)
    val hasChildren = item.children.isNotEmpty()
    
    Column {
        // Component Item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
                .clickable { onComponentSelected(item.component.id) }
                .padding(
                    start = (16 * level + 8).dp,
                    top = 6.dp,
                    end = 8.dp,
                    bottom = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expand/Collapse button
            if (hasChildren) {
                IconButton(
                    onClick = { onToggleExpanded(item.component.id) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            
            // Component icon
            Icon(
                imageVector = getComponentIcon(item.component.type),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colors.primary 
                      else MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Component name and type
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.component.properties["android:id"]?.removePrefix("@+id/") 
                           ?: item.component.id.take(8),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    ),
                    color = if (isSelected) MaterialTheme.colors.primary 
                           else MaterialTheme.colors.onSurface
                )
                
                Text(
                    text = item.component.type.displayName,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Visibility toggle
                IconButton(
                    onClick = { onVisibilityToggled(item.component.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (item.component.isVisible) Icons.Default.Visibility 
                                     else Icons.Default.VisibilityOff,
                        contentDescription = if (item.component.isVisible) "Hide" else "Show",
                        tint = if (item.component.isVisible) MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                              else MaterialTheme.colors.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Lock toggle
                IconButton(
                    onClick = { onLockToggled(item.component.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (item.component.isLocked) Icons.Default.Lock 
                                     else Icons.Default.LockOpen,
                        contentDescription = if (item.component.isLocked) "Unlock" else "Lock",
                        tint = if (item.component.isLocked) MaterialTheme.colors.error.copy(alpha = 0.6f)
                              else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = { onDeleted(item.component.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colors.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        // Children
        if (hasChildren && isExpanded) {
            item.children.forEach { child ->
                HierarchyItem(
                    item = child,
                    level = level + 1,
                    selectedComponentId = selectedComponentId,
                    expandedComponents = expandedComponents,
                    onComponentSelected = onComponentSelected,
                    onToggleExpanded = onToggleExpanded,
                    onVisibilityToggled = onVisibilityToggled,
                    onLockToggled = onLockToggled,
                    onDeleted = onDeleted
                )
            }
        }
    }
}

private data class HierarchyItem(
    val component: UIComponent,
    val children: List<HierarchyItem>
)

private fun buildHierarchy(components: List<UIComponent>): List<HierarchyItem> {
    val componentMap = components.associateBy { it.id }
    val rootComponents = components.filter { it.parentId == null }
    
    fun buildHierarchyItem(component: UIComponent): HierarchyItem {
        val children = component.children.mapNotNull { childId ->
            componentMap[childId]?.let { buildHierarchyItem(it) }
        }
        return HierarchyItem(component, children)
    }
    
    return rootComponents.map { buildHierarchyItem(it) }
}

private fun getComponentIcon(componentType: com.uidesigner.library.model.ComponentType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (componentType) {
        com.uidesigner.library.model.ComponentType.TEXT_VIEW -> Icons.Default.TextFields
        com.uidesigner.library.model.ComponentType.BUTTON -> Icons.Default.SmartButton
        com.uidesigner.library.model.ComponentType.IMAGE_VIEW -> Icons.Default.Image
        com.uidesigner.library.model.ComponentType.EDIT_TEXT -> Icons.Default.Edit
        com.uidesigner.library.model.ComponentType.CHECKBOX -> Icons.Default.CheckBox
        com.uidesigner.library.model.ComponentType.RADIO_BUTTON -> Icons.Default.RadioButtonChecked
        com.uidesigner.library.model.ComponentType.SWITCH -> Icons.Default.ToggleOn
        com.uidesigner.library.model.ComponentType.SEEK_BAR -> Icons.Default.LinearScale
        com.uidesigner.library.model.ComponentType.RATING_BAR -> Icons.Default.Star
        com.uidesigner.library.model.ComponentType.SPINNER -> Icons.Default.ArrowDropDown
        com.uidesigner.library.model.ComponentType.LINEAR_LAYOUT -> Icons.Default.ViewColumn
        com.uidesigner.library.model.ComponentType.RELATIVE_LAYOUT -> Icons.Default.ViewCompact
        com.uidesigner.library.model.ComponentType.CONSTRAINT_LAYOUT -> Icons.Default.GridView
        com.uidesigner.library.model.ComponentType.FRAME_LAYOUT -> Icons.Default.CropFree
        com.uidesigner.library.model.ComponentType.TABLE_LAYOUT -> Icons.Default.TableChart
        com.uidesigner.library.model.ComponentType.GRID_LAYOUT -> Icons.Default.GridOn
        com.uidesigner.library.model.ComponentType.COORDINATOR_LAYOUT -> Icons.Default.Layers
        com.uidesigner.library.model.ComponentType.SCROLL_VIEW -> Icons.Default.SwipeVertical
        com.uidesigner.library.model.ComponentType.HORIZONTAL_SCROLL_VIEW -> Icons.Default.SwipeLeft
        com.uidesigner.library.model.ComponentType.NESTED_SCROLL_VIEW -> Icons.Default.SwipeUp
        com.uidesigner.library.model.ComponentType.CARD_VIEW -> Icons.Default.CreditCard
        com.uidesigner.library.model.ComponentType.RECYCLER_VIEW -> Icons.Default.List
        com.uidesigner.library.model.ComponentType.LIST_VIEW -> Icons.Default.FormatListBulleted
        com.uidesigner.library.model.ComponentType.GRID_VIEW -> Icons.Default.GridView
        com.uidesigner.library.model.ComponentType.VIEW_PAGER -> Icons.Default.ViewCarousel
        com.uidesigner.library.model.ComponentType.TAB_LAYOUT -> Icons.Default.Tab
        com.uidesigner.library.model.ComponentType.BOTTOM_NAVIGATION -> Icons.Default.BottomNavigation
        com.uidesigner.library.model.ComponentType.NAVIGATION_DRAWER -> Icons.Default.Menu
        com.uidesigner.library.model.ComponentType.FLOATING_ACTION_BUTTON -> Icons.Default.Add
        com.uidesigner.library.model.ComponentType.APP_BAR_LAYOUT -> Icons.Default.ViewDay
        com.uidesigner.library.model.ComponentType.TOOLBAR -> Icons.Default.Build
        com.uidesigner.library.model.ComponentType.COLLAPSING_TOOLBAR -> Icons.Default.ExpandMore
        com.uidesigner.library.model.ComponentType.WEB_VIEW -> Icons.Default.Web
        com.uidesigner.library.model.ComponentType.MAP_VIEW -> Icons.Default.Map
        com.uidesigner.library.model.ComponentType.VIDEO_VIEW -> Icons.Default.PlayArrow
        com.uidesigner.library.model.ComponentType.PROGRESS_BAR -> Icons.Default.Refresh
        com.uidesigner.library.model.ComponentType.CUSTOM_VIEW -> Icons.Default.Code
    }
}
