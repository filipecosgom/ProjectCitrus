# Project Citrus 🍊

A comprehensive full-stack performance management system designed to streamline employee evaluations, training management, and team communication within organizations.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

Project Citrus is an enterprise-grade web application that facilitates performance management through structured evaluation cycles, course management, and real-time communication. The system supports role-based access control with different user types (Admin, Manager, Employee) and provides comprehensive tools for organizational performance tracking.

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based secure authentication
- Role-based access control (Admin, Manager, Employee)
- Password reset functionality
- Two-factor authentication support
- Account activation system

### 📊 Performance Management
- **Evaluation Cycles**: Create and manage performance evaluation periods
- **Appraisals**: Conduct structured employee evaluations with scoring
- **Progress Tracking**: Monitor evaluation completion rates
- **Reporting**: Generate performance reports and statistics

### 📚 Training & Development
- **Course Management**: Create, update, and manage training courses
- **Multi-language Support**: Courses available in multiple languages
- **File Management**: Upload and manage course materials
- **Progress Tracking**: Track employee course completions

### 💬 Communication
- **Real-time Chat**: WebSocket-powered messaging system
- **Notifications**: Real-time notifications for important events
- **Message Center**: Centralized communication hub
- **Email Integration**: Automated email notifications

### 🌐 User Experience
- **Responsive Design**: Mobile-friendly interface
- **Internationalization**: Multi-language support (Portuguese, English)
- **Dark Mode**: Theme customization
- **Intuitive Dashboard**: Comprehensive overview with charts and statistics

### 👥 User Management
- **User Profiles**: Comprehensive user information management
- **Avatar Support**: Profile picture upload and management
- **Manager Assignment**: Hierarchical user relationships
- **Bulk Operations**: Import/export user data via Excel

## 🛠 Technology Stack

### Frontend
- **React 19.1.0** - Modern React with hooks and functional components
- **React Router 6.30.1** - Client-side routing
- **Zustand 5.0.5** - State management
- **Bootstrap 5.3.6** - UI framework
- **Material-UI 7.1.1** - Component library
- **Axios 1.4.0** - HTTP client
- **React Hook Form 7.57.0** - Form management
- **React Testing Library 16.3.0** - Component testing
- **Jest** - JavaScript testing framework
- **i18next** - Internationalization

### Backend
- **Java 21** - Latest LTS version
- **Jakarta EE** - Enterprise Java platform
- **JAX-RS** - RESTful web services
- **JPA/Hibernate** - Object-relational mapping
- **EJB** - Enterprise JavaBeans for business logic
- **WebSocket** - Real-time communication
- **Maven** - Build automation and dependency management
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Log4j** - Logging framework

### Database
- **JPA** - Java Persistence API
- **H2** - In-memory database for testing
- **Hibernate** - ORM framework

### Development Tools
- **Maven** - Backend build tool
- **npm** - Frontend package manager
- **Selenium** - Web automation testing
- **JavaDoc** - API documentation generation

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  React Frontend │    │    Jakarta EE   │    │     Database    │
│                 │    │   Backend       │    │                 │
│  • Components   │◄──►│  • REST APIs    │◄──►│  • JPA Entities │
│  • State Mgmt   │    │  • WebSocket    │    │  • Relationships│
│  • Routing      │    │  • Services     │    │  • Constraints  │
│  • Testing      │    │  • Repositories │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Key Components

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic implementation
- **Repositories**: Data access layer
- **Entities**: JPA database entities
- **DTOs**: Data transfer objects
- **WebSocket Endpoints**: Real-time communication
- **React Components**: Reusable UI components
- **Stores**: Global state management
- **Hooks**: Custom React hooks for logic reuse

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Node.js 18** or higher
- **npm** or **yarn**
- **Maven 3.6+**
- **Application Server** (WildFly recommended)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/joaopedroseq/projectcitrus.git
   cd projectcitrus
   ```

2. **Backend Setup**
   ```bash
   cd backend
   mvn clean install
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   ```

4. **Database Configuration**
   - Configure your database connection in `persistence.xml`
   - Update database credentials as needed

5. **Application Server**
   - Deploy the generated WAR file to your application server
   - Configure datasource in your application server

### Running the Application

1. **Start Backend**
   ```bash
   cd backend
   mvn wildfly:deploy
   ```

2. **Start Frontend**
   ```bash
   cd frontend
   npm start
   ```

3. **Access the Application**
   - Frontend: `http://localhost:3000`
   - Backend API: `http://localhost:8080/projectcitrus/rest`

## 📖 Usage

### Initial Setup

1. **Admin Account**: The system creates a default admin account on first startup
2. **Configuration**: Access system settings to configure evaluation periods and other parameters
3. **User Management**: Import users or create them manually through the admin interface

### Key Workflows

1. **Creating Evaluation Cycles**
   - Navigate to Cycles → Create New Cycle
   - Set start/end dates and configure evaluation parameters

2. **Conducting Appraisals**
   - Access assigned evaluations through the dashboard
   - Complete scoring and provide feedback
   - Submit for review

3. **Managing Courses**
   - Add new training courses with materials
   - Assign courses to users or groups
   - Track completion progress

4. **Real-time Communication**
   - Use the integrated chat system for instant messaging
   - Receive real-time notifications for important events

## 📚 API Documentation

The backend provides comprehensive RESTful APIs:

### Authentication Endpoints
- `POST /rest/auth/login` - User authentication
- `POST /rest/auth/logout` - User logout
- `POST /rest/auth/reset-password` - Password reset

### User Management
- `GET /rest/users` - List users
- `POST /rest/users` - Create user
- `PUT /rest/users/{id}` - Update user
- `DELETE /rest/users/{id}` - Delete user

### Evaluation Management
- `GET /rest/cycles` - List evaluation cycles
- `POST /rest/cycles` - Create cycle
- `GET /rest/appraisals` - List appraisals
- `PUT /rest/appraisals/{id}` - Update appraisal

### Course Management
- `GET /rest/courses` - List courses
- `POST /rest/courses` - Create course
- `PUT /rest/courses/{id}` - Update course

For complete API documentation, see the generated JavaDoc in the `backend/javadoc` directory.

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
npm test
```

### Test Coverage
- **Backend**: JUnit 5 and Mockito for comprehensive unit testing
- **Frontend**: Jest and React Testing Library for component testing
- **Integration**: Selenium for end-to-end testing

### Test Reports
- Backend coverage: `backend/htmlReport/index.html`
- Frontend coverage: Generated by Jest

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding standards and best practices
- Use meaningful commit messages
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass before submitting PRs

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built as part of AOR (Advanced Object-Oriented Programming) coursework
- Special thanks to the development team and contributors
- Icons and UI elements from React Icons and Material-UI

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team

---

**Project Citrus** - Empowering organizations through effective performance management 🍊
