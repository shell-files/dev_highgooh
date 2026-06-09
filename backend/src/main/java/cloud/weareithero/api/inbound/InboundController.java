package cloud.weareithero.api.inbound;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.inbound.service.InboundService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
public class InboundController {
    
    private final InboundService inboundService;
    
    @PostMapping
    public ResponseDTO findAll() {
        return inboundService.findAll();
    }

    @PostMapping("/{asnId:[0-9]+}")
    public ResponseDTO findOne(int asnId) {
        return inboundService.findOne(asnId);
    }

}
