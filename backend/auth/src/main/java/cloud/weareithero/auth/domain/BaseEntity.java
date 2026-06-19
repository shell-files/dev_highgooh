package cloud.weareithero.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "delete_yn", nullable = false, length = 1)
  private Boolean deleteYn;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime createdAt;

  @Column(name = "created_by", nullable = true)
  private Long createdBy;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, insertable=false, columnDefinition = "TIMESTAMP")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime updatedAt;

  @Column(name = "updated_by", nullable = true, insertable=false)
  private Long updatedBy;
  
}
