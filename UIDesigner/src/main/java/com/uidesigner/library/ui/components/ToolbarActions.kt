package com.uidesigner.library.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ToolbarActions(
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClear: () -> Unit,
    onGenerateXML: () -> Unit,
    onClose: () -> Unit
) {
    var showXMLDialog by remember { mutableStateOf(false) }
    
    IconButton(onClick = onUndo) {
        Icon(
            imageVector = Icons.Default.Undo,
            contentDescription = "Undo"
        )
    }
    
    IconButton(onClick = onRedo) {
        Icon(
            imageVector = Icons.Default.Redo,
            contentDescription = "Redo"
        )
    }
    
    IconButton(onClick = onClear) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear Canvas"
        )
    }
    
    IconButton(onClick = { showXMLDialog = true }) {
        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = "Generate XML"
        )
    }
    
    IconButton(onClick = onClose) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close"
        )
    }
    
    if (showXMLDialog) {
        AlertDialog(
            onDismissRequest = { showXMLDialog = false },
            title = { Text("Generated XML") },
            text = {
                // This would show the generated XML
                Text("XML generation feature - implement XML display here")
            },
            confirmButton = {
                TextButton(onClick = { showXMLDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
