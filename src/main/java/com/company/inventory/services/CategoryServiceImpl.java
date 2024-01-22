package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.response.CategoryResponseRest;

@Service
public class CategoryServiceImpl implements ICategoryService{
	
	//inyecta al servicio categoryDao
	@Autowired
	private ICategoryDao categoryDao;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoryResponseRest> search() {
		
		CategoryResponseRest response = new CategoryResponseRest();
		
		try {
			
			List<Category> category = (List<Category>) categoryDao.findAll();
			response.getCategoryRest().setCategory(category);
			response.setMetadata("Respuesta ok", "00", "Respuesta exitosa");
			
		} catch (Exception e) {
			
			response.setMetadata("Respuesta Nok", "-1", "Error al consultar");
			//para imprimir en logs
			e.getStackTrace();
			return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
			
		}
		
		return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.OK);
	}
    //metodo buscar por ID
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoryResponseRest> searchById(Long id) {
		
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		
		try {
			//este objeto devuelve un objeto optional busca en la bdd
			Optional<Category> category = categoryDao.findById(id);
			//si la categoria existe por id
			if (category.isPresent()) {
				//se agrega la categoria a la lista
					list.add(category.get());
					response.getCategoryRest().setCategory(list);
					response.setMetadata("Respuesta ok", "00", "Categoria  econtrada");
			} else {
				response.setMetadata("Respuesta Nok", "-1", "Categoria no econtrada");
				//para imprimir en logs
				return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			
			response.setMetadata("Respuesta Nok", "-1", "Error al consultar por id");
			//para imprimir en logs
			e.getStackTrace();
			return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
			
		}
		
		return new ResponseEntity<CategoryResponseRest>(response, HttpStatus.OK);
	}

}
