package cloud.weareithero.hg;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hg/process")
public class ProcessContoller {
    
    private final ProcessService processService;

    @PostMapping
    public ResponseEntity<String> createProcessChain() {
        processService.createProcessChain();
        return ResponseEntity.ok("Process chain created successfully");
    }

}
