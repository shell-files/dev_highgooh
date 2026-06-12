package cloud.weareithero.api.carbonEmission;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.carbonEmission.dto.CarbonEmissionRequestDTO;
import cloud.weareithero.api.carbonEmission.service.CarbonEmissionService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CarbonEmissionController implements CarbonEmissionControllerDocs {

    private final CarbonEmissionService carbonEmissionService;

    @PostMapping("/carbon")
    public ResponseDTO getStats(@RequestBody CarbonEmissionRequestDTO dto) {
        // log.info("Request DTO: {}", dto);
        return carbonEmissionService.getEmissionDetails(dto);
    }
    
}
