package co.entomo.gdhcn.entity;/**
 * @author Uday Matta
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

/**
 * @author Uday Matta
 * @organization Entomo Labs
 */
@Data
@Entity
@Table(name = "ips_file")
public class IpsFile
{
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    String id;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    Date createdAt;
    @Column(nullable = false)
    private String manifestId;

    @Column(columnDefinition = "boolean default false")
    private boolean isAccessed;
}
