package atomic_variables;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Apple {

    private Integer weight;

    public Apple(Integer weight) {
        this.weight = weight;
    }
}
