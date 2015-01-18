/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.db.facades;

import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.TruckServiceClass_;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/** 
 *
 * @author 00917377
 */
@Stateless
public class TruckServiceClassFacade extends AbstractFacade<TruckServiceClass, Long> {
    @PersistenceContext(unitName = "GTGwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TruckServiceClassFacade() {
        super(TruckServiceClass.class);
    }
    
    @Override
    public Long getId(TruckServiceClass entity) {
        return  entity.getId();
    }

    @Override
    public void setId(TruckServiceClass entity, Long id) {
        entity.setId(id);
    }
    
    public List<TruckServiceClass> findBySelection( String serviceClassType ) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<TruckServiceClass> trRoot = cq.from(TruckServiceClass.class);
        
        Predicate wherePredicate = cb.and( cb.like(trRoot.get(TruckServiceClass_.description), serviceClassType));
        cq = cq.select( trRoot ).where(wherePredicate);
        Query q = getEntityManager().createQuery(cq);
        return q.getResultList();

    }
}
