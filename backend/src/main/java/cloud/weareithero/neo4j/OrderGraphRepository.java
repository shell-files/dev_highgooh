package cloud.weareithero.neo4j;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface OrderGraphRepository extends Neo4jRepository<OrderNode, String> {

  @Query("MATCH (c:Customer {name: $customerName})-[r1:ORDERED_BY]-(o:Order)-[r2:CONTAINS]-(p:Product) " +
    "RETURN o, collect(r1), collect(c), collect(r2), collect(p)")
  List<OrderNode> findFullOrderHistory(String customerName);
  
}
