package cloud.weareithero.hg;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ProcessMasterRepository extends Neo4jRepository<ProcessMaster, Long> {

    // 모든 노드와 그 관계를 삭제하는 Cypher 쿼리
    @Query("MATCH (n) DETACH DELETE n")
    void deleteAllNodesAndRelationships();
 
    // 1. 특정 완제품 ID로 등록된 모든 공정 단계를 순서대로 조회
    @Query("MATCH (p:OutboundProduct)<-[:PRODUCED_BY]-(proc:ProcessMaster) " +
           "WHERE p.id = $productId " +
           "RETURN proc " +
           "ORDER BY proc.processOrder ASC")
    List<ProcessMaster> findProcessesByProductId(Long productId);

    // 2. 특정 제품의 시작 공정부터 마지막 공정까지의 전체 경로(Chain) 조회
    @Query("MATCH path = (start:ProcessMaster)-[:NEXT_PROCESS*]->(end:ProcessMaster) " +
           "WHERE start.processOrder = 1 AND (start)-[:PRODUCED_BY]->(:OutboundProduct {id: $productId}) " +
           "RETURN path")
    List<ProcessMaster> findProcessChainByProductId(Long productId);

}
