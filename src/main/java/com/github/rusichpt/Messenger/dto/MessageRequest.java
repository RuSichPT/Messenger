package com.github.rusichpt.Messenger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    @NotBlank
    private final String usernameTo;
    @NotBlank
    private final String content;
}
