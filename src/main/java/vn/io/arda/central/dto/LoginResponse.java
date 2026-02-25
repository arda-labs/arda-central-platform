package vn.io.arda.central.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for login, wrapping Keycloak token data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private Integer expiresIn;

  @JsonProperty("refresh_expires_in")
  private Integer refreshExpiresIn;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("session_state")
  private String sessionState;

  private String scope;
}
