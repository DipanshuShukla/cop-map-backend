package com.dipanshushukla.cop_map_core_service.controller;

import com.dipanshushukla.cop_map_core_service.dto.BeatDTO;
import com.dipanshushukla.cop_map_core_service.dto.NakaDTO;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import com.dipanshushukla.cop_map_core_service.service.MasterDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/core/master")
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService masterDataService;

    @PostMapping("/nakas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<NakaDTO> createNaka(
            @Valid @RequestBody NakaDTO dto,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return new ResponseEntity<>(masterDataService.createNaka(dto, principal), HttpStatus.CREATED);
    }

    @GetMapping("/nakas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO', 'CONSTABLE')")
    public ResponseEntity<List<NakaDTO>> getMyNakas(@AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(masterDataService.getNakasByThana(principal.thanaId()));
    }

    @PostMapping("/beats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<BeatDTO> createBeat(
            @Valid @RequestBody BeatDTO dto,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return new ResponseEntity<>(masterDataService.createBeat(dto, principal), HttpStatus.CREATED);
    }

    @GetMapping("/beats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO', 'CONSTABLE')")
    public ResponseEntity<List<BeatDTO>> getMyBeats(@AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(masterDataService.getBeatsByThana(principal.thanaId()));
    }
}