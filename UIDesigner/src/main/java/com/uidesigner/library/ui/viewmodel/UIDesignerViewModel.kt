package com.uidesigner.library.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uidesigner.library.UIDesignerConfig
import com.uidesigner.library.model.*
import com.uidesigner.library.repository.UIDesignerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UIDesignerViewModel @Inject constructor(
    private val repository: UIDesignerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UIDesignerState())
    val uiState: StateFlow<UIDesignerState> = _uiState.asStateFlow()
    
    private val _config = MutableStateFlow(UIDesignerConfig())
    val config: StateFlow<UIDesignerConfig> = _config.asStateFlow()
    
    private val undoStack = mutableListOf<List<UIComponent>>()
    private val redoStack = mutableListOf<List<UIComponent>>()
    
    fun setConfig(config: UIDesignerConfig) {
        _config.value = config
    }
    
    fun addComponent(type: ComponentType, x: Float, y: Float) {
        val currentComponents = _uiState.value.components
        saveToUndoStack(currentComponents)
        
        val newComponent = UIComponent(
            id = UUID.randomUUID().toString(),
            type = type,
            x = x,
            y = y,
            width = getDefaultWidth(type),
            height = getDefaultHeight(type),
            properties = getDefaultProperties(type),
            textStyle = if (isTextComponent(type)) getDefaultTextStyle() else null,
            background = getDefaultBackground(type)
        )
        
        _uiState.value = _uiState.value.copy(
            components = currentComponents + newComponent,
            selectedComponentId = newComponent.id
        )
    }
    
    fun selectComponent(componentId: String?) {
        _uiState.value = _uiState.value.copy(
            selectedComponentId = componentId
        )
    }
    
    fun moveComponent(componentId: String, deltaX: Float, deltaY: Float) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId && !component.isLocked) {
                val newX = if (_config.value.snapToGrid) {
                    snapToGrid(component.x + deltaX)
                } else {
                    component.x + deltaX
                }
                val newY = if (_config.value.snapToGrid) {
                    snapToGrid(component.y + deltaY)
                } else {
                    component.y + deltaY
                }
                component.copy(x = newX, y = newY)
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun resizeComponent(componentId: String, width: Float, height: Float) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId && !component.isLocked) {
                component.copy(
                    width = if (_config.value.snapToGrid) snapToGrid(width) else width.coerceAtLeast(20f),
                    height = if (_config.value.snapToGrid) snapToGrid(height) else height.coerceAtLeast(20f)
                )
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun deleteComponent(componentId: String) {
        val currentComponents = _uiState.value.components
        saveToUndoStack(currentComponents)
        
        val updatedComponents = currentComponents.filter { it.id != componentId }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents,
            selectedComponentId = if (_uiState.value.selectedComponentId == componentId) null else _uiState.value.selectedComponentId
        )
    }
    
    fun updateComponentProperty(componentId: String, propertyName: String, value: String) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId) {
                val updatedProperties = component.properties.toMutableMap()
                updatedProperties[propertyName] = value
                component.copy(properties = updatedProperties)
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun updateComponentConstraints(componentId: String, constraints: ConstraintSet) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId) {
                component.copy(constraints = constraints)
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun updateComponent(updatedComponent: UIComponent) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == updatedComponent.id) {
                updatedComponent
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun toggleComponentVisibility(componentId: String) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId) {
                component.copy(isVisible = !component.isVisible)
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun toggleComponentLock(componentId: String) {
        val currentComponents = _uiState.value.components
        val updatedComponents = currentComponents.map { component ->
            if (component.id == componentId) {
                component.copy(isLocked = !component.isLocked)
            } else {
                component
            }
        }
        
        _uiState.value = _uiState.value.copy(
            components = updatedComponents
        )
    }
    
    fun undo() {
        if (undoStack.isNotEmpty()) {
            val currentComponents = _uiState.value.components
            redoStack.add(currentComponents)
            
            val previousComponents = undoStack.removeLastOrNull()
            if (previousComponents != null) {
                _uiState.value = _uiState.value.copy(
                    components = previousComponents
                )
            }
        }
    }
    
    fun redo() {
        if (redoStack.isNotEmpty()) {
            val currentComponents = _uiState.value.components
            undoStack.add(currentComponents)
            
            val nextComponents = redoStack.removeLastOrNull()
            if (nextComponents != null) {
                _uiState.value = _uiState.value.copy(
                    components = nextComponents
                )
            }
        }
    }
    
    fun generateXML(): String {
        return repository.generateXML(_uiState.value.components)
    }
    
    fun clearCanvas() {
        val currentComponents = _uiState.value.components
        if (currentComponents.isNotEmpty()) {
            saveToUndoStack(currentComponents)
            _uiState.value = _uiState.value.copy(
                components = emptyList(),
                selectedComponentId = null
            )
        }
    }
    
    fun saveProject(projectName: String) {
        viewModelScope.launch {
            val success = repository.saveProject(_uiState.value.components, projectName)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = if (success) null else "Failed to save project"
            )
        }
    }
    
    fun loadProject(projectName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val components = repository.loadProject(projectName)
            _uiState.value = _uiState.value.copy(
                components = components ?: emptyList(),
                selectedComponentId = null,
                isLoading = false,
                error = if (components == null) "Failed to load project" else null
            )
        }
    }
    
    private fun saveToUndoStack(components: List<UIComponent>) {
        undoStack.add(components)
        if (undoStack.size > _config.value.maxUndoSteps) {
            undoStack.removeFirstOrNull()
        }
        redoStack.clear()
    }
    
    private fun snapToGrid(value: Float): Float {
        val gridSize = _config.value.gridSize
        return (value / gridSize).toInt() * gridSize.toFloat()
    }
    
    private fun getDefaultWidth(type: ComponentType): Float {
        return when (type) {
            ComponentType.BUTTON -> 120f
            ComponentType.TEXT_VIEW -> 100f
            ComponentType.EDIT_TEXT -> 200f
            ComponentType.IMAGE_VIEW -> 100f
            ComponentType.CHECKBOX -> 24f
            ComponentType.RADIO_BUTTON -> 24f
            ComponentType.SWITCH -> 60f
            ComponentType.SEEK_BAR -> 200f
            ComponentType.RATING_BAR -> 150f
            ComponentType.SPINNER -> 150f
            ComponentType.LINEAR_LAYOUT, ComponentType.RELATIVE_LAYOUT, ComponentType.CONSTRAINT_LAYOUT -> 300f
            ComponentType.FRAME_LAYOUT -> 200f
            ComponentType.RECYCLER_VIEW, ComponentType.LIST_VIEW -> 250f
            ComponentType.CARD_VIEW -> 200f
            ComponentType.FLOATING_ACTION_BUTTON -> 56f
            ComponentType.TOOLBAR -> 400f
            else -> 150f
        }
    }
    
    private fun getDefaultHeight(type: ComponentType): Float {
        return when (type) {
            ComponentType.BUTTON -> 48f
            ComponentType.TEXT_VIEW -> 24f
            ComponentType.EDIT_TEXT -> 48f
            ComponentType.IMAGE_VIEW -> 100f
            ComponentType.CHECKBOX -> 24f
            ComponentType.RADIO_BUTTON -> 24f
            ComponentType.SWITCH -> 32f
            ComponentType.SEEK_BAR -> 32f
            ComponentType.RATING_BAR -> 32f
            ComponentType.SPINNER -> 48f
            ComponentType.LINEAR_LAYOUT, ComponentType.RELATIVE_LAYOUT, ComponentType.CONSTRAINT_LAYOUT -> 200f
            ComponentType.FRAME_LAYOUT -> 150f
            ComponentType.RECYCLER_VIEW, ComponentType.LIST_VIEW -> 300f
            ComponentType.CARD_VIEW -> 150f
            ComponentType.FLOATING_ACTION_BUTTON -> 56f
            ComponentType.TOOLBAR -> 56f
            ComponentType.APP_BAR_LAYOUT -> 200f
            else -> 100f
        }
    }
    
    private fun getDefaultProperties(type: ComponentType): Map<String, String> {
        return when (type) {
            ComponentType.TEXT_VIEW -> mapOf(
                "android:text" to "TextView",
                "android:textSize" to "14sp",
                "android:textColor" to "#000000"
            )
            ComponentType.BUTTON -> mapOf(
                "android:text" to "Button",
                "android:textSize" to "14sp"
            )
            ComponentType.EDIT_TEXT -> mapOf(
                "android:hint" to "Enter text",
                "android:textSize" to "14sp",
                "android:inputType" to "text"
            )
            ComponentType.IMAGE_VIEW -> mapOf(
                "android:src" to "@drawable/ic_placeholder",
                "android:scaleType" to "centerCrop"
            )
            ComponentType.CHECKBOX -> mapOf(
                "android:text" to "CheckBox",
                "android:checked" to "false"
            )
            ComponentType.RADIO_BUTTON -> mapOf(
                "android:text" to "RadioButton",
                "android:checked" to "false"
            )
            ComponentType.SWITCH -> mapOf(
                "android:text" to "Switch",
                "android:checked" to "false"
            )
            ComponentType.LINEAR_LAYOUT -> mapOf(
                "android:orientation" to "vertical"
            )
            ComponentType.CONSTRAINT_LAYOUT -> mapOf(
                "app:layout_constraintTop_toTopOf" to "parent",
                "app:layout_constraintStart_toStartOf" to "parent"
            )
            else -> emptyMap()
        }
    }
    
    private fun getDefaultTextStyle(): TextStyle {
        return TextStyle(
            textSize = 14f,
            textColor = "#000000",
            fontFamily = "default",
            fontWeight = FontWeight.NORMAL,
            fontStyle = FontStyle.NORMAL,
            textAlignment = TextAlignment.START
        )
    }
    
    private fun getDefaultBackground(type: ComponentType): BackgroundStyle {
        return when (type) {
            ComponentType.BUTTON -> BackgroundStyle(
                color = "#2196F3",
                cornerRadius = 4f
            )
            ComponentType.CARD_VIEW -> BackgroundStyle(
                color = "#FFFFFF",
                cornerRadius = 8f
            )
            ComponentType.EDIT_TEXT -> BackgroundStyle(
                color = "#FFFFFF",
                strokeWidth = 1f,
                strokeColor = "#CCCCCC"
            )
            else -> BackgroundStyle()
        }
    }
    
    private fun isTextComponent(type: ComponentType): Boolean {
        return when (type) {
            ComponentType.TEXT_VIEW,
            ComponentType.BUTTON,
            ComponentType.EDIT_TEXT,
            ComponentType.CHECKBOX,
            ComponentType.RADIO_BUTTON,
            ComponentType.SWITCH -> true
            else -> false
        }
    }
}

data class UIDesignerState(
    val components: List<UIComponent> = emptyList(),
    val selectedComponentId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
