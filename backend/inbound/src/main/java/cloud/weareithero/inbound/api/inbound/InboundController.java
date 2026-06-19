package cloud.weareithero.inbound.api.inbound;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.inbound.api.inbound.dto.InboundRequestDTO;
import cloud.weareithero.inbound.api.inbound.service.InboundService;
import cloud.weareithero.inbound.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
public class InboundController implements InboundControllerDocs {
    
    private final InboundService inboundService;
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @Override
    public ResponseDTO findAll(@RequestBody InboundRequestDTO inboundRequestDTO) {
        return inboundService.findAll(inboundRequestDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{asnId:[0-9]+}")
    @Override
    public ResponseDTO findOne(@PathVariable Integer asnId) {
        return inboundService.findOne(asnId);
    }

}
