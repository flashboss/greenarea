/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.db.facades;

import it.vige.greenarea.gtg.db.entities.SystemVar;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author 00917308
 */
@Stateless
public class SystemVarFacade extends AbstractFacade<SystemVar, String> {
    @PersistenceContext(unitName = "GTGwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SystemVarFacade() {
        super(SystemVar.class);
    }
    
    public SystemVar findOrCreate( String varName ){
        SystemVar systemVar = find( varName );
        if(systemVar == null){
            systemVar = new SystemVar( varName );
            create( systemVar );
        }
        return systemVar;
    }

    @Override
    public String getId(SystemVar entity) {
        return entity.getVarName();
    }

    @Override
    public void setId(SystemVar entity, String id) {
        entity.setVarName(id);
    }
    
    
}
