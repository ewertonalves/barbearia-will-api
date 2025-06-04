package com.whatsapp.barbeariaWill.application.useCase;

import com.whatsapp.barbeariaWill.domain.port.out.TimeBlockInterfacePort;
import org.springframework.stereotype.Service;

@Service
public class DeleteTimeBlockUseCase {

    private final TimeBlockInterfacePort repository;

    public DeleteTimeBlockUseCase(TimeBlockInterfacePort repository) {
        this.repository = repository;
    }

    public void execute(String id) {
        repository.delete(id);
    }
} 