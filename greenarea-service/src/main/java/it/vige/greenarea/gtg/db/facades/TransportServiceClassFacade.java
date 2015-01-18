/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.db.facades;

import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.TransportServiceClass_;

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
public class TransportServiceClassFacade extends AbstractFacade<TransportServiceClass, Long> {
    @PersistenceContext(unitName = "GTGwebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransportServiceClassFacade() {
        super(TransportServiceClass.class);
    }
    
    @Override
    public Long getId(TransportServiceClass entity) {
        return  entity.getId();
    }

    @Override
    public void setId(TransportServiceClass entity, Long id) {
        entity.setId(id);
    }
    
      public List<TransportServiceClass> findBySelection( String serviceClassType ) {
         
 
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<TransportServiceClass> trRoot = cq.from(TransportServiceClass.class);
        
        Predicate wherePredicate = cb.and( cb.like(trRoot.get(TransportServiceClass_.description), serviceClassType));
        cq = cq.select(trRoot ).where(wherePredicate);
        Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }
}
