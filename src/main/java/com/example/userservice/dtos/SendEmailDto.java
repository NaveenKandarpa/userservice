package com.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDto {
    private String senderEmail;
    private String receiverEmail;
    private String subject;
    private String body;
}
