package cloud.weareithero.neo4j;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Getter;
import lombok.Setter;

@RelationshipProperties
@Getter @Setter
public class ContainsEdge {

  @RelationshipId
  private Long id; // Neo4j 내부 관계 ID

  private int quantity; // 💡 수량을 노드가 아닌 '관계'에 저장!

  @TargetNode
  private ProductNode product; // 목적지 노드 (Product)
  
}
