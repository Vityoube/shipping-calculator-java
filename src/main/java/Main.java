import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        System.out.println(calculator.calculate(new BigDecimal("9.56")));
    }
}
