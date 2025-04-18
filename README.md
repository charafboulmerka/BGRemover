# 🖼️ BGRemover

**BGRemover** is a Java/Kotlin-based Android app that allows users to upload an image, automatically remove its background via an external API, and instantly view the result.

All processed images are saved and linked to each user using **Firebase**, allowing them to revisit their background-removed images at any time.

---

## 🚀 Features

- 📤 Upload and process images with instant background removal  
- 🌐 External API integration for high-quality background removal  
- 🔁 Stores results per user using **Firebase Realtime Database**  
- 💾 Access history of processed images  
- 📢 Includes **AdMob** and **Facebook Ads** support (IDs editable via `strings.xml`)  
- 🔧 API endpoint configurable in `ResultActivity.kt`  
- 🔄 Built-in force update system — fetches version & config from Firebase  
- 🗃️ Firebase data structure and configuration are organized in the `Models` folder  

---

## 📸 Screenshots
<img src="https://i.postimg.cc/XqxbxpbJ/bg-back.png" />
<p float="left">
  <img src="https://i.imgur.com/9Srlyrj.jpeg" width="45%" />
  <img src="https://i.imgur.com/q8zy6ZC.jpeg" width="45%" />
</p>
---

## 🔧 Configuration

- **AdMob & Facebook Ads**: Update ad IDs in `res/values/strings.xml`  
- **API URL**: Set or change the endpoint in `ResultActivity.kt`  
- **Firebase Setup**: Refer to the `Models/` folder for database types and structure  
- **Force Update**: App fetches version and update info from Firebase Database  

---

## 🛠️ Tech Stack

- Kotlin
- Java
- Firebase (Auth + Realtime Database)  
- External API for background removal  
- AdMob & Facebook Audience Network  

---

## 📁 Folder Structure Highlights

- `Models/` – Firebase data types and configuration  
- `ResultActivity.kt` – API integration and background removal logic  
- `strings.xml` – Centralized configuration for Ads & general settings  

---

## 📄 License

This project is free to use and modify under the **MIT License**.

---

## 👤 Author

**Charaf Boulmerka**  
Android & Web Developer  
📧 charaf.boulmerka25@gmail.com  

---

## 🤝 Contributions

Feel free to **fork** the project, **open issues**, or **send pull requests**.  
If you find this useful, give it a ⭐ on GitHub!
