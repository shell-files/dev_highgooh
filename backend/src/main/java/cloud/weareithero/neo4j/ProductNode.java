package cloud.weareithero.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Getter;
import lombok.Setter;

@Node("Product")
@Getter @Setter
public class ProductNode {
    @Id
    private String productId;
    private String productName;
    // private int quantity;
    // private int price;
}
