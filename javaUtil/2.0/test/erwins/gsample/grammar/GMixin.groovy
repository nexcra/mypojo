package erwins.gsample.grammar


import org.junit.Test

/** 믹스인으로 다중상속이 가능하다. 멋진 조합 가능. */
public class GMixin{
    @Test
    public void mix(){
		assert new Plane().fly() == "I'm the Concorde and I fly!"
		assert new Submarine().dive() == "I'm the Yellow Submarine and I dive!"
		
		//JamesBondVehicle.mixin DivingAbility, FlyingAbility  이렇게 런타임으로 지정해도 된다.
		assert new JamesBondVehicle().fly() == "I'm the James Bond's vehicle and I fly!"
		assert new JamesBondVehicle().dive() == "I'm the James Bond's vehicle and I dive!"
    }
}

@Category(Vehicle)
class FlyingAbility {
    def fly() { "I'm the ${name} and I fly!" }
}
@Category(Vehicle)
class DivingAbility {
    def dive() { "I'm the ${name} and I dive!" }
}

interface Vehicle {
    String getName()
}

@Mixin(DivingAbility)
class Submarine implements Vehicle {
    String getName() { "Yellow Submarine" }
}
@Mixin(FlyingAbility)
class Plane implements Vehicle {
    String getName() { "Concorde" }
}
@Mixin([DivingAbility, FlyingAbility])
class JamesBondVehicle implements Vehicle {
    String getName() { "James Bond's vehicle" }
}



