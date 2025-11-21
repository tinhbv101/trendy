# 🎨 Trendy - AI-Powered Image Transformation Platform

<div align="center">
  <img src="src/main/resources/static/logo.png" alt="Trendy Logo" width="200" height="200">
  
  <p><strong>Transform your photos with AI-powered trends</strong></p>

  [![Version](https://img.shields.io/badge/Version-v1.0.1-brightgreen.svg)](https://github.com/tinhbv101/trendy/releases/tag/v1.0.1)
  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
  [![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
  [![AI Generated](https://img.shields.io/badge/🤖_AI-Generated-blueviolet.svg)](https://github.com/tinhbv101/trendy)
  
  <h3>
    <a href="https://trendy.devlord.net">🌐 Live Demo</a> •
    <a href="https://github.com/tinhbv101/trendy">📂 Repository</a> •
    <a href="#-installation-guide">📖 Documentation</a>
  </h3>
</div>

> [!NOTE]
> 🤖 **100% AI-Generated Code**: This entire project was developed using AI assistance (Claude AI by Anthropic). From architecture design to implementation, testing, and documentation - everything was created through AI-powered development. This demonstrates the potential of AI in modern software development! 🚀

> [!TIP]
> 🌐 **Try it now**: Visit the [live demo](https://trendy.devlord.net) to see Trendy in action!

---

## 📖 Introduction

**Trendy** is a web platform that allows users to transform their photos using popular AI trends. The application integrates with Google Gemini AI to create unique and creative images.

### ✨ Key Features

- 🎯 **Browse Trends**: Discover and explore diverse AI trends
- 🖼️ **Image Generation**: Upload multiple images and create new ones with AI
- 📁 **My Gallery**: Manage and review generated images
- 🔒 **User Authentication**: Secure registration and login
- 👑 **Admin Panel**: Manage trends, users, and system
- 💾 **MinIO Storage**: Secure and scalable image storage
- 🔍 **Search & Filter**: Search and filter trends by category
- 📊 **Sort Options**: Sort by popularity, newest, oldest, least used
- 🗑️ **Delete Images**: Remove old images and manage storage

### 🛠️ Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- Liquibase (Database Migration)
- Lombok

**Frontend:**
- Thymeleaf
- Bootstrap 5.3
- Bootstrap Icons
- JavaScript (Vanilla)

**Database:**
- MySQL 8.0

**Storage:**
- MinIO (S3-compatible object storage)

**AI Integration:**
- Google Gemini 2.5 Flash Image API

**DevOps:**
- Docker & Docker Compose
- Gradle

---

## 📋 System Requirements

### Local Development

- **Java Development Kit (JDK)**: 17 or higher
- **Gradle**: 8.5+ (or use included Gradle Wrapper)
- **MySQL**: 8.0+
- **MinIO**: Latest (or Docker)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code

### Using Docker (Recommended)

- **Docker**: 20.10+
- **Docker Compose**: 2.0+

> **🌐 Want to try without installing?** Check out the [live demo](https://trendy.devlord.net)

---

## 🚀 Installation Guide

### Option 1: Run with Docker (Recommended)

#### 1. Clone repository

```bash
git clone https://github.com/tinhbv101/trendy.git
cd trendy
```

#### 2. Configure environment variables

Create a `.env` file in the root directory:

```bash
# Database
DATABASE_URL=jdbc:mysql://mysql:3306/trend_ai
DATABASE_USERNAME=root
DATABASE_PASSWORD=admin

# MinIO
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET_NAME=trend-ai

# Gemini AI (Get free key at https://makersuite.google.com/app/apikey)
GEMINI_API_KEY=your_gemini_api_key_here

# Server
SERVER_PORT=8080
```

#### 3. Build and run with Docker Compose

```bash
# Build and start all services
docker compose up -d --build

# View logs
docker compose logs -f app

# Stop services
docker compose down

# Stop and remove volumes (clean state)
docker compose down -v
```

#### 4. Access the application

- **Web Application**: http://localhost:8080
- **Live Demo**: https://trendy.devlord.net
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin)
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Option 2: Run locally (without Docker)

#### 1. Install MySQL

```bash
# Create database
mysql -u root -p
CREATE DATABASE trend_ai;
```

#### 2. Install and run MinIO

```bash
# Download MinIO
wget https://dl.min.io/server/minio/release/linux-amd64/minio
chmod +x minio

# Run MinIO
./minio server ./minio-data --console-address ":9001"
```

Or use Docker:

```bash
docker run -d \
  -p 9000:9000 -p 9001:9001 \
  --name minio \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v minio_data:/data \
  minio/minio server /data --console-address ":9001"
```

#### 3. Configure application

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trend_ai
    username: root
    password: your_password

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin

gemini:
  api:
    key: your_gemini_api_key
```

#### 4. Build and run application

```bash
# Build with Gradle
./gradlew clean build

# Run application
./gradlew bootRun

# Or run JAR file
java -jar build/libs/Trendy-0.0.1-SNAPSHOT.jar
```

---

## 🔑 Get Gemini API Key (Free)

1. Visit: https://makersuite.google.com/app/apikey
2. Sign in with your Google Account
3. Click "Create API Key"
4. Copy the API key
5. Add it to `.env` file or `application.yml`

**Free Tier:**
- 1500 requests/day
- Very generous limits for development and testing

---

## 👤 Default Accounts

After the first startup, the system creates default accounts:

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN

### User Account
- **Username**: `user`
- **Password**: `user123`
- **Role**: USER

**⚠️ Important:** Change these passwords in production environment!

---

## 📁 Project Structure

```
trendy/
├── src/
│   ├── main/
│   │   ├── java/net/devlord/trendy/
│   │   │   ├── config/          # Security, MinIO, Swagger config
│   │   │   ├── controller/      # REST & Web controllers
│   │   │   │   ├── admin/       # Admin panel controllers
│   │   │   │   ├── api/         # REST API controllers
│   │   │   │   └── user/        # User-facing controllers
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── model/           # Entities, DTOs, Enums
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # Security implementations
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml  # Main configuration
│   │       ├── db/changelog/    # Liquibase migrations
│   │       ├── static/          # CSS, JS, images
│   │       └── templates/       # Thymeleaf templates
│   └── test/                    # Unit & integration tests
├── docker-compose.yml           # Docker services configuration
├── Dockerfile                   # Application container
├── build.gradle                 # Gradle dependencies
└── README.md                    # This file
```

---

## 🎮 User Guide

### For Users

#### 1. Register an account
- Visit http://localhost:8080/register
- Fill in: username, email, password
- Click "Register"

#### 2. Browse Trends
- Go to http://localhost:8080/trends
- Browse available AI trends
- Search or filter by category
- Sort by: Most Popular, Newest, Oldest, Least Used

#### 3. Generate Image
- Click on the trend you want to use
- Upload 1 or more images (depends on trend)
- Click "Generate"
- Wait for AI processing (2-30 seconds)
- View the result

#### 4. My Gallery
- Go to http://localhost:8080/gallery
- View all generated images
- Click "View" for details
- Click "Delete" to remove unwanted images

### For Admins

#### 1. Admin Login
- Visit http://localhost:8080/login
- Username: `admin`, Password: `admin123`

#### 2. Manage Trends
- Go to http://localhost:8080/admin/trends
- **Create**: Click "New Trend", fill in details and upload images
- **Edit**: Click "Edit" on the trend you want to modify
- **Delete**: Click "Delete" to remove (soft delete)
- **Test**: Click "Test" to try out the trend

#### 3. Dashboard
- Go to http://localhost:8080/admin/dashboard
- View statistics: Users, Trends, Images, etc.
- Monitor system status

---

## 🔧 Configuration

### Application Profiles

**Development:**
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Production:**
```bash
java -jar app.jar --spring.profiles.active=prod
```

### Database Configuration

**MySQL Connection:**
```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:mysql://localhost:3306/trend_ai}
    username: ${DATABASE_USERNAME:root}
    password: ${DATABASE_PASSWORD:admin}
```

### File Storage Configuration

**MinIO:**
```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  bucket-name: ${MINIO_BUCKET_NAME:trend-ai}
```

**Folder Structure in MinIO:**
- `trends/` - Trend thumbnails and example images
- `user-uploads/` - User input images
- `generated/` - AI generated images

### AI Configuration

**Gemini:**
```yaml
gemini:
  api:
    key: ${GEMINI_API_KEY:}
```

---

## 🧪 Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Integration Tests

```bash
./gradlew integrationTest
```

### Manual Testing

```bash
# Health check
curl http://localhost:8080/actuator/health

# API endpoint test
curl http://localhost:8080/api/trends
```

---

## 📊 API Documentation

### Swagger UI
Access: http://localhost:8080/swagger-ui.html

### Main Endpoints

#### Public API
```
GET  /api/trends              - List all active trends
GET  /api/trends/{id}         - Get trend details
GET  /api/trends/search?q=... - Search trends
```

#### User API (Authenticated)
```
POST /generate/{trendId}      - Generate image
GET  /gallery                 - View user gallery
POST /gallery/delete/{id}     - Delete generated image
```

#### Admin API (Admin only)
```
GET    /admin/trends          - List all trends
POST   /admin/trends          - Create new trend
PUT    /admin/trends/{id}     - Update trend
DELETE /admin/trends/{id}     - Delete trend
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. MySQL connection refused
```bash
# Check if MySQL is running
docker compose ps mysql

# Restart MySQL
docker compose restart mysql
```

#### 2. MinIO not accessible
```bash
# Check MinIO status
docker compose ps minio

# Access MinIO console
http://localhost:9001
```

#### 3. Gemini API errors
```bash
# Check API key in logs
docker compose logs app | grep Gemini

# Verify API key
curl "https://generativelanguage.googleapis.com/v1/models?key=YOUR_API_KEY"
```

#### 4. Out of Memory
```bash
# Increase memory limit in docker-compose.yml
environment:
  JAVA_OPTS: -Xms1g -Xmx2g
```

#### 5. Port already in use
```bash
# Change port in docker-compose.yml
ports:
  - "8081:8080"  # Instead of 8080:8080
```

---

## 🔐 Security

### Production Checklist

- [ ] Change all default passwords
- [ ] Use HTTPS (SSL/TLS)
- [ ] Enable CSRF protection
- [ ] Configure rate limiting
- [ ] Set strong JWT secret
- [ ] Use environment variables for sensitive data
- [ ] Enable audit logging
- [ ] Regular security updates
- [ ] Backup database regularly
- [ ] Monitor system logs

### Environment Variables

**NEVER** commit these files to Git:
- `.env`
- `application-prod.yml` (if it contains credentials)
- Any files with sensitive data

---

## 📈 Performance Optimization

### Database

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
```

### Caching

```java
@Cacheable("trends")
public List<Trend> getAllActiveTrends() {
    // ...
}
```

### Image Optimization

- Resize images before uploading to MinIO
- Use WebP format
- Enable CDN for static files

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

### Code Style

- Follow Java conventions
- Use Lombok annotations
- Write meaningful comments
- Add unit tests for new features

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Author

- **Tinh Bui (Ryan Bui)** - *Creator & Developer* - [GitHub](https://github.com/tinhbv101)

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Google Gemini](https://ai.google.dev/) - AI image generation
- [MinIO](https://min.io/) - Object storage
- [Bootstrap](https://getbootstrap.com/) - UI framework
- [Thymeleaf](https://www.thymeleaf.org/) - Template engine

---

## 📞 Support

If you encounter any issues or have questions:

- 📧 Email: tinhbv.it@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/tinhbv101/trendy/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/tinhbv101/trendy/discussions)

---

## 🗺️ Roadmap

### Version 1.1 (Coming Soon)
- [ ] Bulk image generation
- [ ] Image editing tools
- [ ] Social sharing features
- [ ] User profiles
- [ ] Comments & ratings

### Version 2.0 (Future)
- [ ] Mobile app (iOS/Android)
- [ ] Video generation
- [ ] Advanced AI models
- [ ] Marketplace for trends
- [ ] API for third-party integration

---

## 🌟 Star History

If you find this project useful, please consider giving it a star ⭐️

---

## 🤖 AI-Powered Development

This project is a **showcase of AI-assisted software development**, demonstrating how modern AI can build production-ready applications.

### What AI Built
- ✅ **Full-stack Architecture**: Backend (Spring Boot) + Frontend (Thymeleaf/Bootstrap)
- ✅ **Database Design**: Complete schema with Liquibase migrations
- ✅ **Security Implementation**: Spring Security with role-based access
- ✅ **API Integration**: Google Gemini AI for image generation
- ✅ **Cloud Storage**: MinIO S3-compatible object storage setup
- ✅ **Docker Configuration**: Multi-container orchestration
- ✅ **Testing Suite**: Unit and integration tests
- ✅ **Documentation**: Comprehensive README and code comments

### Development Process
- **AI Tool Used**: [Claude AI](https://www.anthropic.com/claude) by Anthropic
- **Development Time**: ~8 hours of AI-assisted coding
- **Human Role**: Requirements specification, testing, and validation
- **Code Quality**: Production-ready, following best practices

### Key Achievements
- 🎯 Zero-to-production web application
- 🔒 Enterprise-grade security
- 📦 Complete CI/CD ready with Docker
- 🎨 Modern, responsive UI
- 📚 Well-documented codebase
- 🧪 Test coverage included
- 🚀 Scalable architecture

### Lessons Learned
This project demonstrates that AI can:
- Write complex, production-grade code
- Design scalable architectures
- Implement security best practices
- Create comprehensive documentation
- Debug and optimize code
- Follow industry standards and conventions

**Note**: While AI generated the code, human oversight ensured quality, security, and alignment with requirements.

---

<div align="center">
  <p>Made with ❤️ by Tinh Bui (Ryan Bui)</p>
  <p>🤖 Powered by AI • Built with Claude</p>
  <p>© 2025 Trendy. All rights reserved.</p>
</div>
