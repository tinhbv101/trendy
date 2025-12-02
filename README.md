# 🎨 Trendy - AI-Powered Image Transformation Platform

<div align="center">
  <img src="src/main/resources/static/l_logo.png" alt="Trendy Logo" width="200" height="200">
  
  <p><strong>Transform your photos with AI-powered trends</strong></p>

  [![Version](https://img.shields.io/badge/Version-v1.2.0-brightgreen.svg)](https://github.com/tinhbv101/trendy/releases/tag/v1.2.0)
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

### 🆕 What's New in v1.2.0

**🎨 AI Image Analysis & Editing Features:**
- 🤖 **AI Image Analysis**: Upload images to get detailed AI-powered analysis including:
  - 📊 Dominant colors and color palette extraction
  - 🎯 Object detection and identification
  - 🎨 Style analysis (artistic, photographic, abstract, etc.)
  - 💡 Smart editing suggestions based on image content
- ✏️ **AI Image Editing**: Transform your images with AI-powered editing tools:
  - 🖼️ Multiple edit types: Remove background, Enhance quality, Change style, Add effects, Resize/crop
  - 🎭 Style transformations and artistic effects
  - 🔍 Smart quality enhancement
  - ✨ Background removal and modification

**🔧 UI/UX Improvements:**
- 🎯 **Hidden AI Features from Public Trends**: "AI Image Editing" trend is now hidden from public trend listings
- 🎨 **Cleaner Navigation**: Removed AI-specific menu items from user dropdown to reduce clutter
- 📱 **Better Organization**: AI features remain accessible via navbar for authenticated users

**Technical Improvements:**
- 🗄️ **New Database Schema**: Added image analysis tables for storing AI analysis results
- 🔄 **Enhanced API Controllers**: New endpoints for image analysis and editing operations
- 📊 **DTO Pattern**: Comprehensive DTOs for image analysis results and editing requests
- 🎨 **Color Palette System**: Advanced color extraction and analysis capabilities

### 🆕 What's New in v1.1.2

**🌍 Internationalization & Multi-language Support:**
- 🎯 **15 Supported Languages**: English, Vietnamese, Chinese, German, French, Portuguese, Russian, Japanese, Korean, Arabic, Hindi, Italian, Indonesian, Thai, Spanish
- 🔄 **Seamless Language Switching**: Language selector in header with URL persistence
- 🌐 **Complete Translation**: All UI components, error messages, and notifications fully translated
- 🎨 **RTL Support**: Right-to-left language support for Arabic
- 🔧 **Advanced i18n Implementation**: Thymeleaf inline JavaScript and global i18n objects for dynamic translations

**🎨 Enhanced Image Generation:**
- 📐 **Custom Aspect Ratio Selection**: Choose from 1:1, 9:16, 16:9, 4:3, 3:4 when generating images
- 🤖 **AI Model Selection**: Select between Gemini 2.5 Flash Image or Gemini 3 Pro Image models
- ⚙️ **Flexible Configuration**: Override trend defaults or use trend settings per generation
- 🎯 **User Control**: Full control over image generation parameters for each request

**Technical Improvements:**
- 🐛 Fixed language switching issues on login/register pages
- ⚡ Enhanced locale detection and cookie management
- 🔒 Improved Spring i18n configuration with proper interceptor ordering
- 📝 Complete message properties for all supported languages (477+ keys each)
- 🔧 Dynamic AI model endpoint selection based on user choice

### 🆕 What's New in v1.1.0

**Major Features Added:**
- 👤 **User Profile Management** - View and edit your profile, see statistics
- 📥 **Enhanced Downloads** - Custom filenames and ZIP download for input images
- 📱 **iOS Share Integration** - Native "Save to Photos" for mobile devices
- 🔍 **Advanced Gallery** - Filter by trend, status, date range, and search
- ⭐ **Favorites System** - Save favorite trends and generated images
- 🔗 **Social Sharing** - Create public share links with rich previews

**Improvements:**
- 🐛 Fixed multiple bugs including LazyInitializationException issues
- ⚡ Performance optimizations with @EntityGraph
- 🎨 UI/UX improvements across the platform
- 🔒 Enhanced security with proper CSRF handling

See [CHANGELOG.md](CHANGELOG.md) for complete details.

### ✨ Key Features

**Core Features:**
- 🎯 **Browse Trends**: Discover and explore diverse AI trends
- 🖼️ **Image Generation**: Upload multiple images and create new ones with AI
  - 📐 **Custom Aspect Ratios**: Choose from 5 aspect ratio options (1:1, 9:16, 16:9, 4:3, 3:4)
  - 🤖 **AI Model Selection**: Choose between Gemini 2.5 Flash or Gemini 3 Pro models
- 📁 **My Gallery**: Manage and review generated images with advanced filters
- 🔒 **User Authentication**: Secure registration and login
- 👑 **Admin Panel**: Manage trends, users, and system

**v1.1.0 New Features:**
- 👤 **User Profile Management**: View and edit profile, display statistics
- 📥 **Enhanced Downloads**: Custom filenames and ZIP download for input images
- 📱 **iOS Share Integration**: Native share functionality for mobile devices
- 🔍 **Advanced Gallery Filters**: Filter by trend, status, date range, and search
- ⭐ **Favorites System**: Save favorite trends and generated images
- 🔗 **Social Sharing**: Create public share links with Open Graph support
- 📊 **Sort Options**: Sort by popularity, newest, oldest, least used
- 🗑️ **Delete Images**: Remove old images and manage storage
- 💾 **MinIO Storage**: Secure and scalable image storage

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
- Google Gemini 3 Pro Image API
- Dynamic model selection per generation request

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
java -jar build/libs/trendy-1.2.0.jar
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
- **Password**: `Password123@`
- **Role**: ADMIN

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
- **Choose Aspect Ratio**: Select from 1:1, 9:16, 16:9, 4:3, or 3:4 (or use trend default)
- **Choose AI Model**: Select Gemini 2.5 Flash or Gemini 3 Pro (or use trend default)
- Click "Generate"
- Wait for AI processing (2-30 seconds)
- View the result

#### 4. AI Image Analysis (New in v1.2.0)
- Go to http://localhost:8080/user/ai-analysis
- Upload an image to analyze
- View AI-powered analysis results:
  - **Color Palette**: See dominant colors extracted from the image
  - **Detected Objects**: Identify objects in the image with confidence scores
  - **Style Analysis**: Understand the artistic style of the image
  - **Edit Suggestions**: Get smart recommendations for image editing

#### 5. AI Image Editing (New in v1.2.0)
- Go to http://localhost:8080/user/image-edit
- Upload one or multiple images
- Select edit type:
  - **Remove Background**: Smart background removal
  - **Enhance Quality**: AI-powered quality enhancement
  - **Change Style**: Transform image style (artistic, vintage, modern, etc.)
  - **Add Effects**: Apply creative effects
  - **Resize/Crop**: Smart cropping and resizing
- Click "Edit" to process with AI
- Download individual results or all as ZIP

#### 6. My Gallery
- Go to http://localhost:8080/gallery
- View all generated images
- **Filter & Search**: Use filters to find images by trend, status, or date range
- **Search**: Search by trend name in the search box
- **Sort**: Sort by newest, oldest, or trend name
- Click "View" for details
- Click "Delete" to remove unwanted images
- **Favorite**: Click heart icon to save favorite images

#### 7. User Profile & Account Settings
- Go to http://localhost:8080/account
- View your profile information and statistics
- Edit full name and email address
- Change password securely
- View total images generated count

#### 8. Favorites
- **Favorite Trends**: Click heart icon on any trend card to save it
- **Favorite Images**: Click heart icon on generated images in gallery
- View favorites: Go to "My Favorites" from user menu
- Manage favorite trends and images from dedicated pages

#### 9. Social Sharing
- After generating an image, click "Share Image" button
- Create a public share link with customizable expiry
- Copy link to share with others
- Share directly to Facebook, Twitter, or Pinterest
- Manage all shared links from "My Shares" page
- View share statistics (view count, expiry date)

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
POST /generate/{trendId}              - Generate image
GET  /gallery                         - View user gallery (with filters)
POST /gallery/delete/{id}             - Delete generated image
GET  /account                         - View account settings
POST /account/change-password         - Change password
POST /account/update-profile          - Update profile info
GET  /favorites                       - View favorite trends
GET  /favorites/images                - View favorite images
POST /api/favorites/toggle/{id}       - Toggle trend favorite
POST /api/favorites/image/{id}        - Toggle image favorite
POST /api/share/create                - Create share link
POST /api/share/revoke/{token}        - Revoke share link
GET  /my-shares                       - View my shared links
GET  /share/{token}                   - View public shared image
GET  /user/ai-analysis                - AI Image Analysis page (v1.2.0)
POST /api/image-analysis/analyze      - Analyze image with AI (v1.2.0)
GET  /user/image-edit                 - AI Image Editing page (v1.2.0)
POST /api/image-edit/upload           - Upload images for editing (v1.2.0)
POST /api/image-edit/edit             - Edit images with AI (v1.2.0)
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

### Version 1.1.2 ✅ (Released)
- [x] 🌍 **Multi-language Support** - 15 languages (English, Vietnamese, Chinese, German, French, Portuguese, Russian, Japanese, Korean, Arabic, Hindi, Italian, Indonesian, Thai, Spanish)
- [x] 🔄 **Internationalization (i18n)** - Complete translation system with Thymeleaf
- [x] 🌐 **Language Selector** - Seamless language switching with URL persistence
- [x] 🎯 **RTL Support** - Right-to-left language support for Arabic
- [x] 🔧 **Advanced i18n Configuration** - Spring i18n with proper locale detection
- [x] 📐 **Custom Aspect Ratio Selection** - Choose aspect ratio (1:1, 9:16, 16:9, 4:3, 3:4) when generating images
- [x] 🤖 **AI Model Selection** - Choose between Gemini 2.5 Flash or Gemini 3 Pro models per generation
- [x] ⚙️ **Flexible Generation Options** - Override trend defaults or use trend settings

### Version 1.1.0 ✅ (Released)
- [x] User Profile Management
- [x] Enhanced Image Downloads (custom names + ZIP)
- [x] iOS Native Share Integration
- [x] Advanced Gallery Filters & Search
- [x] Favorites System (Trends & Images)
- [x] Social Sharing with Public Links

### Version 1.2.0 ✅ (Released)
- [x] 🤖 **AI Image Analysis** - Detailed image analysis with color palette, object detection, and style analysis
- [x] ✏️ **AI Image Editing** - Transform images with AI-powered editing tools
- [x] 🎯 **Hidden AI Trends** - Remove "AI Image Editing" from public trend listings
- [x] 🎨 **Cleaner Navigation** - Improved UI/UX with streamlined menus
- [x] 🗄️ **Database Schema Updates** - New tables for image analysis storage

### Version 1.3 (Planned)
- [ ] 🗂️ **Batch Image Editing** - Edit multiple images at once with the same settings
- [ ] 📥 **Download All Results** - Download edited images as ZIP
- [ ] Bulk image generation
- [ ] Comments & ratings on shared images
- [ ] Email notifications
- [ ] Export gallery as PDF/ZIP
- [ ] AI-powered image recommendations

### Version 2.0 (Future)
- [ ] Mobile app (iOS/Android)
- [ ] Video generation
- [ ] Advanced AI models integration
- [ ] Marketplace for trends
- [ ] Public API for third-party integration
- [ ] Real-time collaboration features

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
