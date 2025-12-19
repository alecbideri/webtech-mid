package com.alec.FindJobApp.security;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * Handles successful OAuth2 authentication by creating/updating user and
 * redirecting with JWT.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final UserDetailsService userDetailsService;

  @Value("${app.frontend.url:http://localhost:5173}")
  private String frontendUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String email = (String) attributes.get("email");
    String providerId = (String) attributes.get("sub");
    String picture = (String) attributes.get("picture");

    // Extract name with defaults
    String givenName = (String) attributes.get("given_name");
    String familyName = (String) attributes.get("family_name");

    // Default values if not provided
    final String finalFirstName;
    final String finalLastName;

    if (givenName != null) {
      finalFirstName = givenName;
      finalLastName = familyName != null ? familyName : "";
    } else {
      String name = (String) attributes.get("name");
      if (name != null) {
        String[] parts = name.split(" ", 2);
        finalFirstName = parts[0];
        finalLastName = parts.length > 1 ? parts[1] : "";
      } else {
        finalFirstName = "User";
        finalLastName = "";
      }
    }

    // Find or create user
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> {
          log.info("Creating new OAuth user: {}", email);
          User newUser = User.builder()
              .email(email)
              .firstName(finalFirstName)
              .lastName(finalLastName)
              .provider("google")
              .providerId(providerId)
              .profileImageUrl(picture)
              .role(Role.SEEKER) // Default role for new OAuth users
              .isActive(true)
              .build();
          return userRepository.save(newUser);
        });

    // Update OAuth info if existing user linked to local account
    if (user.getProvider() == null || user.getProvider().equals("local")) {
      user.setProvider("google");
      user.setProviderId(providerId);
      user.setProfileImageUrl(picture);
      userRepository.save(user);
    }

    // Generate JWT token
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtUtils.generateToken(userDetails);

    // Build redirect URL with token
    String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth/callback")
        .queryParam("token", token)
        .queryParam("id", user.getId())
        .queryParam("email", user.getEmail())
        .queryParam("firstName", user.getFirstName())
        .queryParam("lastName", user.getLastName())
        .queryParam("role", user.getRole().name())
        .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}
