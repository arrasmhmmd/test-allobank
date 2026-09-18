package com.allobank.splitbill.repository;

import com.allobank.splitbill.model.BillGroup;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BillGroupRepository {
    private final Map<UUID, BillGroup> groups = new ConcurrentHashMap<>();
    public BillGroup save(BillGroup group) { groups.put(group.getId(), group); return group; }
    public Optional<BillGroup> findById(UUID id) { return Optional.ofNullable(groups.get(id)); }
}
