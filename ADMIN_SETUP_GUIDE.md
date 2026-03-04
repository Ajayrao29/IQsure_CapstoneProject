# 👨‍💼 Admin Quick Start Guide

## 🎯 First Time Setup (5 Minutes)

### Step 1: Login as Admin
1. Go to `http://localhost:4200`
2. Click "Sign In"
3. Enter credentials:
   - Email: `admin@iqsure.com`
   - Password: `admin123`

---

### Step 2: Create Badges (1 minute)

Navigate to **Admin → Badges** and create:

1. **Quiz Beginner**
   - Description: "Earned by scoring your first points on IQsure!"
   - Points Required: `10`

2. **Insurance Learner**
   - Description: "Earned by accumulating 50 points through quiz attempts"
   - Points Required: `50`

3. **Knowledge Pro**
   - Description: "You've reached 150 points — you're becoming an insurance expert!"
   - Points Required: `150`

4. **IQsure Champion**
   - Description: "The ultimate badge — 300 points! You truly understand insurance"
   - Points Required: `300`

---

### Step 3: Create Quizzes (2 minutes)

Navigate to **Admin → Quizzes**

#### Quiz 1: Health Insurance Basics
- **Title:** Health Insurance Basics
- **Category:** Health
- **Difficulty:** EASY

**Questions:**
1. What does a health insurance premium refer to?
   - Options: `Monthly payment for coverage,Medical bill amount,Deductible payment,Copay amount`
   - Correct Answer: `Monthly payment for coverage` (Index: 0)

2. What is a deductible in health insurance?
   - Options: `Monthly premium,Amount you pay before insurance kicks in,Copay for doctor visit,Maximum coverage limit`
   - Correct Answer: `Amount you pay before insurance kicks in` (Index: 1)

3. What does 'copay' mean?
   - Options: `Total bill amount,Fixed amount you pay per visit,Insurance company's share,Annual premium`
   - Correct Answer: `Fixed amount you pay per visit` (Index: 1)

4. What is an HMO plan?
   - Options: `High-cost plan,Managed care requiring referrals,No-network plan,Emergency-only plan`
   - Correct Answer: `Managed care requiring referrals` (Index: 1)

#### Quiz 2: Life Insurance Fundamentals
- **Title:** Life Insurance Fundamentals
- **Category:** Life
- **Difficulty:** MEDIUM

**Questions:**
1. Which type of life insurance provides coverage for a specific period?
   - Options: `Whole Life,Term Life,Universal Life,Variable Life`
   - Correct Answer: `Term Life` (Index: 1)

2. What is a beneficiary in life insurance?
   - Options: `The insurance agent,Person who receives the payout,The policyholder,The underwriter`
   - Correct Answer: `Person who receives the payout` (Index: 1)

3. What does 'face value' of a life insurance policy mean?
   - Options: `Monthly premium cost,Death benefit amount,Cash value,Surrender value`
   - Correct Answer: `Death benefit amount` (Index: 1)

4. Which life insurance type builds cash value over time?
   - Options: `Term Life,Whole Life,Group Life,Accidental Death`
   - Correct Answer: `Whole Life` (Index: 1)

#### Quiz 3: Motor Insurance Knowledge
- **Title:** Motor Insurance Knowledge
- **Category:** Motor
- **Difficulty:** HARD

**Questions:**
1. What does comprehensive auto insurance cover?
   - Options: `Only collisions,Only theft,Damage from non-collision events plus theft,Only liability`
   - Correct Answer: `Damage from non-collision events plus theft` (Index: 2)

2. What is a 'no-claim bonus'?
   - Options: `Penalty for claims,Discount for not making claims,Extra coverage amount,Premium increase`
   - Correct Answer: `Discount for not making claims` (Index: 1)

3. What does liability coverage in motor insurance protect?
   - Options: `Your own car,Other people's property and injuries,Your medical bills only,Car theft`
   - Correct Answer: `Other people's property and injuries` (Index: 1)

---

### Step 4: Create Policies (1 minute)

Navigate to **Admin → Policies**

1. **Basic Health Shield**
   - Type: HEALTH
   - Description: "Affordable health coverage for individuals with basic medical needs"
   - Base Premium: `300`
   - Coverage Amount: `50000`
   - Duration (months): `12`
   - Active: ✅

2. **Premium Health Guard**
   - Type: HEALTH
   - Description: "Comprehensive health insurance with hospital, surgery, and specialist coverage"
   - Base Premium: `750`
   - Coverage Amount: `200000`
   - Duration (months): `12`
   - Active: ✅

3. **Term Life Protector**
   - Type: LIFE
   - Description: "10-year term life insurance with affordable premiums for young professionals"
   - Base Premium: `500`
   - Coverage Amount: `500000`
   - Duration (months): `120`
   - Active: ✅

4. **Whole Life Legacy Plan**
   - Type: LIFE
   - Description: "Lifetime coverage that builds cash value over time"
   - Base Premium: `1200`
   - Coverage Amount: `1000000`
   - Duration (months): `360`
   - Active: ✅

5. **Basic Auto Cover**
   - Type: MOTOR
   - Description: "Third-party liability coverage for your vehicle"
   - Base Premium: `200`
   - Coverage Amount: `25000`
   - Duration (months): `12`
   - Active: ✅

6. **Comprehensive Auto Shield**
   - Type: MOTOR
   - Description: "Full coverage including collision, theft, and natural disasters"
   - Base Premium: `650`
   - Coverage Amount: `100000`
   - Duration (months): `12`
   - Active: ✅

---

### Step 5: Create Discount Rules (1 minute)

Navigate to **Admin → Discount Rules**

1. **Quiz Scorer**
   - Description: "5% discount if you score at least 60% on any quiz"
   - Min Quiz Score %: `60`
   - Min Points: `0`
   - Min Badges: `0`
   - Discount %: `5`
   - Policy Type: `All Types`
   - Active: ✅

2. **Points Achiever**
   - Description: "10% discount for users with 100+ points and 1+ badge"
   - Min Quiz Score %: `0`
   - Min Points: `100`
   - Min Badges: `1`
   - Discount %: `10`
   - Policy Type: `All Types`
   - Active: ✅

3. **Health Expert Bonus**
   - Description: "15% discount on health policies for high scorers with 2+ badges"
   - Min Quiz Score %: `80`
   - Min Points: `50`
   - Min Badges: `2`
   - Discount %: `15`
   - Policy Type: `HEALTH`
   - Active: ✅

---

### Step 6: Create Rewards (Optional)

Navigate to **Admin → Rewards**

1. **Premium Discount**
   - Reward Type: "Premium Discount"
   - Discount Value: `5`
   - Expiry Date: `2026-12-31`

2. **Health Bonus**
   - Reward Type: "Health Bonus"
   - Discount Value: `10`
   - Expiry Date: `2026-09-30`

3. **Loyalty Reward**
   - Reward Type: "Loyalty Reward"
   - Discount Value: `15`
   - Expiry Date: `2027-03-31`

---

## ✅ Setup Complete!

Your IQsure platform is now ready for users!

### Test the Platform:

1. **Logout** from admin account
2. **Register** a new user account
3. **Take a quiz** and earn points
4. **Check badges** - should unlock automatically
5. **Browse policies** - see personalized discounts
6. **Purchase a policy** - test the full flow

---

## 🔧 Admin Panel Features

### Users Management
- View all registered users
- See user points and activity
- Delete users (except admins)

### Quiz Management
- Create/Edit/Delete quizzes
- Add questions with 4 options
- Set correct answers
- Organize by category and difficulty

### Policy Management
- Create/Edit/Delete insurance policies
- Set base premiums and coverage
- Toggle active/inactive status
- Manage all policy types

### Badge Management
- Create achievement badges
- Set point requirements
- Delete badges

### Reward Management
- Create redeemable rewards
- Set discount values
- Manage expiry dates

### Discount Rules
- Create dynamic discount rules
- Set multiple conditions
- Apply to specific policy types
- Toggle active/inactive

---

## 💡 Tips for Admins

1. **Start Simple** - Create 1-2 quizzes first, test them, then add more
2. **Balance Discounts** - Don't make discounts too easy or too hard to achieve
3. **Monitor Users** - Check leaderboard to see engagement
4. **Update Content** - Add new quizzes regularly to keep users engaged
5. **Test Everything** - Create a test user account and go through the full flow

---

## 🚨 Important Notes

- **Cannot delete admin users** - This is a security feature
- **Points only on first attempt** - Users can retake quizzes but won't earn more points
- **Maximum 50% discount** - System caps total discounts at 50%
- **Badges auto-unlock** - No manual intervention needed
- **Email must be unique** - Each user needs a unique email address

---

**🎉 You're all set! Happy administering!**
