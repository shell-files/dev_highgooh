package cloud.weareithero.api.asn.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cloud.weareithero.api.asn.dao.AsnDao;
import cloud.weareithero.api.asn.dto.AsnAddDTO;
import cloud.weareithero.api.asn.dto.AsnCompleteDTO;
import cloud.weareithero.api.asn.dto.AsnDTO;
import cloud.weareithero.api.asn.dto.AsnMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnOrderMaterialDTO;
import cloud.weareithero.api.asn.dto.AsnRequestDTO;
import cloud.weareithero.api.asn.dto.AsnSummaryDTO;
import cloud.weareithero.api.asn.dto.AsnSupplierDTO;
import cloud.weareithero.api.asn.dto.AsnWarehouseDTO;
import cloud.weareithero.dto.PaginationDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsnServiceImp implements AsnService {

  private final AsnDao asnDao;

  @Override
  public ResponseDTO findAll(AsnRequestDTO asnRequestDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      AsnSummaryDTO summary = asnDao.findSummary(asnRequestDTO);
      List<AsnDTO> asnList = asnDao.findAll(asnRequestDTO);
      PaginationDTO pagination = PaginationDTO.builder()
          .page(asnRequestDTO.getPage())
          .totalCount(summary.getTotal())
          .totalPages((int) Math.ceil((double) summary.getTotal() / asnRequestDTO.getSize()))
          .build();
      request.put("summary", summary);
      request.put("list", asnList);
      request.put("pagination", pagination);
      isSuccess = true;
      message = "ASN 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("AsnServiceImp findAll error : {}", e.getMessage());
      message = "ASN 조회에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO findOne(int asnId) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      AsnDTO asn = asnDao.findByAsnId(asnId);
      List<AsnOrderMaterialDTO> orderMaterials = asnDao.findOne(asnId);
      request.put("asn", asn);
      request.put("items", orderMaterials);
      isSuccess = true;
      message = "ASN상세 정보 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("AsnServiceImp findOne error : {}", e.getMessage());
      message = "ASN상세 정보 조회가 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO add(AsnAddDTO asnAddDTO) {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      LocalDate eta = LocalDate.parse(asnAddDTO.getEta());
      int itemCount = asnAddDTO.getItems().size();
      AsnDTO asnDTO = AsnDTO.builder()
          .partnerId(asnAddDTO.getPartnerCompanyId())
          .warehouseId(asnAddDTO.getWarehouseId())
          .itemCount(itemCount)
          .eta(eta)
          .build();

      // log.info("AsnServiceImp add request : {}", asnDTO);
      AsnDTO result = asnDao.add(asnDTO);

      if (result.getAsnId() > 0) {
        // log.info("AsnServiceImp add success : {}", result);
        isSuccess = true;
        message = "ASN 등록이 완료되었습니다.";
        if (itemCount > 0) {
          int size = 0;
          for (AsnOrderMaterialDTO item : asnAddDTO.getItems()) {
            AsnOrderMaterialDTO orderMaterialDTO = AsnOrderMaterialDTO.builder()
                .inboundId(result.getAsnId())
                .itemNo(item.getItemNo())
                .itemName(item.getItemName())
                .diameter(item.getDiameter())
                .weight(item.getWeight())
                .build();
            // log.info("AsnServiceImp add order material : {}", orderMaterialDTO);
            size += asnDao.addOrderMaterial(orderMaterialDTO);
          }

          if (size != itemCount) {
            isSuccess = false;
            message = "ASN 등록이 일부 실패했습니다.";
          }

        }
      }

    } catch (Exception e) {
      log.info("AsnServiceImp add error : {}", e.getMessage());
      message = "ASN 등록이 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Override
  public ResponseDTO findAllAsn() {
    boolean isSuccess = false;
    String message = null;
    Map<String, Object> request = new HashMap<>();
    try {
      List<AsnSupplierDTO> suppliers = asnDao.findBySupplier();
      List<AsnWarehouseDTO> warehouses = asnDao.findByWarehouse();
      List<AsnMaterialDTO> materials = asnDao.findByMaterial();
      request.put("suppliers", suppliers);
      request.put("warehouses", warehouses);
      request.put("materials", materials);
      isSuccess = true;
      message = "사전입고 통지(ASN) 정보 조회가 완료되었습니다.";
    } catch (Exception e) {
      log.info("AsnServiceImp findAllAsn error : {}", e.getMessage());
      message = "사전입고 통지(ASN) 정보 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .data(request)
        .message(message)
        .build();
  }

  @Transactional
  @Override
  public ResponseDTO completeInbound(AsnCompleteDTO asnCompleteDTO) {
    boolean isSuccess = false;
    String message = null;
    try {
        // 1. state_code 확인
        int stateCode = asnDao.findStateCode(asnCompleteDTO.getAsnId());
        if (stateCode != 4) {
            message = "입고예정 상태의 ASN만 완료 처리할 수 있습니다.";
            return ResponseDTO.builder().status(false).message(message).build();
        }
        // 2. 입고 완료 처리
        LocalDateTime ata = LocalDateTime.parse(asnCompleteDTO.getAta());
        int result = asnDao.completeInbound(asnCompleteDTO.getAsnId(), ata);
        if (result > 0) {
            isSuccess = true;
            message = "입고 완료 처리되었습니다.";
        } else {
            message = "입고 완료 처리에 실패했습니다.";
        }
    } catch (Exception e) {
        log.info("AsnServiceImp completeInbound error : {}", e.getMessage());
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        message = "입고 완료 처리에 실패했습니다.";
    }
    return ResponseDTO.builder()
        .status(isSuccess)
        .message(message)
        .build();
    
  }

}
