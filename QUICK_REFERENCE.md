# 🚀 IQsure - Quick Reference Card

## ⚡ Start Application

```bash
# Backend (Terminal 1)
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure
mvn spring-boot:run

# Frontend (Terminal 2)
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure\frontend
ng serve
```

**Access:** http://localhost:4200

---

## 🔑 Login Credentials

**Admin:**
- Email: `admin@iqsure.com`
- Password: `admin123`

**Note:** No demo users. All users must register.

---

## 📋 Admin Setup (5 Min)

1. **Badges** → Create 4 (10, 50, 150, 300 pts)
2. **Quizzes** → Create 3 (Health, Life, Motor)
3. **Policies** → Create 6 (2 per type)
4. **Discount Rules** → Create 3 rules
5. **Rewards** → Create 3 (optional)

---

## 🎮 Key Features

- ⏱️ Quiz Timer (5 min)
- 🚀 Speed Bonus (10-50%)
- 🏆 8 Achievements
- 🏅 4 Badges
- ⚡ Level System
- 💰 Savings Calculator
- 📊 Leaderboard
- 📱 Social Sharing

---

## 🔒 Security

- ✅ BCrypt password hashing
- ✅ Email validation
- ✅ Min 6 char passwords
- ✅ Admin protection
- ✅ Role-based access

---

## 📁 Important Files

- `ADMIN_SETUP_GUIDE.md` - Quick admin setup
- `DEPLOYMENT_GUIDE.md` - Production deploy
- `DEMO_GUIDE.md` - Presentation script
- `PROJECT_SUMMARY.md` - Complete overview

---

## 🎯 Demo Script

1. Login validation
2. Register new user
3. Take quiz with timer
4. Show achievements
5. Browse policies
6. Preview discounts
7. Savings calculator
8. Admin panel tour

---

## 🐛 Troubleshooting

**Port in use:**
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**CORS error:**
- Check backend is running
- Verify WebConfig.java

**Can't login:**
- Check password (min 6 chars)
- Email must include @

---

## 📞 Quick Links

- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

**🎉 You're Ready to Go!**
