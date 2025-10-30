#  Personal Finance & Budget Tracker
ELEC5619 Object Oriented Application Frameworks  
Sprint 0  
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
- **Database**: MySQL / MongoDB 
- **Authentication**: JWT-based authentication  
- **Hosting**: AWS / Azure / Heroku
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
| Xu Liu | QA & Documentation | Perform testing, maintain documentation |

---

## 5. Sprint 0 Deliverables
- GitHub Organisation & Repository created and all members added.  
- README document completed with problem statement, key requirements, and team roles.  
- JIRA project created with Sprint 0 tasks, descriptions, assignees, and due dates.

---

## 6. Developing Guide

### Backend

1. Make sure you have JDK 17+ and Maven installed.

2. Configure your database connection in
```
src/main/resources/application.properties
```
3. Start your database service and ensure it is running.
   
4. Run the backend:
```
Run BackendApplication.java
```
5. The backend will start at:
```
http://localhost:8080
```


   
### Frontend

0. Make sure you have `Node.js` (v22 recommended) and `pnpm` installed globally:

```bash
npm install -g pnpm
```

1. Clone the repo, then install dependencies:

```bash
cd ELEC5619_Practical03_Group_12/frontend
pnpm install
```
```bash
pnpm add i18next react-i18next
```

2. Start the dev server:

```bash
pnpm dev
```

3. Open the app at `http://localhost:5173`

---

## 7. License
This project is developed for the ELEC5619 course at The University of Sydney and is not intended for commercial use.


