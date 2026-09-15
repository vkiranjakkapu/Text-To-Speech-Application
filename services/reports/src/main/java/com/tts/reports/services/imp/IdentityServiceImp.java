package com.tts.reports.services.imp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import com.tts.reports.dto.FetchUsersRequestDto;
import com.tts.reports.dto.RestResponseDto;
import com.tts.reports.dto.UserResponseDto;
import com.tts.reports.exceptions.BusinessException;
import com.tts.reports.exceptions.InternalCommunicationException;
import com.tts.reports.services.IdentityService;

@Service
public class IdentityServiceImp implements IdentityService {

    private final RestClient restClient;

    @Value("${services.uri.identity}")
    private String IDENTITY_SERVICE_URL;

    public IdentityServiceImp(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        try {
            return restClient.get().uri(IDENTITY_SERVICE_URL + "/users/").retrieve()
                    .body(new ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>() {
                    }).getData();
        } catch (HttpStatusCodeException e) {
            String rawResponse = e.getResponseBodyAsString();
            throw new InternalCommunicationException(rawResponse, e.getStatusCode());
        } catch (Exception e) {
            throw new BusinessException("Error Connecting with Identity Service", e);
        }
    }

    @Override
    public Map<UUID, UserResponseDto> getAllUsersByIds(Set<UUID> userIds) {
        return fetchUsers(userIds).stream()
                .collect(Collectors.toMap(UserResponseDto::id, x -> x));
    }

    private List<UserResponseDto> fetchUsers(Set<UUID> ids) {
        try {
            RestResponseDto<List<UserResponseDto>> body = restClient.post().uri(IDENTITY_SERVICE_URL + "/users/search")
                    .body(FetchUsersRequestDto.builder()
                            .ids(ids).build())
                    .retrieve().body(new ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>() {
                    });
            return body.getData();
        } catch (HttpStatusCodeException e) {
            String rawJsonResponseBody = e.getResponseBodyAsString();
            throw new InternalCommunicationException(rawJsonResponseBody, e.getStatusCode());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }
}
