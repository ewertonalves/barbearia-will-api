package com.whatsapp.barbeariaWill.domain.port.out;

import com.whatsapp.barbeariaWill.domain.model.TimeBlock;
import java.util.List;

public interface TimeBlockInterfacePort {
    
    TimeBlock       save(TimeBlock timeBlock);
    void            delete(String id);
    List<TimeBlock> findAll();
} 