# Real-Time Chat Application

[![React](https://img.shields.io/badge/React-18-61DAFB.svg?style=for-the-badge&logo=react)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F.svg?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-6.x-47A248.svg?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/)
[![WebSocket](https://img.shields.io/badge/WebSocket-Supported-blue.svg?style=for-the-badge&logo=socket-dot-io)](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)
[![JWT](https://img.shields.io/badge/JWT-Auth-D63AFF.svg?style=for-the-badge&logo=json-web-tokens)](https://jwt.io/)
[![Render](https://img.shields.io/badge/Hosted_On-Render-46E3B7.svg?style=for-the-badge&logo=render)](https://render.com/)

![Real-Time Chat Application Screenshot](https://github.com/user-attachments/assets/06b01a82-55a6-422e-9519-27159d93bcfc)

## 🚀 About The Project

Welcome to the Real-Time Chat Application, a full-stack solution designed for instant and seamless communication. This project leverages a modern tech stack to deliver a responsive user experience with robust backend support.

The application features a **React** and **Tailwind CSS** frontend for a clean and intuitive user interface, while the backend is powered by a **Spring Boot** monolithic architecture. Real-time message delivery is handled efficiently through **WebSockets**, and all chat history is persisted in a **MongoDB** database. Secure user authentication is managed via **JWTs**, ensuring that conversations are private and secure.

---

## 🔗 Quick Links

> **Note:** Please add your live demo and repository links here.

-   **Live Demo:** `https://real-time-chat-app-project-bhu8wikok.vercel.app/`

---

## ✨ Key Features

-   ✅ **Secure User Authentication:** Safe login and registration system using JWTs, with password encryption.
-   ✅ **Real-Time Presence:** See when users come online with instant notifications broadcasted via WebSockets.
-   ✅ **Instant Messaging:** Bi-directional, low-latency communication for a smooth chatting experience.
-   ✅ **Persistent Chat History:** All conversations are saved to a MongoDB database, allowing users to view their message history.
-   ✅ **Media Sharing:** Users can share images and other files, which are securely stored and delivered via Cloudinary.
-   ✅ **Profile Customization:** Users can update their profile picture and other personal information.

---

## 🛠️ Tech Stack & Architecture

### Architecture
The application uses a **monolithic backend** architecture, which simplifies development and deployment for this project scale. The frontend is a separate, decoupled React application that communicates with the backend via REST APIs for standard requests and a WebSocket connection for real-time events.

### Technologies Used

| Category   | Technology / Service                                 | Purpose                                          |
|------------|------------------------------------------------------|--------------------------------------------------|
| **Frontend** | `React`, `Tailwind CSS`, `SockJS-Client`, `Stomp.js` | UI, Styling, WebSocket Communication             |
| **Backend**  | `Java 17`, `Spring Boot`, `Spring WebSocket`, `Spring Security` | Business Logic, API, Real-Time Layer, Authentication |
| **Database** | `MongoDB`                                            | Storing user data, messages, and chat rooms      |
| **Deployment**| `Render`, `MongoDB Atlas`, `Cloudinary`              | Hosting, Managed Database, Media Storage         |

---

## ⚙️ Local Setup and Installation

To run this project locally, you will need to have the following prerequisites installed:
-   Git
-   Java 17 or higher
-   Apache Maven or Gradle
-   Node.js and npm
-   A local or cloud-based MongoDB instance (e.g., from MongoDB Atlas)
-   A Cloudinary account for media storage

### Backend Setup

1.  **Clone the backend repository:**
    ```bash
    git clone <YOUR_BACKEND_REPO_URL>
    cd <backend-repo-folder>
    ```

2.  **Configure Environment Variables:**
    Create an `application.properties` file in `src/main/resources` and add the following properties:
    ```properties
    # MongoDB Configuration
    spring.data.mongodb.uri=YOUR_MONGODB_CONNECTION_STRING

    # JWT Configuration
    jwt.secret=YOUR_SUPER_SECRET_KEY_FOR_JWT

    # Cloudinary Configuration
    cloudinary.cloud_name=YOUR_CLOUDINARY_CLOUD_NAME
    cloudinary.api_key=YOUR_CLOUDINARY_API_KEY
    cloudinary.api_secret=YOUR_CLOUDINARY_API_SECRET
    ```

3.  **Run the backend server:**
    ```bash
    # Using Maven
    ./mvnw spring-boot:run
    ```
    The backend will be running on `http://localhost:8080`.

### Frontend Setup

1.  **Clone the frontend repository:**
    ```bash
    git clone <YOUR_FRONTEND_REPO_URL>
    cd <frontend-repo-folder>
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Configure Environment Variables:**
    Create a `.env.local` file in the root of the frontend project and add the backend API URL:
    ```env
    REACT_APP_API_URL=http://localhost:8080
    ```

4.  **Run the frontend application:**
    ```bash
    npm start
    ```
    The frontend will be available at `http://localhost:3000`.

---

## 📧 Contact

Nishant Rathore – nishantrathore2002@gmail.com

Project Link: `https://github.com/NISHANT-RATHORE/Chatty`
