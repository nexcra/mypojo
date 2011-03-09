
package erwins.util.lib;

import java.math.BigDecimal;

/**
 * SI단위계 표시
 */
public enum SiUnit {
    
    /**
     * @uml.property  name="yOTTA"
     * @uml.associationEnd  
     */
    YOTTA ("1000000000000000000000000","요타"),
    /**
     * @uml.property  name="zETTA"
     * @uml.associationEnd  
     */
    ZETTA    ("1000000000000000000000","제타"),
    /**
     * @uml.property  name="eXA"
     * @uml.associationEnd  
     */
    EXA         ("1000000000000000000","엑사"),
    /**
     * @uml.property  name="pETA"
     * @uml.associationEnd  
     */
    PETA           ("1000000000000000","페타"),
    /**
     * @uml.property  name="tERA"
     * @uml.associationEnd  
     */
    TERA              ("1000000000000","테라"),
    /**
     * @uml.property  name="gIGA"
     * @uml.associationEnd  
     */
    GIGA                 ("1000000000","기가"),
    /**
     * @uml.property  name="mEGA"
     * @uml.associationEnd  
     */
    MEGA                    ("1000000","메가"),
    /**
     * @uml.property  name="kILO"
     * @uml.associationEnd  
     */
    KILO                       ("1000","킬로"),
    /**
     * @uml.property  name="hECTO"
     * @uml.associationEnd  
     */
    HECTO                       ("100","헥토"),
    /**
     * @uml.property  name="dEKA"
     * @uml.associationEnd  
     */
    DEKA                         ("10","데카"),
    /**
     * @uml.property  name="dECI"
     * @uml.associationEnd  
     */
    DECI                        ("0.1","데시"),
    /**
     * @uml.property  name="cENTI"
     * @uml.associationEnd  
     */
    CENTI                      ("0.01","센티"),
    /**
     * @uml.property  name="mILLI"
     * @uml.associationEnd  
     */
    MILLI                     ("0.001","밀리"),
    /**
     * @uml.property  name="mICRO"
     * @uml.associationEnd  
     */
    MICRO                 ("0.000 001","마이크로"),
    /**
     * @uml.property  name="nANO"
     * @uml.associationEnd  
     */
    NANO                ("0.000000001","나노"),
    /**
     * @uml.property  name="pICO"
     * @uml.associationEnd  
     */
    PICO             ("0.000000000001","피코"),
    /**
     * @uml.property  name="fEMTO"
     * @uml.associationEnd  
     */
    FEMTO         ("0.000000000000001","펨토"),
    /**
     * @uml.property  name="aTTO"
     * @uml.associationEnd  
     */
    ATTO       ("0.000000000000000001","아토"),
    /**
     * @uml.property  name="zEPTO"
     * @uml.associationEnd  
     */
    ZEPTO   ("0.000000000000000000001","젭토"),
    /**
     * @uml.property  name="yOCTO"
     * @uml.associationEnd  
     */
    YOCTO("0.000000000000000000000001","욕토");
    
    /**
     * @uml.property  name="unit"
     */
    private BigDecimal unit;
    private String korean;
    
    private SiUnit(String unit,String korean){
        this.unit = new BigDecimal(unit);
        this.korean = korean;
    }
    
    @Override
    public String toString() {
        return korean;
    }

    /**
     * @return
     * @uml.property  name="unit"
     */
    public BigDecimal getUnit() {
        return unit;
    }
    
    

}