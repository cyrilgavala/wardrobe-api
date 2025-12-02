# Wardrobe API

A RESTful API for wardrobe management built with Spring Boot 4.0, MongoDB, and JWT authentication.

## 🚀 Features

- ✅ User registration and authentication with JWT
- ✅ MongoDB database for flexible document storage
- ✅ BCrypt password encryption
- ✅ Role-based access control (USER, ADMIN)
- ✅ Comprehensive error handling
- ✅ API documentation with Swagger/OpenAPI
- ✅ Health checks and monitoring endpoints
- ✅ CORS configuration for frontend integration
- ✅ Input validation with custom messages

## 📋 Prerequisites

- Java 21 or higher
- MongoDB 5.0+ (local or MongoDB Atlas)
- Gradle 8.x (included via wrapper)

## 🔧 Configuration

### Environment Variables

Set the following environment variables before running the application:

```bash
# MongoDB Configuration
export MONGODB_URI="mongodb://localhost:27017/wardrobe_app"
# or for MongoDB Atlas:
# export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/database"

export MONGODB_DATABASE="wardrobe_app"

# JWT Configuration (512-bit secret, base64 encoded)
export JWT_SECRET="your-512-bit-secret-here"
export JWT_EXPIRATION_MINUTES="60"

# CORS Configuration (comma-separated origins)
export ALLOWED_ORIGINS="http://localhost:3000,http://localhost:4200"
```

### Generate JWT Secret

Generate a secure 512-bit secret:

```bash
# On macOS/Linux
openssl rand -base64 64

# Or use the provided generator in PROJECT_IMPROVEMENTS.md
```

## 🏃 Running the Application

### Using Gradle

```bash
# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun

# Or run with custom properties
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Using Java

```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/wardrobe-api-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Health Check

- **Health Endpoint**: http://localhost:8080/actuator/health
- **Info Endpoint**: http://localhost:8080/actuator/info

## 🔐 API Endpoints

### Authentication

#### Register New User

```http
POST /api/user/register
Content-Type: application/json

{
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "base64-encoded-password",
  "role": "USER"
}
```

**Response:** `201 Created`

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### Login

```http
POST /api/user/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "base64-encoded-password"
}
```

**Response:** `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### Protected Endpoints

For protected endpoints, include the JWT token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

## 🧪 Testing

### Run Tests

```bash
./gradlew test
```

### HTTP Client Testing

See `user-api.http` for ready-to-use HTTP requests (works with IntelliJ IDEA HTTP Client).

See `API_TESTING.md` for detailed testing instructions.

## 📁 Project Structure

```
wardrobe-api/
├── src/main/java/sk/cyrilgavala/wardrobeapi/
│   ├── config/              # Configuration classes
│   │   ├── AppConfiguration.java
│   │   ├── SecurityConfiguration.java
│   │   └── WebConfiguration.java
│   ├── exception/           # Custom exception classes
│   ├── model/               # Domain entities
│   │   ├── User.java
│   │   └── Role.java
│   ├── repository/          # MongoDB repositories
│   │   └── UserRepository.java
│   ├── security/            # Security components
│   │   ├── TokenAuthenticationFilter.java
│   │   └── TokenProvider.java
│   ├── service/             # Business logic
│   │   ├── UserService.java
│   │   ├── SecurityService.java
│   │   └── impl/
│   ├── web/                 # Web layer
│   │   ├── controller/
│   │   ├── dto/
│   │   └── advise/
│   └── WardrobeApiApplication.java
├── src/main/resources/
│   └── application.yaml
├── build.gradle
└── README.md
```

## 🔒 Security Notes

⚠️ **Important Security Considerations:**

1. **HTTPS**: Always use HTTPS in production
2. **Secrets**: Never commit JWT secrets to version control
3. **CORS**: Configure allowed origins strictly for production
4. **Rate Limiting**: Consider adding rate limiting for production
5. **Password Encoding**: Passwords are currently base64-encoded in transit (not encryption!)
    - Use HTTPS to protect credentials
    - Consider removing base64 encoding if HTTPS is in place

## 🛠️ Built With

- [Spring Boot 4.0](https://spring.io/projects/spring-boot) - Framework
- [Spring Security](https://spring.io/projects/spring-security) - Authentication & Authorization
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb) - Database Access
- [JJWT](https://github.com/jwtk/jjwt) - JSON Web Tokens
- [Lombok](https://projectlombok.org/) - Boilerplate Code Reduction
- [SpringDoc OpenAPI](https://springdoc.org/) - API Documentation
- [ModelMapper](http://modelmapper.org/) - Object Mapping

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📧 Support

For issues and questions, please open an issue on GitHub.

## 🔍 Additional Resources

- [HTTP Request Examples](user-api.http)

---

**Version:** 0.0.1-SNAPSHOT  
**Last Updated:** December 2, 2025


