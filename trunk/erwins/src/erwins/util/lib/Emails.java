
package erwins.util.lib;



/**
 * @author     Administrator
 */
public enum Emails {
    
    /**
     * @uml.property  name="google"
     * @uml.associationEnd  
     */
    google("google.com"),
    /**
     * @uml.property  name="hotmail"
     * @uml.associationEnd  
     */
    hotmail("hotmail.com"),
    /**
     * @uml.property  name="nON"
     * @uml.associationEnd  
     */
    NON("");
    
    private String mailAdress;
    
    private Emails(String mailAdress){
        this.mailAdress = mailAdress;
    }
    
    

  /**
   * <option value='writeMode'>직접입력</option>
                                            <option value='hotmail.com'>hotmail.com</option>
                                            <option value='hanmail.net'>hanmail.net</option>
                                            <option value='daum.net'>daum.net</option>
                                            <option value='msn.com'>msn.com</option>
                                            <option value='nate.com'>nate.com</option>
                                            <option value='korea.com'>korea.com</option>
                                            <option value='lycos.co.kr'>lycos.co.kr</option>
                                            <option value='hanafos.com'>hanafos.com</option>
                                            <option value='naver.com'>naver.com</option>
   */


}
