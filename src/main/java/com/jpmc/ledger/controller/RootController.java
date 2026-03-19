package com.jpmc.ledger.controller;

import com.jpmc.ledger.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> root() {
        return ResponseEntity.ok(ApiResponse.success(
                "Transaction Ledger API is running. See /api/v1/health for status."));
    }
}