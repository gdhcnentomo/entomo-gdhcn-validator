package co.entomo.gdhcn.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "qr_code")
public class QrCode {

	@Id
	@Column(length = 36)
	private String id;
	
	private String passCode;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiresOn;
	
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
    private Date createdAt;
	private String countryCode;
	private String jsonUrl;
	private String flag;
	private String key;
}