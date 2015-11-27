/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String name;
    private double value; // x Name = 1 EUR
    
    public Currency() {}

    public Currency(String name, double value){
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return name;
    }

    public void setId(String id) {
        this.name = id;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value){
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Currency[ id=" + name + " ]";
    }
    
}
