package com.coachly.adminpanel.client;

import com.coachly.adminpanel.client.dto.ClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ClientResponse getClient(@PathVariable UUID id) {
        return clientService.getClient(id);
    }

    @GetMapping("/all")
    public Page<ClientResponse> getAllClients(Pageable pageable) {
        return clientService.getAllClients(pageable);
    }

    @PostMapping("/{id}/deactivate")
    public void deactivateClient(@PathVariable UUID id) {
        clientService.deactivateClient(id);
    }
}
