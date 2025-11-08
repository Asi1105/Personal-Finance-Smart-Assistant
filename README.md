#  Personal Finance & Budget Tracker
ELEC5619 Object Oriented Application Frameworks
Practical 03 Group 12

---

## 1. Real-World Problem
In the modern digital era, individuals often have multiple income sources, various expenses, and irregular spending patterns. Without proper tracking, it becomes difficult to:
- Identify where money is spent.
- Detect overspending habits.
- Plan and achieve savings goals.

Traditional methods such as manual spreadsheets are time-consuming, prone to errors, and lack real-time insights. There is a need for a **centralised, user-friendly platform** to manage personal finances effectively.

---

## 2. Key Requirements
Based on the identified problem, our application will address the following requirements:

1. **Expense Aggregation**  
   - Allow users to record expenses manually or import them from files.  
   - Categorise expenses (e.g., food, transport, entertainment).  

2. **Data Visualisation**  
   - Generate interactive charts to display spending by category and over time.  
   - Provide monthly and yearly comparison reports.  

3. **Budget & Savings Goals**  
   - Enable users to set budget limits and savings targets.  
   - Notify users when approaching or exceeding budgets.  

4. **User Account Management**  
   - Secure registration and login.  
   - Personalised dashboard per user.

5. **Data Security**  
   - Ensure all financial data is encrypted and securely stored.

---

## 3. Technology Stack
- **Frontend**: React.js
- **Backend**: Spring Boot
- **Database**: PostgreSQL
- **Authentication**: JWT-based authentication
- **Version Control**: GitHub  
- **Project Management**: JIRA

---

## 4. Team Roles & Responsibilities
| Name | Role | Responsibilities |
|------|------|------------------|
| Zhaowen Jiang | Team Leader | Coordinate tasks, manage JIRA board, oversee integration |
| Feifan Zhang | Frontend Developer | Develop UI, integrate with backend APIs |
| Zexin Han | Backend Developer | Build REST APIs, manage server logic |
| Lili Liu | Database Specialist | Design database schema, ensure data integrity |
| Xu Liu | QA & Documentation | Feature design, Perform testing, maintain documentation |

## 5. Dependencies and Libraries

### Backend Dependencies
- Spring Boot 3.2.5
- Java 17
- Spring Boot Starter Web 3.2.5
- Spring Boot Starter Data JPA 3.2.5
- Spring Boot Starter Security 3.2.5
- Spring Boot Starter Actuator 3.2.5
- PostgreSQL Driver (runtime)
- H2 Database (runtime, for testing)
- Lombok 1.18.30
- JWT (jjwt-api, jjwt-impl, jjwt-jackson) 0.11.5
- jbcrypt 0.4
- Spring Boot DevTools (runtime)
- Spring Security Test (test scope)
- Spring Boot Starter Test (test scope)
- JaCoCo Maven Plugin 0.8.11

### Frontend Dependencies
- React 19.1.1
- React DOM 19.1.1
- TypeScript 5.8.3
- Vite 7.1.2
- Tailwind CSS 4.1.12
- Axios 1.11.0
- React Router 7.8.2
- React Hook Form 7.62.0
- Zod 4.1.5
- Zustand 5.0.8
- React Query (TanStack Query) 5.85.5
- Recharts 3.1.2
- React Hot Toast 2.6.0
- React i18next 16.1.3
- i18next 25.6.0
- date-fns 4.1.0
- Lucide React 0.542.0
- Radix UI components (Dialog, Dropdown Menu, Select, Checkbox, Label, Separator, Slot, Tabs)
- Class Variance Authority 0.7.1
- clsx 2.1.1
- tailwind-merge 3.3.1

### Frontend Dev Dependencies
- Vite React SWC Plugin 4.0.0
- ESLint 9.33.0
- TypeScript ESLint 8.39.1
- Rollup 4.28.1

---

## 6. Project Features

The Personal Finance & Budget Tracker application provides the following functionalities:

### User Authentication
- User registration with secure password hashing
- User login with JWT token-based authentication
- Automatic token validation and session management

### Dashboard
- Overview of total account balance and saved amount
- Monthly spending statistics with trend indicators
- Budget utilization percentage across categories
- Savings goal progress visualization
- Recent transaction history
- Quick access to key financial metrics

### Expense Management
- Add new expenses with category, amount, date, and notes
- Edit existing expense records
- View all expenses in a categorized list
- Filter expenses by date range and category
- Expense categorization (Food & Dining, Transportation, Entertainment, Shopping, Bills & Utilities, Healthcare, Travel, Education, Other)

### Budget Management
- Create budgets for specific categories and time periods
- Set monthly or yearly budget limits
- Edit existing budgets
- View budget utilization percentages
- Visual indicators for budgets nearing or exceeding limits
- Budget tracking across multiple categories

### Savings Management
- Set savings goals with target amounts
- Track progress toward savings goals
- Save money from account balance to savings
- Unsave money from savings back to account balance
- View complete savings history with timestamps
- Savings goal progress tracking with percentage indicators

### Financial Reports
- Monthly income and expense reports
- Category-wise spending analysis
- Budget comparison reports
- Spending trends over time
- Interactive charts and visualizations using Recharts

### Deposits
- Record incoming deposits to account balance
- Add descriptions to deposit transactions
- Automatic balance updates

### Settings
- User account settings management
- Application preferences configuration

All features are protected by authentication, ensuring that users can only access their own financial data.

---

## 7. Quick Start Guide

### Prerequisites


Backend:
- JDK 17 or higher
- Maven 3.6 or higher
- PostgreSQL database server

Frontend:
- Node.js v22 (recommended) or compatible version
- pnpm package manager

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Create the database:
   - Start your PostgreSQL server
   - Create a new database named `accounting_db` (or update the connection string in the properties file)

3. Configure database connection:
   - Create or update `src/main/resources/application-local.properties`
   - Set your database connection details:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/accounting_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

4. Build the project:
```bash
mvn clean install
```

5. Run the backend application:
   - Option 1: Run from IDE by executing `BackendApplication.java`
   - Option 2: Use Maven command:
```bash
mvn spring-boot:run
```

6. Verify the backend is running:
   - The backend should start at `http://localhost:8080`
   - Check the health endpoint at `http://localhost:8080/actuator/health`

### Frontend Setup

1. Install pnpm globally (if not already installed):
```bash
npm install -g pnpm
```

2. Navigate to the frontend directory:
```bash
cd frontend
```

3. Install dependencies:
```bash
pnpm install
```

4. Start the development server:
```bash
pnpm dev
```

5. Access the application:
   - Open your browser and navigate to `http://localhost:5173`

### Running Tests

Backend tests:
```bash
cd backend
mvn test
```

To generate JaCoCo coverage report:
```bash
mvn clean test jacoco:report
```

The coverage report will be available at `backend/target/site/jacoco/index.html`

### Initial Setup

1. Start both backend and frontend servers
2. Open the frontend application in your browser
3. Register a new user account using the registration page
4. Log in with your credentials
5. Begin using the application to track your finances
---

## 9. License
This project is developed for the ELEC5619 course at The University of Sydney and is not intended for commercial use.





<img width="1913" height="946" alt="image" src="https://github.com/user-attachments/assets/ff335719-a10e-4823-a39e-f78178ebbda8" />
<img width="1905" height="929" alt="image" src="https://github.com/user-attachments/assets/b94a9f42-83ed-4a67-b8c7-0cf57af25746" />
<img width="1953" height="932" alt="image" src="https://github.com/user-attachments/assets/0fdf0bcb-0f86-4ca1-bd21-e6dabc30e67d" />
<img width="1915" height="929" alt="image" src="https://github.com/user-attachments/assets/4b946558-90d0-470b-979d-f7fdc9f9b996" />
<img width="1910" height="938" alt="image" src="https://github.com/user-attachments/assets/bf41b21b-dd8d-40f0-842e-cd668da64b4b" />
<img width="1914" height="933" alt="image" src="https://github.com/user-attachments/assets/d0db525a-8fc0-4bf7-a51b-b2d767f4f5d7" />
<img width="1912" height="938" alt="image" src="https://github.com/user-attachments/assets/19f7aee5-8b51-4725-934f-9b53738383a9" />






