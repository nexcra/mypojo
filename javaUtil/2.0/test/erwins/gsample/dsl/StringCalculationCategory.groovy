package erwins.gsample.dsl



import erwins.util.tools.*

/** '+'연산을 재정의 한다. */
public class StringCalculationCategory {
    static def plus(String self, String operand) {
        try {    
            return self.toInteger() + operand.toInteger()
        }
        catch (NumberFormatException fallback){
            return (self << operand).toString()
        }
    }
} 