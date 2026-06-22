package com.leonardoalvarenga.scoutly.tracking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TrackingController {
    private final TrackingService service;

    @PostMapping
    public ResponseEntity<TrackingResponseDTO> create(@Valid @RequestBody TrackingRequestDTO dto){
        TrackingResponseDTO response = service.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TrackingResponseDTO>> get(){
        List<TrackingResponseDTO> productsList = service.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleStatus(@PathVariable UUID id){
        service.toggleStatus(id);
    }

    @PostMapping("/webhook/price")
    public ResponseEntity<Void> updatePrice(@Valid @RequestBody PriceWebhookDTO dto){
        service.updatePrice(dto);

        return ResponseEntity.ok().build();
    }
}
