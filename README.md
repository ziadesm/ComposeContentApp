# Content App - Android Application

A modern Android application built with MVVM architecture, Jetpack Compose, and Kotlin that displays content sections and provides search functionality with debounced input.

## Features

- **Main Screen**: Displays content sections with different layout types (horizontal scroll, two-column grid, square grid)
- **Search Screen**: Real-time search with 200ms debounce to avoid unnecessary API calls
- **Dynamic Content**: Fetches data from REST APIs and displays various content types (books, podcasts, articles)
- **Error Handling**: Comprehensive error handling with retry functionality
- **Responsive UI**: Built with Jetpack Compose for modern, declarative UI
- **Testing**: Comprehensive unit tests and UI tests

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern with the following layers:

### Data Layer
- **API Services**: Retrofit-based services for network communication
- **Repository Pattern**: Abstracts data sources and provides clean API for ViewModels
- **Data Models**: Kotlin data classes for API responses and domain objects

### Presentation Layer
- **ViewModels**: Handle business logic and state management
- **Composables**: Jetpack Compose UI components
- **Navigation**: Navigation Compose for screen transitions

### Dependency Injection
- **Hilt**: Provides dependency injection throughout the application

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Coroutines**: For asynchronous operations
- **Testing**: JUnit, Mockito, Compose Testing
- **Image Loading**: Coil

## API Endpoints

- **Home Sections**: `https://api-v2-b2sit6oh3a-uc.a.run.app/home_sections`
- **Search**: `https://mock.apidog.com/m1/735111-711675-default/search`

## Project Structure

```
app/src/main/java/com/example/contentapp/
├── data/
│   ├── api/                 # API service interfaces
│   ├── model/              # Data models and DTOs
│   └── repository/         # Repository implementations
├── di/                     # Dependency injection modules
├── ui/
│   ├── components/         # Reusable UI components
│   ├── main/              # Main screen implementation
│   ├── search/            # Search screen implementation
│   └── theme/             # App theming
└── MainActivity.kt        # Main activity with navigation
```

## Key Features Implementation

### 1. Main Screen with Dynamic Layouts

The main screen displays content sections with different layout types:

- **Horizontal Scroll**: For featured content
- **Two-Column Grid**: For regular content browsing
- **Square Grid**: For compact content display
- **Vertical List**: Default fallback layout

### 2. Debounced Search

The search functionality implements a 200ms debounce using Kotlin coroutines:

```kotlin
searchQuery
    .debounce(200) // 200ms debounce as required
    .distinctUntilChanged()
    .filter { it.isNotBlank() }
    .onEach { query ->
        searchContent(query)
    }
    .launchIn(viewModelScope)
```

### 3. Error Handling

Comprehensive error handling with:
- Network error detection
- HTTP error code handling
- User-friendly error messages
- Retry functionality

### 4. State Management

Uses StateFlow for reactive state management:
- Loading states
- Success states with data
- Error states with messages
- Empty states

## Testing

### Unit Tests
- **ViewModels**: Test business logic and state management
- **Repository**: Test API integration and error handling
- **Debounced Search**: Test timing and behavior

### UI Tests
- **Main Screen**: Test content display and navigation
- **Search Screen**: Test search input and results display
- **Error States**: Test error handling and retry functionality

## Building and Running

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the application on an emulator or device

### Requirements
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9.10

## Dependencies

Key dependencies used in the project:

```gradle
// Compose
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.activity:activity-compose'

// ViewModel
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose'

// Navigation
implementation 'androidx.navigation:navigation-compose'

// Networking
implementation 'com.squareup.retrofit2:retrofit'
implementation 'com.squareup.retrofit2:converter-gson'

// Dependency Injection
implementation 'com.google.dagger:hilt-android'

// Image Loading
implementation 'io.coil-kt:coil-compose'

// Testing
testImplementation 'junit:junit'
testImplementation 'org.mockito:mockito-core'
androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
```

## Future Enhancements

- Offline caching with Room database
- Pull-to-refresh functionality
- Pagination for search results
- Content detail screens
- User preferences and favorites
- Dark theme support
- Accessibility improvements

