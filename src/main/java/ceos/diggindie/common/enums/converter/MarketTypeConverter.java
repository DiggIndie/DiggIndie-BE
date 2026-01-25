package ceos.diggindie.common.enums.converter;

import ceos.diggindie.common.enums.MarketType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MarketTypeConverter implements Converter<String, MarketType> {

    @Override
    public MarketType convert(String source) {
        return MarketType.from(source);
    }
}