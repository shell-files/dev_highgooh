package cloud.weareithero.carbon.api.carbonEmission;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.carbon.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.carbon.api.carbonEmission.service.CarbonEmissionService;
import cloud.weareithero.carbon.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CarbonEmissionController implements CarbonEmissionControllerDocs {

    private final CarbonEmissionService carbonEmissionService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/carbon")
    public ResponseDTO getStats(@RequestBody CarbonEmissionRequestDTO dto) {
        // log.info("Request DTO: {}", dto);
        return carbonEmissionService.getEmissionDetails(dto);
    }
    
}
