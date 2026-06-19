package cloud.weareithero.auth.user;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.weareithero.auth.config.RsaKeyProperties;
import cloud.weareithero.auth.domain.role.RoleRepository;
import cloud.weareithero.auth.domain.role.RoleUserEntity;
import cloud.weareithero.auth.domain.role.RoleUserRepository;
import cloud.weareithero.auth.domain.user.UserEntity;
import cloud.weareithero.auth.domain.user.UserRepository;
import cloud.weareithero.auth.dto.AuthReqDTO;
import cloud.weareithero.auth.dto.FileDTO;
import cloud.weareithero.auth.dto.KeyDTO;
import cloud.weareithero.auth.dto.MailDTO;
import cloud.weareithero.auth.dto.ResDTO;
import cloud.weareithero.auth.dto.UserDTO;
import cloud.weareithero.auth.dto.UserInfoReqDTO;
import cloud.weareithero.auth.dto.UserReqDTO;
import cloud.weareithero.auth.file.FileService;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
  
  private final UserRepository userRepository;
  private final RoleUserRepository roleUserRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final FileService fileService;
  private final JwtEncoder jwtEncoder;
  private final Boolean USEYN = false;
  private final JavaMailSender mailSender;
  private final StringRedisTemplate redisTemplate;
  private static List<KeyDTO> KEYS = new ArrayList<>();

  @Value("${spring.mail.username}")
  private String emailFrom;

  @Value("${token.expire.access}")
  private Integer accessExpire;

  @Value("${token.expire.refresh}")
  private Integer refreshExpire;

  @Override
  public ResDTO userInfo(Authentication authentication) {
    boolean status = false;
    String message = "존재하지 않는 사용자 입니다.";
    UserEntity userEntity = null;
    UserDTO userDTO = null;
    try {
      if (authentication != null) {
        userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow();
        if(!userEntity.getDeleteYn()) {
          message = null;
          status = true;
          userDTO = UserDTO.findByUser(userEntity);
        }
      }
    } catch (Exception e) {
      message = e.getMessage();
    }
    return ResDTO.builder().status(status).result(userDTO).message(message).build();
  }

  @Override
  public ResDTO signIn(AuthReqDTO authReqDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    boolean status = false;
    String message = "존재하지 않는 사용자 입니다.";
    String roles = null;
    String access_token = null;
    String refresh_token = null;
    try {
      List<KeyDTO> KEYS2 = new ArrayList<>();
      String email = null;
      for(KeyDTO keyDTO : KEYS) {
        if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
          if(keyDTO.getKey().equals(authReqDTO.getCode())) {
            email = keyDTO.getEmail();
          } else {
            KEYS2.add(keyDTO);
          }
        }
      }
      KEYS = KEYS2;
      KEYS.forEach(System.out::println);

      if(email == null) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
          for(int i = 0; i < cookies.length; i++) {
            cookies[i].setMaxAge(0);
            cookies[i].setPath("/");
            response.addCookie(cookies[i]);
          }
        }
      } else {
        UserEntity userEntity = userRepository.findByEmailAndDeleteYn(email, USEYN); //.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
        UserDTO userDTO = UserDTO.findByUser(userEntity); // "RoleUser" 테이블 useYn 확인 후 DTO 생성
        Set<String> arr = new HashSet<>();
        userDTO.getRoles().forEach(d -> arr.add(d.getRole()));
        roles = String.join(",", arr);

        JwtClaimsSet accessClaimsSet = JwtClaimsSet.builder()
            .issuer("Authorization_Server")
            .subject(userDTO.getEmail())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(accessExpire, ChronoUnit.MINUTES))
            .claim("role", roles)
            .claim("userId", userDTO.getId())
            .build();
        JwtEncoderParameters accessParameters = JwtEncoderParameters.from(accessClaimsSet);
        access_token = jwtEncoder.encode(accessParameters).getTokenValue();

        JwtClaimsSet refreshClaimsSet = JwtClaimsSet.builder()
            .issuer("Authorization_Server")
            .subject(userDTO.getEmail())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(refreshExpire, ChronoUnit.DAYS))
            .claim("role", roles)
            .claim("userId", userDTO.getId())
            .build();
        JwtEncoderParameters refreshParameters = JwtEncoderParameters.from(refreshClaimsSet);
        refresh_token = jwtEncoder.encode(refreshParameters).getTokenValue();

        String uuid_key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("AT:" + uuid_key, access_token, accessExpire, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("RT:" + uuid_key, refresh_token);

        Cookie cookie = new Cookie("access_token", uuid_key);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        cookie.setPath("/");
        // cookie.setMaxAge(session.getMaxInactiveInterval());
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        status = true;
        message = userEntity.getName() + "님 환영합니다.";
      }
    } catch (Exception e) {
      message = e.getMessage();
    }
    return ResDTO.builder().status(status).result(access_token).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO signUp(UserReqDTO userDto) {
    boolean status = false;
    String message = "정상적으로 가입이 되지 않았습니다.";
    // RoleEntity roleEntity = roleRepository.findById(userDto.getService()).orElseThrow(() -> new RuntimeException("존재하지 않는 권한 입니다."));
    UserEntity userEntity = UserEntity.builder()
        .email(userDto.getEmail())
        // .password(passwordEncoder.encode(userDto.getPassword()))
        .name(userDto.getName())
        .build();
    userEntity.setDeleteYn(USEYN);
    log.info("============================================================================================================");
    log.info("USER : {}", userEntity);
    // userEntity.setRole(Set.of(roleEntity));
    userEntity = userRepository.save(userEntity);
    log.info("USER : {}", userEntity);
    if(userEntity.getId() > 0) {
      userEntity.setCreatedBy(userEntity.getId());
      userRepository.save(userEntity);

      RoleUserEntity roleUserEntity = RoleUserEntity.builder()
        .roleId(userDto.getService())
        .userId(userEntity.getId())
        .build();

      roleUserEntity.setDeleteYn(USEYN);
      roleUserEntity.setCreatedBy(userEntity.getId());
      log.info("ROLE_USER : {}", roleUserEntity);

      roleUserRepository.save(roleUserEntity);

      status = true;
      message = "정상적으로 가입이 되었습니다.";
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO delete(Authentication authentication) {
    boolean status = false;
    String message = "정상적으로 탈퇴 되지 않았습니다.";
    UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
    userEntity.setUpdatedBy(userEntity.getId());
    userEntity.setDeleteYn(true);
    userEntity = userRepository.save(userEntity);
    if(userEntity.getId() > 0) {
      status = true;
      message = null;
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO modify(UserInfoReqDTO userInfoReqDTO, Authentication authentication) {
    boolean status = false;
    String message = "정상적으로 수정 되지 않았습니다.";
    UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
    if(userInfoReqDTO.getFile() != null) {
      ResDTO resDTO = fileService.upload(userInfoReqDTO.getFile(), authentication);
      if (resDTO.isStatus()) {
        FileDTO fileDTO = (FileDTO) resDTO.getResult();
        if (fileDTO != null) {
          userEntity.setFileId(fileDTO.getId());
        }
      }
    }
    userEntity.setUpdatedBy(Long.parseLong(authentication.getName()));
    userEntity.setName(userInfoReqDTO.getName());
    userEntity = userRepository.save(userEntity);
    if(userEntity.getId() > 0) {
      status = true;
      message = null;
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO email(UserReqDTO userDto) {
    boolean status = false;
    String message = "이미 사용 중인 이메일 입니다.";

    // 키 목록 확인
    List<KeyDTO> KEYS2 = new ArrayList<>();
    for(KeyDTO keyDTO : KEYS) {
      if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
        KEYS2.add(keyDTO);
      }
    }
    KEYS = KEYS2;
    // KEYS.forEach(System.out::println);

    UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
    if( "1".equals(userDto.getType()) ) {
      if (userEntity == null) {
        // 회원가입 신규 로직 동작
        status = true; //setKey(userDto.getEmail());
        message = "";
      } else {
        if(userEntity.getDeleteYn()) {
          userEntity.setDeleteYn(USEYN);
          userEntity.setUpdatedBy(userEntity.getId());
          userEntity = userRepository.save(userEntity);
          message = userEntity.getName() + "님 다시 오셨군요!!";
        } else {
          message = userEntity.getName() + "님 회원 가입 중이십니다.";
        }
      }
    } else {
      if (userEntity == null) {
        message = "존재하지 않는 이메일 주소입니다.";
      } else {
        if(userEntity.getDeleteYn()) {
          message = "비활성화된 이메일 주소 입니다.";
        } else {
          // 로그인 로직 동작
          status = setKey(userDto.getEmail());
          message = "";
        }
      }
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Override
  public ResDTO auth(AuthReqDTO authReqDTO) {
    boolean status = false;
    String message = "유효하지 않는 인증번호 입니다.";
    List<KeyDTO> KEYS2 = new ArrayList<>();
    for(KeyDTO keyDTO : KEYS) {
      if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
        if(keyDTO.getKey().equals(authReqDTO.getCode())) {
          status = true;
          message = "인증코드 확인 완료";
        } else {
          KEYS2.add(keyDTO);
        }
      }
    }
    KEYS = KEYS2;
    KEYS.forEach(System.out::println);
    return ResDTO.builder().status(status).message(message).build();
  }

  @Override
  public ResDTO logout(HttpServletRequest request, HttpServletResponse response) {
    boolean status = true;
    try {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
          cookies[i].setMaxAge(0);
          cookies[i].setPath("/");
          response.addCookie(cookies[i]);
        }
      }
    } catch (Exception e) {
      status = false;
    }
    return ResDTO.builder().status(status).build();
  }

  private boolean setKey(String email) {
    String key = new BigInteger(130, new SecureRandom()).toString(32);
    log.info("KEY : {}", key);
    KEYS.add(KeyDTO.builder()
        .email(email)
        .key(key)
        .regTime(LocalTime.now())
        .build());
    return sendMail(MailDTO.builder()
        .emailFrom(emailFrom)
        .emailTo(email)
        .emailSubject("[HIEDU] 이메일 확인 인증코드 전송")
        .emailBody(generateEmailContent(key,"/mail.html"))
        .emailHtmlEnable(true)
        .build());
  }

  private boolean sendMail(MailDTO mailDTO) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(mailDTO.getEmailFrom());
      helper.setTo(mailDTO.getEmailTo());
      helper.setSubject(mailDTO.getEmailSubject());
      helper.setText(mailDTO.getEmailBody(), mailDTO.isEmailHtmlEnable());
      mailSender.send(message);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // 템플릿을 이용한 이메일 내용 생성
  public String generateEmailContent(String key, String tempatePath) {
    String emailContent = loadEmailTemplate(tempatePath);
    emailContent = emailContent.replace("{key}", key);
    return emailContent;
  }

  // 리소스 디렉토리에서 이메일 템플릿을 읽어오기
  private String loadEmailTemplate(String tempatePath) {
    try {
      Resource resource = new ClassPathResource(tempatePath);
      // 파일 내용을 문자열로 읽어오기
      return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    } catch (IOException e) {
      e.printStackTrace();
      return "이메일 템플릿 로드를 확인해주세요.";
    }
  }

}
