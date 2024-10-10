# CONNECTVERSE: CHAT APP
![Figma App](https://github.com/ahmetymtkn/ConnectVerse/blob/main/CONNECTVERSE.png)

ConnectVerse is a robust messaging application built on the Android platform that leverages Firebase services for authentication, data storage, and real-time communication. The app allows users to connect with friends, engage in one-on-one chats, and utilize artificial intelligence features for enhanced user interaction. This document provides a comprehensive overview of the application's architecture, functionalities, and the technologies employed.

## Technologies Used

### Android Development
- **Java**: The primary programming language used for Android application development.
- **Android SDK**: Tools and libraries for building Android apps.

### Firebase Services
- **Firebase Authentication**: For user registration, login, email verification, and password reset.
- **Firebase Realtime Database**: To store and retrieve messages and user data in real-time.
- **Firebase Firestore**: Used for structured data storage, including user profiles and friendship management.
- **Firebase Storage**: For storing and retrieving user-uploaded media (photos).

### UI Components
- **RecyclerView**: Used for displaying lists of users, messages, and other data efficiently.
- **ViewPager2**: For implementing swipeable views to navigate between different fragments in the app.

### AI Integration
- **AI Chat Features**: The app incorporates AI capabilities for chat enhancements, allowing users to interact in a more engaging manner.

## Functionalities

### User Authentication

#### Sign Up and Login
Users can register for a new account or log in using their existing credentials. The application verifies email addresses and allows users to reset their passwords if forgotten.

- **Email Verification**: After registering, users receive an email to verify their account, ensuring they are using a valid email address. This helps maintain a secure and trustworthy user environment.
  
- **Password Reset**: The application provides functionality for users to reset their passwords, facilitating easy access in case they forget their credentials.

#### User Profile Management
The app manages user profiles, including the ability to update profile pictures and display names.

### Chat Functionality

#### Real-Time Messaging
Users can send and receive messages in real-time through the Firebase Realtime Database, ensuring that conversations are seamless and immediate.

#### Sending Photos
Users can send photos to each other, enhancing the chat experience. This feature is supported through Firebase Storage, where images are uploaded and retrieved easily.

#### Friend Management
Users can manage their friends list, add new friends, and view all existing friends.

- **Friend Requests**: Users can send invitations to others to become friends, which can be accepted or declined.


#### Online/Offline Case Management
The app keeps track of users' online or offline status. When users open the app, they can show other users that they are online. This feature helps users see each other's status and know when they are ready to chat. 

### User Interface

#### Navigation
The application utilizes **ViewPager2** to provide a smooth transition between different functional pages such as:
- **Add Friends Page**: Where users can search for and invite friends.
- **All User Friends Page**: Displays the list of all friends.
- **All User Chats Page**: Shows recent chats and messages.
- **AI Chat Page**: Integrates AI functionalities to enhance user interactions.

#### Real-Time Updates
Through Firebase listeners, the app updates the user interface in real-time as new messages are received or friends are added.

### Error Handling and User Feedback
The app includes robust error handling mechanisms that inform users of issues (e.g., no user found, database errors) using **Toast** messages and log outputs.

## Class Implementations

### UserAndChattingPage Class
This class serves as the main hub for managing user interactions and chats. It integrates various features including displaying the user’s current online status and handling user logouts. The user’s online presence is updated in the Firebase Realtime Database whenever they start or pause the app.

### ChatPages Class
Manages the different pages in the chat interface, utilizing a `ViewPager2` to allow users to swipe between different fragments such as Add Friends, All User Friends, and All User Chats.

### AllUserFriendsPage Class
This fragment manages the list of friends and provides a search functionality to filter friends based on their usernames. It also implements a text watcher to allow real-time filtering of friends as the user types.

### AllUserChatsPage Class
Responsible for displaying recent chats. It retrieves the last messages from Firebase and presents them in a `RecyclerView`. The user can see who they chatted with last and view details of their conversations.

### AddFriendsPage Class
Allows users to search for other users and send friend invitations. It provides feedback on whether the search was successful and updates the UI accordingly.

### AIChatPage Class
This section of the app leverages AI functionalities to engage users in chat, possibly providing responses or suggestions based on user inputs.

## Deficiencies
#### Online feature is not stable. 
#### Notification feature is missing. 
#### Only chat with AI, can't send photos.

## Conclusion

ConnectVerse is a feature-rich messaging application that provides basic communication tools as well as the ability to chat with artificial intelligence. With its user-friendly interface and seamless integration of Firebase services, it enables users to efficiently manage their social connections.


[DOWNLOAD APK](https://github.com/ahmetymtkn/photo/releases/download/untagged-2cdfa898115c1344cb98/ConnectVerse1.3.apk)

