# UIDesigner Android Library

A comprehensive Android library module that provides drag-and-drop UI design functionality with XML generation capabilities, inspired by Android Studio's UI designer with VS Code theming.

## Features

- **Drag & Drop Interface**: Intuitive drag-and-drop functionality for UI components
- **XML Generation**: Generate Android XML layouts from visual designs
- **VS Code Inspired Theming**: Multiple theme options including VS Code Dark/Light and Android Studio Dark/Light
- **Component Palette**: Comprehensive set of Android UI components
- **Properties Panel**: Real-time property editing for selected components
- **Modular Architecture**: Clean architecture with MVVM pattern using Jetpack Compose
- **Undo/Redo Support**: Full undo/redo functionality with configurable history
- **Grid Snapping**: Optional grid snapping for precise component placement

## Supported Components

- TextView
- EditText
- Button
- ImageView
- LinearLayout
- RelativeLayout
- ConstraintLayout
- RecyclerView
- CardView
- Switch
- CheckBox
- RadioButton
- ProgressBar
- SeekBar
- Spinner
- WebView

## Installation

Add the UIDesigner module to your Android project:

1. Include the module in your `settings.gradle.kts`:
```kotlin
include(":UIDesigner")
```

2. Add the dependency in your app's `build.gradle.kts`:
```kotlin
implementation(project(":UIDesigner"))
```

3. Add Hilt to your application class:
```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

## Usage

### Basic Usage

```kotlin
// Launch the UI Designer
UIDesignerLibrary.launch(context)

// Launch with custom configuration
val config = UIDesignerConfig(
    theme = UIDesignerTheme.VS_CODE_DARK,
    enableXMLGeneration = true,
    snapToGrid = true,
    gridSize = 8
)
UIDesignerLibrary.launch(context, config)
```

### XML Generation

```kotlin
// Generate XML from components
val components = listOf(/* your UI components */)
val xml = UIDesignerLibrary.generateXML(components)

// Parse XML to components
val xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>..."
val components = UIDesignerLibrary.parseXML(xmlString)
```

## Architecture

The library follows a modular architecture with clear separation of concerns:

```
UIDesigner/
├── src/main/java/com/uidesigner/library/
│   ├── UIDesignerLibrary.kt          # Main entry point
│   ├── UIDesignerConfig.kt           # Configuration classes
│   ├── model/                        # Data models
│   │   └── UIComponent.kt
│   ├── ui/                          # UI layer
│   │   ├── UIDesignerActivity.kt
│   │   ├── screen/
│   │   │   └── UIDesignerScreen.kt
│   │   ├── components/              # Reusable UI components
│   │   ├── theme/                   # Theme definitions
│   │   └── viewmodel/               # ViewModels
│   ├── repository/                  # Data layer
│   ├── xml/                        # XML processing
│   └── di/                         # Dependency injection
```

## Theming

The library supports multiple themes:

- **VS_CODE_DARK**: Dark theme inspired by VS Code
- **VS_CODE_LIGHT**: Light theme inspired by VS Code  
- **ANDROID_STUDIO_DARK**: Dark theme inspired by Android Studio
- **ANDROID_STUDIO_LIGHT**: Light theme inspired by Android Studio

## Configuration Options

```kotlin
data class UIDesignerConfig(
    val theme: UIDesignerTheme = UIDesignerTheme.VS_CODE_DARK,
    val enableXMLGeneration: Boolean = true,
    val enablePreview: Boolean = true,
    val maxUndoSteps: Int = 50,
    val gridSize: Int = 8,
    val snapToGrid: Boolean = true
)
```

## Requirements

- Android API 24+
- Kotlin 1.9.22+
- Jetpack Compose
- Hilt for dependency injection

## Dependencies

The library uses the following key dependencies:

- Jetpack Compose BOM 2024.02.00
- Hilt 2.48.1
- Material3
- AndroidX Core, Activity, Fragment, Lifecycle, Navigation
- Drag and Drop support library

## License

This library is provided as-is for educational and development purposes.

## Contributing

This is a comprehensive library module ready for integration into your Android projects. The modular architecture makes it easy to extend and customize according to your specific needs.
