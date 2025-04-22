package faustofan.app.framework.cache.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
public class StringRedisTemplateProxyTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

}
