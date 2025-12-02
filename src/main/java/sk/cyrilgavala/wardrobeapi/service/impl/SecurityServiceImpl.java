package sk.cyrilgavala.wardrobeapi.service.impl;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.service.SecurityService;

@Component
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encryptPassword(String password) {
    return passwordEncoder.encode(new String(Base64.getDecoder().decode(password.getBytes())));
  }
}
