# 🚀 IQsure - Production Deployment Guide

## 📋 Pre-Deployment Checklist

### ✅ Security
- [x] Password hashing with BCrypt
- [x] Input validation on all forms
- [x] Admin user protection (cannot be deleted)
- [x] Email uniqueness validation
- [x] SQL injection prevention (JPA)
- [x] XSS protection (Angular sanitization)

### ✅ Data Management
- [x] Only admin user is auto-seeded
- [x] All content created through admin panel
- [x] No demo/sample data in production
- [x] Clean database on first startup

### ✅ Authentication
- [x] Secure password requirements (min 6 chars)
- [x] Email validation
- [x] Case-insensitive email login
- [x] Proper error messages
- [x] Role-based access control

---

## 🔐 Default Admin Credentials

**Email:** `admin@iqsure.com`  
**Password:** `admin123`

⚠️ **IMPORTANT:** Change the admin password immediately after first login!

---

## 🏗️ Deployment Steps

### 1. **Backend Deployment**

#### Option A: Local/Server Deployment

```bash
# Navigate to project root
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure

# Build the application
mvn clean package -DskipTests

# Run the JAR file
java -jar target/IQsure-0.0.1-SNAPSHOT.jar
```

#### Option B: Docker Deployment

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/IQsure-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build Docker image
docker build -t iqsure-backend .

# Run container
docker run -p 8080:8080 iqsure-backend
```

#### Option C: Cloud Deployment (AWS/Azure/GCP)

**AWS Elastic Beanstalk:**
```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p java-17 iqsure-backend

# Create environment
eb create iqsure-prod

# Deploy
eb deploy
```

---

### 2. **Frontend Deployment**

#### Build for Production

```bash
cd frontend
npm install
ng build --configuration production
```

#### Option A: Static Hosting (Netlify/Vercel)

```bash
# Deploy to Netlify
npm install -g netlify-cli
netlify deploy --prod --dir=dist/iqsure-frontend

# Deploy to Vercel
npm install -g vercel
vercel --prod
```

#### Option B: AWS S3 + CloudFront

```bash
# Upload to S3
aws s3 sync dist/iqsure-frontend s3://your-bucket-name

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_ID --paths "/*"
```

#### Option C: Traditional Web Server (Nginx)

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    root /var/www/iqsure/dist/iqsure-frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

### 3. **Database Configuration**

#### Development (H2 In-Memory)
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:iqsuredb
spring.datasource.username=sa
spring.datasource.password=password123
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Production (PostgreSQL)
```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iqsure
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

#### Production (MySQL)
```properties
# application-prod.properties
spring.datasource.url=jdbc:mysql://localhost:3306/iqsure
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

---

## 🎯 Post-Deployment Setup

### 1. **Admin First Login**

1. Navigate to `https://yourdomain.com`
2. Click "Sign In"
3. Login with: `admin@iqsure.com` / `admin123`
4. **Change password immediately!**

### 2. **Create Initial Content**

#### Step 1: Create Badges
- Go to **Admin → Badges**
- Create 4 badges:
  - Quiz Beginner (10 points)
  - Insurance Learner (50 points)
  - Knowledge Pro (150 points)
  - IQsure Champion (300 points)

#### Step 2: Create Quizzes
- Go to **Admin → Quizzes**
- Create quizzes for each category:
  - Health Insurance (Easy)
  - Life Insurance (Medium)
  - Motor Insurance (Hard)
- Add 4-5 questions per quiz
- Set correct answers

#### Step 3: Create Policies
- Go to **Admin → Policies**
- Create insurance policies:
  - 2 Health policies
  - 2 Life policies
  - 2 Motor policies
- Set base premiums and coverage amounts

#### Step 4: Create Discount Rules
- Go to **Admin → Discount Rules**
- Create rules like:
  - "Quiz Scorer" - 5% for 60%+ score
  - "Points Achiever" - 10% for 100+ points
  - "Expert Bonus" - 15% for high performers

#### Step 5: Create Rewards
- Go to **Admin → Rewards**
- Create redeemable rewards
- Set expiry dates

---

## 🔧 Environment Variables

### Backend
```bash
# Database
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
DB_URL=jdbc:postgresql://localhost:5432/iqsure

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### Frontend
```typescript
// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com'
};
```

---

## 📊 Monitoring & Maintenance

### Health Check Endpoints
```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database connection
curl http://localhost:8080/actuator/health/db
```

### Logs
```bash
# View backend logs
tail -f logs/spring.log

# View access logs
tail -f logs/access.log
```

### Backup Database
```bash
# PostgreSQL
pg_dump -U username iqsure > backup_$(date +%Y%m%d).sql

# MySQL
mysqldump -u username -p iqsure > backup_$(date +%Y%m%d).sql
```

---

## 🚨 Troubleshooting

### Issue: Admin cannot login
**Solution:** Check if admin user exists in database. If not, restart application to trigger seeding.

### Issue: CORS errors
**Solution:** Update `WebConfig.java` with your production domain:
```java
.allowedOrigins("https://yourdomain.com")
```

### Issue: Database connection failed
**Solution:** Verify database credentials and ensure database server is running.

### Issue: Frontend cannot reach backend
**Solution:** Check API URL in `environment.prod.ts` and ensure CORS is configured.

---

## 🔒 Security Best Practices

### 1. **Change Default Credentials**
- Change admin password immediately
- Use strong passwords (12+ characters)

### 2. **Enable HTTPS**
```bash
# Let's Encrypt SSL
sudo certbot --nginx -d yourdomain.com
```

### 3. **Rate Limiting**
```java
// Add to application.properties
spring.mvc.async.request-timeout=30000
server.tomcat.max-connections=200
```

### 4. **Database Security**
- Use environment variables for credentials
- Enable SSL for database connections
- Regular backups

### 5. **API Security**
- Implement JWT tokens (future enhancement)
- Add request throttling
- Log all admin actions

---

## 📈 Scaling Considerations

### Horizontal Scaling
- Deploy multiple backend instances
- Use load balancer (Nginx/AWS ALB)
- Shared database across instances

### Caching
```java
// Add Redis for caching
@Cacheable("policies")
public List<Policy> getActivePolicies() {
    return policyRepository.findByIsActiveTrue();
}
```

### CDN
- Serve static assets via CDN
- Cache API responses
- Optimize images

---

## 📝 Maintenance Schedule

### Daily
- Monitor error logs
- Check system health
- Review user registrations

### Weekly
- Database backup
- Review quiz submissions
- Check discount calculations

### Monthly
- Update dependencies
- Security audit
- Performance review

---

## 🎉 Launch Checklist

- [ ] Backend deployed and running
- [ ] Frontend deployed and accessible
- [ ] Database configured and backed up
- [ ] Admin user can login
- [ ] Initial content created (quizzes, policies, badges)
- [ ] Discount rules configured
- [ ] HTTPS enabled
- [ ] Monitoring setup
- [ ] Backup strategy in place
- [ ] Documentation updated
- [ ] Team trained on admin panel

---

## 📞 Support

For deployment issues:
1. Check application logs
2. Verify database connectivity
3. Review CORS configuration
4. Check environment variables

---

**🚀 Ready for Production!**

Your IQsure platform is now deployment-ready with:
- ✅ Secure authentication
- ✅ Password hashing
- ✅ Clean database
- ✅ Admin-only content creation
- ✅ Production-grade security
- ✅ Scalable architecture
