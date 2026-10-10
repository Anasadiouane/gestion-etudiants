package com.edu.gestionetudiantsbackend.config;


import com.edu.gestionetudiantsbackend.model.Admin;
import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import com.edu.gestionetudiantsbackend.repository.AdminRepository;
import com.edu.gestionetudiantsbackend.repository.AnneeScolaireRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/** Premier lancement: kayخلق admin + mawssim 2026/2027 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(AdminRepository admins, AnneeScolaireRepository annees, PasswordEncoder enc) {
        return args -> {
            if (admins.count() == 0) {
                Admin a = new Admin();
                a.setUsername("admin");
                a.setPasswordHash(enc.encode("admin123"));
                admins.save(a);
                System.out.println(">>> Admin par défaut: admin / admin123  (BDDLO!)");
            }
            if (annees.count() == 0) {
                AnneeScolaire s = new AnneeScolaire();
                s.setLibelle("2026/2027");
                s.setFraisInscription(new BigDecimal("30"));
                s.setActive(true);
                annees.save(s);
            }
        };
    }
}
