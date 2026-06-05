package cloud.weareithero.hg;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcessService {
    
    private final ProcessMasterRepository processMasterRepository;
    private final ProcessMapper processMapper;
    private final ProductMapper productMapper;

    @Transactional
    public void createProcessChain() {

        // 0. 전체 데이터 초기화 (기존 노드 및 관계 삭제)
        processMasterRepository.deleteAllNodesAndRelationships();

        // 1. 공정 객체들을 리스트에 먼저 생성 (제품 참조 제거됨)
        List<ProcessMasterDTO> processList = processMapper.getProcessList();
        List<ProcessMaster> steps = new ArrayList<>();
        for (ProcessMasterDTO dto : processList) {
            ProcessMaster step = ProcessMaster.builder()
                .process(dto.getProcess())
                .processOrder(dto.getProcess_order())
                .properDirectEmission(dto.getProper_direct_emission())
                .properElectricityUsed(dto.getProper_electricity_used())
                .properIndirectEmission(dto.getProper_indirect_emission())
                .build();
            steps.add(step);
        }

        // 2. 공정별로 순서대로 연결 (공정 -> 공정)
        for (int i = 0; i < steps.size() - 1; i++) {
            steps.get(i).setNextProcess(steps.get(i + 1));
        }

        // 3. [핵심] 전체 공정 연결 끝(마지막 노드)에 여러 제품을 연결
        ProcessMaster lastStep = steps.get(steps.size() - 1);

        List<ProductDTO> productList = productMapper.getProductList();
        for (ProductDTO productDTO : productList) {
            OutboundProduct product = OutboundProduct.builder()
                .name(productDTO.getName())
                .build();
            lastStep.getOutboundProducts().add(product);
        }

        // 4. 최상위(첫 번째: 빌릿 가열기) 공정 노드를 저장 (하위 공정과 제품은 cascade로 함께 저장됨)
        processMasterRepository.save(steps.get(0));
    }
    
}
