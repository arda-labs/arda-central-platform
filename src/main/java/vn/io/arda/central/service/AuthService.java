package vn.io.arda.central.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.WebApplicationException;
import vn.io.arda.central.dto.LoginRequest;
import vn.io.arda.central.dto.LoginResponse;
import vn.io.arda.shared.exception.ArdaException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final String KEYCLOAK_BASE_URL = "http://localhost:8081";
  private final String CLIENT_ID = "arda-shell";

  /**
   * Authenticates user against Keycloak via Keycloak Admin Client library.
   */
  public LoginResponse login(LoginRequest request) {
    log.info("Processing login for user: {} on tenant: {}", request.getUsername(), request.getTenantKey());

    try (Keycloak keycloak = KeycloakBuilder.builder()
        .serverUrl(KEYCLOAK_BASE_URL)
        .realm(request.getTenantKey())
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(CLIENT_ID)
        .username(request.getUsername())
        .password(request.getPassword())
        .build()) {

      AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();

      return new LoginResponse(
          tokenResponse.getToken(),
          (int) tokenResponse.getExpiresIn(),
          (int) tokenResponse.getRefreshExpiresIn(),
          tokenResponse.getRefreshToken(),
          tokenResponse.getTokenType(),
          tokenResponse.getSessionState(),
          tokenResponse.getScope());
    } catch (WebApplicationException e) {
      String errorResponse = e.getResponse().readEntity(String.class);
      log.error("Keycloak authentication failed for user {}: {} - Details: {}",
          request.getUsername(), e.getMessage(), errorResponse);
      throw new ArdaException("Tài khoản hoặc mật khẩu không chính xác");
    } catch (Exception e) {
      log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
      throw new ArdaException("Đã có lỗi xảy ra trong quá trình đăng nhập");
    }
  }

  /**
   * Refresh token using refresh_token grant type
   */
  /**
   * Refresh token using direct HTTP call to Keycloak
   */
  public LoginResponse refreshToken(String refreshToken, String tenantKey) {
    log.info("Refreshing token for tenant: {}", tenantKey);

    try {
      String url = KEYCLOAK_BASE_URL + "/realms/" + tenantKey + "/protocol/openid-connect/token";

      org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

      org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
      headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

      org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
      map.add("client_id", CLIENT_ID);
      map.add("grant_type", "refresh_token");
      map.add("refresh_token", refreshToken);

      org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(
          map, headers);

      org.keycloak.representations.AccessTokenResponse tokenResponse = restTemplate.postForObject(url, request,
          org.keycloak.representations.AccessTokenResponse.class);

      if (tokenResponse == null) {
        throw new ArdaException("Không nhận được phản hồi từ Keycloak");
      }

      return new LoginResponse(
          tokenResponse.getToken(),
          (int) tokenResponse.getExpiresIn(),
          (int) tokenResponse.getRefreshExpiresIn(),
          tokenResponse.getRefreshToken(),
          tokenResponse.getTokenType(),
          tokenResponse.getSessionState(),
          tokenResponse.getScope());

    } catch (org.springframework.web.client.HttpClientErrorException e) {
      log.error("Token refresh failed: {} - Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw new ArdaException("Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
    } catch (Exception e) {
      log.error("Token refresh failed with exception: {}", e.getMessage(), e);
      throw new ArdaException("Không thể làm mới phiên làm việc: " + e.getMessage());
    }
  }
}
