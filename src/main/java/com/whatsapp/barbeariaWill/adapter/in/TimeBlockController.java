package com.whatsapp.barbeariaWill.adapter.in;

import com.whatsapp.barbeariaWill.application.dto.TimeBlockDTO;
import com.whatsapp.barbeariaWill.application.useCase.CreateTimeBlockUseCase;
import com.whatsapp.barbeariaWill.application.useCase.DeleteTimeBlockUseCase;
import com.whatsapp.barbeariaWill.application.useCase.ListTimeBlocksUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-blocks")
public class TimeBlockController {

    private final CreateTimeBlockUseCase createTimeBlockUseCase;
    private final DeleteTimeBlockUseCase deleteTimeBlockUseCase;
    private final ListTimeBlocksUseCase listTimeBlocksUseCase;

    public TimeBlockController(
            CreateTimeBlockUseCase createTimeBlockUseCase,
            DeleteTimeBlockUseCase deleteTimeBlockUseCase,
            ListTimeBlocksUseCase listTimeBlocksUseCase) {
        this.createTimeBlockUseCase = createTimeBlockUseCase;
        this.deleteTimeBlockUseCase = deleteTimeBlockUseCase;
        this.listTimeBlocksUseCase  = listTimeBlocksUseCase;
    }

    @PostMapping
    public ResponseEntity<TimeBlockDTO> createTimeBlock(@RequestBody TimeBlockDTO timeBlock) {
        return ResponseEntity.ok(createTimeBlockUseCase.execute(timeBlock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeBlock(@PathVariable String id) {
        deleteTimeBlockUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TimeBlockDTO>> listTimeBlocks() {
        return ResponseEntity.ok(listTimeBlocksUseCase.execute());
    }
} 