package cloud.weareithero.api.packing.dao;

import java.util.List;

import cloud.weareithero.api.packing.dto.PackingCarrierDTO;
import cloud.weareithero.api.packing.dto.PackingDTO;
import cloud.weareithero.api.packing.dto.PackingInvoiceDTO;
import cloud.weareithero.api.packing.dto.PackingOrderProductDTO;
import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.api.packing.dto.PackingSummaryDTO;

public interface PackingDao {
    PackingSummaryDTO findSummary(PackingRequestDTO packingRequestDTO);
    List<PackingDTO> findAll(PackingRequestDTO packingRequestDTO);
    PackingDTO findOne(int orderId);
    List<PackingOrderProductDTO> findOrderProduct(int orderId);
    List<PackingCarrierDTO> findCarrierCompany();
    List<PackingInvoiceDTO> findInvoice(int orderId);
    public int updateStateCode(int orderId);
    public int addInvoice(PackingInvoiceDTO packingInvoiceDTO);
    public int findPackingInvoiceStateCode(String invoiceId);
    public int updatePackingInvoiceStateCode(String invoiceId);
    public int countNotCompleted(int orderId);
    public int updateOrderStateCode(int orderId);
    public int findOrderIdByInvoiceId(String invoiceId);

}
