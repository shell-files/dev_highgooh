package cloud.weareithero.api.packing;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.service.PackingService;
import cloud.weareithero.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/packing")
@RequiredArgsConstructor
public class PackingController implements PackingControllerDocs {

    private final PackingService packingService;

    @PostMapping
    @Override
    public ResponseDTO findAll(@RequestBody PackingRequestDTO packingRequestDTO) {
        return packingService.findAll(packingRequestDTO);
    }
    
}
