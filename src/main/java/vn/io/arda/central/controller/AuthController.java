package vn.io.arda.central.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.LoginRequest;
import vn.io.arda.central.dto.LoginResponse;
import vn.io.arda.central.service.AuthService;

/**
 * Authentication Mapping Controller.
 * Provides BFF (Backend For Frontend) endpoints for authentication.
 */
@RestController
@RequestMapping("/v1/public/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("BFF Login request received for user: {}", request.getUsername());
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@RequestBody java.util.Map<String, String> body) {
    String refreshToken = body.get("refreshToken");
    String tenantKey = body.get("tenantKey");
    LoginResponse response = authService.refreshToken(refreshToken, tenantKey);
    return ResponseEntity.ok(response);
  }
}
