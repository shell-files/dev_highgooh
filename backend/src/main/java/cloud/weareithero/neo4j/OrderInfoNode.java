package cloud.weareithero.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Getter;
import lombok.Setter;

@Node("OrderInfo")
@Getter @Setter
public class OrderInfoNode {

  @Id
  private String orderInfoId;
  private String orderDate;

  @Relationship(type = "ORDERED_BY", direction = Relationship.Direction.OUTGOING)
  private CustomerNode customer;

  @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
  private List<ProductNode> products = new ArrayList<>();

}
