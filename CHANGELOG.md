# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2025-11-22

Major feature release with User Profile Management, Enhanced Downloads, iOS Share Integration, Gallery Enhancements, Favorites System, and Social Sharing features.

### Notes
- **Image Generation History** feature (#5 from roadmap) was cancelled
  - Reason: Prompt templates should remain admin-only for security
  - Alternative: Gallery already provides comprehensive history with filters
  - Generation time already displayed in result page

### Added
- **User Profile Management**: Users can now view and edit their profile information
  - View username, email, full name, role, and member since date
  - Display total images generated count
  - Edit full name and email address
  - Email uniqueness validation
  - Profile update form with validation
- **Image Download Improvements**: Enhanced download functionality for generated images
  - Download result image with custom filename format (`trend-name-date.png`)
  - Download all input images as ZIP file
  - Quick download button in gallery view
  - Organized download buttons in result page
  - Automatic filename generation based on trend name and date
  - Support for batch downloading of input images
- **iOS Share Integration**: Native share functionality for mobile devices
  - Web Share API integration for iOS/Android
  - "Save to Photos" button on iOS devices
  - Native share sheet with multiple options
  - Responsive design: Share on mobile, Download on desktop
  - Graceful fallback for browsers without Share API support
  - Loading states during image fetch
  - Works in both result page and gallery
- **Gallery Enhancements**: Advanced filtering and search capabilities
  - Filter by trend (dropdown selection)
  - Filter by status (COMPLETED, PENDING, PROCESSING, FAILED)
  - Filter by date range (start date and end date)
  - Search by trend name (real-time search)
  - Sort options: Newest/Oldest first, Trend name A-Z/Z-A, Status
  - Results summary showing filtered count
  - Clear all filters button
  - Preserved filter state across pagination
- **Favorites/Bookmarks System**: Save and organize favorite trends
  - Heart icon on trend cards (home page and browse page)
  - One-click favorite/unfavorite with AJAX
  - Visual feedback (filled heart = favorited)
  - "My Favorites" page showing all favorited trends
  - Favorites count and creation date
  - Quick remove from favorites page
  - Unique constraint: one user can favorite a trend only once
  - Auto-load favorite states on page load
- New DTO: `UpdateProfileRequest` for profile updates
- New service method: `UserService.updateProfile()` for updating user profile
- New service method: `UserService.countGeneratedImagesByUsername()` for statistics
- New endpoint: `/images/download/{folder}/{filename}` with custom filename support
- New endpoint: `/images/download-inputs/{imageId}` for ZIP download of input images
- New repository method: `findByIdWithUserAndTrend()` with JOIN FETCH to prevent LazyInitializationException
- New repository methods: `findByUserIdWithFilters()` and `searchByUserIdAndTrendName()` for advanced filtering
- New service method: `GenerateImageService.getUserImagesWithFilters()` for filtering support
- New service method: `GenerateImageService.searchUserImages()` for search functionality
- New service method: `TrendService.getAllActivePublicTrends()` for filter dropdowns
- New entity: `FavoriteTrend` for storing user-trend favorites
- New repository: `FavoriteTrendRepository` with favorite management queries
- New service: `FavoriteService` with toggle/check/list favorites methods
- New controller: `FavoriteController` with REST API and page endpoints
- New database migration: `v1.3-create-favorite-trends-table.xml`
- Enhanced `/account` page with profile information and edit form
- Enhanced `/gallery` page with comprehensive filter and search UI
- New `/favorites` page showing user's favorited trends
- Enhanced home page and trends page with heart icon buttons

### Changed
- Updated `AccountController` to load and display user information
- Updated `ImageController` with download endpoints and ZIP creation functionality
- Updated `GeneratedImageRepository` with advanced filtering queries and JOIN FETCH
- Updated `GenerateImageService` with filter and search methods
- Updated `GalleryController` with comprehensive filter parameter handling
- Updated `TrendService` with method to get all active public trends
- Updated `account.html` template with profile information section
- Updated `result.html` with improved download button layout and responsive share functionality
- Updated `gallery.html` with comprehensive filter UI, search box, and sort options
- Updated `home.html` with heart icon buttons and favorite toggle functionality
- Updated `trend-list.html` with heart icon buttons and AJAX favorite handling
- Updated `favorites.html` (new) with favorite trends display and removal
- Updated `main.html` navigation with "Favorites" link in navbar and user dropdown
- Updated `db.changelog-master.xml` to include favorites table migration
- Improved account settings page layout and organization

### Fixed
- **LazyInitializationException** when downloading input images as ZIP
  - Added `findByIdWithUserAndTrend()` with JOIN FETCH for eager loading
  - Optimized from 3 queries to 1 query (N+1 problem resolved)
  - Improved performance and eliminated session management issues
- **CSRF Protection Issue** with favorite image endpoints
  - Changed endpoint from `/favorite/image/{imageId}` to `/api/favorites/image/{imageId}`
  - Ensured proper CSRF token handling for AJAX requests
- **Copy Link Button Not Working** on shared image pages
  - Fixed JavaScript function loading timing with Thymeleaf Layout Decorator
  - Moved functions from `layout:fragment="scripts"` to `layout:fragment="content")`
  - Functions now defined at top of content fragment as `window` properties
  - Resolved timing issues with inline `onclick` handlers
  - Fixed for both `/share/{token}` and `/my-shares` pages
- **Facebook Share Preview Issues**
  - Added missing `og:url` meta tag for proper link attachment
  - Implemented absolute URLs for `og:image` and `og:url` tags
  - Enhanced Open Graph and Twitter Card meta tags
  - Facebook now properly displays link, image, title, and description
- **Duplicate URL in Copy Link Function**
  - Fixed double concatenation of `window.location.origin`
  - Corrected share URL construction in modal
- **LazyInitializationException on My Shares Page** (`/my-shares`)
  - Added `@EntityGraph` to `findByUserIdOrderByCreatedAtDesc()` repository method
  - Eagerly fetch `generatedImage`, `user`, and nested `trend` entities
  - Eliminated N+1 query problem
- **LazyInitializationException on Public Shared View** (`/share/{token}`)
  - Added `JOIN FETCH s.user` to `findByShareTokenWithDetails()` query
  - Eagerly loaded User entity accessed in template
- **Dropdown Menu Clipping on Shared Image Page**
  - Applied CSS overrides for `.card`, `.card-body`, `.card-footer` overflow
  - Changed dropdown to `dropup` with `dropdown-menu-end` alignment
  - Fixed z-index stacking issues
- **Social Share Buttons Not Triggering Events**
  - Fixed `querySelector('#')` error in `main.js` smooth scroll logic
  - Added validation to skip empty or `#` href values
  - Added try-catch block for robustness
- **Multiple Input Images Not Sent to Gemini API**
  - Fixed `GeminiService.buildImageEditRequest()` to iterate through all input images
  - Changed from sending only first image to sending all uploaded images
  - Gemini now receives complete image context for generation

---

## [1.0.1] - 2024-11-21

### Security
- **Critical Fix**: Implemented authorization check for generated image viewing
  - Users can now only view their own generated images
  - Unauthorized access to `/generate/result/{id}` redirects to gallery with error message
  - Added ownership validation using `@AuthenticationPrincipal`

### Added
- Password change functionality
  - New `/account` page for account settings
  - Users can change their password with current password verification
  - Validation for password strength (minimum 6 characters)
  - New DTO: `ChangePasswordRequest` for password changes
  - New service method: `UserService.changePassword()`
- GitHub link in footer for open source visibility
- Live demo link in README: https://trendy.devlord.net
- Prominent AI-generated code disclaimer in README
- "Account Settings" menu item in user dropdown

### Changed
- Updated README.md with version badge, live demo link, and AI development section
- Enhanced footer with "🤖 Powered by AI • Built with Claude" tagline
- Updated navigation menu with account settings link

### Documentation
- Created CHANGELOG.md to track version history
- Updated README.md with comprehensive project information

---

## [1.0.0] - 2024-11-20

### Added
- Initial release of Trendy - AI-Powered Image Generation Platform
- User authentication and authorization (Spring Security)
  - User registration with username, email, password, and full name
  - Login/logout functionality
  - Role-based access control (ADMIN, USER)
- Image generation features
  - Integration with Google Gemini AI for image generation
  - Support for multiple input images
  - Custom prompt support
  - Trend-based image generation
- Trend management
  - Browse trends by category (Fashion, Art, Photography, Design, Tech, Social)
  - Search trends by name
  - Sort trends (Most Popular, Least Used, Newest, Oldest)
  - Featured trends on homepage
  - Admin can create/edit/delete trends
  - Admin can test trends before publishing
- Gallery features
  - View all generated images
  - Delete generated images
  - Modal view for image details
  - Display input images and prompts
- MinIO integration for object storage
  - All images stored in MinIO (not local filesystem)
  - Organized folders: `trends/`, `user-uploads/`, `generated/`
  - Presigned URLs for secure image access
- Admin dashboard
  - User management
  - Trend statistics
  - System overview
- Modern, responsive UI
  - Bootstrap 5 integration
  - Thymeleaf templates
  - Custom logo and branding
  - Favicon support
  - Mobile-friendly design
- Docker support
  - Dockerfile for Spring Boot application
  - Docker Compose with MySQL and MinIO services
  - Health checks configured
  - Multi-stage build for optimization
- Configuration management
  - YAML-based configuration (converted from properties)
  - Environment-specific profiles (dev, prod)
  - Externalized sensitive configuration
- Database management
  - Liquibase for database migrations
  - MySQL database
  - Entities: User, Trend, TrendExample, GeneratedImage, Rating
- API documentation
  - Swagger/OpenAPI integration
  - Interactive API documentation at `/swagger-ui.html`
- Error handling
  - Custom error pages (403, 404, 500)
  - Global exception handler
  - User-friendly error messages
- Security features
  - Password encryption (BCrypt)
  - CSRF protection
  - Secure static resource serving
  - Public access to logo and static assets

### Technical Stack
- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL 8.0, Liquibase
- **Storage**: MinIO
- **AI**: Google Gemini AI API
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Security**: Spring Security
- **Build**: Gradle 8.5
- **Containerization**: Docker, Docker Compose
- **Documentation**: Swagger/OpenAPI

### Development
- 100% AI-generated code using Claude AI
- Production-ready architecture
- Best practices followed
- Comprehensive error handling
- Logging with SLF4J and Logback

---

## Version Naming Convention

- **Major version** (x.0.0): Breaking changes, major features
- **Minor version** (1.x.0): New features, non-breaking changes
- **Patch version** (1.0.x): Bug fixes, security patches

---

## Links

- **Live Demo**: https://trendy.devlord.net
- **GitHub**: https://github.com/tinhbv101/trendy
- **Documentation**: [README.md](README.md)
- **Roadmap**: [ROADMAP.md](ROADMAP.md)

