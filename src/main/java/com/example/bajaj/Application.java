package com.example.bajaj;

import com.example.bajaj.dto.GenerateWebhookResponse;
import com.example.bajaj.dto.SubmitRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    // Replace with the URL from the task PDF. The example in the PDF uses:
    // POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA"; // fallback if webhook is not returned

    // SAMPLE request body. Replace with your actual details.
    private static final Map<String, String> SAMPLE_REQUEST = Map.of(
            "name", "Nadisetti Jyotsna",
            "regNo", "22BCE20098",
            "email", "njyotsna30@gmail.com"
    );

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting webhook generation flow...");

        // 1) Send POST to generateWebhook
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(SAMPLE_REQUEST, headers);

        ResponseEntity<GenerateWebhookResponse> response = restTemplate.exchange(
                GENERATE_WEBHOOK_URL, HttpMethod.POST, request, GenerateWebhookResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            GenerateWebhookResponse body = response.getBody();
            System.out.println("Received webhook: " + body.getWebhook());
            System.out.println("Received accessToken: " + body.getAccessToken());

            // 2) Decide which question (based on last two digits of regNo)
            String regNo = SAMPLE_REQUEST.get("regNo");
            String lastTwo = getLastTwoDigits(regNo);
            int lastTwoInt = Integer.parseInt(lastTwo);
            boolean isOdd = (lastTwoInt % 2) == 1;
            System.out.println("Last two digits: " + lastTwo + " -> " + (isOdd ? "Odd (Question 1)" : "Even (Question 2)"));

            // 3) Placeholder: Download the corresponding PDF from the links in the task description and solve the SQL problem.
            //   The project cannot automatically access Google Drive links without credentials. So you must
            //   either manually inspect the assigned PDF and craft the final SQL query, or implement an automated
            //   parser if the PDF is accessible programmatically.
            
            // --- IMPORTANT: Put your final SQL query string here ---
            String finalSqlQuery = "SELECT d.DEPARTMENT_NAME, " +
    "ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE, " +
    "GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.EMP_ID ASC SEPARATOR ', ') AS EMPLOYEE_LIST " +
    "FROM EMPLOYEE e " +
    "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
    "WHERE p.AMOUNT > 70000 " +
    "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
    "ORDER BY d.DEPARTMENT_ID DESC " +
    "LIMIT 10;";

            // If you already computed the SQL query, paste it in finalSqlQuery variable.

            // 4) Submit the final query to the webhook URL returned by the service.
            String webhookUrl = body.getWebhook();
            if (webhookUrl == null || webhookUrl.isBlank()) {
                System.out.println("No webhook returned by generateWebhook; falling back to default submit URL");
                webhookUrl = SUBMIT_WEBHOOK_URL;
            }

            SubmitRequest submitRequest = new SubmitRequest(finalSqlQuery);

            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            // The PDF says: use the accessToken as JWT in Authorization header.
            // Common pattern: Authorization: <accessToken>  OR Authorization: Bearer <accessToken>
            // If the service expects a raw JWT in the header (no 'Bearer') you might need to send exactly the token.
            // Many services use 'Authorization: Bearer <token>'. Try that first.
            submitHeaders.set("Authorization", "Bearer " + body.getAccessToken());

            HttpEntity<SubmitRequest> submitEntity = new HttpEntity<>(submitRequest, submitHeaders);

            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);
            System.out.println("Submit response status: " + submitResponse.getStatusCode());
            System.out.println("Submit response body: " + submitResponse.getBody());

        } else {
            System.err.println("Failed to generate webhook. Status: " + response.getStatusCode());
        }
    }

    private String getLastTwoDigits(String regNo) {
        // Extract digits from regNo; if last two characters aren't digits, get last two digits present
        String digits = regNo.replaceAll("\\D+", "");
        if (digits.length() >= 2) return digits.substring(digits.length() - 2);
        if (digits.length() == 1) return "0" + digits;
        return "00";
    }
}
