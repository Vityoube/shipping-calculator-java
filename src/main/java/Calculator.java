import java.math.BigDecimal;
import java.util.Map;

public class Calculator {
    private final CalculatorConfig config;

    public Calculator() {
        try {
            config = CalculatorConfig.loadConfig();
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Error occurred during calculator initialization: %s", e.getMessage()), e);
        }
    }

    public BigDecimal calculate(BigDecimal totalWeight) {
        BigDecimal rate = findRate(totalWeight);
        return totalWeight.multiply(rate).setScale(config.rateScale(), config.roundingMode());
    }

    private BigDecimal findRate(BigDecimal totalWeight) {
        assert totalWeight.compareTo(BigDecimal.ZERO) > 0;
        return config.priceRanges().entrySet()
                .stream()
                .filter(entry -> totalWeight.subtract(entry.getKey()).compareTo(BigDecimal.ZERO) >= 0)
                .map(entry -> Map.entry(totalWeight.subtract(entry.getKey()), entry.getValue()))
                .min(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("No rate found for " + totalWeight));
    }
}
