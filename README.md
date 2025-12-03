# SecureNest – Android Password Manager

SecureNest is a modern Android password manager built with Jetpack Compose, designed for simplicity, security, and clean architecture.
It includes password generation, breach-checking via API, secure storage, and a clean, modular code structure.

<img width="373" height="825" alt="image" src="https://github.com/user-attachments/assets/76fcd044-a40b-4c2d-b546-4dc7386ba712" />

<img width="372" height="826" alt="image" src="https://github.com/user-attachments/assets/788fa157-b000-4a9c-9f7e-9b142c63ac5b" />

## Features

- Add, view, update, and delete saved passwords
- Built-in password generator
- Pwned Passwords API integration: check if a password has ever been exposed in data breaches
- Organized screens: Home, Login, Add Password, Details, Settings, Trash
- Material 3 UI with custom theme, colors, and typography
- Navigation Component + Compose
- Strong local encryption for stored entries

# API: Pwned Passwords

SecureNest integrates with the Have I Been Pwned Pwned Passwords API to show how many times a password has appeared in global breaches.

This uses the secure k-Anonymity method:

- Only the first 5 characters of the SHA-1 hash of the password are sent
- Full password is never transmitted

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
