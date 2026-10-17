//  קובץ חדש: יוצר משתמש admin ראשוני בעלייה של האפליקציה.
//  סיבה: כל ה-endpoints (כולל POST / ליצירת משתמש) דורשים טוקן, וטוקן מקבלים רק ע"י התחברות
//  עם משתמש קיים. בלי משתמש ראשוני ב-DB (במיוחד ב-Postgres החדש והריק של Docker) אי אפשר להתחיל.
package com.example.myapp;

import com.example.myapp.entities.User;
import com.example.myapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final long ADMIN_ID = 1L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminEmail;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, 
                           @Value("${app.admin.username}") String adminUsername,
                           @Value("${app.admin.password}") String adminPassword,
                           @Value("${app.admin.email}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; 
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminEmail = adminEmail;
    }

    //  רץ פעם אחת מיד אחרי שהאפליקציה עולה
    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            System.out.println("--- משתמש ה-admin כבר קיים, מדלגים על יצירה ---");
            return;
        }
        if (userRepository.existsById(ADMIN_ID)) {
            System.out.println("--- לא נוצר admin: ה-id " + ADMIN_ID + " כבר תפוס במשתמש אחר ---");
            return;
        }
        userRepository.save(new User(ADMIN_ID, adminUsername, passwordEncoder.encode(adminPassword), adminEmail, "0000000000", true));
        System.out.println("--- נוצר משתמש admin: " + adminUsername + " ---");
    }
}
