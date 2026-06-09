package cloud.weareithero.api.inbound;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.api.inbound.dto.AsnAddDTO;
import cloud.weareithero.api.inbound.dto.AsnRequestDTO;
import cloud.weareithero.api.inbound.service.AsnService;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/asn")
@RequiredArgsConstructor
public class AsnController implements AsnControllerDocs {

  private final AsnService asnService;
  
  @PostMapping
  public ResponseDTO findAll(@RequestBody AsnRequestDTO asnRequestDTO) {
    return asnService.findAll(asnRequestDTO);
  }

  @PostMapping("/{asnId:[0-9]+}")
  public ResponseDTO findOne(@PathVariable Integer asnId) {
    return asnService.findOne(asnId);
  }

  @PutMapping
  public ResponseDTO add(@RequestBody AsnAddDTO asnAddDTO) {
    return asnService.add(asnAddDTO);
  }

  @GetMapping
  public ResponseDTO findAllAsn() {
    return asnService.findAllAsn();
  }

}
