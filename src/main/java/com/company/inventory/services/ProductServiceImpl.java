package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServiceImpl implements IProductService {
	
	private ICategoryDao categoryDao;
	private IProductDao productDao;

	public ProductServiceImpl(ICategoryDao categoryDao, IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}
	
	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> save(Product product, long categoryId) {
		//metodo guardar
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list= new ArrayList<>();
		
		try {
			//buscar categoria seteando al objeto producto
			Optional<Category> category = categoryDao.findById(categoryId);
			
			if( category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("respuesta nok","-1", "categoria no en contrada asociada al producto");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			//guardado del producto
			Product productSaved = productDao.save(product);
			
			if(productSaved != null) {
				list.add(productSaved);
				response.getProduct().setProducts(list);
				response.setMetadata("respuesta ok","00", "Producto guardado");
				
			} else {
				response.setMetadata("respuesta nok","-1", "Producto no guardado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta nok","-1", "Error al guardar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional (readOnly = true)
	public ResponseEntity<ProductResponseRest> searchById(Long id) {
		
			ProductResponseRest response = new ProductResponseRest();
			List<Product> list= new ArrayList<>();
			
			try {
				//buscar producto por ID
				Optional<Product> product = productDao.findById(id);
				
				if( product.isPresent()) {
					//recuperando mediante descomprimir para presentar al cliente angular
					byte [] imageDescompresse = Util.decompressZLib(product.get().getPicture());
					product.get().setPicture(imageDescompresse);
					list.add(product.get());
					response.getProduct().setProducts(list);
					response.setMetadata("Respuesta ok", "00", "Producto encontrado" );
				} else {
					response.setMetadata("respuesta nok","-1", "producto no encontrado");
					return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
				}
				
				
			} catch (Exception e) {
				e.getStackTrace();
				response.setMetadata("respuesta nok","-1", "Error al guardar producto");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional (readOnly = true)
	public ResponseEntity<ProductResponseRest> searchByName(String name) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list= new ArrayList<>();
		//lista auxiliar
		List<Product> listAux= new ArrayList<>();
		
		try {
			//buscar producto by name
			listAux = productDao.findByNameContainingIgnoreCase(name);
			
			if(listAux.size() >0 ) {
				//se comprime las imagenes, y bscara por nombre tipo contenga, recorre la lista para obtener x cada product
				listAux.stream().forEach( (p) -> {
					byte [] imageDescompresse = Util.decompressZLib(p.getPicture());
					p.setPicture(imageDescompresse);
					list.add(p);
				});
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta ok", "00", "Productos encontrados" );
			
			} else {
				response.setMetadata("respuesta nok","-1", "productos no encontrados");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			
			
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta nok","-1", "Error al buscar producto por nombre");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> deleteById(Long id) {
		ProductResponseRest response = new ProductResponseRest();
		
		try {
			//eliminar producto por ID
			productDao.deleteById(id);
			response.setMetadata("Respuesta ok", "00", "Producto eliminado" );
				
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta nok","-1", "Error al eliminar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}

	@Override
	@Transactional  (readOnly = true)
	public ResponseEntity<ProductResponseRest> search() {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list= new ArrayList<>();
		//lista auxiliar
		List<Product> listAux= new ArrayList<>();
		
		try {
			//buscar producto 
			listAux = (List<Product>) productDao.findAll();
	
			if(listAux.size() >0 ) {
				//se comprime las imagenes, y bscara por nombre tipo contenga, recorre la lista para obtener x cada product
				listAux.stream().forEach( (p) -> {
					byte [] imageDescompresse = Util.decompressZLib(p.getPicture());
					p.setPicture(imageDescompresse);
					list.add(p);
				});
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta ok", "00", "Productos encontrados" );
			
			} else {
				response.setMetadata("respuesta nok","-1", "productos no encontrados");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			
			
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta nok","-1", "Error al buscar productos");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);

	}

	@Override
	@Transactional //sirve para dar un rolback y commit a la bdd cuando falla algo
	public ResponseEntity<ProductResponseRest> update(Product product, long categoryId, Long id) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list= new ArrayList<>();
		
		try {
			//buscar categoria seteando al objeto producto
			Optional<Category> category = categoryDao.findById(categoryId);
			
			if( category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("respuesta nok","-1", "categoria no en contrada asociada al producto");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			//buscar el producto a actualizar
			Optional<Product> productSearch = productDao.findById(id);
			
			if(productSearch.isPresent()) {
				
				//se actualiza el producto
				productSearch.get().setAccount(product.getAccount());
				productSearch.get().setCategory(product.getCategory());
				productSearch.get().setName(product.getName());
				productSearch.get().setPicture(product.getPicture());
				productSearch.get().setPrice(product.getPrice());
				
				//guarda el producto en bdd
				Product productToUpdate = productDao.save(productSearch.get());
				
				if (productToUpdate != null) {
					list.add(productToUpdate);
					response.getProduct().setProducts(list);
					response.setMetadata("respuesta ok","00", "Producto actualizado");
					
				} else {
					response.setMetadata("respuesta nok","-1", "Producto no actualizado");
					return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
				}
				
			} else {
				response.setMetadata("respuesta nok","-1", "Producto no actualizado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta nok","-1", "Error al actualizar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

}
