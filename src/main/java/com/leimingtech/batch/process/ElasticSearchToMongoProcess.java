package com.leimingtech.batch.process;

import com.alibaba.fastjson.JSON;
import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/11 10:23
 * @Description:
 */
@Component
public class ElasticSearchToMongoProcess implements ItemProcessor<Map, Goods> {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchToMongoProcess.class);


    @Override
    public Goods process(Map item) throws Exception {
        Goods goods = JSON.parseObject(JSON.toJSONString(item), Goods.class);
        return goods;
    }
}
