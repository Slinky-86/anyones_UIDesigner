package com.uidesigner.library.xml

import com.uidesigner.library.model.UIComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XMLGenerator @Inject constructor() {
    
    fun generateXML(components: List<UIComponent>): String {
        if (components.isEmpty()) {
            return generateEmptyLayout()
        }
        
        val xmlBuilder = StringBuilder()
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        xmlBuilder.append("<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n")
        xmlBuilder.append("    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n")
        xmlBuilder.append("    android:layout_width=\"match_parent\"\n")
        xmlBuilder.append("    android:layout_height=\"match_parent\">\n\n")
        
        components.forEach { component ->
            xmlBuilder.append(generateComponentXML(component))
            xmlBuilder.append("\n")
        }
        
        xmlBuilder.append("</RelativeLayout>")
        
        return xmlBuilder.toString()
    }
    
    private fun generateComponentXML(component: UIComponent): String {
        val xmlBuilder = StringBuilder()
        val indent = "    "
        
        xmlBuilder.append("$indent<${component.type.xmlTag}\n")
        xmlBuilder.append("$indent    android:id=\"@+id/${component.id}\"\n")
        xmlBuilder.append("$indent    android:layout_width=\"${component.width.toInt()}dp\"\n")
        xmlBuilder.append("$indent    android:layout_height=\"${component.height.toInt()}dp\"\n")
        xmlBuilder.append("$indent    android:layout_marginStart=\"${component.x.toInt()}dp\"\n")
        xmlBuilder.append("$indent    android:layout_marginTop=\"${component.y.toInt()}dp\"\n")
        
        // Add component-specific properties
        component.properties.forEach { (key, value) ->
            xmlBuilder.append("$indent    $key=\"$value\"\n")
        }
        
        xmlBuilder.append("$indent    />")
        
        return xmlBuilder.toString()
    }
    
    private fun generateEmptyLayout(): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</RelativeLayout>"""
    }
}
