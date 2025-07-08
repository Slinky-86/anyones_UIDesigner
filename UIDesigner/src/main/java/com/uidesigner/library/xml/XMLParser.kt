package com.uidesigner.library.xml

import com.uidesigner.library.model.ComponentType
import com.uidesigner.library.model.UIComponent
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.StringReader
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource

@Singleton
class XMLParser @Inject constructor() {
    
    fun parseXML(xmlContent: String): List<UIComponent> {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document: Document = builder.parse(InputSource(StringReader(xmlContent)))
            
            val components = mutableListOf<UIComponent>()
            parseElement(document.documentElement, components)
            
            components
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseElement(element: Element, components: MutableList<UIComponent>) {
        val tagName = element.tagName
        val componentType = getComponentTypeFromTag(tagName)
        
        if (componentType != null) {
            val component = createUIComponentFromElement(element, componentType)
            components.add(component)
        }
        
        // Parse child elements
        val childNodes: NodeList = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element) {
                parseElement(node, components)
            }
        }
    }
    
    private fun createUIComponentFromElement(element: Element, type: ComponentType): UIComponent {
        val id = element.getAttribute("android:id").removePrefix("@+id/").ifEmpty { 
            UUID.randomUUID().toString() 
        }
        
        val width = parseSize(element.getAttribute("android:layout_width"))
        val height = parseSize(element.getAttribute("android:layout_height"))
        val x = parseSize(element.getAttribute("android:layout_marginStart"))
        val y = parseSize(element.getAttribute("android:layout_marginTop"))
        
        val properties = mutableMapOf<String, String>()
        
        // Extract all attributes as properties
        val attributes = element.attributes
        for (i in 0 until attributes.length) {
            val attr = attributes.item(i)
            if (!attr.nodeName.startsWith("android:layout_") && 
                attr.nodeName != "android:id") {
                properties[attr.nodeName] = attr.nodeValue
            }
        }
        
        return UIComponent(
            id = id,
            type = type,
            x = x,
            y = y,
            width = width,
            height = height,
            properties = properties
        )
    }
    
    private fun getComponentTypeFromTag(tagName: String): ComponentType? {
        return ComponentType.values().find { it.xmlTag == tagName }
    }
    
    private fun parseSize(sizeString: String): Float {
        return when {
            sizeString.isEmpty() -> 100f
            sizeString == "match_parent" -> 300f
            sizeString == "wrap_content" -> 100f
            sizeString.endsWith("dp") -> sizeString.removeSuffix("dp").toFloatOrNull() ?: 100f
            else -> sizeString.toFloatOrNull() ?: 100f
        }
    }
}
