/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.CashierFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("conversionManager")
@ConversationScoped
public class ConversionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EJB
    private CashierFacade cashierFacade;
    private String fromCurrency;
    private String toCurrency;
    private String value;
    private String result;
    
    @Inject
    private Conversation conversation;
    
    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setValue(String amount) {
        this.value = amount;
    }
    
    public void setResult(String result){
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }
    
    public String getResult(){
        return result;
    }
    
    

    public void convertCurrencies() {
        //System.out.println("HELLO BUTTON WORKS");
        cashierFacade.createCurrencies();
        double r = cashierFacade.convert(fromCurrency,toCurrency,Double.parseDouble(value));
        String re = String.format("%.2f", r);
        setResult(re + " in " + toCurrency);
    }
    
    
    
    public void reset(){
        value = "";
        result = null;
    }
}
