package com.coachly.adminpanel.client;

import com.coachly.adminpanel.client.dto.ClientResponse;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse getClient(UUID id) {
        return clientRepository.findById(id)
                .map(client -> new ClientResponse(
                        client.getId(),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getLocale(),
                        client.getIsActive()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    public Page<ClientResponse> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(client -> new ClientResponse(
                        client.getId(),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getLocale(),
                        client.getIsActive()
                ));
    }

    @Transactional
    public void deactivateClient(UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        if (Boolean.FALSE.equals(client.getIsActive())) {
            return;
        }
        client.setIsActive(false);
        clientRepository.save(client);
    }
}
