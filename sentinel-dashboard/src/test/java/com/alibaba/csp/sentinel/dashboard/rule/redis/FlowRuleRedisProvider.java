package com.alibaba.csp.sentinel.dashboard.rule.redis;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李春
 * @create 2024/07/23
 */
@Component("flowRuleRedisProvider")
public class FlowRuleRedisProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private Converter<String, List<FlowRuleEntity>> converter;

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        return redisTemplate.execute((RedisCallback<List<FlowRuleEntity>>) (connection) -> {
            List<FlowRuleEntity> rules = new ArrayList<>();

            connection.subscribe((msg,channel) -> {
                String message = new String(msg.getBody());

                List<FlowRuleEntity> convert = converter.convert(message);

                if (CollectionUtils.isNotEmpty(convert)) {
                    rules.addAll(convert);
                }

            }, (appName + RedisConfigUtil.FLOW_CHANNEL_POSTFIX).getBytes());

            return rules;
        });
    }
}
