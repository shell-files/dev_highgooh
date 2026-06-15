package cloud.weareithero.api.packing.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.api.packing.dto.PackingCarrierDTO;
import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingInvoiceDTO;
import cloud.weareithero.api.packing.dto.PackingOrderProductDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PackingDaoImp implements PackingDao {

    private final PackingMapper packingMapper;

    @Override
    public PackingSummaryDTO findSummary(PackingRequestDTO packingRequestDTO) {
        return packingMapper.findSummary(packingRequestDTO);
    }

    @Override
    public List<PackingDTO> findAll(PackingRequestDTO packingRequestDTO) {
        return packingMapper.findAll(packingRequestDTO);
    }
    
    @Override
    public PackingDTO findOne(int orderId) {
        return packingMapper.findOne(orderId);
    }
    
    @Override
    public List<PackingOrderProductDTO> findOrderProduct(int orderId) {
        return packingMapper.findOrderProduct(orderId);
    }

    @Override
    public List<PackingCarrierDTO> findCarrierCompany() {
        return packingMapper.findCarrierCompany();
    }

    @Override
    public List<PackingInvoiceDTO> findInvoice(int orderId) {
        return packingMapper.findInvoice(orderId);
    }

    @Override
    public int updateStateCode(int orderId) {
        return packingMapper.updateStateCode(orderId);
    }

    @Override
    public int addInvoice(PackingInvoiceDTO packingInvoiceDTO) {
        return packingMapper.addInvoice(packingInvoiceDTO);
    }

    @Override
    public int findPackingInvoiceStateCode(String packingInvoiceNumber) {
        return packingMapper.findPackingInvoiceStateCode(packingInvoiceNumber);
    }

    @Override
    public int updatePackingInvoiceStateCode(String packingInvoiceNumber) {
        return packingMapper.updatePackingInvoiceStateCode(packingInvoiceNumber);
    }

    @Override
    public int countNotCompleted(int orderId) {
        return packingMapper.countNotCompleted(orderId);
    }

    @Override
    public int updateOrderStateCode(int orderId) {
        return packingMapper.updateOrderStateCode(orderId);
    }

    @Override
    public int findOrderIdByInvoiceId(String packingInvoiceNumber) {
        return packingMapper.findOrderIdByInvoiceId(packingInvoiceNumber);
    }
}
