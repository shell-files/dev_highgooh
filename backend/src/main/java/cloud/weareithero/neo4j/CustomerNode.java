package cloud.weareithero.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Getter;
import lombok.Setter;

@Node("Customer")
@Getter @Setter
public class CustomerNode {
    @Id
    private String customerId;
    private String name;
}
