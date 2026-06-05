package cloud.weareithero.neo4j;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface OrderGraphRepository extends Neo4jRepository<OrderNode, String> {

  @Query("MATCH (c:Customer {name: $customerName})-[r1:ORDERED_BY]-(o:Order)-[r2:CONTAINS]-(p:Product) " +
    "RETURN o, collect(DISTINCT r1), collect(DISTINCT c), collect(DISTINCT r2), collect(DISTINCT p)")
  List<OrderNode> findFullOrderHistory(String customerName);
  
}
