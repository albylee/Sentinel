package com.alibaba.csp.sentinel.dashboard.rule.redis;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李春
 * @create 2024/07/23
 */
@Component("degradeRuleRedisPublisher")
public class DegradeRuleRedisPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private Converter<List<DegradeRuleEntity>, String> converter;

    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }

        String strRules = converter.convert(rules);

        redisTemplate.opsForValue().set(app + RedisConfigUtil.DEGRADE_RULE_KEY_POSTFIX, strRules);

        redisTemplate.convertAndSend(app + RedisConfigUtil.DEGRADE_CHANNEL_POSTFIX, strRules);
    }
}
