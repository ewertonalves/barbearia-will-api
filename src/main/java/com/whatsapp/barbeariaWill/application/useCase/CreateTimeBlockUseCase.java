package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.application.dto.TimeBlockDTO;
import com.whatsapp.barbeariaWill.domain.model.TimeBlock;
import com.whatsapp.barbeariaWill.domain.port.out.TimeBlockInterfacePort;
import org.springframework.stereotype.Service;

@Service
public class CreateTimeBlockUseCase {

    private final TimeBlockInterfacePort repository;

    public CreateTimeBlockUseCase(TimeBlockInterfacePort repository) {
        this.repository = repository;
    }

    public TimeBlockDTO execute(TimeBlockDTO dto) {
        TimeBlock timeBlock = new TimeBlock(
            dto.id(),
            dto.date(),
            dto.startTime(),
            dto.endTime(),
            dto.reason()
        );
        
        TimeBlock saved = repository.save(timeBlock);
        return new TimeBlockDTO(
            saved.getId(),
            saved.getDate(),
            saved.getStartTime(),
            saved.getEndTime(),
            saved.getReason()
        );
    }
} 