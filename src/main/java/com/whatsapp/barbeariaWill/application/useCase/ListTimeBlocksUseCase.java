package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.application.dto.TimeBlockDTO;
import com.whatsapp.barbeariaWill.domain.port.out.TimeBlockInterfacePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListTimeBlocksUseCase {

    private final TimeBlockInterfacePort repository;

    public ListTimeBlocksUseCase(TimeBlockInterfacePort repository) {
        this.repository = repository;
    }

    public List<TimeBlockDTO> execute() {
        return repository.findAll().stream()
            .map(block -> new TimeBlockDTO(
                block.getId(),
                block.getDate(),
                block.getStartTime(),
                block.getEndTime(),
                block.getReason()
            ))
            .collect(Collectors.toList());
    }
} 