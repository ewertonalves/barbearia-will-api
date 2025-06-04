package com.whatsapp.barbeariaWill.adapter.out.persistence.repository;

import com.whatsapp.barbeariaWill.domain.model.TimeBlock;
import com.whatsapp.barbeariaWill.domain.port.out.TimeBlockInterfacePort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TimeBlockRepository implements TimeBlockInterfacePort {
    
    private final Map<String, TimeBlock> timeBlocks = new ConcurrentHashMap<>();

    @Override
    public TimeBlock save(TimeBlock timeBlock) {
        timeBlocks.put(timeBlock.getId(), timeBlock);
        return timeBlock;
    }

    @Override
    public void delete(String id) {
        timeBlocks.remove(id);
    }

    @Override
    public List<TimeBlock> findAll() {
        return new ArrayList<>(timeBlocks.values());
    }
} 