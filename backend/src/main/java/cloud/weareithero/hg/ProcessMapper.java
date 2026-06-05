package cloud.weareithero.hg;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProcessMapper {
    
    @Select("""
     SELECT 
            `id`, 
            `process`,
            `process_order`,
            `proper_direct_emission`,
            `proper_electricity_used`,
            `proper_indirect_emission`
        FROM `highgooh`.`PROCESS_MASTER`
        ORDER BY `process_order`
            """)
    public List<ProcessMasterDTO> getProcessList();

}
