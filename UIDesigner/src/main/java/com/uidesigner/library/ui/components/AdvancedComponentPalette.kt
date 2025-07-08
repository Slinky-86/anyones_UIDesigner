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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uidesigner.library.model.ComponentCategory
import com.uidesigner.library.model.ComponentType

@Composable
fun AdvancedComponentPalette(
    onComponentSelected: (ComponentType) -> Unit,
    searchQuery: String = "",
    selectedCategory: ComponentCategory? = null,
    modifier: Modifier = Modifier
) {
    var expandedCategories by remember { mutableStateOf(setOf<ComponentCategory>()) }
    
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
                    text = "Components",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.primary
                )
                
                IconButton(
                    onClick = { 
                        expandedCategories = if (expandedCategories.size == ComponentCategory.values().size) {
                            emptySet()
                        } else {
                            ComponentCategory.values().toSet()
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expandedCategories.size == ComponentCategory.values().size) 
                            Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                        contentDescription = "Toggle All Categories",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Component count
            val filteredComponents = getFilteredComponents(searchQuery, selectedCategory)
            Text(
                text = "${filteredComponents.size} components",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Categories and Components
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ComponentCategory.values().forEach { category ->
                    val categoryComponents = ComponentType.values()
                        .filter { it.category == category }
                        .filter { component ->
                            searchQuery.isEmpty() || 
                            component.displayName.contains(searchQuery, ignoreCase = true)
                        }
                        .filter { selectedCategory == null || it.category == selectedCategory }
                    
                    if (categoryComponents.isNotEmpty()) {
                        item {
                            CategoryHeader(
                                category = category,
                                componentCount = categoryComponents.size,
                                isExpanded = expandedCategories.contains(category),
                                onToggle = {
                                    expandedCategories = if (expandedCategories.contains(category)) {
                                        expandedCategories - category
                                    } else {
                                        expandedCategories + category
                                    }
                                }
                            )
                        }
                        
                        if (expandedCategories.contains(category)) {
                            items(categoryComponents) { componentType ->
                                ComponentPaletteItem(
                                    componentType = componentType,
                                    onClick = { onComponentSelected(componentType) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    category: ComponentCategory,
    componentCount: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onToggle() }
            .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "($componentCount)",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
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
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getComponentIcon(componentType),
            contentDescription = componentType.displayName,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = componentType.displayName,
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colors.onSurface
            )
            
            Text(
                text = componentType.xmlTag,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun getFilteredComponents(searchQuery: String, selectedCategory: ComponentCategory?): List<ComponentType> {
    return ComponentType.values()
        .filter { component ->
            (searchQuery.isEmpty() || component.displayName.contains(searchQuery, ignoreCase = true)) &&
            (selectedCategory == null || component.category == selectedCategory)
        }
}

private fun getCategoryIcon(category: ComponentCategory): ImageVector {
    return when (category) {
        ComponentCategory.BASIC -> Icons.Default.Widgets
        ComponentCategory.INPUT -> Icons.Default.Input
        ComponentCategory.LAYOUT -> Icons.Default.ViewQuilt
        ComponentCategory.CONTAINER -> Icons.Default.Inventory2
        ComponentCategory.COLLECTION -> Icons.Default.List
        ComponentCategory.NAVIGATION -> Icons.Default.Navigation
        ComponentCategory.MATERIAL -> Icons.Default.Palette
        ComponentCategory.ADVANCED -> Icons.Default.Extension
        ComponentCategory.CUSTOM -> Icons.Default.Code
    }
}

private fun getComponentIcon(componentType: ComponentType): ImageVector {
    return when (componentType) {
        ComponentType.TEXT_VIEW -> Icons.Default.TextFields
        ComponentType.BUTTON -> Icons.Default.SmartButton
        ComponentType.IMAGE_VIEW -> Icons.Default.Image
        ComponentType.EDIT_TEXT -> Icons.Default.Edit
        ComponentType.CHECKBOX -> Icons.Default.CheckBox
        ComponentType.RADIO_BUTTON -> Icons.Default.RadioButtonChecked
        ComponentType.SWITCH -> Icons.Default.ToggleOn
        ComponentType.SEEK_BAR -> Icons.Default.LinearScale
        ComponentType.RATING_BAR -> Icons.Default.Star
        ComponentType.SPINNER -> Icons.Default.ArrowDropDown
        ComponentType.LINEAR_LAYOUT -> Icons.Default.ViewColumn
        ComponentType.RELATIVE_LAYOUT -> Icons.Default.ViewCompact
        ComponentType.CONSTRAINT_LAYOUT -> Icons.Default.GridView
        ComponentType.FRAME_LAYOUT -> Icons.Default.CropFree
        ComponentType.TABLE_LAYOUT -> Icons.Default.TableChart
        ComponentType.GRID_LAYOUT -> Icons.Default.GridOn
        ComponentType.COORDINATOR_LAYOUT -> Icons.Default.Layers
        ComponentType.SCROLL_VIEW -> Icons.Default.SwipeVertical
        ComponentType.HORIZONTAL_SCROLL_VIEW -> Icons.Default.SwipeLeft
        ComponentType.NESTED_SCROLL_VIEW -> Icons.Default.SwipeUp
        ComponentType.CARD_VIEW -> Icons.Default.CreditCard
        ComponentType.RECYCLER_VIEW -> Icons.Default.List
        ComponentType.LIST_VIEW -> Icons.Default.FormatListBulleted
        ComponentType.GRID_VIEW -> Icons.Default.GridView
        ComponentType.VIEW_PAGER -> Icons.Default.ViewCarousel
        ComponentType.TAB_LAYOUT -> Icons.Default.Tab
        ComponentType.BOTTOM_NAVIGATION -> Icons.Default.BottomNavigation
        ComponentType.NAVIGATION_DRAWER -> Icons.Default.Menu
        ComponentType.FLOATING_ACTION_BUTTON -> Icons.Default.Add
        ComponentType.APP_BAR_LAYOUT -> Icons.Default.ViewDay
        ComponentType.TOOLBAR -> Icons.Default.Build
        ComponentType.COLLAPSING_TOOLBAR -> Icons.Default.ExpandMore
        ComponentType.WEB_VIEW -> Icons.Default.Web
        ComponentType.MAP_VIEW -> Icons.Default.Map
        ComponentType.VIDEO_VIEW -> Icons.Default.PlayArrow
        ComponentType.PROGRESS_BAR -> Icons.Default.Refresh
        ComponentType.CUSTOM_VIEW -> Icons.Default.Code
    }
}
