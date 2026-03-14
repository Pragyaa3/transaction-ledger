package com.jpmc.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Owner name is required")
    private String ownerName;
}