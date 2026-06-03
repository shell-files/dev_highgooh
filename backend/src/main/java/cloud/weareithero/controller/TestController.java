package cloud.weareithero.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TestController {
  
  @GetMapping("/test")
  public String test(HttpServletRequest request) {
    try {
        InetAddress localHost = InetAddress.getLocalHost();
        
        String hostName = localHost.getHostName();       // 예: my-computer-name
        String serverName = request.getServerName(); // 결과: example.com (도메인만)
        String hostAddress = localHost.getHostAddress(); // 예: 192.168.0.15
        
        System.out.println("서버 호스트 이름: " + hostName);
        System.out.println("서버 이름: " + serverName);
        System.out.println("서버 IP 주소: " + hostAddress);
        
    } catch (UnknownHostException e) {
        e.printStackTrace();
    }
    return "Test completed.";
  }

}
