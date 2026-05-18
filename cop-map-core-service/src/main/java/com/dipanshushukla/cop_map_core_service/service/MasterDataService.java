package com.dipanshushukla.cop_map_core_service.service;

import com.dipanshushukla.cop_map_core_service.dto.BeatDTO;
import com.dipanshushukla.cop_map_core_service.dto.NakaDTO;
import com.dipanshushukla.cop_map_core_service.entity.Beat;
import com.dipanshushukla.cop_map_core_service.entity.Naka;
import com.dipanshushukla.cop_map_core_service.repository.BeatRepository;
import com.dipanshushukla.cop_map_core_service.repository.NakaRepository;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterDataService {

    private final NakaRepository nakaRepository;
    private final BeatRepository beatRepository;

    public NakaDTO createNaka(NakaDTO dto, CopMapPrincipal principal) {
        Naka naka = dto.toEntity(principal.thanaId());
        return NakaDTO.fromEntity(nakaRepository.save(naka));
    }

    public List<NakaDTO> getNakasByThana(String thanaId) {
        return nakaRepository.findAllByThanaId(thanaId).stream()
                .map(NakaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BeatDTO createBeat(BeatDTO dto, CopMapPrincipal principal) {
        Beat beat = dto.toEntity(principal.thanaId());
        return BeatDTO.fromEntity(beatRepository.save(beat));
    }

    public List<BeatDTO> getBeatsByThana(String thanaId) {
        return beatRepository.findAllByThanaId(thanaId).stream()
                .map(BeatDTO::fromEntity)
                .collect(Collectors.toList());
    }
}