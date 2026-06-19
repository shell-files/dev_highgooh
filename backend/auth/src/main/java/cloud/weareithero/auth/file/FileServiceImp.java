package cloud.weareithero.auth.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import cloud.weareithero.auth.domain.file.FileEntity;
import cloud.weareithero.auth.domain.file.FileRepository;
import cloud.weareithero.auth.domain.user.UserEntity;
import cloud.weareithero.auth.domain.user.UserRepository;
import cloud.weareithero.auth.dto.FileDTO;
import cloud.weareithero.auth.dto.ResDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileServiceImp implements FileService {
  
  private final FileRepository fileRepository;
  private final UserRepository userRepository;

  private final Boolean USEYN = false;

  @Value("${swagger.api-gateway-url}")
  private String uri;

  @Value("${file.upload.path}")
  private String uploadPath;

  private String getRootPath() {
    // return new File("").getAbsolutePath();
    return uploadPath;
  }
  // private String lastPath = "/upload";
  private String lastPath = "";
  private String getCurrnetDatePath() {return "/".concat(new SimpleDateFormat("yyyyMMdd").format(new Date()));}
  private String getName(MultipartFile file) {return file.getOriginalFilename();}
  private String setName() {return Long.toString(System.nanoTime());}
  private String getContentType(MultipartFile file) {return file.getContentType();}
  private String getExtension(MultipartFile file) {
    String contentType = file.getContentType();
    String name = getName(file);
    String originalFileExtension = "";
    if (!ObjectUtils.isEmpty(contentType)){
      if(contentType.contains("image/jpeg")){originalFileExtension = ".jpg";}
      else if(contentType.contains("image/png")){originalFileExtension = ".png";}
      else if(contentType.contains("image/gif")){originalFileExtension = ".gif";}
      else if(name.lastIndexOf(".") > 0){originalFileExtension = name.substring(name.lastIndexOf("."), name.length());}
    }
    return originalFileExtension;
  }

  @Transactional
  @Override
  public ResDTO upload(MultipartFile file, Authentication authentication) {
    boolean status = false;
    String message = "정상적으로 파일 저장이 되지 않았습니다.";
    FileDTO fileDTO = null;
    if(authentication != null && !file.isEmpty()) {
      Long userId = Long.parseLong(authentication.getName());
      String origin = getName(file);
      String name = setName();
      String ext = getExtension(file);
      String mediaType = getContentType(file);
      String attachPath = getRootPath().concat(lastPath).concat(getCurrnetDatePath()).concat("/" + userId);
      log.info("attachPath : {}", attachPath);
      try {
        File newFile = new File(attachPath.concat("/").concat(name).concat(ext));
        if(!newFile.exists()){newFile.mkdirs();}
        file.transferTo(newFile);
        UserEntity user = userRepository.findById(userId).orElseThrow();
        FileEntity fileInfo = FileEntity.builder()
            .origin(origin)
            .name(name)
            .attachPath(attachPath)
            .ext(ext)
            .mediaType(mediaType)
            .build();
        fileInfo.setDeleteYn(true);
        fileInfo.setCreatedBy(userId);
        fileInfo = fileRepository.save(fileInfo);
        if(fileInfo.getId() > 0) {
          user.setFileId(fileInfo.getId());
          userRepository.save(user);
          log.info("url : {}", uri.concat("/file").concat("/"+fileInfo.getId()));
          status = true;
          message = "정상적으로 파일 저장이 되었습니다.";
          fileDTO = FileDTO.builder()
              .id(fileInfo.getId())
              .url(uri.concat("/file").concat("/"+fileInfo.getId()))
              .build();
        }
      } catch (IOException e) {
        message = e.getMessage();
      }
    }
    return ResDTO.builder().status(status).result(fileDTO).message(message).build();
  }

  @Override
  public ResponseEntity<?> uri(String type, Long fileId, Authentication authentication) {
    FileEntity fileInfo = fileRepository.findByIdAndDeleteYn(fileId, USEYN).orElseThrow(() -> new RuntimeException("존재하지 않는 파일 입니다."));
    // if(authentication != null) 
    return getFile(fileInfo);
    // return ResponseEntity.notFound().build();
  }

  @Override
  public ResponseEntity<?> uri(Long fileId, Authentication authentication) {
    FileEntity fileInfo = fileRepository.findByIdAndDeleteYn(fileId, USEYN).orElseThrow(() -> new RuntimeException("존재하지 않는 파일 입니다."));
    try {
      String attachPath = fileInfo.getAttachPath();
      String origin = fileInfo.getOrigin();
      String name = fileInfo.getName();
      String ext = fileInfo.getExt();
      String mediaType = fileInfo.getMediaType();
      File file = new File(attachPath.concat("/").concat(name).concat(ext));
      String encodedFileName = URLEncoder.encode(origin, StandardCharsets.UTF_8).replace("+", "%20");
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(file.length())
          .contentType(MediaType.parseMediaType(mediaType))
          .body(new InputStreamResource(new FileInputStream(file)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.notFound().build();
  }

  private ResponseEntity<?> getFile(FileEntity fileInfo) {
    try {
      String attachPath = fileInfo.getAttachPath();
      String origin = fileInfo.getOrigin();
      String name = fileInfo.getName();
      String ext = fileInfo.getExt();
      String mediaType = fileInfo.getMediaType();
      File file = new File(attachPath.concat("/").concat(name).concat(ext));
      String encodedFileName = URLEncoder.encode(origin, StandardCharsets.UTF_8).replace("+", "%20");
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename*=UTF-8''" + encodedFileName);
      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(file.length())
          .contentType(MediaType.parseMediaType(mediaType))
          .body(new InputStreamResource(new FileInputStream(file)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.notFound().build();
  }

}
