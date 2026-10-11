# 🚌 School Transportation Management System
### 💙 El Kheir Association — A Community Impact Project

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Angular-19-DD0031?logo=angular&logoColor=white" alt="Angular 19" />
  <img src="https://img.shields.io/badge/SQLite-Local_Database-003B57?logo=sqlite&logoColor=white" alt="SQLite" />
  <img src="https://img.shields.io/badge/Platform-Windows-0078D4?logo=windows&logoColor=white" alt="Windows" />
</p>

---

## 🌍 About the Project

**School Transportation Management System** is a personal social-impact project developed voluntarily to support **El Kheir Association**, a local association in my community.

The initiative aims to help the association organize and manage its school transportation service more efficiently by replacing scattered spreadsheets and paper records with a centralized, easy-to-use desktop application.

🎯 **The main goals are to:**
- 🧑‍🎓 Simplify student and parent management.
- 💰 Improve the tracking of registration fees and monthly payments.
- 📊 Provide a clear overview of payment status and outstanding balances.
- 🗂️ Reduce repetitive administrative tasks.
- 🤝 Put software engineering skills at the service of the local community.

> 💙 This project reflects my belief that technology can create meaningful local impact when it addresses real-world needs.

## ✨ Key Features

<table>
  <tr>
    <td width="50%">
      <h3>🧑‍🎓 Student Management</h3>
      <ul>
        <li>Add, update, and archive students.</li>
        <li>Search by student or parent name.</li>
        <li>Filter by gender and village.</li>
        <li>Browse paginated student lists.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>👨‍👩‍👧 Parent Management</h3>
      <ul>
        <li>Associate multiple children with one parent.</li>
        <li>Create parent records directly from student forms.</li>
        <li>Keep related student records organized.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>💳 Payment Management</h3>
      <ul>
        <li>Record registration fees and monthly payments.</li>
        <li>Automatically generate receipt numbers.</li>
        <li>Support partial payments.</li>
        <li>Track paid, due, remaining, and credited amounts.</li>
        <li>View payment records from September to June.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>📈 Reports & Export</h3>
      <ul>
        <li>Export student lists to Excel.</li>
        <li>Export payment tables and parent balances.</li>
        <li>Print records and generate PDF documents.</li>
        <li>Identify outstanding payments quickly.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>⚙️ Application Settings</h3>
      <ul>
        <li>Configure registration and monthly fees.</li>
        <li>Manage school years.</li>
        <li>Change the administrator password.</li>
        <li>Create manual database backups.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🖥️ Desktop Experience</h3>
      <ul>
        <li>Launch directly from the Windows desktop.</li>
        <li>Run locally without an Internet connection.</li>
        <li>Support French and Arabic interfaces.</li>
        <li>Include automatic daily backups.</li>
        <li>Access the application from the system tray.</li>
      </ul>
    </td>
  </tr>
</table>

## 💵 Business Rules

| 📌 Item | 📝 Rule |
|---|---|
| 📝 Registration fee | MAD 30 per parent, once per school year |
| 🚌 First child | MAD 100 per month |
| 👨‍👩‍👧 Additional children | MAD 50 per child per month |
| 📅 School year | September – June (10 months) |
| ➕ Mid-year enrollment | Billing starts from the enrollment month |
| ➖ Student departure | Billing stops from the departure month; payment history is retained |
| 🏡 Supported villages | Lamnizla, Talbort, Talaïnt, Tamda, Ighzer Aghanime |
| ⚙️ Custom pricing | Optional individual rate per student |

*All fees can be configured from the application's Settings section.*

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| 🎨 Frontend | Angular 19 — Standalone Components |
| ⚙️ Backend | Spring Boot 3.3 |
| 🔌 API | REST API |
| 🔐 Security | Spring Security — HTTP Basic |
| 🗄️ Database | SQLite |
| ☕ Language | Java 21 |
| 📦 Packaging | `jpackage` + Inno Setup 6 |

## 🏗️ Architecture

```text
┌──────────────────────────────────────┐
│        Angular Web Interface         │
│        French 🇫🇷 / Arabic 🇲🇦        │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Spring Boot Application        │
│          127.0.0.1:8081              │
│                                      │
│  ┌────────────────────────────────┐  │
│  │       REST API (/api/**)        │  │
│  └────────────────────────────────┘  │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│          SQLite Database             │
│               data.db                │
└──────────────────────────────────────┘
```

🚀 **Production mode:** Spring Boot serves both the REST API and the compiled Angular interface in a single process.

🔒 **Local access only:** The application listens on `127.0.0.1` and is not directly accessible from other devices on the network.

## 📂 Repository Structure

```text
📦 project-root/
├── 📁 gestion-etudiants-backend/
│   ├── 📁 src/main/java/              # Java source code
│   ├── 📁 src/main/resources/static/  # Compiled Angular frontend
│   ├── 📄 build-exe.bat               # Build Windows executable
│   ├── 📄 build-installer.bat         # Build Windows installer
│   └── 📄 installer.iss               # Inno Setup configuration
├── 📁 frontend/                       # Angular source code
└── 📄 README.md
```

## 🚀 Getting Started

### 📋 Prerequisites

- ☕ JDK 21
- 📦 Maven or the included Maven wrapper

### ▶️ Run the Backend

1. Open `gestion-etudiants-backend` in IntelliJ IDEA.
2. Run `GestionEtudiantsBackendApplication`.
3. Open [http://localhost:8081](http://localhost:8081).

To disable the automatic browser launch and system tray icon during development:

```text
--app.launcher.enabled=false
```

### 🎨 Run the Frontend Separately

Prerequisite: Node.js 20 or 22.

```bash
cd frontend
npm install
npm start
```

The Angular development server runs at [http://localhost:4200](http://localhost:4200) and uses a proxy to communicate with the backend on port 8081.

To build the frontend and copy it into the backend:

```bash
npm run deploy
```

## 📦 Build the Windows Installer

### Prerequisites

- JDK 21 with `jpackage` available in `PATH`.
- [Inno Setup 6](https://jrsoftware.org/isdl.php).

Run the following commands from the backend directory:

```bat
build-exe.bat
build-installer.bat
```

📁 The installer is generated at:

```text
dist/Setup-GestionEtudiants-1.0.0.exe
```

The installer is designed to be distributed as a single file and to work without administrator privileges.

To change the application version, update `AppVersion` in `installer.iss` and `--app-version` in `build-exe.bat`.

## 💾 Data Storage & Backups

Application data is stored outside the installation directory:

```text
%LOCALAPPDATA%\GestionEtudiants\
├── 📄 data.db
├── 📁 backups/    # Daily automatic backups
└── 📁 logs/       # Application logs
```

- 🔄 Automatic daily backups, with the last 30 days retained.
- 🛡️ Uninstalling the application does not delete the stored data.
- 📥 Manual backups are available from Settings.
- ♻️ To restore a backup, close the application, replace `data.db` with the desired backup, and restart the application.

## 🔐 Security & Privacy

- 🔑 An administrator account is created on the first launch. Change the initial password immediately.
- 🛡️ Student and parent information, including phone numbers and payment records, must remain confidential.
- 🚫 Never publish `data.db` or its backups in a public repository.
- 📄 The `.gitignore` file excludes local sensitive data files.

## 💙 Community Impact

This project is more than a software development exercise: it is a **voluntary contribution to a local community initiative**.

By providing El Kheir Association with a dedicated digital tool, the project aims to improve administrative organization, simplify payment tracking, and reduce the workload associated with manual record-keeping.

It demonstrates how software engineering can be used not only to build applications, but also to support local organizations and respond to practical community needs.

## 👨‍💻 Author

**Anas Adiouane**  
🎓 State Engineer in Computer Science  
💻 Junior Backend Developer — Java & Spring Boot

🤝 Developed voluntarily for **El Kheir Association** as a personal social-impact project.

🔒 **Repository:** Private

---

<p align="center">
  <i>Building useful software. Supporting the local community. 💙</i>
</p>