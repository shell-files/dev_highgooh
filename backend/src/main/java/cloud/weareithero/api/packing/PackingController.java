package cloud.weareithero.api.packing;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.packing.dto.PackingAddDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.service.PackingService;
import cloud.weareithero.dto.ResponseDTO;
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

    @PostMapping("/{orderId:[0-9]+}")
    @Override
    public ResponseDTO findOne(@PathVariable int orderId) {
        return packingService.findOne(orderId);
    }
    
    @PutMapping
    @Override
    public ResponseDTO InsertPacking(@RequestBody PackingAddDTO packingAddDTO) {
        return packingService.addPacking(packingAddDTO);
    }

    @PatchMapping("/{packingInvoiceNumber:[A-Za-z0-9-]+}")
    @Override
    public ResponseDTO completePacking(@PathVariable String packingInvoiceNumber) {
        return packingService.completePacking(packingInvoiceNumber);
    }
}
