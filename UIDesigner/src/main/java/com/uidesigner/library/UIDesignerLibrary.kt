package com.uidesigner.library

import android.content.Context
import android.content.Intent
import com.uidesigner.library.ui.UIDesignerActivity

/**
 * Main entry point for the UIDesigner library
 */
object UIDesignerLibrary {
    
    /**
     * Launch the UI Designer with optional configuration
     */
    fun launch(context: Context, config: UIDesignerConfig = UIDesignerConfig()) {
        val intent = Intent(context, UIDesignerActivity::class.java).apply {
            putExtra("config", config)
        }
        context.startActivity(intent)
    }
    
    /**
     * Generate XML from UI components
     */
    fun generateXML(components: List<UIComponent>): String {
        return XMLGenerator.generate(components)
    }
    
    /**
     * Parse XML to UI components
     */
    fun parseXML(xml: String): List<UIComponent> {
        return XMLParser.parse(xml)
    }
}
