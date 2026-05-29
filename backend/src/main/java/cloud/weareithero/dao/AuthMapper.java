package cloud.weareithero.dao;

import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cloud.weareithero.dto.TokenDTO;
import cloud.weareithero.dto.UserRoleDto;

@Mapper
public interface AuthMapper {
  
  @Select("""
    SELECT 
        u.`id`,
        u.`name`, 
        u.`email`, 
        u.`password`,
        GROUP_CONCAT(r.`role` SEPARATOR ', ') AS `role`
    FROM `highgooh`.`USER` AS u 
    INNER JOIN `highgooh`.`USER_ROLE` AS ur 
        ON (u.id = ur.user_id AND ur.delete_yn = 0) 
    INNER JOIN `highgooh`.`ROLE` AS r 
        ON (ur.role_id = r.id AND r.delete_yn = 0) 
    WHERE 1 = 1
      AND u.email = #{email}
      AND u.delete_yn = 0
    GROUP BY u.id
    """)
  public Optional<UserRoleDto> findByEmail(@Param("email") String email);

  @Insert("""
    INSERT INTO `highgooh`.`TOKEN` (`refresh_token`) VALUE (#{refresh_token})
    """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public int saveToken(TokenDTO tokenDTO);

  @Update("""
    UPDATE `highgooh`.`TOKEN` SET `delete_yn` = 1 WHERE `id` = #{id}
    """)
  public int delToken(@Param("id") long id);

}
