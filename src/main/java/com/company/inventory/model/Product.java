package com.company.inventory.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7461389651533509262L;
	//Se creara en base estas tablas
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private int price;
	private int account;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JsonIgnoreProperties ( {"hibernateLazyInitializer", "handler"} )
	private Category category;
	
	//Almacenamiento de foto en base 64
	//lob campo mas pesado para imagenes
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column( name ="picture", columnDefinition = "longblob")
	private byte[] picture;

}
