package com.whatsapp.barbeariaWill.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeBlockDTO(
    
    String      id,
    LocalDate   date,
    LocalTime   startTime,
    LocalTime   endTime,
    String      reason
) {} 