# Non-Functional Requirements

## 1. Performance
- The system should handle at least 50 concurrent users without noticeable latency.  
- Average response time for API requests should be less than 500 ms under normal load.  
- Dashboard data should load within 2 seconds after login.  

## 2. Security
- All communication between frontend and backend must use HTTPS**.  
- User authentication must be secured with JWT tokens and passwords must be hashed.  
- The system must implement role-based access control (e.g., normal user vs. admin).  

## 3. Reliability & Availability
- The system should achieve at least 99% uptime during active hours (9 am – 9 pm).  
- In case of server crash, the system should be able to recover automatically using restart scripts or container orchestration.  
- Database transactions must guarantee ACID properties to ensure financial data consistency.  

## 4. Scalability
- The backend should be deployable in a containerized environment (e.g., Docker), allowing horizontal scaling.  
- The system should support database sharding or replication when the transaction volume grows.  

## 5. Maintainability
- The backend must follow a layered architecture (Controller → Service → Repository).  
- Code should follow naming conventions and include comments where necessary.  
- Unit tests should cover at least 60% of core business logic.  

## 6. Usability
- The UI should follow responsive design principles, ensuring accessibility across desktop and mobile devices.  
- Navigation must be consistent across all pages.  
- System feedback (e.g., success/error messages) must be provided within 1 second after user action.  

## 7. Portability
- The system should run on Windows, macOS, and Linux for development.  
- The deployed application should run on any standard Java-supported server with PostgreSQL.  

## 8. Monitoring & Logging
- The backend must expose health-check endpoints via Spring Boot Actuator.  
- All errors must be logged with timestamps, severity levels, and stack traces.  
- Logs should be rotated and stored for at least 30 days for debugging and auditing.  
