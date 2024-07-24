package com.alibaba.csp.sentinel.dashboard.rule.redis;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李春
 * @create 2024/07/23
 */
@Component("degradeRuleRedisProvider")
public class DegradeRuleRedisProvider implements DynamicRuleProvider<List<DegradeRuleEntity>> {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private Converter<String, List<DegradeRuleEntity>> converter;

    @Override
    public List<DegradeRuleEntity> getRules(String appName) throws Exception {
        String rules = redisTemplate.opsForValue().get(appName + RedisConfigUtil.DEGRADE_RULE_KEY_POSTFIX);

        if (StringUtils.isBlank(rules)) {
            return new ArrayList<>();
        }

        return converter.convert(rules);
    }
}
