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

@Node("OutboundProduct")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboundProduct {
    
    @Id @GeneratedValue
    private Long id;

    private String name;

    // @Relationship(type = "FIRST_PROCESS", direction = Relationship.Direction.OUTGOING)
    // private ProcessMaster firstProcess;

    @Builder.Default
    @Relationship(type = "RUNS", direction = Relationship.Direction.OUTGOING)
    private List<ProcessRoute> routes = new ArrayList<>();

}
