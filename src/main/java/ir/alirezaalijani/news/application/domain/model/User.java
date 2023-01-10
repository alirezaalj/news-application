package ir.alirezaalijani.news.application.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 100,nullable = false,unique = true,updatable = false)
    private String email;
    @Column(length = 50)
    private String name;
    @Column(length = 100)
    @JsonIgnore
    private String password;
    @Column(length = 10,nullable = false,updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    @Enumerated(EnumType.STRING)
    @Column(length = 20,nullable = false)
    private UserRole role;
}
