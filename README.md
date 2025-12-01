# 🚀 Bajaj Finserv Webhook SQL Solution – Spring Boot

### 📌 Candidate Details
- **Name:** Nadisetti Jyotsna  
- **Registration No:** 22BCE20098  
- **Email:** njyotsna30@gmail.com  

---

## 📖 Project Overview

This Spring Boot application performs the following steps:

1. Sends a POST request to generate a webhook and access token:

https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA

2. Extracts:
- `webhook`
- `accessToken`
3. Determines SQL question based on last two digits of regNo (98 → Even → Question 2).
4. Manually solves the SQL question and inserts the SQL query into the project.
5. Submits the SQL query to the webhook using the JWT accessToken in the `Authorization` header.

---

## 🛠 Tech Stack

| Technology | Version |
|------------|---------|
| Java       | 17+     |
| Spring Boot| 2.7.9   |
| Maven      | —       |
| Rest API   | RestTemplate |

---

## 📂 Project Structure

bajaj-finserv-webhook-solver/
│ pom.xml
└── src/main/java/com/example/bajaj/
├── Application.java
└── dto/
├── GenerateWebhookResponse.java
└── SubmitRequest.java


---

## ▶️ How to Run the Project

```bash
mvn clean package
java -jar target/bajaj-finserv-webhook-solver-0.0.1-SNAPSHOT.jar

Final SQL Query Used
SELECT d.DEPARTMENT_NAME,
       ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE,
       GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.EMP_ID ASC SEPARATOR ', ') AS EMPLOYEE_LIST
FROM EMPLOYEE e
JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE p.AMOUNT > 70000
GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME
ORDER BY d.DEPARTMENT_ID DESC
LIMIT 10;

Expected Output

Successful run prints:

Submit response status: 200 OK
SUCCESS: Query submitted successfully!


---

Copy and paste that directly into your `README.md`.  
Let me know if you want a GitHub commit message or packaging instructions next!
