# IQsure API Documentation

## 📋 Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Admin APIs](#admin-apis)
3. [User APIs](#user-apis)

---

## 🔐 Authentication APIs
**Base URL:** `/api/auth`

### Register User/Admin
- **POST** `/api/auth/register`
- **Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "role": "USER"  // or "ADMIN"
}
```

### Get User by ID
- **GET** `/api/auth/user/{id}`

### Get User by Email
- **GET** `/api/auth/user/email/{email}`

---

## 👨‍💼 Admin APIs
**Base URL:** `/api/admin`

### Quiz Management
- **POST** `/api/admin/quizzes` - Create quiz
- **GET** `/api/admin/quizzes` - Get all quizzes
- **GET** `/api/admin/quizzes/{id}` - Get quiz by ID
- **DELETE** `/api/admin/quizzes/{id}` - Delete quiz

### Question Management
- **POST** `/api/admin/questions` - Create question
- **GET** `/api/admin/questions/quiz/{quizId}` - Get questions by quiz
- **DELETE** `/api/admin/questions/{id}` - Delete question

### Answer Management
- **POST** `/api/admin/answers` - Create answer

### Badge Management
- **POST** `/api/admin/badges` - Create badge
- **GET** `/api/admin/badges` - Get all badges
- **DELETE** `/api/admin/badges/{id}` - Delete badge

### Reward Management
- **POST** `/api/admin/rewards` - Create reward
- **GET** `/api/admin/rewards` - Get all rewards
- **DELETE** `/api/admin/rewards/{id}` - Delete reward

### User Management
- **GET** `/api/admin/users` - Get all users
- **GET** `/api/admin/users/{id}` - Get user by ID
- **DELETE** `/api/admin/users/{id}` - Delete user

### Analytics & Reports
- **GET** `/api/admin/attempts` - Get all quiz attempts
- **GET** `/api/admin/attempts/quiz/{quizId}` - Get attempts by quiz

---

## 👤 User APIs
**Base URL:** `/api/user`

### Quiz Browsing
- **GET** `/api/user/quizzes` - Browse all quizzes
- **GET** `/api/user/quizzes/{id}` - View quiz details
- **GET** `/api/user/quizzes/category/{category}` - Filter by category
- **GET** `/api/user/quizzes/difficulty/{difficulty}` - Filter by difficulty

### Quiz Taking
- **GET** `/api/user/questions/quiz/{quizId}` - Get quiz questions
- **POST** `/api/user/quiz/submit` - Submit quiz answers
```json
{
  "userId": 1,
  "quizId": 1,
  "userAnswers": {
    "1": "Option A",
    "2": "Option B"
  }
}
```

### User Profile
- **GET** `/api/user/{userId}/profile` - Get my profile
- **GET** `/api/user/{userId}/attempts` - Get my quiz attempts
- **GET** `/api/user/{userId}/attempts/quiz/{quizId}` - Get attempts for specific quiz

### Badges
- **GET** `/api/user/badges/available` - View all available badges
- **GET** `/api/user/{userId}/badges` - Get my earned badges

### Rewards
- **GET** `/api/user/rewards` - View available rewards
- **POST** `/api/user/rewards/redeem` - Redeem a reward
```json
{
  "userId": 1,
  "rewardId": 1
}
```
- **GET** `/api/user/{userId}/rewards` - Get my redeemed rewards

### Leaderboard
- **GET** `/api/user/leaderboard` - View top 10 users by points

---

## 📊 Sample Workflow

### Admin Workflow:
1. Create Quiz → POST `/api/admin/quizzes`
2. Add Questions → POST `/api/admin/questions`
3. Add Answers → POST `/api/admin/answers`
4. Create Badges → POST `/api/admin/badges`
5. Create Rewards → POST `/api/admin/rewards`
6. Monitor Attempts → GET `/api/admin/attempts`

### User Workflow:
1. Register → POST `/api/auth/register`
2. Browse Quizzes → GET `/api/user/quizzes`
3. View Questions → GET `/api/user/questions/quiz/{quizId}`
4. Submit Quiz → POST `/api/user/quiz/submit`
5. Check Badges → GET `/api/user/{userId}/badges`
6. Redeem Rewards → POST `/api/user/rewards/redeem`
7. View Leaderboard → GET `/api/user/leaderboard`

---

## 🎯 Key Features
✅ Role-based access (Admin vs User)
✅ Quiz management with categories & difficulty
✅ Points & gamification system
✅ Badge auto-assignment
✅ Reward redemption
✅ Leaderboard
✅ Analytics for admins
✅ First-attempt-only points
