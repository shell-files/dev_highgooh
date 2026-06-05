package cloud.weareithero.hg;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {
    
    @Select("""
        SELECT 
                `id`, 
                `name`
          FROM `highgooh`.`OUTBOUND_PRODUCT_MASTER`
        """)
    public List<ProductDTO> getProductList();
}
