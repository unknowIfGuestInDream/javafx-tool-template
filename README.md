# JavaFX Tool Template

A template repository for JavaFX tool development with built-in support for internationalization (i18n), theme management, and user preferences.

## Features

- **JavaFX 21** - Modern JavaFX UI framework
- **ControlsFX** - Extended UI controls for JavaFX
- **Internationalization (i18n)** - Built-in support for English, Chinese, and Japanese
- **Theme Management** - AtlantaFX themes with light/dark variants (Primer, Nord)
- **Preferences** - PreferencesFX for user settings management
- **Logging** - SLF4J with Logback
- **CI/CD** - GitHub Actions workflows for build, artifact, and release

## Requirements

- Java 21 or later
- Maven 3.9+

## Getting Started

### Build

```bash
mvn clean package
```

### Run

```bash
mvn javafx:run
```

### Test

```bash
mvn clean verify
```

## Project Structure

```
src/main/java/com/tlcsdm/fxtemplate/
├── Launcher.java              # Entry point for shaded JAR
├── TemplateApplication.java   # JavaFX Application class
├── config/
│   ├── AppSettings.java       # Preferences management
│   ├── AppTheme.java          # Theme configuration
│   └── I18N.java              # Internationalization utility
├── controller/
│   └── MainController.java    # Main FXML controller
└── model/
    └── DisplayLocale.java     # Locale display wrapper

src/main/resources/com/tlcsdm/fxtemplate/
├── i18n/
│   ├── messages.properties        # English (default)
│   ├── messages_zh.properties     # Chinese (Simplified)
│   └── messages_ja.properties     # Japanese
└── main.fxml                      # Main UI layout
```

## Workflows

- **Build** - Triggered on push/PR to main/master, builds and tests on Ubuntu, Windows, and macOS
- **Artifact** - Manual trigger to package and upload build artifacts
- **Release** - Triggered on release publication, packages and uploads release assets

## License

[MIT License](LICENSE)
