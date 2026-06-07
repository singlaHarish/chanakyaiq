package com.chanakyaiq.controller;

import com.chanakyaiq.model.Transaction;
import com.chanakyaiq.repository.TransactionRepository;
import com.chanakyaiq.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(error);
        }
        String userId = oauth2User.getAttribute("sub");
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(error);
        }
        String userId = oauth2User.getAttribute("sub");
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok(transactions);
    }
}
