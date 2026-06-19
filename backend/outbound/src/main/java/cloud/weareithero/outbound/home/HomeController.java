package cloud.weareithero.outbound.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {
  
  @GetMapping("/")
  public String home() {
    log.info("outbound!!");
    return "outbound";
  }

}
