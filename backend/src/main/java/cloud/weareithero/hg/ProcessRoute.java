package cloud.weareithero.hg;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@RelationshipProperties
public class ProcessRoute {
    @RelationshipId
    private Long id;

    private int processOrder; // 관계 위에 올라가는 순서 정보

    @TargetNode
    private ProcessMaster processMaster; // 목적지가 되는 공정 노드

}
