package com.chanakyaiq.controller;

import com.chanakyaiq.model.User;
import com.chanakyaiq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> response = new HashMap<>();
        
        if (oauth2User == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        if (googleId == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        // On-demand user provisioning
        Optional<User> userOpt = userRepository.findById(googleId);
        User user;
        if (userOpt.isEmpty()) {
            user = User.builder()
                    .id(googleId)
                    .email(email)
                    .cashBalance(new BigDecimal("1000000.00")) // ₹10,00,000 Starting virtual cash
                    .build();
            userRepository.save(user);
        } else {
            user = userOpt.get();
        }

        response.put("authenticated", true);
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("cashBalance", user.getCashBalance());
        return ResponseEntity.ok(response);
    }
}
