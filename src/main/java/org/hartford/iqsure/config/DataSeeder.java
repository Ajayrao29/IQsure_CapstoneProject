/*
 * ============================================================================
 * FILE: DataSeeder.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Automatically creates a default ADMIN user when the app starts
 *          for the FIRST TIME (when the database is empty).
 *          This ensures there's always an admin who can log in and set up
 *          quizzes, policies, badges, etc. through the admin panel.
 *
 * HOW IT WORKS:
 *   - @PostConstruct → Spring calls seedAdmin() after this bean is created
 *     and all dependencies (userRepository, passwordEncoder) are injected
 *   - Checks if any users exist in the database
 *   - If NO users exist → creates an admin with email "admin@iqsure.com"
 *   - If users already exist → does nothing (skips seeding)
 *
 * DEFAULT ADMIN CREDENTIALS:
 *   - Email:    admin@iqsure.com
 *   - Password: admin123
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Slf4j (Lombok) → Auto-creates a "log" variable for printing messages
 *   - @Component → Tells Spring: "create an instance of this class automatically"
 *   - @RequiredArgsConstructor (Lombok) → Auto-creates constructor for 'final' fields
 *   - @PostConstruct → Method runs once after bean initialization (replaces CommandLineRunner)
 *
 * CONNECTS TO:
 *   - UserRepository.java (repository/) → to check user count and save admin
 *   - SecurityConfig.java (config/) → provides the PasswordEncoder for hashing
 *   - User.java (entity/) → the User entity that gets saved to the "users" table
 * ============================================================================
 */
package org.hartford.iqsure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.hartford.iqsure.repository.EducationContentRepository educationContentRepository;
    private final org.hartford.iqsure.repository.QuestionRepository questionRepository;
    private final org.hartford.iqsure.repository.AnswerRepository answerRepository;
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    // This method runs automatically after the bean is created and dependencies are injected
    @PostConstruct
    public void seedAdmin() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@iqsure.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1234567890")
                    .role(User.Role.ROLE_ADMIN)
                    .userPoints(0)
                    .build());

            log.info("Admin user created: admin@iqsure.com / admin123");
        }

        if (educationContentRepository.count() == 0) {
            seedEducationContent();
        }
    }
    private void seedEducationContent() {
        // English
        saveEduContent("insurance_basics", "en", "Insurance Basics",
                "Insurance is a financial protection system where an individual pays a premium to an insurance company in exchange for protection against financial losses caused by unexpected events such as accidents, illness, or property damage.\n\nKey Terms:\n\nPremium\nAmount paid periodically for insurance coverage.\n\nDeductible\nAmount the policyholder must pay before the insurance company starts covering expenses.\n\nCoverage\nMaximum amount an insurance company will pay for a covered loss.");
        saveEduContent("health_insurance", "en", "Health Insurance",
                "Health insurance covers the cost of medical care. It provides financial protection against high health care costs.");
        saveEduContent("vehicle_insurance", "en", "Vehicle Insurance",
                "Vehicle insurance protects you against financial loss in the event of an accident or theft of your vehicle.");
        saveEduContent("life_insurance", "en", "Life Insurance",
                "Life insurance guarantees the insurer pays a sum of money to named beneficiaries when the insured dies.");
        saveEduContent("claims_process", "en", "Claims Process",
                "The claims process includes reporting an incident, documentation, investigation, and settlement by the insurer.");
        saveEduContent("premiums_and_deductibles", "en", "Premiums and Deductibles",
                "A premium is what you pay to keep your policy active. A deductible is what you pay out-of-pocket before insurance covers a claim.");

        // Spanish
        saveEduContent("insurance_basics", "es", "Conceptos Básicos de Seguros",
                "El seguro es un sistema de protección financiera en el que un individuo paga una prima a una compañía de seguros a cambio de protección contra pérdidas financieras...");
        
        // Hindi
        saveEduContent("insurance_basics", "hi", "बीमा की मूल बातें",
                "बीमा एक वित्तीय सुरक्षा प्रणाली है जहाँ एक व्यक्ति अप्रत्याशित घटनाओं के कारण होने वाले वित्तीय नुकसान से सुरक्षा के बदले में बीमा कंपनी को प्रीमियम का भुगतान करता है।...");

        // Telugu
        saveEduContent("insurance_basics", "te", "బీమా ప్రాథమిక అంశాలు",
                "బీమా అనేది ఊహించని సంఘటనల వలన కలిగే ఆర్థిక నష్టాల నుండి రక్షణ కోసం బీమా సంస్థకు ప్రీమియం చెల్లించే ఆర్థిక రక్షణ వ్యవస్థ.");
        
        // Kannada
        saveEduContent("insurance_basics", "kn", "ವಿಮಾ ಮೂಲಭೂತ ಅಂಶಗಳು",
                "ವಿಮೆಯು ಆರ್ಥಿಕ ರಕ್ಷಣಾ ವ್ಯವಸ್ಥೆಯಾಗಿದ್ದು, ಇಲ್ಲಿ ಒಬ್ಬ ವ್ಯಕ್ತಿಯು ಅನಿರೀಕ್ಷಿತ ಘಟನೆಗಳಿಂದ ಉಂಟಾಗುವ ಆರ್ಥಿಕ ನಷ್ಟಗಳ ವಿರುದ್ಧ ರಕ್ಷಣೆಗಾಗಿ ವಿಮಾ ಕಂಪನಿಗೆ ಪ್ರೀಮಿಯಂ ಪಾವತಿಸುತ್ತಾನೆ.");
                
        log.info("Education content seeded.");
    }

    private void saveEduContent(String topic, String lang, String title, String content) {
        educationContentRepository.save(org.hartford.iqsure.entity.EducationContent.builder()
                .topic(topic)
                .language(lang)
                .title(title)
                .content(content)
                .build());
    }
}
