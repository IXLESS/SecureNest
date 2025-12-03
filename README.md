# SecureNest – Android Password Manager

SecureNest is a modern Android password manager built with Jetpack Compose, designed around simplicity, security, and clean architecture principles.
It includes secure password storage, a built-in password generator, breach-checking via the Pwned Passwords API, and a modular, maintainable codebase.

## Development Note
I have temporarily hardcoded the username and master password for testing purposes. You can modify them as needed in the LoginScreen.kt 
file, where they are defined.:

```kotlin
Button(
                onClick = {
                    // Trda (hardcoded) prijava za primer aplikacije
                    if (username == "admin" && password == "admin") {
                        navController.navigate("welcome")
                    } else {
                        error = true
                    }
                },
```
## Screenshots
Home screen:

<img width="373" height="825" alt="image" src="https://github.com/user-attachments/assets/76fcd044-a40b-4c2d-b546-4dc7386ba712" />

---
Password details screen:

<img width="372" height="826" alt="image" src="https://github.com/user-attachments/assets/788fa157-b000-4a9c-9f7e-9b142c63ac5b" />

## Features

- Add, view, update, and delete saved passwords
- Built-in password generator
- Pwned Passwords API integration: check if a password has ever been exposed in data breaches
- Organized screens: Home, Login, Add Password, Details, Settings, Trash
- Material 3 UI with custom theme, colors, and typography
- Navigation Component + Compose
- Strong local encryption for stored entries

# API Integration: Pwned Passwords

SecureNest integrates with the Have I Been Pwned "Pwned Passwords" API to detect whether a password has appeared in known data breaches.

The implementation follows the secure k-Anonymity model:

- Only the first 5 characters of the SHA-1 hash are sent to the API
- Full password is never transmitted
- All matching hash suffixes and breach counts are processed locally

## Installation & Setup

### 1.Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/SecureNest.git
cd SecureNest
```
### 2.Open in Android Studio

- Open Android Studio
- Click File > Open
- Select the cloned project folder

Android Studio will automatically download Gradle dependencies.

### 3. Run the app
Connect an Android device or launch an emulator
Click Run
