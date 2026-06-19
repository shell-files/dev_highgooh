package cloud.weareithero.inbound.api.asn;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.weareithero.inbound.api.asn.dto.AsnAddDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnCompleteDTO;
import cloud.weareithero.inbound.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.inbound.api.asn.service.AsnService;
import cloud.weareithero.inbound.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/asn")
@RequiredArgsConstructor
public class AsnController implements AsnControllerDocs {

  private final AsnService asnService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  @Override
  public ResponseDTO findAll(@RequestBody AsnRequestDTO asnRequestDTO) {
    return asnService.findAll(asnRequestDTO);
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{asnId:[0-9]+}")
  @Override
  public ResponseDTO findOne(@PathVariable Integer asnId) {
    return asnService.findOne(asnId);
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping
  @Override
  public ResponseDTO add(@RequestBody AsnAddDTO asnAddDTO) {
    return asnService.add(asnAddDTO);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  @Override
  public ResponseDTO findAllAsn() {
    return asnService.findAllAsn();
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping
  @Override
  public ResponseDTO completeInbound(@RequestBody AsnCompleteDTO asnCompleteDTO) {
      return asnService.completeInbound(asnCompleteDTO);
  }

}
