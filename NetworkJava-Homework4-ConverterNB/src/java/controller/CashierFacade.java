package controller;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.Currency;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class CashierFacade {
    @PersistenceContext(unitName = "ConverterNBPU")
    private EntityManager em;
    
    public void createCurrencies(){
        List<Object> results = em.createQuery("SELECT c FROM Currency c").getResultList();
        if(results.size()<=0){
            em.persist(new Currency("eur", 1));
            em.persist(new Currency("chf", 1.08));
            em.persist(new Currency("sek", 9.27));
            em.persist(new Currency("usd", 1.06));
        }
    }

    public double convert(String fromCurr, String toCurr, double value) {
        System.out.println("looking for : " + fromCurr  + " and : " + toCurr);
        Currency from = em.find(Currency.class, fromCurr);
        Currency to = em.find(Currency.class, toCurr);
        if(from == null || to == null) 
            throw new IllegalArgumentException("from or to currencies not found");
        double fromV = from.getValue();
        double toV = to.getValue();
          
        return value*toV/fromV;
    }
}
