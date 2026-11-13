import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public record CalculatorConfig(Map<BigDecimal, BigDecimal> priceRanges, int rateScale, RoundingMode roundingMode) {
    private static final String CONFIGURATION_FILE = "configuration.properties";
    private static final String PRECISION_PROPERTY = "precision";
    private static final String ROUNDING_MODE_PROPERTY = "roundingMode";
    private static final String RANGE_PREFIX =  "range.";

    static CalculatorConfig loadConfig() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONFIGURATION_FILE)) {
            properties.load(inputStream);
            int precision = Integer.parseInt(Optional.ofNullable(properties.get(PRECISION_PROPERTY))
                    .orElseThrow(() -> new IllegalArgumentException("precision is not provided")).toString());
            RoundingMode roundingMode = RoundingMode.valueOf(Optional.ofNullable(properties.get(ROUNDING_MODE_PROPERTY))
                    .orElseThrow(()-> new IllegalArgumentException("roundingMode is not provided")).toString().toUpperCase());
            Map<BigDecimal, BigDecimal> priceRanges = properties.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null)
                    .filter(entry -> entry.getValue() != null)
                    .filter(entry -> entry.getKey().toString().startsWith(RANGE_PREFIX))
                    .map(entry -> Map.entry(new BigDecimal(entry.getKey().toString().replaceFirst(RANGE_PREFIX, "")),
                            new BigDecimal(entry.getValue().toString())))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
            if (priceRanges.isEmpty()) {
                throw new IllegalArgumentException("ranges are not provided");
            }
            return new CalculatorConfig(priceRanges, precision, roundingMode);
        }
    }
}
