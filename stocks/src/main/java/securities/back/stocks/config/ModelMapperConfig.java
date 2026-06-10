package securities.back.stocks.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    public ModelMapper modelmapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT) // property 이름이 정확히 일치해야만 자동 mapping
                .setSkipNullEnabled(true); // 객체에 새로운 값들을 업데이트 할 때, null이 아닌 값만 업데이트

        return modelMapper;
    }
}
