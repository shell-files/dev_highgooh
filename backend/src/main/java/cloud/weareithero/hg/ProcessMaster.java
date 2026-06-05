package cloud.weareithero.hg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Node("ProcessMaster")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessMaster {

    @Id @GeneratedValue
    private Long id;

    private String process;
    private Integer processOrder;
    private BigDecimal properDirectEmission;
    private BigDecimal properElectricityUsed;
    private BigDecimal properIndirectEmission;

    // 해당 공정이 어떤 제품에 속하는지 연결 (OUTGOING: ProcessMaster -> OutboundProduct)
    @Relationship(type = "PRODUCED_BY", direction = Relationship.Direction.OUTGOING)
    private OutboundProduct outboundProduct;

    // 다음 공정 체인 연결 (OUTGOING: 현재공정 -> 다음공정)
    @Relationship(type = "NEXT_PROCESS", direction = Relationship.Direction.OUTGOING)
    private ProcessMaster nextProcess;

    @Builder.Default
    @Relationship(type = "LAST_PROCESS", direction = Relationship.Direction.OUTGOING)
    private List<OutboundProduct> outboundProducts = new ArrayList<>();
    
}
